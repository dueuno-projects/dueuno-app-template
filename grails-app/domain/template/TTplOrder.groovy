package template

import dueuno.types.Money
import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TTplOrder implements GormEntity, MultiTenant<TTplOrder> {

    Long id
    LocalDateTime dateCreated

    TTplCompany supplier
    TTplCompany client
    String ref
    String subject
    Money total

    static embedded = [
            'total',
    ]

    Set<TTplOrderItem> items
    static hasMany = [
            items: TTplOrderItem,
    ]

    static constraints = {
        total nullable: true
    }

}
