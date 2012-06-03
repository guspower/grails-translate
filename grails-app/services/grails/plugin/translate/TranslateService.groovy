package grails.plugin.translate

interface TranslateService {

    List<Locale> getSupportedLocales()

    void translate(Translation translation) throws UnsupportedLocaleException

    void translate(Iterable<Translation> translations, TranslateCollector collector)

}
