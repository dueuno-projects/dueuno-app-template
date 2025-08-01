package dueunoapp

import dueuno.elements.core.ApplicationService
import dueuno.elements.security.SecurityService
import dueuno.elements.tenants.TenantPropertyService
import dueuno.elements.types.QuantityService
import dueuno.elements.types.QuantityUnit
import grails.web.servlet.mvc.GrailsHttpSession

import javax.servlet.ServletContext

class BootStrap {

    ServletContext servletContext

    TenantPropertyService tenantPropertyService
    ApplicationService applicationService
    SecurityService securityService

    CompanyService companyService
    ProductService productService
    QuantityService quantityService

    def init = {

        applicationService.onApplicationInstall {

        }

        applicationService.onInstall { String tenantId ->
            tenantPropertyService.setString('PRIMARY_BACKGROUND_COLOR', '#cc0000')
            tenantPropertyService.setString('LOGIN_COPY', '<a href="https://dueuno.com" target="_blank">Dueuno</a> &copy; 2023')

            quantityService.enableUnit(QuantityUnit.PCS)
        }

        applicationService.onDevInstall { String tenantId ->
            companyService.create(failOnError: true, name: 'My Company Inc.', isOwned: true, isClient: false, isSupplier: false)
            companyService.create(failOnError: true, name: 'Shower World Inc.', isOwned: false, isClient: true, isSupplier: false)
            companyService.create(failOnError: true, name: 'Shower Land Inc.', isOwned: false, isClient: true, isSupplier: false)
            companyService.create(failOnError: true, name: 'Shower Tower Inc.', isOwned: false, isClient: true, isSupplier: false)

            productService.create(failOnError: true, ref: 'P123', name: 'PARMENIDE Shower Kit')
            productService.create(failOnError: true, ref: 'A456', name: 'ARISTOTELE Shower Kit')
            productService.create(failOnError: true, ref: 'L789', name: 'PLATONE Shower Kit')
        }

        applicationService.beforeInit {

        }

        applicationService.init {
            applicationService.registerPrettyPrinter(TCompany, '${it.name}')
            applicationService.registerPrettyPrinter(TProduct, '${it.ref} - ${it.name}')

            applicationService.registerFeature(
                    controller: 'order',
                    icon: 'fa-flag',
                    favourite: true,
            )
            applicationService.registerFeature(
                    controller: 'config',
            )
            applicationService.registerFeature(
                    parent: 'config',
                    controller: 'company',
                    icon: 'fa-house-flag',
            )
            applicationService.registerFeature(
                    parent: 'config',
                    controller: 'product',
                    icon: 'fa-heart',
            )
        }

        applicationService.afterInit {

        }

        securityService.afterLogin { GrailsHttpSession session ->

        }

        securityService.afterLogout {

        }
    }

    def destroy = {
    }

}
