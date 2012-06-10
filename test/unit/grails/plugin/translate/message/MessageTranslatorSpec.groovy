package grails.plugin.translate.message

import spock.lang.Specification
import grails.test.mixin.TestFor
import grails.plugin.translate.Message
import grails.plugin.translate.TranslateService
import org.apache.log4j.Logger

@TestFor(Message)
class MessageTranslatorSpec extends Specification {

    def noLocale = [language:'', country:'', variant:'']
    def german =   [language:'de', country:'', variant:'']

    def 'can translate default messages into specific language'() {
        given:
        new Message([code:'msg.1', text:'House'] + noLocale).save()
        new Message([code:'msg.2', text:'Work'] + noLocale).save()

        and:
        def service = Mock(TranslateService)
        1 * service.translate({
            it[0].translation = 'Haus'
            it[1].translation = 'Arbeit'
            true
        }, null)
        def messageTranslator = new MessageTranslator(translateService: service)
        
        when:
        messageTranslator.run(Locale.GERMAN)
        
        then:
        Message.count() == 4
        Message.findAllByLanguage('de').size() == 2
        Message.findAllByLanguage('de').text == ['Haus', 'Arbeit']
    }

    def 'ignores non-default messages'() {
        given:
        new Message(code:'msg.1', language:'en', country:'GB', variant:'', text:'House').save()

        and:
        def service = Mock(TranslateService)
        def messageTranslator = new MessageTranslator(translateService: service)

        when:
        messageTranslator.run(Locale.GERMAN)

        then:
        Message.count() == 1
        Message.findAllByLanguage('de').size() == 0
    }

    def 'can update existing messages'() {
        given:
        new Message([code:'msg.1', text:'HAUS'] + german).save()
        new Message([code:'msg.1', text:'House'] + noLocale).save()

        and:
        def service = Mock(TranslateService)
        1 * service.translate({
            it[0].translation = 'Haus'
            true
        }, null)
        def messageTranslator = new MessageTranslator(translateService: service)

        when:
        messageTranslator.run(Locale.GERMAN)

        then:
        Message.count() == 2
        Message.findAllByLanguage('de').size() == 1
        Message.findAllByLanguage('de').text == ['Haus']
    }

    def 'will not update existing messages with newer last updated'() {
        given:
        new Message([code:'msg.1', text:'House', lastUpdated: yesterday] + noLocale).save()
        new Message([code:'msg.1', text:'HAUS', lastUpdated: now] + german).save()

        and:
        def service = Mock(TranslateService)
        def messageTranslator = new MessageTranslator(translateService: service)

        when:
        messageTranslator.run(Locale.GERMAN)

        then:
        Message.count() == 2
        Message.findAllByLanguage('de').size() == 1
        Message.findAllByLanguage('de').text == ['HAUS']
    }

    def 'will only translate text once'() {
        given:
        new Message([code:'msg.1', text:'House'] + noLocale).save()
        new Message([code:'msg.2', text:'House'] + noLocale).save()

        and:
        def service = Mock(TranslateService)
        1 * service.translate({
            it[0].translation = 'Haus'
            it.size() == 1
        }, null)
        def messageTranslator = new MessageTranslator(translateService: service)

        when:
        messageTranslator.run(Locale.GERMAN)

        then:
        Message.count() == 4
        Message.findAllByLanguage('de').size() == 2
        Message.findAllByLanguage('de').text == ['Haus', 'Haus']
    }

    def 'will not create new message for empty translation'() {
        given:
        new Message([code:'msg.1', text:'House'] + noLocale).save()

        and:
        def service = Mock(TranslateService)
        1 * service.translate(_, null)
        def messageTranslator = new MessageTranslator(translateService: service)
        
        def log = Mock(Logger)
        messageTranslator.log = log
        0 * log.error(_)
        1 * log.warn(_)

        when:
        messageTranslator.run(Locale.GERMAN)

        then:
        Message.count() == 1
        Message.findAllByLanguage('de').size() == 0
    }
    
    private getNow() { new Date() }
    private getYesterday() { now.minus 1 }

}
