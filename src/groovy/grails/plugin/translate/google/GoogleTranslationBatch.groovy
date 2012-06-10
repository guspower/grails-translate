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
import org.apache.log4j.Logger

class GoogleTranslationBatch {

    static final int DEFAULT_MAX_BATCH_SIZE = 1500
    static final int DEFAULT_RATE_LIMIT_DURATION = 1000

    private log = Logger.getLogger(GoogleTranslationBatch)
    private HTTPBuilder _http
    private long lastRunAt

    private String language
    private List<Translatable> translations = []
    private validator = new ObjectValidator(constraints: Translatable.constraints)

    int maxBatchSize = DEFAULT_MAX_BATCH_SIZE
    int rateLimitDuration = DEFAULT_RATE_LIMIT_DURATION

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
        if(validator.validate(translatable) && !exceedsBatchTextLimit(translatable)) {
            if(!language || (language == toLanguage(translatable.to))) {
                if(!language) { language = toLanguage(translatable.to) }
                translations << translatable
                result = true
            }
        }
        result
    }

    void run() throws HttpResponseException {
        if(TranslateConfig.ok) {
            log.info "TRANSLATING BATCH OF SIZE: $totalTextSize"
            if(translations) {
                applyRateLimitIfRequired()

                http.request(POST, JSON) {
                    headers.'X-HTTP-Method-Override' = 'GET'
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
        } else {
            logConfigurationError()
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

    private applyRateLimitIfRequired() {
        if(lastRunAt) {
            long delta = System.currentTimeMillis() - lastRunAt
            if(delta < rateLimitDuration) {
                Thread.sleep (rateLimitDuration - delta)
            }
        }
        lastRunAt = System.currentTimeMillis()
    }
    
    private Closure applyTranslatedText = { data, index ->
        Translatable translation = translations[index]
        translation.result = data.translatedText
        collector?.call translation
    }

    private boolean exceedsBatchTextLimit(Translatable translation) {
        totalTextSize + translation.original.size() > maxBatchSize
    }

    private int getTotalTextSize() {
        translations ? translations.original*.size().sum() : 0
    }

    private logConfigurationError() {
        log.error("Invalid/unset google translate configuration!")
        log.error("translate.google.api.key : ${TranslateConfig.googleApiKey}")
        log.error("translate.google.api.version : ${TranslateConfig.googleApiVersion}")
        log.error("translate.google.api.baseUrl : ${TranslateConfig.googleApiBaseUrl}")
    }

}
