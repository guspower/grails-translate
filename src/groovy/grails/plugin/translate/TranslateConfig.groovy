package grails.plugin.translate

import grails.util.Holders

class TranslateConfig {

    static String getGoogleApiKey() { Holders.config.translate.google.api.key ?: '' }
    static void setGoogleApiKey(String apiKey) { Holders.config.translate.google.api.key = apiKey }

    static String getGoogleApiVersion() { Holders.config.translate.google.api.version ?: 'v2' }
    static void setGoogleApiVersion(String version) { Holders.config.translate.google.api.version = version }

    static String getGoogleApiBaseUrl() { Holders.config.translate.google.api.baseUrl ?: '' }
    static void setGoogleApiBaseUrl(String baseUrl) { Holders.config.translate.google.api.baseUrl = baseUrl }

}
