package com.energizedwork.grails.validation

import spock.lang.Specification
import org.springframework.validation.Errors

import grails.plugin.translate.Translatable

class TranslatableValidationSpec extends Specification {

    def 'can validate translatable using externally specified constraints'() {
        given:
        def target = new SomethingTranslatable(original: 'ohai')

        and:
        def constraints = {
            original nullable: false, blank: false, maxSize: Translatable.MAX_TEXT_SIZE
            from     nullable: true,  validator: { value, object -> if(object.to == value) { ['same.locale'] } }
            to       nullable: false
        }

        when:
        def validator = new ObjectValidator(constraints: constraints)

        then:
        !validator.validate(target)

        and:
        validator.errors.getFieldError('from')
        validator.errors.getFieldError('to')

        and:
        validator.errors == target.errors
    }

}

class SomethingTranslatable implements Translatable {

    String original, result
    Locale from, to
    
    Errors errors

}
