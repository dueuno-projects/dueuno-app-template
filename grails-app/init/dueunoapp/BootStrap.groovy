package dueunoapp

import dueuno.elements.core.ApplicationService
import dueuno.elements.security.SecurityService
import dueuno.elements.tenants.TenantPropertyService
import dueuno.elements.types.Money
import dueuno.elements.types.Quantity
import dueuno.elements.types.QuantityService
import dueuno.elements.types.QuantityUnit
import grails.web.servlet.mvc.GrailsHttpSession

import jakarta.servlet.ServletContext

class BootStrap {

    ServletContext servletContext

    TenantPropertyService tenantPropertyService
    ApplicationService applicationService
    SecurityService securityService

    CompanyService companyService
    ProductService productService
    OrderService orderService
    OrderItemService orderItemService
    QuantityService quantityService

    def init = {

        applicationService.onInstall {

        }

        applicationService.onTenantInstall { String tenantId ->
            tenantPropertyService.setString('PRIMARY_BACKGROUND_COLOR', '#cc0000')
            tenantPropertyService.setString('LOGIN_COPY', '<a href="https://dueuno.com" target="_blank">Dueuno</a> &copy; 2023')
            quantityService.enableUnit(QuantityUnit.PCS)
        }

        applicationService.onDevInstall { String tenantId ->
            securityService.updateGroup(tenantId: tenantId, name: 'USERS', landingPage: 'order')

            def myCompany = companyService.create(failOnError: true, name: 'My Company Inc.', isOwned: true, isClient: false, isSupplier: false)
            def showerWorld = companyService.create(failOnError: true, name: 'Shower World Inc.', isOwned: false, isClient: true, isSupplier: false)
            def showerLand = companyService.create(failOnError: true, name: 'Shower Land Inc.', isOwned: false, isClient: true, isSupplier: false)
            def showerTower = companyService.create(failOnError: true, name: 'Shower Tower Inc.', isOwned: false, isClient: true, isSupplier: false)

            def parmenide = productService.create(failOnError: true, ref: 'P123', name: 'PARMENIDE Shower Kit')
            def aristotele = productService.create(failOnError: true, ref: 'A456', name: 'ARISTOTELE Shower Kit')
            def platone = productService.create(failOnError: true, ref: 'L789', name: 'PLATONE Shower Kit')

            def o1 = orderService.create(failOnError: true, supplier: myCompany, client: showerLand, ref: '0001', subject: 'The ROSSI house')
            def o2 = orderService.create(failOnError: true, supplier: myCompany, client: showerWorld, ref: '0002', subject: 'The BIANCHI house')
            def o3 = orderService.create(failOnError: true, supplier: myCompany, client: showerTower, ref: '0003', subject: 'The SARTORI house')

            orderItemService.create(failOnError: true, order: o1, product: parmenide, unitPrice: new Money(100), quantity: new Quantity(12))
            orderItemService.create(failOnError: true, order: o1, product: aristotele, unitPrice: new Money(200), quantity: new Quantity(3))
            orderItemService.create(failOnError: true, order: o2, product: parmenide, unitPrice: new Money(100), quantity: new Quantity(6))
            orderItemService.create(failOnError: true, order: o2, product: aristotele, unitPrice: new Money(200), quantity: new Quantity(6))
            orderItemService.create(failOnError: true, order: o2, product: platone, unitPrice: new Money(300), quantity: new Quantity(6))
            orderItemService.create(failOnError: true, order: o3, product: parmenide, unitPrice: new Money(100), quantity: new Quantity(1))
            orderItemService.create(failOnError: true, order: o3, product: aristotele, unitPrice: new Money(200), quantity: new Quantity(2))
            orderItemService.create(failOnError: true, order: o3, product: platone, unitPrice: new Money(300), quantity: new Quantity(3))
        }

        applicationService.beforeInit {

        }

        applicationService.onInit {
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

        applicationService.onTenantInit {

        }

        applicationService.afterInit { String tenantId ->

        }

        securityService.afterLogin { String tenantId, GrailsHttpSession session ->

        }

        securityService.afterLogout { String tenantId ->

        }
    }

    def destroy = {
    }

}
