package template

import dueuno.types.Money
import dueuno.types.Quantity
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TOrderItem implements GormEntity, MultiTenant<TOrderItem> {

    Long id
    LocalDateTime dateCreated

    TProduct product
    Money unitPrice
    Quantity quantity
    Money price

    static embedded = [
            'unitPrice',
            'quantity',
            'price',
    ]

    TOrder order
    static belongsTo = [
            order: TOrder,
    ]

    static constraints = {
        price nullable: true
    }

}
