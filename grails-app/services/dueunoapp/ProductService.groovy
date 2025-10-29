package dueunoapp

import dueuno.elements.audit.AuditOperation
import dueuno.elements.audit.AuditService
import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.annotation.PostConstruct

@Slf4j
@CurrentTenant
@CompileStatic
class ProductService {

    AuditService auditService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    @CompileDynamic
    private DetachedCriteria<TProduct> buildQuery(Map filterParams) {
        def query = TProduct.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                true
                        || name =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    private Map getFetchAll() {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        return [
                'relationshipName': 'join',

                // hasMany relationships
                'hasManyRelationship': 'join',
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        return [
                'relationshipName': 'join',
        ]
    }

    TProduct get(Serializable id) {
        return buildQuery(id: id).get(fetch: fetchAll)
    }

    List<TProduct> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @CompileDynamic
    TProduct create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TProduct obj = new TProduct(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @CompileDynamic
    TProduct update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TProduct obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @CompileDynamic
    void delete(Serializable id) {
        TProduct obj = get(id)
        obj.delete(flush: true, failOnError: true)
        auditService.log(AuditOperation.DELETE, obj)
    }

}
