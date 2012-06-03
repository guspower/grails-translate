package com.energizedwork.grails.plugin.translate

import static java.util.Locale.*

import grails.test.mixin.Mock
import org.springframework.context.NoSuchMessageException
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import spock.lang.Unroll
import grails.plugin.translate.Message

@Mock(Message)
class DatabaseMessageSourceSpec extends AbstractMessageSpec {

    DatabaseMessageSource source
    ReloadableResourceBundleMessageSource bundle

    def setup() {
        bundle = new ReloadableResourceBundleMessageSource()
        bundle.basename = 'translate-test'
        source = new DatabaseMessageSource()
    }

    @Unroll('database messages source returns #expected given #key and locale #locale')
    def 'database message source returns correct value given key and locale'() {
        given:
            build([one:'One', two:'Two'])
            build([one:'Ein', two:'Zwei'], GERMAN)
            build([one:'EinCH', two:'ZweiCH'], SWISS_GERMAN)
            build([one:'Wan', two:'Too'], DUBLIN_ENGLISH)

        when:
            def actual = source.getMessage(key, null, locale)

        then:
            actual == bundle.getMessage(key, null, locale)

        and:
            actual == expected
        
        where:
            locale         | key    | expected
            ENGLISH        | 'one'  | 'One'
            ENGLISH        | 'two'  | 'Two'
            GERMAN         | 'one'  | 'Ein'
            GERMAN         | 'two'  | 'Zwei'
            null           | 'one'  | 'One'
            null           | 'two'  | 'Two'
            KOREAN         | 'one'  | 'One'
            KOREAN         | 'two'  | 'Two'
            SWISS_GERMAN   | 'one'  | 'EinCH'
            SWISS_GERMAN   | 'two'  | 'ZweiCH'
            DUBLIN_ENGLISH | 'one'  | 'Wan'
            DUBLIN_ENGLISH | 'two'  | 'Too'
    }

    @Unroll('database messages source throws NoSuchMessageException given unknown key and locale #locale')
    def 'database messages source throws NoSuchMessageException given unknown key'() {
        when:
        source.getMessage('unknown', null, locale)

        then:
        thrown NoSuchMessageException

        where:
        locale << [ENGLISH, GERMAN, SWISS_GERMAN, DUBLIN_ENGLISH, KOREAN, null]
    }

    @Unroll('database messages source returns code as message when given unknown code if configured to do so')
    def 'database messages source returns code as message when given unknown code if configured to do so'() {
        given:
        source.useCodeAsDefaultMessage = true
        bundle.useCodeAsDefaultMessage = true

        when:
        def actual = source.getMessage('unknown', null, locale)

        then:
        actual == 'unknown'
        actual == bundle.getMessage('unknown', null, locale)

        where:
        locale << [ENGLISH, GERMAN, SWISS_GERMAN, DUBLIN_ENGLISH, KOREAN, null]
    }

}
