import com.energizedwork.grails.plugin.translate.DatabaseMessageSource
import com.energizedwork.grails.plugin.translate.DatabaseMessageLoader

class TranslateGrailsPlugin {

    def version = "0.1-SNAPSHOT"
    def grailsVersion = "2.0 > *"
    def loadAfter = ['domainClass']
    def dependsOn = ['domainClass': '2.0 > *']
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Translate Plugin" // Headline display name of the plugin
    def author = "Gus Power"
    def authorEmail = "gus@energizedwork.com"
    def description = '''\
Translates message resources into other locales.
'''
    def documentation = "http://energizedwork.com/grails/plugin/translate"
    def license = "APACHE"
    def organization = [ name: "Energized Work", url: "http://www.energizedwork.com/" ]
    def developers = [[ name: "Odette Power", email: "odette@energizedwork.com" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.grails-plugins.codehaus.org/browse/grails-plugins/" ]

    def doWithWebDescriptor = { xml -> }
    def doWithDynamicMethods = { ctx -> }
    def doWithApplicationContext = { content ->
        content.getBean('messageLoader').load()
    }
    def onChange = { event -> }
    def onConfigChange = { event -> }
    def onShutdown = { event -> }

    def doWithSpring = {
        messageSource(DatabaseMessageSource) {
            useCodeAsDefaultMessage = true
        }
        messageLoader(DatabaseMessageLoader) {
            updateExisting = false
        }
    }

}
