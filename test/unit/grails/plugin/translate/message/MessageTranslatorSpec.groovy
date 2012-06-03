package grails.plugin.translate.message

import spock.lang.Specification
import grails.test.mixin.TestFor
import grails.plugin.translate.Message
import grails.plugin.translate.TranslateService

@TestFor(Message)
class MessageTranslatorSpec extends Specification {

    def 'can translate default messages into specific language'() {
        given:
        def message = new Message(code:'msg.1', language:'', country:'', variant:'', text:'House')
        message.save()
        
        and:
        def service = Mock(TranslateService)
        1 * service.translate({ it[0].translation = 'Haus'; true }, null)
        def messageTranslator = new grails.plugin.translate.message.MessageTranslator(service: service)
        
        when:
        messageTranslator.run(Locale.GERMAN)
        
        then:
        Message.count() == 2
        Message.findAllByLanguage('de').size() == 1
        Message.findAllByLanguage('de')[0].text == 'Haus'
    }

}
