package grails.plugin.translate.message

import grails.plugin.translate.Translatable
import grails.plugin.translate.Message
import com.energizedwork.grails.plugin.translate.DatabaseMessageUtils
import groovy.transform.ToString

@ToString
class TranslatableMessage implements Translatable {

    Locale to
    Message message
    String translation

    @Override
    String getOriginal() { message.text }

    @Override
    void setResult(String result) { translation = result }

    @Override
    Locale getFrom() { message.locale }

    @Override
    Locale getTo() { to }
    
    @Lazy Message target = {
        DatabaseMessageUtils.findMessage(message.code, to)
    }()
    
    boolean isShouldTranslate() {
        boolean result = true
        if(target && target.lastUpdated) {
            result = target.lastUpdated < message.lastUpdated
        }
        result
    }

}
