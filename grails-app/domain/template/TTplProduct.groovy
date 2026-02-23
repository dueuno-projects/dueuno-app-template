package template

import grails.gorm.MultiTenant
import org.grails.datastore.gorm.GormEntity

import java.time.LocalDateTime

class TTplProduct implements GormEntity, MultiTenant<TTplProduct> {

    Long id
    LocalDateTime dateCreated

    String ref
    String name

}
