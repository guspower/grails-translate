package grails.plugin.translate.google

import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON

import grails.test.mixin.web.ControllerUnitTestMixin
import groovyx.net.http.HTTPBuilder

import spock.lang.Ignore
import org.apache.http.StatusLine
import org.apache.http.HttpResponse
import groovyx.net.http.HttpResponseException
import groovyx.net.http.HttpResponseDecorator

import grails.plugin.translate.Translation
import grails.plugin.translate.TranslateCollector

@Mixin(ControllerUnitTestMixin)
class GoogleTranslationBatchSpec extends AbstractGoogleTranslateSpec {

    def setupSpec() { configureGrailsWeb() }
    def cleanup() { clearGrailsWebRequest() }
    def cleanupSpec() { cleanupGrailsWeb() }

    def setup() {
        setupGoogleTestConfig()
        bindGrailsWebRequest()
        mockCommandObject Translation
    }

    def 'does nothing for empty batch'() {
        given:
        boolean run
        def batch = new GoogleTranslationBatch()                
        batch.collector = { translation -> run = true } as TranslateCollector
        
        when:
        batch.run()

        then:
        !run
    }

    def 'will not add invalid translation'() {
        given:
        def batch = new GoogleTranslationBatch()
        def translation = new Translation()

        expect:
        !batch.add(translation)
        translation.hasErrors()
        !batch.translations
    }
    
    def 'can clear batch for next run'() {
        given:
        def batch = new GoogleTranslationBatch()
        def t1 = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')
        def t2 = new Translation(to: Locale.FRENCH, original: 'Tirez sur le verre')

        when:
        batch.add t1
        batch.clear()
        
        then:
        !batch.translations
        !batch.language

        and:
        batch.add t2
        batch.translations == [t2]
        batch.language == 'fr'
    }

    def 'can add and translate multiple entries'() {
        given:
        def translations = []
        def collector = { translation -> translations << translation } as TranslateCollector
        def t1 = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')
        def t2 = new Translation(to: Locale.GERMAN, original: 'My name is Hans Grüber')
        def batch = new GoogleTranslationBatch(collector: collector)
        
        and:
        def request
        def http = Mock(HTTPBuilder)
        1 * http.request(POST, JSON, { request = processHttpBuilderConfiguration(it, batch); true })
        batch.http = http
        
        when:
        assert batch.add(t1)
        assert batch.add(t2)
        batch.run()

        then:
        request.headers['X-HTTP-Method-Override'] == 'GET'
        request.uri.path == '/language/translate/vN'
        request.uri.query.q == ['Shoot the glass', 'My name is Hans Grüber']
        request.uri.query.key == config.key
        request.uri.query.target == 'de'
        request.requestContentType == JSON

        and:
        2 == translations.size()
        t1.result == 'Schiess dem Fenster'
        t2.result == 'Mein name ist Hans Gruber'
    }

    def 'batch throws http response error'() {
        given:
        def batch = new GoogleTranslationBatch()
        def translation = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')

        and:
        def statusLine = [getStatusCode:{403}, getReasonPhrase:{'Daily Limit Exceeded'}] as StatusLine
        def response = [getStatusLine: { statusLine }] as HttpResponse

        def http = Mock(HTTPBuilder)
        1 * http.request(POST, JSON, _) >> { throw new HttpResponseException(new HttpResponseDecorator(response, null)) }
        batch.http = http

        when:
        batch.add translation
        batch.run()

        then:
        thrown HttpResponseException
        !translation.result
    }

    def 'will not add different target language entry'() {
        given:
        def t1 = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')
        def t2 = new Translation(to: Locale.FRENCH, original: 'Is this the George Pompidou centre?')
        def batch = new GoogleTranslationBatch()

        expect:
        batch.add t1
        !batch.add(t2)
        [t1] == batch.translations
        'de' == batch.language
    }

    def 'will not add entries that increase batch size to above maximum payload size'() {
        given:
        def t1 = new Translation(to: Locale.GERMAN, original: 'X'.padRight(Translation.MAX_TEXT_SIZE -2))
        def t2 = new Translation(to: Locale.GERMAN, original: 'Second message')
        def batch = new GoogleTranslationBatch()

        expect:
        batch.add t1
        !batch.add(t2)
        [t1] == batch.translations
        'de' == batch.language
    }

    def 'cannot modify language or translation batch directly'() {
        given:
        def t1 = new Translation(to: Locale.GERMAN, original: 'X')
        def t2 = new Translation(to: Locale.FRENCH, original: 'Y')
        def batch = new GoogleTranslationBatch()
        batch.add t1

        when:
        batch.language = 'en'
        batch.translations << t2
        batch.translations = [t2]

        then:
        thrown UnsupportedOperationException
        batch.language == 'de'
        batch.translations == [t1]
    }

    @Ignore
    def 'hit real batch translation service'() {
        given:
        setupGoogleRealConfig()
        def t1 = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')
        def t2 = new Translation(to: Locale.GERMAN, original: 'My name is Hans Grüber')

        and:
        def translations = []

        and:
        def batch = new GoogleTranslationBatch(collector : { translations << it } as TranslateCollector)
        batch.add t1
        batch.add t2

        when:
        batch.run()

        then:
        2 == translations.size()
        t1.result == 'Nehmen Sie das Glas'
        t2.result == 'Mein Name ist Hans Gruber'
    }

    private processHttpBuilderConfiguration(Closure closure, GoogleTranslationBatch batch) {
        def config = new Expando()
        config.translations = batch.translations
        config.language = batch.language

        config.uri = new Expando()
        config.uri.metaClass.addQueryParam = { name, value ->
            if(!delegate.query."$name") { delegate.query."$name" = [] }
            delegate.query."$name" << value
        }
        config.response = new Expando()

        closure.delegate = config
        closure.owner = batch
        closure.resolveStrategy = Closure.OWNER_FIRST
        closure.call()

        config.response.success null, json('multi-response.json', false)
        config
    }

}
