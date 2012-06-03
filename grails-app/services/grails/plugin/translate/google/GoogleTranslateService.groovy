package grails.plugin.translate.google

import static groovyx.net.http.ContentType.JSON

import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import grails.plugin.translate.TranslateService
import grails.plugin.translate.TranslateConfig
import grails.plugin.translate.TranslateCollector
import grails.plugin.translate.Translatable

class GoogleTranslateService implements TranslateService {

    def log
    RESTClient _restClient
    
    RESTClient getRestClient() {
        if(!_restClient) {
            _restClient = new RESTClient(TranslateConfig.googleApiBaseUrl)
        }
        _restClient
    }

    void setRestClient(RESTClient client) {
        _restClient = client
    }

    List<Locale> getSupportedLocales() {
        def response = restClient.get(
                path:"/language/translate/${TranslateConfig.googleApiVersion}/languages",
                query:[key:TranslateConfig.googleApiKey],
                requestContentType: JSON)

        response.data.data.languages.collect {
            it.language.tokenize('-') as Locale
        }
    }
    
    void translate(Translatable translation) {
        translate([translation], null)
    }

    void translate(Iterable<Translatable> translations, TranslateCollector collector) {
        def batch = new GoogleTranslationBatch(collector: collector)
        translations.each { Translatable translation ->
            if(!batch.add(translation)) {
                runBatch batch
                batch.clear()
                batch.add translation
            }
        }
        runBatch batch
    }
    
    private runBatch(GoogleTranslationBatch batch) {
        try {
            batch.run()
        } catch(HttpResponseException exception) {
            def status = exception.response.statusLine
            log.error "translate: ${status.statusCode}: ${status.reasonPhrase}", exception
        }        
    }

}

