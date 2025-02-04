package dueunoapp

import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant

import javax.annotation.PostConstruct

@CurrentTenant
class OrderItemService {

    OrderService orderService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    private DetachedCriteria<TOrderItem> buildQuery(Map filterParams) {
        def query = TOrderItem.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }
        if (filterParams.containsKey('order')) query = query.where { order.id == filterParams.order }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where {
                true
                        || product.name =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    TOrderItem get(Serializable id) {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        Map fetch = [
                relationshipName: 'join',
        ]

        return buildQuery(id: id).get(fetch: fetch)
    }

    List<TOrderItem> list(Map filterParams = [:], Map fetchParams = [:]) {
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

    TOrderItem create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TOrderItem obj = new TOrderItem(args)
        obj.save(flush: true, failOnError: args.failOnError)

        if (!obj.hasErrors()) {
            obj.price = obj.unitPrice * obj.quantity
            obj.save(flush: true, failOnError: args.failOnError)
        }

        orderService.update(
                id: obj.order.id,
                total: obj.order.items*.price.sum(),
        )

        return obj
    }

    TOrderItem update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TOrderItem obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)

        if (!obj.hasErrors()) {
            obj.price = obj.unitPrice * obj.quantity
            obj.save(flush: true, failOnError: args.failOnError)
        }

        orderService.update(
                id: obj.order.id,
                total: obj.order.items*.price.sum(),
        )

        return obj
    }

    void delete(Serializable id) {
        TOrderItem obj = get(id)
        obj.delete(flush: true, failOnError: true)
    }
}
