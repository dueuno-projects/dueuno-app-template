package dueunoapp

import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.MoneyField
import dueuno.elements.controls.QuantityField
import dueuno.elements.controls.Select
import dueuno.elements.controls.TextField
import dueuno.elements.core.ElementsController
import dueuno.elements.types.QuantityUnit
import grails.plugin.springsecurity.annotation.Secured

import javax.annotation.PostConstruct

@Secured(['ROLE_USER', /* other ROLE_... */])
class OrderItemController implements ElementsController {

    ProductService productService
    OrderItemService orderItemService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

//    def handleException(Exception e) {
//        // Display a message instead of the "Error" screen
//        display exception: e
//    }

    def index() {
        def c = createContent(ContentList)
        c.table.with {
            filters.with {
                addField(
                        class: TextField,
                        id: 'find',
                        label: 'default.filters.text',
                        cols: 12,
                )
            }
            sortable = [
                    dateCreated: 'desc',
            ]
            columns = [
                    'supplier',
                    'client',
                    'ref',
                    'subject',
                    'total',
            ]

            body.eachRow { TableRow row, Map values ->
                // Do not execute slow operations here to avoid slowing down the table rendering
            }
        }

        c.table.body = orderItemService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = orderItemService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(TOrderItem obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        if (params.embeddedController) {
            c.header.addBackButton(
                    controller: params.embeddedController ?: controllerName,
                    action: params.embeddedAction ?: 'index',
                    params: [id: params.embeddedId],
            )
        }

        c.form.with {
            validate = TOrderItem
            grid = true
            addKeyField('embeddedController', 'TEXT')
            addKeyField('embeddedAction', 'TEXT')
            addKeyField('embeddedId')
            addField(
                    class: Select,
                    id: 'product',
                    optionsFromRecordset: productService.list(),
                    cols: 6,
            )
            addField(
                    class: QuantityField,
                    id: 'quantity',
                    defaultUnit: QuantityUnit.PCS,
                    cols: 3,
            )
            addField(
                    class: MoneyField,
                    id: 'unitPrice',
                    cols: 3,
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
        def obj = orderItemService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        if (params.embeddedController) {
            display controller: params.embeddedController, action: params.embeddedAction, params: [id: params.embeddedId], modal: true
        } else {
            display action: 'index'
        }
    }

    def edit() {
        def obj = orderItemService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onEdit() {
        def obj = orderItemService.update(params)

        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        if (params.embeddedController) {
            display controller: params.embeddedController, action: params.embeddedAction, params: [id: params.embeddedId], modal: true
        } else {
            display action: 'index'
        }
    }

    def onDelete() {
        try {
            orderItemService.delete(params.id)

            if (params.embeddedController) {
                display controller: params.embeddedController, action: params.embeddedAction, params: [id: params.embeddedId], modal: true
            } else {
                display action: 'index'
            }

        } catch (e) {
            display exception: e
        }
    }
}
