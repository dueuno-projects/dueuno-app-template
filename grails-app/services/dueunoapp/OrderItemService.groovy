package dueunoapp

import dueuno.audit.AuditOperation
import dueuno.elements.audit.AuditService
import dueuno.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

@Slf4j
@CurrentTenant
class OrderItemService {

    AuditService auditService
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

    TOrderItem get(Serializable id) {
        return buildQuery(id: id).get(fetch: fetchAll)
    }

    List<TOrderItem> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
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
                total: obj.order.items ? obj.order.items*.price.sum() : obj.price,
        )

        return obj
    }

    @Transactional
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
                total: obj.order.items ? obj.order.items*.price.sum() : obj.price,
        )

        return obj
    }

    @Transactional
    void delete(Serializable id) {
        TOrderItem obj = get(id)
        obj.delete(flush: true, failOnError: true)
        auditService.log(AuditOperation.DELETE, obj)
    }
}
