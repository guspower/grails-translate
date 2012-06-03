package com.energizedwork.grails.plugin.translate.resource

import grails.util.BuildSettingsHolder

class GrailsDevelopmentPropertyFileFinder extends PatternMatchingPropertyFileFinder {

    @Override
    String getPattern() {
        def resourcesDir = BuildSettingsHolder.settings.resourcesDir.path
        "file:${resourcesDir}/grails-app/i18n/**/*.properties"
    }

}
