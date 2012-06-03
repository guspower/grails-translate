package grails.plugin.translate

import groovy.transform.ToString
import grails.validation.Validateable

@ToString
class Translation implements Translatable {

    String original, result
    Locale from, to

}
