package dueunoapp

import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant

import javax.annotation.PostConstruct

@CurrentTenant
class ProductService {

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    private DetachedCriteria<TProduct> buildQuery(Map filterParams) {
        def query = TProduct.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where { 1 == 1
                || name =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    TProduct get(Serializable id) {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        Map fetch = [
                relationshipName: 'join',
        ]

        return buildQuery(id: id).get(fetch: fetch)
    }

    List<TProduct> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']

        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        fetchParams.fetch = [
                relationshipName: 'join',
        ]

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    TProduct create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TProduct obj = new TProduct(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    TProduct update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TProduct obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    void delete(Serializable id) {
        TProduct obj = get(id)
        obj.delete(flush: true, failOnError: true)
    }
}
