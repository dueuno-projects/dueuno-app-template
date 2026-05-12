# Features

A _Dueuno_ application is a container for a finite set of features that you want to expose to the users. Features are defined in the `init` closure. The main menu on the right side of the GUI lists all the features accessible by a user depending on its privileges.

Once defined, features are than implemented in [controllers](#controllers).

## registerFeature()

Registers a Feature.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onInit {
            registerFeature(
                    controller: 'book', // <1>
                    action: 'index', // <2>
                    icon: 'fa-book', // <3>
                    authorities: ['ROLE_CAN_EDIT_BOOKS'] // <4>
            )
            registerFeature(
                    controller: 'read',
                    icon: 'fa-glasses',
            )

            registerFeature(
                    controller: 'configuration', // <5>
            )
            registerFeature(
                    parent: 'configuration', // <6>
                    controller: 'authors',
                    icon: 'fa-user',
            )
            registerFeature(
                    parent: 'configuration',
                    controller: 'publishers',
                    icon: 'fa-user-shield',
            )
        }

    }
}
```

- `<1>` Name of the controller that implements the feature
- `<2>` Name of the action to execute when the feature is clicked (default: `index`)
- `<3>` Menu item icon, you can choose one from [Font Awesome](https://fontawesome.com/)
- `<4>` The feature will be displayed only to the users configured with the roles in the list (default: `ROLE_USER`)
- `<5>` A feature with just a controller can be created to group features. This will become the parent feature.
- `<6>` Tells the feature which one is its parent

> **IMPORTANT:** The controller class must be annotated with `@Secured(['ROLE_CAN_EDIT_BOOKS'])` to actually block all users without that authority from accessing the feature. See: [controllers](#controllers)

Available options:

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `controller` | `String` | `controllerName` | The name of the controller that implements the feature. If not specified it is automatically set to the current controller name. |
| `action` | `String` | `'index'` | _(OPTIONAL)_ The name of the action to execute |
| `params` | `Map<String, Object>` |  | _(OPTIONAL)_ Parameters to add when calling the `action` or `url` |
| `submit` | `List<String>` |  | _(OPTIONAL)_ List of the component names that will be processed to retrieve the values to be passed when calling the `action` or `url` |
| `icon` | `String` |  | _(OPTIONAL)_ Menu item icon, you can choose one from [Font Awesome](https://fontawesome.com/) |
| `authorities` | `List<String>` | `['ROLE_USER']` | _(OPTIONAL)_ The feature will be displayed only to the users configured with the roles in the list |
| `favourite` | `Boolean` |  | _(OPTIONAL)_ If `true` the feature will be displayed on the bookmark page as well (accessible clicking the home menu) |
| `url` | `String` |  | _(OPTIONAL)_ An absolute URL. When specified it takes precedence so `controller` and `action` won't be taken into account |
| `direct` | `Boolean` |  | _(OPTIONAL)_ Menu items are URLs managed by _Dueuno_. When set to `true` the URL gets managed directly by the browser without any processing |
| `target` | `String` |  | _(OPTIONAL)_ The feature will be displayed in a new browser tab with the provided name |
| `targetNew` | `String` |  | _(OPTIONAL)_ The feature will be displayed in a new browser tab (`_blank`) |
| `confirmMessage` | `String` |  | _(OPTIONAL)_ Message to display before the feature is displayed giving the option to cancel or confirm the operation |
| `infoMessage` | `String` |  | _(OPTIONAL)_ If set, the message will be displayed instead of the feature |

## registerUserFeature()

Registers a Feature in the _User Menu_. For the available options see: [registerFeature()](#registerfeature)

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onInit {
            registerUserFeature(
                    controller: 'manual',
                    icon: 'fa-book',
                    targetNew: true,
            )
        }

    }
}
```

## registerPrettyPrinter()

