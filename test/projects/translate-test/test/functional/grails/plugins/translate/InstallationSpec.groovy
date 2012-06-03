package grails.plugins.translate

import geb.spock.GebSpec
import grails.plugin.remotecontrol.RemoteControl
import grails.plugin.translate.Message
import grails.util.BuildSettingsHolder
import grails.util.BuildSettings
import spock.lang.Unroll

class InstallationSpec extends GebSpec {

    def 'plugin installs correctly'() {
        given:
        go '/'

        expect:
        $('#status li', text: ~/translate.*/)
    }

    def 'messages are automagically loaded'() {
        given:
        def remote = buildRemoteControl()

        expect:
        remote { Message.count() }
    }

    def 'messages appear in the scaffolded ui'() {
        given:
        go '/message/list'

        expect:
        10 == $('#list-message tbody tr').size()
    }

    @Unroll("i.can.eat.glass returns #expected for #locale")
    def 'messages are localized using the lang parameter'() {
        given:
        go "/glass?lang=$locale"

        expect:
        expected == $('#glass').text()
        
        where:
        locale | expected
        'en'   | 'I can eat glass, it doesn\'t hurt me'
        'de'   | 'Ich kann Glas essen, das tut mir nicht weh.'
        'de_CH'| 'Ich chan Glaas ässe, das tuet mir nöd weeh.'
        'es'   | 'Puedo comer vidrio, no me duele.'
        'ru'   | 'Я могу есть стекло, оно мне не больно'
        'sv'   | 'Jag kan äta glas, det gör inte ont.'
        'th'   | 'ฉันสามารถกินแก้วมันไม่ได้เจ็บ'
    }

    private buildRemoteControl() {
        if(!BuildSettingsHolder.settings) {
            BuildSettingsHolder.settings = new BuildSettings()
            System.properties[BuildSettings.FUNCTIONAL_BASE_URL_PROPERTY] = 'http://localhost:8080'
        }
        new RemoteControl()
    }

}
