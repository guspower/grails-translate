package grails.plugin.translate

import java.text.MessageFormat

class Message {

    String code
    String language
    String country
    String variant
    String text

    static constraints = {
        code unique: ['language', 'country', 'variant'], maxSize: 5000
        language nullable: false, blank: true
        country nullable: false, blank: true
        variant nullable: false, blank: true
    }
    
    static mapping = {
        cache true
        sort code: "desc"
    }
    
    static transients = ['locale']
    
    Locale getLocale() {
        Locale result
        if(language) {
            if(variant) { result = new Locale(language, country, variant) }
            else if(country) { result = new Locale(language, country) }
            else { result = new Locale(language) }
        }
        result
    }

    void setLocale(Locale locale) {
        language = locale?.language ?: ''
        country = locale?.country ?: ''
        variant = locale?.variant ?: ''
    }
    
    MessageFormat asMessageFormat() {
        new MessageFormat(text.replaceAll("('(^['])*)+", "''"), locale)
    }
    
    String toString() {
        "Message[$code] $language/$country/$variant"
    }

}
