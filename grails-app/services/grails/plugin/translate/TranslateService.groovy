package grails.plugin.translate

interface TranslateService {

    List<Locale> getSupportedLocales()

    void translate(Translatable translation) throws UnsupportedLocaleException

    void translate(Iterable<Translatable> translations, TranslateCollector collector)

}
