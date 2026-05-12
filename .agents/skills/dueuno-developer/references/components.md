# Components

Everything in Dueuno is a `Component`. A component is itself a tiny web application. Each component is built with at least an HTML view, a CSS styling and a JavaScript logic. A Component can provide a supporting `Service` or `Controller`.

Unless we want to create a new component there is no need to know HTML, CSS or JavaScript to develop a _Dueuno_ application.

Each component extends the base class `Component` so each component share the following properties and methods.

## Properties

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `id` | `String` |  | Id of the component instance. This is mandatory, it must be unique and provided in the constructor. |
| `visible` | `Boolean` | `true` | Shows or hides the component without changing the layout |
| `display` | `Boolean` | `true` | Displays or hides the component, adding or removing it from the layout |
| `readonly` | `Boolean` | `false` | Readonly controls are disabled |
| `skipFocus` | `Boolean` | `false` | The component won't participate in keyboard or mouse selection |
| `sticky` | `Boolean` |  | The component is sticky on top |
| `containerSpecs` | `Map` |  | Contains instructions for the container. The container component may or may not respect them, see the documentation for the specific container component. |
| `textColor` | `String` |  | The text color, CSS format |
| `backgroundColor` | `String` |  | Background color, CSS format |
| `cssClass` | `String` |  | Custom CSS class to apply. The CSS class must be a [Bootstrap](https://getbootstrap.com/) CSS class or a custom one declared into the `grails-app/assets/dueuno/custom/application.css` file. See [custom-css](#custom-css). |

## Methods

| Method | Description |
| --- | --- |
| `addComponent(Map)` | Adds a component as children. See [components](#components). |
| `addControl(Map)` | Adds a control as children. See [controls](#controls). |
| `on(Map)` | Configures an event. See [events](#events). |

## Header

A `Header` is a bar at the top of the `Content` area. It can be sticky on top or it can scroll with the content. Its main purpose is to hold navigation buttons.

A `Header` can have a `backButton` on the left and a `nextButton` on the right. In the middle we can find the `title`.

### Properties

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `sticky` | `Boolean` |  | When set to `true` the header will stick on top. When a `backButton` or `nextButton` is added to the header than `sticky` is automatically set to `true` to let the user reach the buttons even if the content has been scrolled down. To force the header to scroll with the content explicitly set sticky to `false`. |
| `title` | `String` |  | The title to display |
| `titleArgs` | `List` |  | Args to be used when indexing an i18n message. Eg: in `messages.properties` exists the following property `book.index.header.title=Books for {0} {1}` and `titleArgs = ['Mario', 'Rossi']`. The `title` will result in `Books for Mario Rossi`. |
| `icon` | `String` |  | An icon to be displayed before the `title`. We can choose one from [Font Awesome](https://fontawesome.com/) |
| `hasBackButton` | `Boolean` |  | `true` if a `backButton` has been added |
| `hasNextButton` | `Boolean` |  | `true` if a `nextButton` has been added |
| `backButton` | `Button` |  | The back button object. See [button](#button) |
| `nextButton` | `Button` |  | The next button object. See [button](#button) |

### Methods

| Method | Description |
| --- | --- |
| `addBackButton(Map)` | Add the backButton. Accepts the arguments of [button](#button) |
| `removeBackButton()` | Removes the backButton. |
| `addNextButton(Map)` | Add the nextButton. Accepts the arguments of [button](#button) |
| `removeNextButton()` | Removes the nextButton. |

## Table

A `Table` is a convenient way to display a recordset.

Each table can implement some [table filters](#tablefilters) and each row can have its own set of action buttons. For each row, depending on the logged in user and the status of the record we can define which actions are available.

### Properties

#### `columns`

- Type: `List<String>`
- Description: A list of column names to display. Each column name must match the recordset column name to automatically display its values.

```groovy
c.table.with {
    columns = [
        'title',
        'author',
    ]
}
```

#### `keys`

- Type: `List<String>`
- Description: List of key names. When specified, a new column will be created for each key. The keys will be automatically submitted when a row action is activated.

```groovy
c.table.with {
    keys = [
        'publisher_id',
    ]
}
```

#### `sortable`

- Type: `Map<String, String>`
- Description: Defines the sortable columns.

```groovy
c.table.with {
    sortable = [
        title: 'asc',
    ]
}
```

#### `sort`

- Type: `Map<String, String>`
- Description: Defines the sorting of the recordset. It takes precedence over the `sortable` property and forces the specified sorting.

```groovy
c.table.with {
    sort = [
        title: 'asc',
    ]
}
```

#### `submit`

- Type: `List<String>`
- Description: The name of the column names whose values must be included when the table is submitted by a [button](#button) or [link](#link).

```groovy
c.table.with {
    submit = [
        'author',
    ]
}
```

#### `labels`

- Type: `Map<String, String>`
- Description: Programmatically change the label of the specified columns.

```groovy
c.table.with {
    labels = [
        author: '-',
    ]
}
```

#### `transformers`

- Type: `Map<String, String>`
- Description: Sets a transformer to a column. Each value of that column will be processed by the specified transformer. See `registerTransformer()`.

```groovy
c.table.with {
    transformers = [
        title: 'UPPERCASE_TITLE',
    ]
}
```

#### `prettyPrinters`

- Type: `Map<String, Object>`
- Description: Sets a pretty printer to a column. Each value of that column will be processed by the specified pretty printer. See `registerPrettyPrinter()`.

```groovy
c.table.with {
    prettyPrinter = [
        title: '${it.code}',
    ]
}
```

#### `prettyPrinterProperties`

- Type: `Map<String, Map>`
- Description: Sets some pretty printer properties to a column. Each value of that column will be processed by the specified properties. See [pretty-printer-properties](#pretty-printer-properties).

```groovy
c.table.with {
    prettyPrinterProperties = [
        salary: [
            highlightNegative: false,
            renderZero: '-',
        ],
        name: [
            renderTextPrefix: true,
        ],
    ]
}
```

#### `stickyHeader`

- Type: `Boolean`
- Default: `true`
- Description: If `true` the table header will stick to top when scrolling. Not available in modals.

#### `filters`

- Type: `TableFilters`
- Description: To define table filters:

```groovy
c.table.with {
    filters.with {
        addField(
            class: TextField,
            id: 'title',
            cols: 6,
        )
        addField(
            class: TextField,
            id: 'author',
            cols: 6,
        )
    }
}

Map filters = c.table.filters.values // <1>
```

`<1>` The submitted values of the filters fields. See [table filters](#tablefilters).

#### Other Properties

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `rowActions` | `Boolean` | `true` | Whether to display the row action buttons or not |
| `hasHeader` | `Boolean` | `true` | Whether to display the table header or not |
| `hasFooter` | `Boolean` | `true` | Whether to display the table footer or not |
| `hasPagination` | `Boolean` | `true` | Whether to display the table pagination or not |
| `hasComponents` | `Boolean` | `false` | Whether to render the table to host custom components on its cells or not. Enabling this feature slows down the rendering. |
| `rowHighlight` | `Boolean` | `true` | Whether to highlight the rows on mouse pointer hover |
| `rowStriped` | `Boolean` | `false` | Whether to set the zebra style or not |
| `noResults` | `Boolean` | `true` | Whether to display a box with an icon and a text when the table has no results |
| `noResultsIcon` | `String` |  | The icon ti display when the table has no results. Choose one from [Font Awesome](https://fontawesome.com/). |
| `noResultsMessage` | `String` |  | The message to display when the table has no results |

### Methods

#### `body`

Assigns a recordset to the table body. See [recordsets](#recordsets).

```groovy
c.table.body = bookService.list()
```

#### `footer`

Assigns a recordset to the table footer. See [recordsets](#recordsets).

```groovy
c.table.footer = bookService.listTotals()
```

#### `paginate`

If set the table will paginate the results. Must be set to the total count of the records to show.

```groovy
c.table.paginate = bookService.count()
```

#### `eachRow`

This closure gets called for each row displayed by the table. Don't execute slow code here since it will slow down the whole table rendering.

```groovy
c.table.body.eachRow { TableRow row, Map values -> // <1>
    row.cells['title'] // <2>
    row.actions // <3>
}
```

- `<1>` The record values
- `<2>` See [label](#label)
- `<3>` See [table row actions](#row-actions)

### Recordsets

What can we load a table with?

#### List of Lists

Loading a table with a List of Lists is possible, the sequence will determine how each column will be mapped to each value. There is no hard relationship between the displayed column name and the value.

For this reason we suggest using [list of maps](#list-of-maps) instead.

```groovy
c.table.columns = [
    'title',
    'author',
    'description',
]

c.table.body = [
    ['The Teachings of Don Juan', 'Carlos Castaneda', 'This is a nice fictional book'],
    ['The Antipodes of the Mind', 'Benny Shanon', 'This is a nice scientific book'],
]
```

#### List of Maps

We can load a table with a "recordset" style data structure like the List of Maps. This way each column will display exactly the value associated to the key of the record (`Map`) having the same name of the column.

```groovy
c.table.columns = [
    'title',
    'author',
    'id',
]

c.table.body = [
    [id: '1', title: 'The Teachings of Don Juan', author: 'Carlos Castaneda', description: 'This is a nice fictional book'],
    [id: '2', title: 'The Antipodes of the Mind', author: 'Benny Shanon', description: 'This is a nice scientific book'],
]
```

#### List of POGOs

A List of _Plain Old Groovy Objects_ can also be used to load a table.

Given this POGO:

```groovy
class Book {
    String id
    String title
    Strng author
    String description
}
```

We can load our table:

```groovy
c.table.columns = [
    'title',
    'author',
    'id',
]

c.table.body = [
    new Book(id: '1', title: 'The Teachings of Don Juan', author: 'Carlos Castaneda', description: 'This is a nice fictional book'),
    new Book(id: '2', title: 'The Antipodes of the Mind', author: 'Benny Shanon', description: 'This is a nice scientific book'),
]
```

#### GORM Recordsets

Using a [GORM Recordset](https://gorm.grails.org/latest/hibernate/manual/) is an easy way to load a table. See how to build a [CRUD](../user-guide/4-building-applications.adoc#building-crud).

```groovy
c.table.columns = [
    'title',
    'author',
]

c.table.body = TBook.list()
c.table.paginate = TBook.count()
```

### Row Actions

There are two ways to configure row actions. All at once and on a row basis. To set all rows to have the same actions we can set them up in the table namespace as follows:

```groovy
c.table.with {
    columns = [
        'title',
        'author',
    ]
    actions.addAction(action: 'borrow') // <1>
    actions.addAction(action: 'return')
}
```

`<1>` See [button](#button) for all the `Button` properties.

If we need to configure the row actions depending on the record values or other logics we can do it from the `eachRow` closure.

```groovy
c.table.with {
    columns = [
        'title',
        'author',
    ]

    body.eachRow {
        if (values.borrowed) {
            row.actions.addAction(action: 'return') // <1>
        } else {
            row.actions.addAction(action: 'borrow')
        }
    }
}
```

`<1>` See [button](#button) for all the `Button` properties.

### Group Actions

The table can be configured to select multiple rows ad apply to all of them the same action.

```groovy
c.table.with {
    columns = [
        'title',
        'author',
    ]

    groupActions.addAction(action: 'return') // <1>
    groupActions.addAction(action: 'borrow')
}
```

`<1>` See [button](#button) for all the `Button` properties.

## TableFilters

Each table can have its own search `Form` to filter results. When submitting the filters, the action containing them will be reloaded and the filters values will be available in the Grails `params` map.

```groovy
c.table.with {
    filters.with {
        addField(
            class: Select,
            optionsFromRecordset: bookService.list(),
            prettyPrinter: 'BOOK',
            id: 'book',
            cols: 4,
        )
        addField(
            class: TextField,
            id: 'search',
            cols: 8,
        )
    }

    Map filters = c.table.filters.values // <1>
}
```

`<1>` The submitted values of the filters fields.

### Properties

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `isFiltering` | `Boolean` |  | `true` if the filters form has values in its fields |
| `fold` | `Boolean` | `true` | Whether the filters form is displayed as folded or not at its first appearance. After that its folded state will be stored in the session. |
| `autoFold` | `Boolean` | `false` | If set to `true` the filters form will be folded each time a search is submitted |

### Methods

| Method | Description |
| --- | --- |
| `addField()` | Adds a form field. See [form field](#formfield) and [controls](#controls) |

## Form

A form is the component we use to layout [components](#components) and [controls](#controls). `Form` implements the grid system, once activated we have 12 columns we can use to arrange form fields horizontally.

When the application is accessed from a mobile phone all the fields will be displayed in a single column. This makes them usable when the available space is not enough to organise them in a meaningful way.

```groovy
c.form.with {
    grid = true
    addField(
        class: TextField,
        id: 'title',
        cols: 6,
    )
    addField(
        class: TextField,
        id: 'author',
        cols: 6,
    )
}
```

### Properties

| Property | Type | Default | Description |
| --- | --- | --- | --- |
| `validate` | `Class` |  | A `grails.validation.Validateable` class or a GORM domain class used to automatically render the field as required. A red `*` will be displayed next to the field label if appropriate. |
| `grid` | `Boolean` | `false` | Whether to activate the grid system or not |
| `readonly` | `Boolean` | `false` | Sets all the form fields readonly |

### Methods

| Method | Description |
| --- | --- |
| `addField()` | Adds a form field. See [form field](#formfield) and [controls](#controls) |

## FormField

A form field wraps a `Control` with a label and sets it into the grid system. A `FormField` is automatically created each time we add a field to a `Form` calling its `addField()` method.

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `component` | `Component` |  | The contained component |
| `label` | `String` |  | The field label |
| `labelArgs` | `List` |  | A list of objects to pass to the localized message (Eg. when using `{0}` in `message.properties`) |
| `help` | `String` |  | A help message |
| `helpArgs` | `List` |  | A list of objects to pass to the localized message (Eg. when using `{0}` in `message.properties`) |
| `nullable` | `Boolean` | `true` | Whether to display the field as nullable or not. If set will override the form `validate` logic. See [form](#form). |
| `displayLabel` | `Boolean` |  | If set to `false` the label will not be displayed. The space occupied by the label will be taken off the screen resulting in a different vertical positioning of the `Control`. |
| `cols` | `Integer` |  | Defines how many columns of the grid system will be used to span the `Control` to. Its value must be between `1` and `12` included. |
| `rows` | `Integer` |  | If the `Control` is a `multiline` one we can set how many lines it is going to occupy |

## Button

Buttons are key components of the GUI. We use buttons to let the user trigger actions. The `Button` component can provide the user with multiple actions to be executed.

A single button can display two directly accessible actions, the `defaultAction` and `tailAction` and a menu with a list of links, the `actionMenu`.

| `defaultAction` | `tailAction` | `actionMenu` |
| --- | --- | --- |
|  |  |  |

A simple button will have just the `defaultAction`.

```groovy
c.form.with {
    def addBookField = addField( // <1>
        class: Button,
        id: 'addBook',
        action: 'addBook',
        submit: ['form'],
    )

    def button = addBookField.component
    button.addAction(controller: 'addAuthor')
}
```

`<1>` A `Button` can be initialized with the properties of an event. See [events](#events) and [link](#link).

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `defaultAction` | `Menu` |  | The default action |
| `tailAction` | `Menu` |  | The tail action |
| `actionMenu` | `Menu` |  | The action menu |
| `primary` | `Boolean` | `false` | When set to `true` the button color will use the `PRIMARY_BACKGROUND_COLOR` and `PRIMARY_TEXT_COLOR` tenant properties indicating that its role in the GUI is primary. See [tenant-properties](#tenant-properties). |
| `stretch` | `Boolean` | `false` | Set to `true` to let the button fill all the available horizontal space |
| `group` | `Boolean` | `false` | If set to `true` all actions of the button will be displayed inline and directly accessible |
| `maxWidth` | `Integer` |  | The max width in pixels that the button can reach |

### Events

| Event | Description |
| --- | --- |
| `click` | The event is triggered on mouse click or finger tab on touch devices |

## Menu

A menu is the component we use to organize the `Shell` and `Button` menus. It can hold a tree of items with a parent-children structure but we use only one level to group items. See [features](#features).

This component is meant for internal use only.

## Link

Links are everywhere, they are in the `Shell` menus, in `Buttons` actions, `TextField` or `Select` actions, and they can be used as stand alone. Links and buttons share the same properties.

```groovy
c.form.with {
    addField( // <1>
        class: Link,
        id: 'addBook',
        action: 'addBook',
        submit: ['form'],
        icon: 'fa-book',
    )
}
```

`<1>` A `Link` can be initialized with the properties of a [label](#label) and an event. See [events](#events).

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `icon` | `String` |  | Icon that graphically represents the link. Choose one from [Font Awesome](https://fontawesome.com/). |
| `image` | `String` |  | An SVG image that graphically represents the link. If specified a corresponding file must exist in the `grails-app/assets` folder. |
| `text` | `String` |  | A label that describes the link, usually a code found in `messages.properties` |
| `url` | `String` |  | Point to a specific URL |
| `direct` | `Boolean` |  | Whether to render the whole html page (or raw http body) or a Transition |
| `target` | `String` |  | Set a target name to open the page into a new browser tab. All links with te same target will display in the same tab. |
| `targetNew` | `Boolean` |  | If set to `true` the link will display on a new tab each time it is clicked |
| `modal` | `Boolean` |  | Whether to display the content in a modal dialog or not |
| `wide` | `Boolean` |  | When displaying the content as `modal` the dialog will be wider. |
| `fullscreen` | `Boolean` |  | When displaying the content as `modal` the dialog will fit the whole browser window size. |
| `closeButton` | `Boolean` | `true` | When displaying the content as `modal` the dialog will present a close button on the top-left side to let the user close the dialog cancelling the operation. |
| `updateUrl` | `Boolean` | `false` | If set to `true` the browser address bar will be updated with the link destination URL, otherwise the browser will not update its address bar. NOTE: Accessing from a mobile phone the address bar will never be updated to enhance the user experience. |
| `animate` | `String` |  | Can be set to `fade`, `next` and `back`. At the moment only `fade` is implemented as a graphical transition when changing content. |
| `infoMessage` | `String` |  | If specified an info message will pop up, the link will never be executed |
| `confirmMessage` | `String` |  | If specified a confirmation message will pop up giving the user a chance to cancel the action |

### Events

| Event | Description |
| --- | --- |
| `click` | The event is triggered on mouse click or finger tap on touch devices |

## Label

A `Label` is a canvas for text and custom HTML.

```groovy
c.form.with {
    addField(
        class: Label,
        id: 'label',
        html: '<b>This is a bold statement!</b>',
        textAlign: TextAlign.END,
        textWrap: TextWrap.LINE_WRAP,
        textStyle: TextStyle.BOLD,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `text` | `Object` |  | The text to display. If it's a `Boolean` value a check will be displayed. |
| `html` | `String` |  | An html string, useful to format text or insert links |
| `url` | `String` |  | If specified the `text` will be a link to this URL |
| `icon` | `String` |  | An icon to display before the text, you can choose one from [Font Awesome](https://fontawesome.com/) |
| `textAlign` | `TextAlign` |  | Determines the text horizontal alignment. It can be set to `DEFAULT`, `START`, `END` or `CENTER` (Default: `DEFAULT`). |
| `textWrap` | `TextWrap` |  | Determines how the text is wrapped. |
| `textStyle` | `TextStyle` |  | Determines the text style. |
| `border` | `Boolean` |  | Draws a coloured background. Useful when we want to display the label in a different color. |
| `renderBoolean` | `Boolean` | `true` | If `true` a check symbol will be displayed, otherwise the text `true` or `false` will be displayed. |

`textWrap` values:

- `NO_WRAP` The text will be displayed in one line
- `SOFT_WRAP` The text will wrap when the max width of the container is reached. Lines breaks are NOT considered.
- `LINE_WRAP` Each line will be displayed in one line until the max width of the container is reached. Line breaks are taken in consideration.
- `LINE_BREAK` Each line will be displayed in one line. Line breaks are taken in consideration.

`textStyle` values:

- `NORMAL`
- `BOLD`
- `ITALIC`
- `MONOSPACE`
- `UNDERLINE`
- `LINE_THROUGH`

## Separator

Wa can use separators to space between a set of fields and another one in a form.

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `squeeze` | `Boolean` |  | Reduces the space the separator will introduce leaving just the space for the label |

## KeyPress

We use the `KeyPress` component to intercept key pressed by the user on the GUI. Its main use is to integrate barcode readers but it can be used for any other scenario.

```groovy
def c = createContent(ContentTable)
c.addComponent(
    class: KeyPress,
    id: 'keyPress',
    action: 'onKeyPress', // <1>
)
```

`<1>` See [events](#events) to configure the event.

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `triggerKey` | `String` | `'Enter'` | Key pressed are stored into a buffer until a trigger key is pressed. When this happens the configured event is called. The trigger key can be any character or `Enter`. If set to blank `''` each key pressed will be immediately sent. |
