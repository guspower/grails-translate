package com.energizedwork.grails.plugin.translate.resource

import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource

class PatternMatchingPropertyFileFinder implements PropertyFileFinder {

    String pattern
    
    @Override
    List<URL> getUrls() {
        def result = []
        if(getPattern()) {
            def resolver = new PathMatchingResourcePatternResolver(new DefaultResourceLoader())
            result = resolver.getResources(getPattern()).collect { Resource resource -> resource.URL }
        }
        result
    }

}
