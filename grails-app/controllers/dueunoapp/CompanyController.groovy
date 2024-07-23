package dueunoapp

import dueuno.elements.components.TableRow
import dueuno.elements.contents.ContentCreate
import dueuno.elements.contents.ContentEdit
import dueuno.elements.contents.ContentList
import dueuno.elements.controls.Checkbox
import dueuno.elements.controls.TextField
import dueuno.elements.core.ElementsController
import dueuno.elements.style.TextDefault
import grails.plugin.springsecurity.annotation.Secured

import javax.annotation.PostConstruct

@Secured(['ROLE_USER', /* other ROLE_... */])
class CompanyController implements ElementsController {

    CompanyService companyService

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
                        label: TextDefault.FIND,
                        cols: 12,
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
                // Do not execute slow operations here to avoid slowing down the table rendering
            }
        }

        c.table.body = companyService.list(c.table.filterParams, c.table.fetchParams)
        c.table.paginate = companyService.count(c.table.filterParams)

        display content: c
    }

    private buildForm(TCompany obj = null) {
        def c = obj
                ? createContent(ContentEdit)
                : createContent(ContentCreate)

        c.form.with {
            validate = TCompany
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

        } catch (e) {
            display exception: e
        }
    }
}
