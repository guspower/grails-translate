package grails.plugin.translate.google

import static groovyx.net.http.Method.POST
import static groovyx.net.http.ContentType.JSON
import static GoogleTranslateUtils.*

import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException
import com.energizedwork.grails.validation.ObjectValidator
import grails.plugin.translate.TranslateCollector
import grails.plugin.translate.Translatable
import grails.plugin.translate.TranslateConfig

class GoogleTranslationBatch {

    private HTTPBuilder _http

    private String language
    private List<Translatable> translations = []
    private validator = new ObjectValidator(constraints: Translatable.constraints)

    TranslateCollector collector

    List<Translatable> getTranslations() { translations.asImmutable() }
    void setTranslations(List<Translatable> translations) {}

    String getLanguage() { language }
    void setLanguage(String language) {}

    void clear() {
        translations.clear()
        language = null
    }

    //TODO: this needs more than a boolean: it can reject for invalid OR full batch
    boolean add(Translatable translatable) {
        boolean result
        if(validator.validate(translatable) && !exceedsTextLimit(translatable)) {
            if(!language || (language == toLanguage(translatable.to))) {
                if(!language) { language = toLanguage(translatable.to) }
                translations << translatable
                result = true
            }
        }
        result
    }

    void run() throws HttpResponseException {
        if(translations) {
            http.request(POST, JSON) {
                headers = ['X-HTTP-Method-Override': 'GET']
                requestContentType = JSON

                uri.path = "/language/translate/${TranslateConfig.googleApiVersion}"
                uri.query = [key:TranslateConfig.googleApiKey, target:language]
                translations.each {
                    uri.addQueryParam('q', it.original)
                }

                response.success = { response, json ->
                    json.data.translations.eachWithIndex applyTranslatedText
                }
            }
        }
    }
    
    HTTPBuilder getHttp() {
        if(!_http) {
            _http = new HTTPBuilder(TranslateConfig.googleApiBaseUrl)
        }
        _http
    }

    void setHttp(HTTPBuilder http) {
        _http = http
    }

    private Closure applyTranslatedText = { data, index ->
        Translatable translation = translations[index]
        translation.result = data.translatedText
        collector?.call translation
    }

    private boolean exceedsTextLimit(Translatable translation) {
        totalTextSize + translation.original.size() > Translatable.MAX_TEXT_SIZE
    }

    private int getTotalTextSize() {
        translations ? translations.original*.size().sum() : 0
    }

}
