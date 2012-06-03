package com.energizedwork.grails.plugin.translate

import spock.lang.Shared
import spock.lang.Specification
import org.apache.log4j.Logger
import org.apache.log4j.BasicConfigurator
import org.apache.log4j.Level
import grails.plugin.translate.Message

abstract class AbstractMessageSpec extends Specification {

    @Shared Locale DUBLIN_ENGLISH
    @Shared Locale SWISS_GERMAN

    def setupSpec() {
        if(!Logger.rootLogger.allAppenders) {
            BasicConfigurator.configure()
            Logger.rootLogger.level = Level.INFO
        }
        SWISS_GERMAN = new Locale('de', 'CH')
        DUBLIN_ENGLISH = new Locale('en', 'IE', 'Dublin')
    }

    void build(Map data, Locale locale = null) {
        data.each { code, text ->
            new Message(code: code, locale: locale, text: text).save()
        }
    }


}