Registers a string template to render an instance of a specific _Class_. A pretty printer can be registered with just a name, in this case it must be explicitly assigned to a Control when defining it.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onInit {
            registerPrettyPrinter(TProject, '${it.name}') //<1>
            registerPrettyPrinter('PROJECT_ID', '${it.padLeft(4, "0")}') // <2>
        }

    }
}
```

- `<1>` Registers a pretty printer for the `TProject` domain class. The `it` variable will refer to an instance of a `TProject` in this case we will display the `name` property
- `<2>` Registers a pretty printer called `PROJECT_ID`. Since we know that the project id is going to be a `String` we can call the `padLeft()` method on it

### PrettyPrinterProperties

Every value in _Dueuno_ gets displayed by the `PrettyPrinter` subsystem. [Components](components.md) and [controls](controls.md) can be configured to override the user settings and the system settings. Refer to the documentation of each component to see how those settings can be configured.

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| `prettyPrinter` | `Object` |  | `Class` or `String` name of the pretty printer |
| `transformer` | `String` |  | Transformer name |
| `locale` | `Locale` |  | - |
| `renderTextPrefix` | `Boolean` |  | Default: `false`, set to `true` to translate the value into `message.properties` files |
| `textPrefix` | `String` |  | Add or change the message prefix |
| `textArgs` | `List` |  | Add args for the i18n message |
| `renderBoolean` | `Boolean` | `true` | If `false` renders the text `true/false` otherwise renders a check symbol when `true` and nothing when `false` |
| `highlightNegative` | `Boolean` | `false` | If the value is `< 0` the text will be highlighted in red |
| `renderZero` | `String` |  | If the value is 0 render the specified string instead |
| `renderDate` | `Boolean` |  | For `LocalDateTime` values, whether to render the DATE part or not |
| `renderDatePattern` | `String` |  | Change the way the date is rendered. See [DateTimeFormatter](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html). |
| `renderTime` | `Boolean` |  | For `LocalDateTime` values, whether to render the TIME part or not |
| `renderSeconds` | `Boolean` |  | For `LocalTime` values, whether to display the seconds or not |
| `renderDelimiter` | `String` | `', '` | For `Map` and `List` values, use this delimiter to list the items |
| `decimals` | `Integer` |  | For `Number` values, how many decimals digits to display |
| `decimalFormat` | `String` | `ISO_COM` | For `Number` values, which decimal separator to use. It can be `ISO_COM` (,) or `ISO_DOT` (.). |
| `prefixedUnit` | `Boolean` | `false` | For `Quantity` and `Money` values, whether to display the unit of measure before or after the value |
| `symbolicCurrency` | `Boolean` | `true` | For `Money` values, whether to display the currency with a symbolic or ISO code |
| `symbolicQuantity` | `Boolean` | `true` | For `Quantity` values, whether to display the unit of measure with a symbolic or SI code |
| `invertedMonth` | `Boolean` | `false` | For `Date` values, whether to display month/day/year (`true`) or day/month/year (`false`) |
| `twelveHours` | `Boolean` | `false` | For `Time` values, whether to display 12H (`true`, uses AM/PM) or 24H (`false`) |
| `firstDaySunday` | `Boolean` | `false` | Whether to display Sunday as the first day of the week (`true`) or not |


## registerTransformer()

Registers a callback used to render an instance of a specific _Class_. To make it work it must be explicitly assigned to a Control when defining it.

> **NOTE:** The closure will receive the value that is being transformed and must return a _String_.

> **IMPORTANT:** Be careful when using transformers since it may impact performances when the closure takes long time to execute.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService
    SecurityService securityService

    def init = {

        applicationService.onInit {
            registerTransformer('USER_FULLNAME') { Object value ->
                return securityService.getUserByUsername(value).fullname
            }
        }

    }
}
```

## registerCredits()

Registers a role along with the people who took that role during the development of the project. When a credit reference is registered a new menu item will appear in the _User Menu_.

`grails-app/init/BootStrap.groovy`

```groovy
class BootStrap {

    ServletContext servletContext
    ApplicationService applicationService

    def init = {

        applicationService.onInit {
            registerCredits('Application Development', 'Francesco Piceghello', 'Gianluca Sartori')
        }

    }
}
```
