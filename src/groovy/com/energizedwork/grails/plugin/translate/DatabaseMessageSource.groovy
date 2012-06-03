package com.energizedwork.grails.plugin.translate

import java.text.MessageFormat
import org.springframework.context.support.AbstractMessageSource
import org.springframework.context.NoSuchMessageException
import grails.plugin.translate.Message

class DatabaseMessageSource extends AbstractMessageSource {

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        MessageFormat format

        Message message = resolveMessage(code, locale)

        if(message) {
            format = message.asMessageFormat()
        } else {
            if(useCodeAsDefaultMessage) {
                format = new MessageFormat(code, locale)
            } else {
                throw new NoSuchMessageException(code, locale)
            }
        }

        format
    }

    private Message resolveMessage(String code, Locale locale) {
        Message message
        if(locale) {
            if(locale.variant) {
                message = Message.findByCodeAndLanguageAndCountryAndVariant(code, locale.language, locale.country, locale.variant)
            }
            if(!message && locale.country) {
                message = Message.findByCodeAndLanguageAndCountryAndVariant(code, locale.language, locale.country, '')
            }
            if(!message) {
                message = Message.findByCodeAndLanguageAndCountry(code, locale.language, '', '')
            }
        }
        if(!message) {
            message = Message.findByCodeAndLanguage(code, '')
        }
        message
    }

}
