package template

import dueuno.types.Money
import dueuno.types.Quantity
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TTplOrderItem implements GormEntity, MultiTenant<TTplOrderItem> {

    Long id
    LocalDateTime dateCreated

    TTplProduct product
    Money unitPrice
    Quantity quantity
    Money price

    static embedded = [
            'unitPrice',
            'quantity',
            'price',
    ]

    TTplOrder order
    static belongsTo = [
            order: TTplOrder,
    ]

    static constraints = {
        price nullable: true
    }

}
