package com.energizedwork.grails.plugin.translate

import static com.energizedwork.grails.plugin.translate.DatabaseMessageUtils.*

import grails.plugin.translate.Message
import com.energizedwork.grails.plugin.translate.resource.PropertyFileFinder
import com.energizedwork.grails.plugin.translate.resource.GrailsDevelopmentPropertyFileFinder
import org.apache.commons.logging.LogFactory

class DatabaseMessageLoader {

    private static final log = LogFactory.getLog('grails.plugin.translate')

    boolean updateExisting
    PropertyFileFinder finder
    
    void load() {
        int updates = 0
        Message.withTransaction { status ->
            def finder = finder ?: new GrailsDevelopmentPropertyFileFinder()
            finder.urls.each { URL propertyFileUrl ->
                log.debug "[translate] Loading messages from $propertyFileUrl ..."
                Locale locale = calculateLocaleFromFileName(propertyFileUrl.file)
                parsePropertiesFromUrl propertyFileUrl, { String code, String text ->
                    createOrUpdateMessage code, locale, text
                    updates++
                }
            }
        }
        log.info "[translate] Updated $updates messages..."
    }

    private createOrUpdateMessage(String code, Locale locale, String text) {
        Message message = findMessage(code, locale)
        if(updateExisting && message) {
            message.text = text
        } else if(!message) {
            message = new Message(code:code, locale:locale, text:text)
        } else {
            message = null
        }

        if(message) {
            assert message.save() : message.errors
        }
    }

    private Closure parsePropertiesFromUrl = { URL url, Closure closure ->        
        if(url) {
            def properties = new Properties()
            properties.load url.openStream()
            properties.each closure
        }
    }

}
