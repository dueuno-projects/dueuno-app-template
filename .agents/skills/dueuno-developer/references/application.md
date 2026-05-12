# Application

`ApplicationService` is the main component in charge of the application setup, mainly used in `BootStrap.groovy`.

## onInstall()

Gets executed only the first time the application is run.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onInstall {
            // ...
        }

    }
}
```

## onTenantInstall()

Installs the tenant. This closure gets called only once when the application is run for the first time. It is executed for the DEFAULT tenant and when a new tenant is created from the super admin GUI.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onTenantInstall { String tenantId -> //<1>
            // ...
        }

    }
}
```

1. The `tenantId` tells what tenant is being installed

## onDevInstall()

Gets executed only once if the application is run from the IDE (only when the development environment is active). You can use this to preload data to test the application.

This closure will NOT be executed when the application is run as JAR, WAR or when the test environment is active.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onDevInstall { String tenantId ->
            // ...
        }

    }
}
```

## onUpdate()

On application releases, may you need to update the database or any other component, you can programmatically do it adding an `onUpdate` closure.

These closures get executed only once when the application starts up. The execution order is defined by the argument, in alphabetical order.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = { // <1>

        applicationService.onUpdate('2021-01-03') { String tenantId ->
            println "${tenantId}: UPDATE N.2"
        }

        applicationService.onUpdate('2021-01-02') { String tenantId ->
            println "${tenantId}: UPDATE N.1"
        }

        applicationService.onUpdate('2021-01-05') { String tenantId ->
            println "${tenantId}: UPDATE N.4"
        }

        applicationService.onUpdate('2021-01-04') { String tenantId ->
            println "${tenantId}: UPDATE N.3"
        }
    }
}
```

1. The closures will be executed in the following order based on the specified version string: `2021-01-02`, `2021-01-03`, `2021-01-04`, `2021-01-05`.

## onInit()

Initializes the application. This closure gets called each time the application is executed.

IMPORTANT: The `onInit` closure must be defined after the `onInstall`, `onTenantInstall` and `onDevInstall` closures otherwise they will not be executed.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService // <1>

    def init = {

        applicationService.onInit { // <2>
            // ...
        }

    }
}
```

1. Injects an instance of the `ApplicationService`
2. The `onInit { ... }` closure is executed each time the application starts up

## afterLogin()

Gets executed after the user logged in. The session is active, you can set session variables from here.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    SecurityService securityService // <1>

    def init = {

        securityService.afterLogin {
            // ...
        }

    }
}
```

1. Injects an instance of the `SecurityService`

## afterLogout()

Gets executed after the user logged in. The session is NOT active, you can NOT manage session variables from here.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    SecurityService securityService // <1>

    def init = {

        securityService.afterLogout {
            // ...
        }

    }
}
```

1. Injects an instance of the `SecurityService`

## Initialize the database with mockup data

Use `onTenantInstall` (data is created only once during the first application run) or `onDevInstall` (data is created only in development environment) to create records that are useful while developing the application. Since the closure is executed only once in the development environment, it is a good place to initialize lookup tables, sample records, and realistic data for manual testing.

Inject the service that owns the business logic and use its `create()` method instead of creating domain objects directly.

Set the `failOnError` argument to `true` to prevent the application from running if there is an error.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService
    CompanyService companyService

    def init = {

        applicationService.onDevInstall { String tenantId ->
            companyService.create(
                    failOnError: true,
                    name: 'Northwind Trading',
                    vat: 'IT02468135790',
                    address: 'Via Torino 15, Milano',
            )

            companyService.create(
                    failOnError: true,
                    name: 'Blue Harbor Logistics',
                    vat: 'IT13579246801',
                    address: 'Porto Industriale 8, Genova',
            )

            companyService.create(
                    failOnError: true,
                    name: 'Alpine Software Lab',
                    vat: 'IT99887766554',
                    address: 'Corso Trento 42, Bolzano',
            )

            companyService.create(
                    failOnError: true,
                    name: 'Green Valley Foods',
                    vat: 'IT11223344556',
                    address: 'Strada Agricola 3, Parma',
            )
        }

    }
}
```

The `tenantId` argument identifies the tenant being initialized. When the application is multi-tenant, use it only if the mockup data must change depending on the tenant.

# URL Path

We can set up our application to be accessible from a specific URL path. For example `http://localhost:8080/admin`.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService
    TenantPropertyService tenantPropertyService

    def init = {

        applicationService.onTenantInstall { String tenantId ->
            tenantPropertyService.setString('SHELL_URL_MAPPING', '/admin')
            tenantPropertyService.setString('LOGIN_LANDING_URL', '/') // <1>
            tenantPropertyService.setString('LOGOUT_LANDING_URL', '/') // <2>
        }

    }
}
```

1. After the user logs in the shell will redirect to this path
2. When the user logs out the shell will redirect to this path

We also need to configure the URL mappings to match the new configuration. For example if the main controller of our website is `WebsiteController.groovy` we can create the following file:

`grails-app/controllers/WebsiteUrlMappings.groovy`

```groovy
class WebsiteUrlMappings {

    static mappings = {
        "/"(controller: 'website')
        "/admin"(controller: 'shell') // <1>
    }

}
```

1. The configured path must match the value of the `SHELL_URL_MAPPING` tenant property.

