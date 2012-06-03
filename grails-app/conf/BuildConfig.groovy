grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6

grails.project.dependency.resolution = {
    inherits("global") {}

    log "warn"

    repositories {
        grailsCentral()
        grailsHome()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
            excludes 'groovy', 'httpclient', 'httpcore', 'xml-apis'
        }
        compile 'postgresql:postgresql:9.1-901-1.jdbc4'

        test 'org.gmock:gmock:0.8.2'
        test 'org.objenesis:objenesis:1.2'
        test('org.spockframework:spock-core:0.6-groovy-1.8') {
            excludes 'groovy-all'
        }
    }

    plugins {
        build(":tomcat:$grailsVersion", ":release:1.0.0") {
            export = false
        }
        test(":spock:0.6")
    }
}
