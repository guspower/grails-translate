package com.energizedwork.grails.plugin.translate.resource

class LocaleScannerPropertyFileFinder implements PropertyFileFinder {

    String basename
    
    @Override
    List<URL> getUrls() {
        (Locale.availableLocales.toList() + [null]).collect { Locale locale ->
            toPropertiesFileUrl(locale)
        }
    }

    private URL toPropertiesFileUrl(Locale locale) {
        def result = new StringBuilder()
        result << basename
        if(locale) {
            result << "_${locale.language}"
            if(locale.country) { result << "_${locale.country}" }
            if(locale.variant) { result << "_${locale.variant}" }
        }
        result << '.properties'
        this.class.classLoader.getResource(result.toString())
    }

}
