package dueunoapp

import dueuno.elements.types.Money
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TOrder implements GormEntity, MultiTenant<TOrder> {
    LocalDateTime dateCreated

    TCompany supplier
    TCompany client
    String ref
    String subject
    Money total

    static embedded = [
            'total',
    ]

    static hasMany = [
            items: TOrderItem
    ]

    static constraints = {
        total nullable: true
    }
}
