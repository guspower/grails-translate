package grails.plugin.translate.google

import grails.util.Holders
import net.sf.json.groovy.JsonSlurper
import spock.lang.Specification
import grails.plugin.translate.TranslateConfig

abstract class AbstractGoogleTranslateSpec extends Specification {

    def config = [key:'my-api-key', version:'vN', baseUrl:'https://path-to-google-api']

    def setupGoogleTestConfig() {
        Holders.config = new ConfigObject()
        config.each { String key, value ->
            TranslateConfig."googleApi${key.capitalize()}" = value
        }
    }

    def json(String name, boolean inData = true) {
        def json = new JsonSlurper().parse(findResource(name))
        inData ? [data:json]: json
    }

    private URL findResource(String name) {
        this.class.classLoader.getResource(name) ?: new File('test/unit', name).toURL()
    }

    def setupGoogleRealConfig() {
        Holders.config = new ConfigObject()
        TranslateConfig.googleApiKey = 'YOUR_API_KEY_HERE'
        TranslateConfig.googleApiVersion = 'v2'
        TranslateConfig.googleApiBaseUrl = 'https://www.googleapis.com'
    }

}
