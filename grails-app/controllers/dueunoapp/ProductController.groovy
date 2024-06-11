package dueunoapp

import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.TextField
import dueuno.elements.core.ElementsController
import dueuno.elements.style.TextDefault
import grails.plugin.springsecurity.annotation.Secured

import javax.annotation.PostConstruct

@Secured(['ROLE_USER', /* other ROLE_... */])
class ProductController implements ElementsController {

    ProductService productService

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
                fold = false
                addField(
                        class: TextField,
                        id: 'find',
                        label: TextDefault.FIND,
                        cols: 12,
                )
            }
            sortable = [
                    name: 'asc',
            ]
            columns = [
                    'ref',
                    'name',
            ]

            body.eachRow { TableRow row, Map values ->
                // Do not execute slow operations here to avoid slowing down the table rendering
            }
        }

        c.table.body = productService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = productService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(TProduct obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        c.form.with {
            validate = TProduct
            addField(
                    class: TextField,
                    id: 'ref',
            )
            addField(
                    class: TextField,
                    id: 'name',
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
        def obj = productService.create(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def edit() {
        def obj = productService.get(params.id)
        def c = buildForm(obj)
        display content: c, modal: true
    }

    def onEdit() {
        def obj = productService.update(params)
        if (obj.hasErrors()) {
            display errors: obj
            return
        }

        display action: 'index'
    }

    def onDelete() {
        try {
            productService.delete(params.id)
            display action: 'index'

        } catch (e) {
            display exception: e
        }
    }
}
