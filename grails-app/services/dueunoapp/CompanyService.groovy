package dueunoapp

import dueuno.elements.audit.AuditOperation
import dueuno.elements.audit.AuditService
import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

import jakarta.annotation.PostConstruct

@Slf4j
@CurrentTenant
class CompanyService {

    AuditService auditService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    private DetachedCriteria<TCompany> buildQuery(Map filterParams) {
        def query = TCompany.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }
        if (filterParams.containsKey('isOwned')) query = query.where { isOwned == filterParams.isOwned }
        if (filterParams.containsKey('isClient')) query = query.where { isClient == filterParams.isClient }

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

    TCompany get(Serializable id) {
        return buildQuery(id: id).get(fetch: fetchAll)
    }

    List<TCompany> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    TCompany create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TCompany obj = new TCompany(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    TCompany update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TCompany obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    void delete(Serializable id) {
        TCompany obj = get(id)
        obj.delete(flush: true, failOnError: true)
        auditService.log(AuditOperation.DELETE, obj)
    }
}
