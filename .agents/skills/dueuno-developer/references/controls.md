# Controls

Controls are Components that can hold a value. Controls are the main way to interact with the application. We mainly use controls in forms to easily submit their values.

## TextField

A text field.

```groovy
c.form.addField(
    class: TextField,
    id: 'username',
    icon: 'fa-user',
    textStyle: TextStyle.LINE_THROUGH,
)
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `icon` | `String` |  | An icon to display within the control, you can choose one from [Font Awesome](https://fontawesome.com/) |
| `prefix` | `String` |  | A text to display before the edit area of the control |
| `maxSize` | `Integer` |  | Max number of characters the user can input |
| `placeholder` | `String` |  | A text to display when the text area is empty |
| `textTransform` | `TextTransform` |  | Transforms the input while typing. |
| `textStyle` | `TextStyle` |  | Determines the text style. |
| `pattern` | `String` |  | A RegEx pattern to accept only specific input (Eg. `'^[0-9\\.\\,]*$'` will accept only numbers, dots and columns) |

`textTransform` values:

- `UPPERCASE`
- `LOWERCASE`
- `CAPITALIZE` each word

`textStyle` values:

- `NORMAL`
- `BOLD`
- `ITALIC`
- `MONOSPACE`
- `UNDERLINE`
- `LINE_THROUGH`

### Methods

| Method | Description |
| --- | --- |
| `addAction()` | Adds an action button at the end of the control. See [link](components.md#link). |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded |
| `change` | Triggered when the value changes |

## Select

Displays a list of options to choose from.

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `optionsFromRecordset` | `List<Map>` or `List<Object>` or `GORM Recordset` |  | Options will be set from the recordset |
| `optionsFromList` | `List` |  | Options will be set from the List items. The key of each item will match the value of the item itself. |
| `optionsFromEnum` | `Enum` |  | Options will be set from the Enum. The key of each item will match the value of the item itself. |
| `options` | `Map` |  | Options will be set from the Map items (key/value) |
| `keys` | `List<String>` | `['id']` | List of column names to submit as the key for the selected option |
| `prettyPrinter` | `Class` or `String` |  | Use the specified pretty printer to display the options. See `registerPrettyPrinter()`. If the registered pretty printer `Class` matches the item class, the pretty printer will be automatically applied. |
| `transformer` | `String` |  | Name of the transformer to use to display the options. See `registerTransformer()` |
| `textStyle` | `TextStyle` |  | Determines the text style. |
| `textPrefix` | `String` |  | Prefix to add to each item so it can be referred in `message.properties` files to localise it |
| `renderTextPrefix` | `Boolean` | `true` | Whether to display the `textPrefix` or not |
| `placeholder` | `String` |  | Displays a text when no option is selected |
| `allowClear` | `Boolean` |  | If `true` the selection can be cleared |
| `autoSelect` | `Boolean` | `true` | When there is only one available option in the list it will be automatically selected |
| `multiple` | `Boolean` | `false` | Enables multiple selections |
| `search` | `Boolean` |  | Displays a search box to filter the available options. It works on the client side, to search on the server we need to user the `search` event. |
| `searchMinInputLength` | `Integer` | `0` | Minimum number of characters to input before the search on the server can start. Works in combination with the `search` event. |

`textStyle` values:

- `NORMAL`
- `BOLD`
- `ITALIC`
- `MONOSPACE`
- `UNDERLINE`
- `LINE_THROUGH`

### Methods

| Method | Description |
| --- | --- |
| `Select.optionsFromRecordset(recordset: ...)` | Returns a `Map` of options to be used in a transition. See [search on server](#search-on-server). Accepts a `Map`, you can set the following arguments: `keys`, `keysSeparator`, `prettyPrinter`, `transformer`, `textPrefix`, `renderTextPrefix`, `locale`. |
| `Select.optionsFromList(list: ...)` | Returns a `Map` of options to be used in a transition. See [search on server](#search-on-server). Accepts a `Map`, you can set the above arguments. |
| `Select.optionsFromEnum(enum: ...)` | Returns a `Map` of options to be used in a transition. See [search on server](#search-on-server). Accepts a `Map`, you can set the above arguments. |
| `Select.options(options: ...)` | Returns a `Map` of options to be used in a transition. See [search on server](#search-on-server). Accepts a `Map`, you can set the above arguments. |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded |
| `change` | Triggered when the value changes |
| `search` | Triggered when `searchMinInputLength` is reached |

### Search on Server

Example of setting up a server search.

```groovy
c.form.with {
    addField(
        class: Select,
        id: 'activity',
        onLoad: 'onActivityLoad', // <1>
        onChange: 'onActivityChange',
        onSearch: 'onActivitySearch', // <2>
        searchMinInputLength: 0, // <3>
        submit: ['form'],
        allowClear: true,
    )
}
```

- `<1>` The `load` event must return a single option to display
- `<2>` The `search` event will return a list of matching options
- `<3>` If `0` then the `search` event will be triggered as soon as the user clicks on the control to open the options list.

We need to create the following actions.

```groovy
ActivityService activityService

