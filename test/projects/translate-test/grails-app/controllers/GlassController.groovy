import grails.plugin.translate.message.MessageTranslator
import grails.plugin.translate.Message

class GlassController {

    def googleTranslateService

    def index = {}

    def translate = {
        def translator = new MessageTranslator(googleTranslateService: googleTranslateService)
        translator.run(Message.findByCodeAndLanguage('i.can.eat.glass', ''), new Locale(params.id))
        
        render view: 'index'
    }

}
