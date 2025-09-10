package dueunoapp

import dueuno.commons.utils.LogUtils
import dueuno.elements.components.*
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentTable
import dueuno.elements.controls.MoneyField
import dueuno.elements.controls.QuantityField
import dueuno.elements.controls.Select
import dueuno.elements.controls.TextField
import dueuno.elements.core.ElementsController
import dueuno.elements.style.TextDefault
import dueuno.elements.types.QuantityUnit
import grails.plugin.springsecurity.annotation.Secured
import groovy.util.logging.Slf4j

import jakarta.annotation.PostConstruct

@Slf4j
@Secured(['ROLE_USER', /* other ROLE_... */])
class OrderController implements ElementsController {

    CompanyService companyService
    ProductService productService
    OrderService orderService
    OrderItemService orderItemService

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    def handleException(Exception e) {
        // Display a popup message instead of the "Error" screen
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
                        cols: 12,
                )
            }
            sortable = [
                    dateCreated: 'desc',
            ]
            columns = [
                    'ref',
                    'subject',
                    'supplier',
                    'client',
                    'total',
            ]

            body.eachRow { TableRow row, Map values ->
                // Do not execute slow operations here to avoid slowing down the table rendering
            }
        }

        c.table.body = orderService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = orderService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(TOrder obj = null, Boolean readonly = false) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        if (obj) {
            c.header.addBackButton(action: 'index', icon: 'fa-times', text: '')
        }

        if (readonly) {
            c.header.removeNextButton()
            c.form.readonly = true
        }

        c.form.with {
            validate = TOrder
            addField(
                    class: Select,
                    id: 'supplier',
                    optionsFromRecordset: companyService.list(isOwned: true),
                    cols: 6,
            )
            addField(
                    class: Select,
                    id: 'client',
                    optionsFromRecordset: companyService.list(isClient: true),
                    cols: 6,
            )
            addField(
                    class: TextField,
                    id: 'ref',
                    cols: 3,
            )
            addField(
                    class: TextField,
                    id: 'subject',
                    cols: 9,
            )
        }

        if (obj) {
            def itemForm = c.addComponent(Form, 'itemForm')
            itemForm.with {
                validate = TOrderItem
                addKeyField('order', obj.id)
                addField(
                        class: Separator,
                        id: 'items',
                        icon: 'fa-cart-shopping',
                        squeeze: true,
                        cols: 12,
                )
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
                        cols: 2,
                )
                addField(
                        class: MoneyField,
                        id: 'unitPrice',
                        cols: 2,
                )
                addField(
                        class: Button,
                        id: 'btnAddItem',
                        action: 'onAddItem',
                        params: [id: obj.id],
                        submit: ['itemForm'],
                        icon: 'fa-plus',
                        cols: 2,
                )
            }

            def table = c.addComponent(Table)
            table.with {
                sortable = [
                        dateCreated: 'asc',
                ]
                columns = [
                        'product',
                        'unitPrice',
                        'quantity',
                        'price',
                ]

                actions.defaultAction.controller = 'orderItem'
                actions.defaultAction.params = [embeddedController: 'order', embeddedAction: 'edit', embeddedId: obj.id]
                actions.tailAction.controller = 'orderItem'
                actions.tailAction.params = [embeddedController: 'order', embeddedAction: 'edit', embeddedId: obj.id]

                body.eachRow { TableRow row, Map values ->
                }

                def filters = filters.values
                filters.order = obj.id
                body = orderItemService.list(filters)
                footer = [
                        [price: obj.total],
                ]
                paginate = orderItemService.count(filters)
            }

            c.form.values = obj
        }

        return c
    }

    def onAddItem() {
        def obj = orderItemService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'edit', params: [id: params.id], modal: true
    }

    def create() {
        def c = buildForm()
        display content: c, modal: true
    }

    def onCreate() {
        def obj = orderService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'edit', params: [id: obj.id], modal: true
    }

    def edit() {
        def obj = orderService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true, wide: true, closeButton: false
    }

    def onEdit() {
        def obj = orderService.update(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            orderService.delete(params.id)
            display action: 'index'

        } catch (e) {
            display exception: e
        }
    }
}