def onActivityLoad() {
    def t = createTransition()
    def activities = activityService.list(id: params.activity) // <1>
    def options = Select.optionsFromRecordset(recordset: activities)
    t.set('activity', 'options', options)
    display transition: t
}

def onActivityChange() {
    def t = createTransition()
    // Do something...
    display transition: t
}

def onActivitySearch() {
    def t = createTransition()
    def activities = activityService.list(find: params.activity) // <2>
    def options = Select.optionsFromRecordset(recordset: activities)
    t.set('activity', 'options', options)
    display transition: t
}
```

- `<1>` `params.activity` will hold the selected id
- `<2>` `params.activity` will hold the search string

## Checkbox

A checkbox is a way to interact with `Boolean` values.

```groovy
c.form.with {
    addField(
        class: Checkbox,
        id: 'fullscreen',
        displayLabel: false,
        cols: 3,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `text` | `String` |  | The text to display |

### Events

| Event | Description |
| --- | --- |
| `click` | Not implemented yet |

## MultipleCheckbox

Manage multiple checkboxes as it was a Select control with many options. See [Select](#select).

## Textarea

A text area who can span multiple lines of a form.

```groovy
c.form.with {
    addField(
        class: Textarea,
        id: 'textarea',
        maxSize: 100,
        cols: 12,
        rows: 5,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `maxSize` | `Integer` |  | Max number of characters the user can input |

### Events

| Event | Description |
| --- | --- |
| `change` | Triggered when the value changes |

## QuantityField

A text field to input quantities.

```groovy
c.form.with {
    addField(
        class: QuantityField,
        id: 'quantity',
        defaultUnit: QuantityUnit.KM,
        availableUnits: quantityService.listAllUnits(),
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `decimals` | `Integer` | `2` | Allowed decimal digits |
| `negative` | `Boolean` | `false` | If negative values are allowed |
| `unitOptions` | `List` |  | A list of units to select from |
| `defaultUnit` | `QuantityUnit` |  | The default unit to display |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded |
| `change` | Triggered when the value changes |

## MoneyField

A text field to input currency values.

```groovy
c.form.with {
    addField(
        class: MoneyField,
        id: 'salary',
        decimals: 0,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `decimals` | `Integer` | `2` | Allowed decimal digits |
| `negative` | `Boolean` | `false` | If negative values are allowed |

### Events

| Event | Description |
| --- | --- |
| `load` | Triggered once the content is loaded |
| `change` | Triggered when the value changes |

## NumberField

A text field to manage number values.

```groovy
c.form.with {
    addField(
        class: NumberField,
        id: 'number',
        min: -2,
        max: 10,
    )
}
```

### Properties

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `decimals` | `Integer` | `2` | Allowed decimal digits |
| `negative` | `Boolean` | `false` | If negative values are allowed |
| `min` | `Integer` |  | Minimum number the user can input |
| `max` | `Integer` |  | Maximum number the user can input |
