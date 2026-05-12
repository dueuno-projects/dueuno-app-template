# Events

Each `Component` can trigger one or more events. Please see [components](components.md) and [controls](controls.md) to see what events each specific component can trigger.

Each available event has a lowercase name. We can configure the event directly when creating a component as follows.

```groovy
c.form.with {
    addField(
            class: Select,
            id: 'book',
            onChange: 'onChangeBook', // <1>
            submit: ['form'],
    )
}
```

`<1>` The parameter name is composed by `on` followed by the capitalized name of the event (the event `change` in this case). The parameter value is the name of the action to be called.

Multiple events can be configured as follows.

```groovy
c.form.with {
    def books = addField(
            class: Select,
            id: 'book',
    ).component // <1>

    books.with {
        on( // <2>
                event: 'load',
                action: 'onLoadBooks',
        )
        on( // <3>
                event: 'change',
                action: 'onChangeBook',
                submit: ['form'],
        )
    }
}
```

- `<1>` We reference the component hold by the `FormField`, not the form field itself
- `<2>` Configuring the `load` event
- `<3>` Configuring the `change` event

The following properties can be specified when configuring an event on a component.

## Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `controller` | `String` |  | The name of the controller to redirect to. If no `action` is specified the `index` action will be displayed |
| `action` | `String` |  | The name of the action to redirect to. If no `controller` is specified and we are in the context of a web request (Eg. it's a user triggered event) the current controller will be used. If we are configuring the event outside of a web request (Eg. sending an event from a job) a `controller` must be specified. |
| `params` | `Map<String, Object>` |  | The params to pass when redirecting to a `controller` or `action` |
| `submit` | `List<String>` |  | Name list of the components whose values we want to submit. Each component is responsible to define the data structure for the values it contains. The default behaviour will send the values of all the controls contained within the component. |


# Transitions

A Transition is a set of instructions sent from the server to the client (browser) to alter the currently displayed content. For instance, when selecting a book from a list we want a text field to be populated with its description. To implement such behaviors, we use transitions.

> **NOTE:** Please refer to [controls](controls.md) and [components](components.md) to see what events are available to each component.

> **NOTE:** Refer to [websockets](#websockets) to understand how to trigger events programmatically from sources other than the user input.

`grails-app/controllers/ReadController.groovy`

```groovy
class ReadController implements ElementsController {

    BookService bookService

    def index() {
        def c = createContent(ContentForm)

        c.header.removeNextButton()

        c.form.with {
            addField(
                    class: Select,
                    id: 'book',
                    optionsFromRecordset: bookService.list(),
                    onChange: 'onChangeBook', // <1>
            )
            addField(
                    class: Textarea,
                    id: 'description',
            )
        }

        display content: c
    }

    def onChangeBook() {
        def t = createTransition() // <2>
        def book = bookService.get(params.book)

        if (book) {
            t.set('description', book.description) // <3>
            t.set('description', 'readonly', true) // <4>
        } else {
            t.set('description', null)
            t.set('description', 'readonly', false)
        }

        display transition: t
    }
}
```

- `<1>` We tell the `Select` field which action to execute when the `change` event occurs. See [events](#events).
- `<2>` We create a new Transition
- `<3>` The `set` method sets the value of the `description` field
- `<4>` We also set the `Textarea` to a `readonly` state


To finish it up we register a Pretty Printer for the book record and tell the `Select` control to use it to display the items.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onInit {
            registerPrettyPrinter('BOOK', '${it.title} - ${it.author}') // <1>
        }

    }
}
```

`<1>` A pretty printer called `BOOK` will display each book by title and author. The `it` variable refers to an instance of the book record (a `Map` in this case).

`grails-app/controllers/ReadController.groovy`

```groovy
class ReadController implements ElementsController {
    ...

        addField(
                class: Select,
                id: 'book',
                optionsFromRecordset: bookService.list(),
                prettyPrinter: 'BOOK', // <1>
                onChange: 'onChangeBook',
        )

    ...
}
```

`<1>` We configure the `Select` control to use the `BOOK` pretty printer to format the books.


## display()

The most relevant feature of _Dueuno_ is the `display` method. It renders the GUI on the server and sends is to the browser.

You can call `display` with one or more of the following parameters:

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `controller` | `String` |  | The name of the controller to redirect to. If no `action` is specified the `index` action will be displayed |
| `action` | `String` |  | The name of the action to redirect to. If no `controller` is specified the current controller will be used |
| `params` | `Map<String, Object>` |  | The params to pass when redirecting to a `controller` or `action` |
| `content` | `PageContent` |  | The content to display. See [contents](#contents). |
| `transition` | `Transition` |  | The transition to display. See [transitions](#transitions). |
| `modal` | `Boolean` |  | Whether to display the content in a modal dialog or not |
| `wide` | `Boolean` |  | When displaying the content as `modal` the dialog will be wider. |
| `fullscreen` | `Boolean` |  | When displaying the content as `modal` the dialog will fit the whole browser window size. |
| `closeButton` | `Boolean` | `true` | When displaying the content as `modal` the dialog will present a close button on the top-left side to let the user close the dialog cancelling the operation. |
| `errors` | `org.springframework.validation.Errors` |  | Validation errors to display. See [validation](#validation). |
| `errorMessage` | `String` |  | Message to display in a message box to the user |
| `exception` | `Exception` |  | Exception to display in a message box to the user |
| `message` | `String` |  | Message to display in a message box to the user |
