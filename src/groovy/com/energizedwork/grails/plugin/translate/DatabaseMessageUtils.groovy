package com.energizedwork.grails.plugin.translate

import grails.plugin.translate.Message

class DatabaseMessageUtils {

    static findMessage(String code, Locale locale) {
        Message.findWhere([code:code] + asMap(locale))
    }
    
    private static Map asMap(Locale locale) {
        def result = [language: '', country: '', variant: '']
        if(locale) {
            result.language = locale.language
            result.country = locale.country
            result.variant = locale.variant
        }
        result
    }
    
    static Locale calculateLocaleFromFileName(String name) {
        Locale result

        if(name.endsWith('.properties')) {
            name -= '.properties'
            if(name.contains('_')) {
                def tokens = name.tokenize('_')
                if(tokens.size() > 1) {
                    if(tokens.size() >= 4) { result = new Locale(tokens[1], tokens[2], tokens[3]) }
                    else if(tokens.size() == 3) { result = new Locale(tokens[1], tokens[2]) }
                    else { result = new Locale(tokens[1]) }
                }
            }
        }

        result
    }

}
