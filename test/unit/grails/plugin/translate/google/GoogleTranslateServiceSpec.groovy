package grails.plugin.translate.google

import static groovyx.net.http.ContentType.JSON

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpResponse
import org.apache.http.StatusLine
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.cli.support.MetaClassRegistryCleaner
import org.gmock.WithGMock
import spock.lang.Ignore

import grails.plugin.translate.Translation
import grails.plugin.translate.TranslateCollector

@WithGMock
class GoogleTranslateServiceSpec extends AbstractGoogleTranslateSpec {

    GoogleTranslateService service
    RESTClient restClient
    MetaClassRegistryCleaner cleaner

    def setup() {
        service = new GoogleTranslateService()
        restClient = Mock(RESTClient)
        service.restClient = restClient

        setupGoogleTestConfig()

        cleaner = MetaClassRegistryCleaner.createAndRegister()
        GoogleTranslationBatch.metaClass.constructor = { Map map -> new GoogleTranslationBatch(map) }
    }

    def cleanup() {
        MetaClassRegistryCleaner.cleanAndRemove(cleaner)
    }

    def 'service returns list of supported locales'() {
        given:
        def request
        restClient.get( { request = it; it } ) >> json('languages.json')

        when:
        def locales = service.supportedLocales

        then:
        request.query.key == config.key
        request.requestContentType == JSON
        request.path == '/language/translate/vN/languages'

        and:
        54 == locales.size()
        'af' == locales[0].language
        'zh' == locales[-1].language
        'TW' == locales[-1].country
    }

    def 'service can translate a single message'() {
        given:
        def translation = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')

        and:
        def batch = mock(GoogleTranslationBatch, constructor([collector:null]))
        batch.add(translation).returns(true)
        batch.run()

        expect:
        play {
            service.translate translation
        } || true
    }
    
    def 'service batches translations'() {
        given:
        def t1 = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')
        def t2 = new Translation(to: Locale.GERMAN, original: 'My name is Hans Grüber')

        and:
        def collector = { } as TranslateCollector

        and:
        def batch = mock(GoogleTranslationBatch, constructor([collector: collector]))
        batch.add(t1).returns(true)
        batch.add(t2).returns(true)
        batch.run()

        expect:
        play {
            service.translate([t1, t2], collector)
        } || true
    }

    def 'service logs translation errors'() {
        given:
        def translation = new Translation(to: Locale.GERMAN, original: 'Shoot the glass')

        and:
        def statusLine = [getStatusCode:{403}, getReasonPhrase:{'Daily Limit Exceeded'}] as StatusLine
        def response = [getStatusLine: { statusLine }] as HttpResponse

        and:
        def batch = mock(GoogleTranslationBatch, constructor([collector:null]))
        batch.add(translation).returns(true)
        batch.run().raises(new HttpResponseException(new HttpResponseDecorator(response, null)))

        and:
        def log = Mock(Logger)
        1 * log.error('translate: 403: Daily Limit Exceeded', _)
        service.log = log

        expect:
        play {
            service.translate translation
        } || true
    }

    @Ignore
    def 'hit real language service'() {
        given:
        service = new GoogleTranslateService()
        setupGoogleRealConfig()

        when:
        def locales = service.supportedLocales

        then:
        54 == locales.size()
        'af' == locales[0].language
        'zh' == locales[-1].language
        'TW' == locales[-1].country
    }

    @Ignore
    def 'hit real translation service'() {
        given:
        service = new GoogleTranslateService()
        setupGoogleRealConfig()
        
        def translation = new Translation(original: 'Do you speak German, baby?', to: Locale.GERMAN)

        when:
        service.translate translation

        then:
        'Sprechen Sie Deutsch, Baby?' == translation.result
    }

}
