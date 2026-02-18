package template

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TProduct implements GormEntity, MultiTenant<TProduct> {

    Long id
    LocalDateTime dateCreated

    String ref
    String name

}
