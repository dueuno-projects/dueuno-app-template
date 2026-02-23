package template

import dueuno.tenants.TenantService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.quartz.JobExecutionContext

@Slf4j
class TplTestJob {

    TenantService tenantService

    static triggers = {
        cron name: 't1', cronExpression: '0 */5 * * * ?' // Every 5 minutes
    }

    @CompileStatic
    void execute(JobExecutionContext context) {
        Map params = context.mergedJobDataMap
        String tenantId = params.tenantId
        if (!tenantId) {
            return
        }

        tenantService.withTenant(tenantId) {
            // Transactional database operations here
        }

        context.result = '0'
    }
}
