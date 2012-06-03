package grails.plugin.translate.message

import grails.plugin.translate.TranslateService
import grails.plugin.translate.Message

class MessageTranslator {

    TranslateService service

    void run(Message message, Locale to) {
        def translatable = new TranslatableMessage(message: message, to: to)
        service.translate translatable
        def translation = new Message(code: translatable.message.code, text: translatable.translation, locale: to)
        translation.save()
    }

    void run(Locale to) {
        def messages = Message.findAllByLanguage('')
        def translatables = messages.collect { new TranslatableMessage(to: to, message: it) }
        service.translate translatables, null
        translatables.each { TranslatableMessage translated ->
            def message = new Message(code: translated.message.code, text: translated.translation, locale: to)
            message.save()
        }
    }

}
