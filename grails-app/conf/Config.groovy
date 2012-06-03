log4j = {
    appenders {
        console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    }

    error  'org.codehaus.groovy.grails.web.servlet',
           'org.codehaus.groovy.grails.web.pages',
           'org.codehaus.groovy.grails.web.sitemesh',
           'org.codehaus.groovy.grails.web.mapping.filter',
           'org.codehaus.groovy.grails.web.mapping',
           'org.codehaus.groovy.grails.commons',
           'org.codehaus.groovy.grails.plugins',
           'org.codehaus.groovy.grails.orm.hibernate',
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}
