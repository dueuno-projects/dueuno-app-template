package template

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TTplCompany implements GormEntity, MultiTenant<TTplCompany> {

    Long id
    LocalDateTime dateCreated

    String name
    Boolean isOwned
    Boolean isSupplier
    Boolean isClient

}
