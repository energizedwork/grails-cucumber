import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.resolver.IvyRepResolver

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.plugins.dir = 'plugins'
//grails.plugin.location."my-plugin" = "../my-plugin"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.release.scm.enabled = false
grails.project.repos.default = "grailsCentral"

IvyRepResolver ewResolver = new IvyRepResolver()
IvySettings settings = new IvySettings()
settings.defaultLatestStrategy = settings.getLatestStrategy('latest-time')
ewResolver.settings = settings
ewResolver.changingPattern = '^.+-SNAPSHOT$'
ewResolver.name = 'ew'
ewResolver.ivyroot = 'http://repo.energizedwork.com/'
ewResolver.ivypattern = '[organisation]/[module]-ivy-[revision].xml'
ewResolver.artroot = 'http://repo.energizedwork.com/'
ewResolver.artpattern = '[organisation]/[module]-[revision](-[classifier]).[ext]'
ewResolver.checkmodified = true
ewResolver.latest = 'latest-time'

grails.project.dependency.resolution = {

    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }

    log "warn" // Ivy resolver: 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        resolver ewResolver

        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
        mavenLocal()

        mavenRepo 'http://repo.energizedwork.com'
    }

    def cucumberVersion = "1.0.0.RC20"
    
    plugins {
        runtime ":hibernate:$grailsVersion"
        build ":tomcat:$grailsVersion"

        test (':spock:0.6-SNAPSHOT') {
            export = false
        }
    }

    dependencies {
        // scopes: 'build', 'compile', 'runtime', 'test' or 'provided'

        // cucumber
        compile ("info.cukes:cucumber-groovy:${cucumberVersion}") {
           excludes 'ant'   // avoid ant version conflict
        }

        // spock
        test ('org.objenesis:objenesis:1.2') {
            export = false
        }

        test ("com.energizedwork:test-commons:0.4-SNAPSHOT") {
            export = false
        }

    }
}
