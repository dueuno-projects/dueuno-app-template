package dueunoapp

import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant

import javax.annotation.PostConstruct

@CurrentTenant
class OrderService {

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    private DetachedCriteria<TOrder> buildQuery(Map filterParams) {
        def query = TOrder.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                true
            || ref =~ "%${search}%"
            || subject =~ "%${search}%"
            || supplier.name =~ "%${search}%"
            || client.name =~ "%${search}%"
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

    TOrder get(Serializable id) {
        return buildQuery(id: id).get(fetch: fetchAll)
    }

    List<TOrder> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']
        fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    TOrder create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TOrder obj = new TOrder(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    TOrder update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TOrder obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    void delete(Serializable id) {
        TOrder obj = get(id)
        obj.delete(flush: true, failOnError: true)
    }
}
