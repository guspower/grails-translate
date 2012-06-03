package grails.plugin.translate

import spock.lang.Specification
import grails.test.mixin.TestFor
import spock.lang.Unroll

@TestFor(Message)
class MessageSpec extends Specification {

    @Unroll("message escapes single quotes in text (#text)")
    def 'message keeps text the same when using g.message'() {
        given:
        def message = new Message(text: text)
        
        when:
        def format = message.asMessageFormat()
        
        then:
        expected == format.toPattern()

        where:
        text                                    | expected
        'Sometimes'                             | 'Sometimes'
        'I say things like "o hai"'             | 'I say things like "o hai"'
        "Other times it just isn't appropriate" | "Other times it just isn''t appropriate"
    }

}
