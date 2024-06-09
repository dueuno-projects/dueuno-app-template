package dueunoapp

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean

@CompileStatic
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }

    // ENABLES HTTPS IF CONFIGURED IN APPLICATION.YML
    // See: https://o7planning.org/11867/configure-spring-boot-to-redirect-http-to-https
    @Bean
    ServletWebServerFactory servletContainer() {
        Integer sslPort = Holders.config.getProperty('server.port') as Integer
        Integer httpPort = Holders.config.getProperty('server.port-http') as Integer
        String sslKeyStore = Holders.config.getProperty('server.ssl.key-store') as String

        Boolean isSSLEnabled = sslKeyStore != null && !sslKeyStore.isEmpty()
        if (!isSSLEnabled) {
            return new TomcatServletWebServerFactory()
        }

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol")
        connector.port = httpPort
        connector.redirectPort = sslPort

        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint()
                securityConstraint.setUserConstraint("CONFIDENTIAL")
                SecurityCollection collection = new SecurityCollection()
                collection.addPattern("/*")
                securityConstraint.addCollection(collection)
                context.addConstraint(securityConstraint)
            }
        }

        tomcat.addAdditionalTomcatConnectors(connector)
        return tomcat
    }
}