---
name: dueuno-developer
description: |
  Expert knowledge for developing Dueuno full-stack applications using Groovy and Grails.
  Covers architecture, UI (Dueuno), business logic, security, and advanced features.
  Use this skill when building or modifying Dueuno applications, UI components, services, or system configuration.
version: 1.0.0
tags:
  - groovy
  - grails
  - dueuno
  - fullstack
  - enterprise
  - backend
  - low-code
---

# Dueuno Developer Skill

## When to use this skill
Use this skill when the task involves:
- Building or modifying a Dueuno application
- Designing UI with Dueuno Elements (ContentTable, ContentForm, etc.)
- Implementing business logic with GORM / Grails services
- Managing multi-tenant architecture
- Configuring security (Spring Security / roles / permissions)
- Customizing application UI or behavior

---

## Core Knowledge

### Create/Initialize a new Dueuno application

Verify that the following dependency is present in `build.gradle`: implementation "org.apache.grails:grails-core". If not, alter the user and stop here.

Latest Dueuno version: 3.1.0

```groovy
dependencies {
    ...
    implementation "org.dueuno:dueuno-core:${latestDueunoVersion}"
}
```

Add the following code to initialize the application:

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onInit {
            // no-op // <1>
        }

    }
}
```

Delete the file `grails-app/controllers/**/UrlMappings.groovy`

Delete all files in:
- `grails-app/assets/javascripts/`
- `grails-app/assets/stylesheets/`
- `grails-app/assets/images/`
- `grails-app/views/`

Replace `grails-app/init/BootStrap.groovy` with `BootStrap.groovy` from assets. 

Replace `grails-app/conf/logback-spring.xml` with `logback-spring.xml` from assets.

Add the following into `application.yml` replacing ${project-name} with the project name:
```yml
---
server:
  tomcat:
    basedir: ${project-name}/tomcat
  servlet:
    session:
      persistent: true
      store-dir: ${project-name}
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json

---
javamelody:
  init-parameters:
    log: true
    storage-directory: ${user.dir}/${project-name}/java-melody

---
grails:
  plugin:
    springsecurity:
      debug:
        useFilter: true
  controllers:
    upload:
      maxFileSize: 20000000
      maxRequestSize: 20000000

---
logs:
  cleanHistoryOnStart: true
  maxHistory: 10
  maxFileSize: 100MB
  totalSizeCap: 1GB
```

Set the H2 database url to `jdbc:h2:./project-name/project-name;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE`.

Set the `dbCreate` property to `update` for the development environment.

Add the line `/project-name/` to the `.gitignore` in the root of the project.


## Create a CRUD

If the project is not a Grails project, stop here.

If the project is not a Dueuno project (does not depend on `org.dueuno:dueuno-core`) create a Dueuno project first, then proced.

To create a CRUD, create the following:

- Create a Domain Class
- Create a Service
- Create a Controller

IMPORTANT: Verify with the Compliance Checklist and correct if needed

## Create a Domain Class

Dueuno domain classes have the following characteristics:

- Are located in `grails-app/domain/`
- Are prefixed with the letter `T` (eg. `TCompany`)
- Have a corresponding `grails-app/services/` service class. If not present, create one for the domain class.
- They all implement the following classes and fields:

```groovy
import java.time.LocalDateTime

@GrailsCompileStatic
class TCompany implements GormEntity, MultiTenant<TCompany> {

  Long id
  LocalDateTime dateCreated
  LocalDateTime lastUpdated
  
}
```

IMPORTANT: Verify with the Compliance Checklist and correct if needed


## Create a Service

Ask the user the name of the Domain class to create a service for, if not specified in the prompt.

Use the template found in `assets` to create a new service.

The service name is not prefixed with `T`.

Implement the filters for each field in the specified domain class.

Define the `fetch` properties for each relationship in the domain class.

IMPORTANT: Verify with the Compliance Checklist and correct if needed


## Create a Controller

To create a controller, a Domain class and a Service must exist in the project. If not ask the user to create them.

Use the template found in `assets` to create a new controller.

The controller name is not prefixed with `T`.

Implement the related feature in `BootStrap.groovy`. For the first controller of the application set the `favourite` parameter to `true`.'

Dueuno provides the following component types: `Content`, `Component`, `Controller`.
Implement the controller using the available Dueuno components.

IMPORTANT: Verify with the Compliance Checklist and correct if needed

### Contents

See the reference documentation for the available contents.

IMPORTANT: Verify with the Compliance Checklist and correct if needed


### Components

See the reference documentation for the available components.

IMPORTANT: Verify with the Compliance Checklist and correct if needed


### Controls

See the reference documentation for the available controls.

IMPORTANT: Verify with the Compliance Checklist and correct if needed


### Actions

See the reference documentation for the available actions.

IMPORTANT: Verify with the Compliance Checklist and correct if needed


### Features

See the reference documentation for the available features.

IMPORTANT: Verify with the Compliance Checklist and correct if needed

---

## Code Style

* Prefere the `for` construct every time you can

## Compliance Checklist

Verify each one of the following steps before proceeding.

* [ ] Implements `ElementsController`
* [ ] Uses `@Slf4j` and `@Secured`
* [ ] Service injected with explicit type
* [ ] Uses `createContent(...)`
* [ ] Defines all required actions
* [ ] Uses `display` for responses
* [ ] Forms defined in `buildForm`
* [ ] No business logic in controllers
* [ ] UI built only with Dueuno APIs

IMPORTANT: The `applicationService.onInit()` method MUST be called after `onInstall`, `onTenantInstall` and `onDevInstall` closures otherwise the installation will not be executed.

### Forbidden Deviations

Generated code MUST NOT:

* rename `c`, `obj`, or `params`
* inline form definitions
* skip `with {}` blocks
* introduce business logic
* bypass `display`

## Forbidden Practices

* business logic inside controllers
* direct persistence access
* bypassing `createContent`
* building UI outside Dueuno APIs
* defining forms outside `buildForm`
* heavy logic inside `eachRow`
