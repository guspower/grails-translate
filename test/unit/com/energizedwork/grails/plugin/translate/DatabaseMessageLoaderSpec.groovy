package com.energizedwork.grails.plugin.translate

import static java.util.Locale.GERMAN

import com.energizedwork.grails.plugin.translate.resource.PatternMatchingPropertyFileFinder
import grails.test.mixin.TestFor
import spock.lang.Shared

import grails.plugin.translate.Message

@TestFor(Message)
class DatabaseMessageLoaderSpec extends AbstractMessageSpec {

    @Shared messageCount = 12
    @Shared finder = new PatternMatchingPropertyFileFinder(pattern: "file:**/translate-test*.properties")

    DatabaseMessageSource source
    DatabaseMessageLoader loader

    def setup() {
        source = new DatabaseMessageSource()
        loader = new DatabaseMessageLoader(finder: finder)
    }

    def cleanup() {
        Message.withTransaction {
            Message.list().each { it.delete() }
        }
    }

    def 'loads messages into database from resource bundle'() {
        when:
        loader.load()
        
        then:
        Message.count() == messageCount

        and:
        ['One', 'Two', 'Three']       == messages
        ['Ein', 'Zwei', 'Drei']       == getMessages(GERMAN)
        ['EinCH', 'ZweiCH', 'DreiCH'] == getMessages(SWISS_GERMAN)
        ['Wan', 'Too', 'Tree']        == getMessages(DUBLIN_ENGLISH)
    }
    
    def 'adds new messages to database from resource bundle'() {
        given:
        build('one':'One')
        build('two':'Zwei', GERMAN)
        build('three':'DreiCH', SWISS_GERMAN)
        build('three':'Tree', DUBLIN_ENGLISH)

        when:
        assert Message.count() == 4
        loader.load()

        then:
        ['One', 'Two', 'Three']       == messages
        ['Ein', 'Zwei', 'Drei']       == getMessages(GERMAN)
        ['EinCH', 'ZweiCH', 'DreiCH'] == getMessages(SWISS_GERMAN)
        ['Wan', 'Too', 'Tree']        == getMessages(DUBLIN_ENGLISH)

        and:
        Message.count() == messageCount
    }

    def 'does not update messages in database from resource bundle by default'() {
        given:
        build('one':'ONE')
        build('two':'ZWEI', GERMAN)
        build('three':'DREICH', SWISS_GERMAN)
        build('three':'TREE', DUBLIN_ENGLISH)

        when:
        assert Message.count() == 4
        loader.load()

        then:
        ['ONE', 'Two', 'Three']       == messages
        ['Ein', 'ZWEI', 'Drei']       == getMessages(GERMAN)
        ['EinCH', 'ZweiCH', 'DREICH'] == getMessages(SWISS_GERMAN)
        ['Wan', 'Too', 'TREE']        == getMessages(DUBLIN_ENGLISH)

        and:
        Message.count() == messageCount
    }

    def 'updates messages in database from resource bundle if configured to do so'() {
        given:
        loader.updateExisting = true

        and:
        build('one':'ONE')
        build('two':'ZWEI', GERMAN)
        build('three':'DREICH', SWISS_GERMAN)
        build('three':'TREE', DUBLIN_ENGLISH)

        when:
        assert Message.count() == 4
        loader.load()

        then:
        ['One', 'Two', 'Three']       == messages
        ['Ein', 'Zwei', 'Drei']       == getMessages(GERMAN)
        ['EinCH', 'ZweiCH', 'DreiCH'] == getMessages(SWISS_GERMAN)
        ['Wan', 'Too', 'Tree']        == getMessages(DUBLIN_ENGLISH)

        and:
        Message.count() == messageCount
    }

    private getMessages(Locale locale = Locale.default) {
        ['one', 'two', 'three'].collect { source.getMessage(it, null, locale) }
    }

}
