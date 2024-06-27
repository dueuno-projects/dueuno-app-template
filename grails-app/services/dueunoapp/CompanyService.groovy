package dueunoapp

import dueuno.elements.exceptions.ArgsException
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant

import javax.annotation.PostConstruct

@CurrentTenant
class CompanyService {

    @PostConstruct
    void init() {
        // Executes only once when the application starts
    }

    private DetachedCriteria<TCompany> buildQuery(Map filterParams) {
        def query = TCompany.where {}

        if (filterParams.containsKey('id')) query = query.where { id == filterParams.id }
        if (filterParams.containsKey('isOwned')) query = query.where { isOwned == filterParams.isOwned }
        if (filterParams.containsKey('isClient')) query = query.where { isClient == filterParams.isClient }

        if (filterParams.find) {
            String search = filterParams.find.replaceAll('\\*', '%')
            query = query.where { 1 == 1
                || name =~ "%${search}%"
            }
        }

        // Add additional filters here

        return query
    }

    TCompany get(Serializable id) {
        // Add any relationships here (Eg. references to other DomainObjects or hasMany)
        Map fetch = [
                relationshipName: 'join',
        ]

        return buildQuery(id: id).get(fetch: fetch)
    }

    List<TCompany> list(Map filterParams = [:], Map fetchParams = [:]) {
        if (!fetchParams.sort) fetchParams.sort = [dateCreated: 'asc']

        // Add single-sided relationships here (Eg. references to other DomainObjects)
        // DO NOT add hasMany relationships, you are going to have troubles with pagination
        fetchParams.fetch = [
                relationshipName: 'join',
        ]

        def query = buildQuery(filterParams)
        return query.list(fetchParams)
    }

    Integer count(Map filterParams = [:]) {
        def query = buildQuery(filterParams)
        return query.count()
    }

    TCompany create(Map args = [:]) {
        if (args.failOnError == null) args.failOnError = false

        TCompany obj = new TCompany(args)
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    TCompany update(Map args = [:]) {
        Serializable id = ArgsException.requireArgument(args, 'id')
        if (args.failOnError == null) args.failOnError = false

        TCompany obj = get(id)
        obj.properties = args
        obj.save(flush: true, failOnError: args.failOnError)
        return obj
    }

    void delete(Serializable id) {
        TCompany obj = get(id)
        obj.delete(flush: true, failOnError: true)
    }
}
