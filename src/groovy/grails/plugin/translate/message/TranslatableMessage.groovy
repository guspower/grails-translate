package grails.plugin.translate.message

import grails.plugin.translate.Translatable
import grails.plugin.translate.Message

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

}
