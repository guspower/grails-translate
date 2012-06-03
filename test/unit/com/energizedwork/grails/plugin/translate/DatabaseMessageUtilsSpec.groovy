package com.energizedwork.grails.plugin.translate

import static java.util.Locale.*
import static com.energizedwork.grails.plugin.translate.DatabaseMessageUtils.calculateLocaleFromFileName

import spock.lang.Unroll

class DatabaseMessageUtilsSpec extends AbstractMessageSpec {
    
    @Unroll('returns #expected given filename #name')
    def 'can determine locale from filename'() {
        expect:
        expected == calculateLocaleFromFileName(name)

        where:
        expected       | name
        null           | 'messages.properties'
        null           | 'messages_.properties'
        null           | 'messages.properties_'
        UK             | 'messages_en_GB.properties'
        US             | 'messages_en_US.properties'
        GERMAN         | 'messages_de.properties'
        SWISS_GERMAN   | 'messages_de_CH.properties'
        DUBLIN_ENGLISH | 'messages_en_IE_Dublin.properties'
    }

}
