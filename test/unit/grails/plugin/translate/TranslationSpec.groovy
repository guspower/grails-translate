package grails.plugin.translate

import spock.lang.Specification
import spock.lang.Unroll
import com.energizedwork.grails.validation.ObjectValidator

class TranslationSpec extends Specification {

    @Unroll("Translation of #text from #from to #to has error: #code")
    def 'verify translation constraints'() {
        given:
        def translation = new Translation(original: text, from: from, to: to)
        
        when:
        def validator = new ObjectValidator(constraints: Translatable.constraints)
        validator.validate translation
        
        then:
        validator.errors.allErrors.size() == (code ? 1: 0)
        code == validator.errors?.allErrors[0]?.code

        where:
        text | from      | to        | code
        'The'| Locale.UK | null      | 'nullable'
        'The'| null      | Locale.UK | null
        'The'| Locale.UK | Locale.UK | 'same.locale'
        null | Locale.UK | Locale.US | 'nullable'
        'The'| Locale.UK | Locale.US | null
    }
    
}
