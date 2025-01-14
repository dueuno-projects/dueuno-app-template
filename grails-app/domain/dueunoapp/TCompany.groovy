package dueunoapp

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TCompany implements GormEntity, MultiTenant<TCompany> {

    LocalDateTime dateCreated

    String name
    Boolean isOwned
    Boolean isSupplier
    Boolean isClient

}
