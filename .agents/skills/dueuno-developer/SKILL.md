<!--
SPDX-License-Identifier: Apache-2.0
-->

---

name: dueuno-developer
description: Strict conventions and patterns for building UI controllers with Dueuno, including content structure, forms, tables, and interaction handling
license: Apache-2.0
compatibility: opencode, claude, grok, gemini, copilot, cursor, windsurf
metadata:
audience: developers
frameworks: dueuno
---------------------------

## What I Do

* Provide strict conventions for building UI controllers using Dueuno.
* Enforce consistent patterns for tables, forms, and modal interactions.
* Guide the correct use of `createContent`, `display`, and UI components.
* Ensure controllers remain clean, declarative, and UI-focused.
* Standardize naming, structure, and interaction flows.

---

## When to Use Me

Activate this skill when working with Dueuno, including:

* Building UI controllers using `ElementsController`
* Creating tables, filters, and paginated views
* Implementing create/edit/delete flows
* Defining forms using Dueuno components
* Handling modal interactions and UI state

---

## Controller Architecture

Controllers represent the **presentation layer**.

They MUST:

* be concise
* be declarative
* contain NO business logic
* delegate all operations to services

---

## Controller Requirements

### Base Structure

```groovy
import dueuno.elements.ElementsController
import groovy.util.logging.Slf4j
import grails.plugin.springsecurity.annotation.Secured

@Slf4j
@Secured(['ROLE_USER'])
class CompanyController implements ElementsController {

    CompanyService companyService
}
```

---

## Action Conventions

Controllers MUST define:

* `index`
* `create` / `onCreate`
* `edit` / `onEdit`
* `onDelete`

Naming MUST NOT change.

---

## Content Creation

UI MUST be created using:

```groovy
def c = createContent(ContentTable)
```

Allowed content types:

* `ContentTable`
* `ContentCreate`
* `ContentEdit`

---

## Table Pattern

```groovy
def index() {
    def c = createContent(ContentTable)

    c.table.with {

        filters.with {
            addField(
                class: TextField,
                id: 'find',
            )
        }

        sortable = [
            name: 'asc',
        ]

        columns = [
            'name',
            'isActive',
        ]

        body.eachRow { TableRow row, Map values ->
            // UI-only logic (MUST be lightweight)
        }
    }

    c.table.body = companyService.list(c.table.filterParams, c.table.fetchParams)
    c.table.paginate = companyService.count(c.table.filterParams)

    display content: c
}
```

---

## Form Pattern

### Mandatory Method

```groovy
private buildForm(Company obj = null, Boolean readonly = false)
```

---

### Form Implementation

```groovy
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
            id: 'isActive',
            cols: 4,
        )
    }

    if (obj) {
        c.form.values = obj
    }

    return c
}
```

---

## CRUD Actions

### Create

```groovy
def create() {
    def c = buildForm()
    display content: c, modal: true
}
```

```groovy
def onCreate() {
    def obj = companyService.create(params)
    if (obj.hasErrors()) {
        display errors: obj
        return
    }

    display action: 'index'
}
```

---

### Edit

```groovy
def edit() {
    def obj = companyService.get(params.id)
    def c = buildForm(obj)

    display content: c, modal: true
}
```

```groovy
def onEdit() {
    def obj = companyService.update(params)
    if (obj.hasErrors()) {
        display errors: obj
        return
    }

    display action: 'index'
}
```

---

### Delete

```groovy
def onDelete() {
    try {
        companyService.delete(params.id)
        display action: 'index'

    } catch (Exception e) {
        display exception: e
    }
}
```

---

## Error Handling

```groovy
def handleException(Exception e) {
    log.error e.message, e
    display exception: e
}
```

---

## Code Generation Bias (CRITICAL)

The following rules MUST be followed when generating code.

### Variable Naming

* content variable MUST be named:

```groovy
def c = createContent(...)
```

* domain instance MUST be named:

```groovy
def obj = ...
```

* request data MUST be accessed via:

```groovy
params
```

---

### Structural Bias

* ALWAYS use `with {}` blocks for:

    * `c.table`
    * `c.form`
    * `filters`

* ALWAYS structure methods in this order:

1. create content
2. configure UI
3. call service
4. call `display`

---

### Method Patterns

* `index()` MUST:

    * configure table
    * call `list` and `count`
    * call `display`

* `create()` and `edit()` MUST:

    * call `buildForm`
    * use `modal: true`

* `onCreate()` / `onEdit()` MUST:

    * call service
    * check `hasErrors()`
    * use `display errors`
    * redirect with `display action`

---

### UI Consistency

* Field IDs SHOULD follow domain property names:

    * `name`
    * `isActive`
    * `dateCreated`

* Columns MUST match field IDs

---

### Forbidden Deviations

Generated code MUST NOT:

* rename `c`, `obj`, or `params`
* inline form definitions
* skip `with {}` blocks
* introduce business logic
* bypass `display`

---

## Forbidden Practices

* business logic inside controllers
* direct persistence access
* bypassing `createContent`
* building UI outside Dueuno APIs
* defining forms outside `buildForm`
* heavy logic inside `eachRow`

---

## Compliance Checklist

* [ ] Implements `ElementsController`
* [ ] Uses `@Slf4j` and `@Secured`
* [ ] Service injected with explicit type
* [ ] Uses `createContent(...)`
* [ ] Defines all required actions
* [ ] Uses `display` for responses
* [ ] Forms defined in `buildForm`
* [ ] No business logic in controller
* [ ] UI built only with Dueuno APIs
