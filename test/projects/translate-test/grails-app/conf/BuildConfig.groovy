grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6

grails.project.dependency.resolution = {
    inherits("global") {}
    log "error"
    checksums true

    def seleniumVersion = "2.22.0"
	def gebVersion = "0.7.0"

    repositories {
        inherits true
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenCentral()
    }

    dependencies {
        compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
            excludes 'groovy', 'httpclient', 'httpcore', 'xml-apis'
        }
        compile 'postgresql:postgresql:9.1-901-1.jdbc4'
        test "org.codehaus.geb:geb-spock:$gebVersion"
		test "org.seleniumhq.selenium:selenium-support:$seleniumVersion"
//		test "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"
//		test "org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion"
		test "org.seleniumhq.selenium:selenium-htmlunit-driver:$seleniumVersion"
    }

    plugins {
        compile ":remote-control:1.2"
        runtime ":hibernate:$grailsVersion"
        runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
        build ":tomcat:$grailsVersion"
        test ":spock:0.6"
		test ":geb:$gebVersion"
    }
}

grails.plugin.location.'translate' = '../../..'
