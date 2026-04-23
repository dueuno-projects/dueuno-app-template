# AGENTS.md

## Scope

This file defines **strict, enforceable rules** for generating and modifying code in this Grails project.

Agents MUST follow all rules.

---

## Naming Conventions (MANDATORY)

* Domain classes: singular, PascalCase
  `Company`, `Person`

* Services: suffix `Service`
  `CompanyService`

* Controllers: suffix `Controller`
  `CompanyController`

* Service property injection name: camelCase
  `CompanyService companyService`

---

## Domain Classes

### Requirements

A domain class MUST:

* implement `GormEntity`
* declare explicitly:
    * `Long id`
    * `LocalDateTime dateCreated`
    * `LocalDateTime lastUpdated`
* declare explicitly all:
    * `belongsTo` fields
    * `hasMany` fields
* use typed collections (`Set<Type>`)

---

## Services

### Style

Services use a **Java-like, strongly typed style**:

* explicit types
* explicit return values
* act as internal API

---

### Mandatory Rules

* MUST be annotated with:

  ```groovy
  @CompileStatic
  ```

* MUST use `@CompileDynamic` ONLY in methods implementing Grails Data (GORM) queries:
    * Where Queries
    * Dynamic finders
    * '.properties` assignment

* MUST handle persistence (controllers cannot)
* MUST comply strictly with the given REFERENCE

---

### Service Template (REFERENCE)

```groovy
package template

import grails.gorm.DetachedCriteria
import grails.gorm.transactions.Transactional
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.contracts.Requires

@CompileStatic
class CompanyService {

    AuditService auditService
    
    @CompileDynamic
    private DetachedCriteria<Company> buildQuery(Map filterParams) {
        def query = Company.where {}

        if (filterParams.containsKey('id')) {
            query = query.where { id == filterParams.id }
        }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where { true
                || name =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    private Map getFetchAll() {
        // Add any relationship here (Eg. references to other DomainObjects or hasMany)
        return [
            'parent': 'join',
            'employees': 'join',
        ]
    }

    private Map getFetch() {
        // Add only single-sided relationships here (Eg. references to other Domain Objects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        return [
            'parent': 'join',
        ]
    }

    Company get(Serializable id) {
        return find(id: id)
    }

    Company find(Map filterParams) {
        return buildQuery(filterParams).get(fetch: fetchAll)
    }

    List<Company> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']
        if (!fetchParams.fetch) fetchParams.fetch = fetch

        DetachedCriteria<Company> query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Number count(Map filterParams = [:]) {
        DetachedCriteria<Company> query = buildQuery(filterParams)
        return query.count()
    }

    @Transactional
    Company create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        Company obj = new Company(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    @CompileDynamic
    @Requires({ args.id })
    Company update(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        Company obj = get(args.id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    @Transactional
    void delete(Serializable id) {
        Company obj = get(id)
        obj.delete(flush: true, failOnError: true)
        auditService.log(AuditOperation.DELETE, obj)
    }
}
```

---

## Controllers

### Style

Controllers use a **script-like style**:

* concise
* UI-oriented
* minimal business logic
* optimized for readability and rapid modification

Controllers are built using **Dueuno Elements** and act as the presentation layer.

---

### Mandatory Rules

* MUST implement:

  ```groovy
  ElementsController
  ```

* MUST be annotated with:

  ```groovy
  @Slf4j
  @Secured([...])
  ```

* MUST inject services with explicit type:

  ```groovy
  CompanyService companyService
  ```

* MUST:
    * delegate ALL business logic to services
    * NEVER access GORM directly
    * use `display` for output handling

* MUST structure actions using:
    * `index` → table view
    * `create` / `onCreate`
    * `edit` / `onEdit`
    * `onDelete`

* MUST use:
    * `createContent(...)`
    * `ContentTable`, `ContentCreate`, `ContentEdit`
    * `TableRow` for table iteration

* MUST define forms via a dedicated method (`buildForm`)

---

### Controller Template (REFERENCE)

```groovy
package template

import dueuno.commons.utils.LogUtils
import dueuno.elements.ElementsController
import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentTable
import dueuno.elements.controls.Checkbox
import dueuno.elements.controls.TextField
import dueuno.elements.style.TextDefault
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j
import jakarta.annotation.PostConstruct

@Slf4j
@Secured(['ROLE_USER'])
class CompanyController implements ElementsController {

    CompanyService companyService

    @PostConstruct
    void init() {
        // Runs once at startup
    }

    def handleException(Exception e) {
        log.error LogUtils.logStackTrace(e)
        display exception: e
    }

    def index() {
        def c = createContent(ContentTable)

        c.table.with {
            filters.with {
                addField(
                    class: TextField,
                    id: 'find',
                    label: TextDefault.FIND,
                )
            }

            sortable = [
                name: 'asc',
            ]

            columns = [
                'name',
                'isOwned',
                'isClient',
                'isSupplier',
            ]

            body.eachRow { TableRow row, Map values ->
                // Avoid slow operations here
            }
        }

        c.table.body = companyService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = companyService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(Company obj = null, Boolean readonly = false) {
        def c = obj
            ? createContent(ContentEdit)
            : createContent(ContentCreate)

        if (readonly) {
            c.header.removeNextButton()
            c.form.readonly = true
        }

        c.form.with {
            validate = Company

            addField(
                class: TextField,
                id: 'name',
                cols: 12,
            )

            addField(
                class: Checkbox,
                id: 'isOwned',
                label: '',
                cols: 4,
            )

            addField(
                class: Checkbox,
                id: 'isClient',
                label: '',
                cols: 4,
            )

            addField(
                class: Checkbox,
                id: 'isSupplier',
                label: '',
                cols: 4,
            )
        }

        if (obj) {
            c.form.values = obj
        }

        return c
    }

    def create() {
        def c = buildForm()
        display content: c, modal: true
    }

    def onCreate() {
        def obj = companyService.create(params)

        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def edit() {
        def obj = companyService.get(params.id)
        def c = buildForm(obj)

        display content: c, modal: true
    }

    def onEdit() {
        def obj = companyService.update(params)

        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            companyService.delete(params.id)
            display action: 'index'

        } catch (Exception e) {
            display exception: e
        }
    }
}
```

---

### Forbidden

Agents MUST NOT:

* put business logic in controllers
* access domain classes directly
* bypass `createContent`
* build UI outside Dueuno Elements APIs
* skip `buildForm` for forms
* perform heavy logic inside `eachRow`

---

### Compliance Checklist

* [ ] Implements `ElementsController`
* [ ] Uses `@Slf4j` and `@Secured`
* [ ] Service injected and typed
* [ ] Uses `createContent`
* [ ] Has `index`, `create/onCreate`, `edit/onEdit`, `onDelete`
* [ ] Uses `display` for responses
* [ ] Form defined in `buildForm`
* [ ] No business logic inside controller
