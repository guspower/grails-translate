package grails.plugin.translate.google

class GoogleTranslateUtils {

    static String toLanguage(Locale locale) {
        [locale.language, locale.country, locale.variant].findAll { it }.join('-')
    }

}
