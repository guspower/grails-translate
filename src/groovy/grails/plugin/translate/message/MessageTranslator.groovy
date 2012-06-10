package grails.plugin.translate.message

import grails.plugin.translate.TranslateService
import grails.plugin.translate.Message
import org.apache.log4j.Logger

class MessageTranslator {

    def log = Logger.getLogger(MessageTranslator)

    TranslateService translateService

    void run(Message message, Locale to) {
        def translatable = new TranslatableMessage(message: message, to: to)
        translateService.translate translatable
        createOrUpdate translatable
    }

    void run(Locale to) {
        def translatables = asTranslatables(Message.findAllByLanguage(''), to)
        def duplicates = extractDuplicates(translatables)

        translateService.translate translatables, null
        
        translateDuplicates duplicates, translatables
        translatables.each { TranslatableMessage translated ->
            createOrUpdate translated
        }
    }

    private void createOrUpdate(TranslatableMessage translated) {
        if(translated.translation) {
            def message = translated.target
            if(message) {
                message.text = translated.translation
            } else {
                message = new Message(code: translated.message.code, text: translated.translation, locale: translated.to)
            }
            if(!message.save()) {
                log.error "Unable to save $message: \n ${message.errors}"
            }
        } else {
            log.warn "No translation created for $translated.message"
        }
    }

    private List<TranslatableMessage> asTranslatables(List<Message> messages, Locale to) {
        messages.collect { new TranslatableMessage(to: to, message: it) }.findAll { TranslatableMessage tm ->
            tm.shouldTranslate
        }
    }
    
    private Map<String, List<TranslatableMessage>> extractDuplicates(List<TranslatableMessage> messages) {
        def data = messages.groupBy { it.message.text }.findAll { it.value.size() > 1 }
        data.each { key, duplicates ->
            duplicates[1..-1].each { messages.remove(it) }
            duplicates.remove 0
        }
        data
    }
    
    private void translateDuplicates(Map<String, List<TranslatableMessage>> duplicates, List<TranslatableMessage> messages) {
        duplicates.each { String text, List<TranslatableMessage> dups ->
            String translation = messages.find { it.original == text }.translation
            dups*.translation = translation
            messages.addAll dups
        }
    }

}
