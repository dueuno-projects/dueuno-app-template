package dueunoapp

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TProduct implements GormEntity, MultiTenant<TProduct> {
    LocalDateTime dateCreated

    String ref
    String name
}
