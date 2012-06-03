//import org.apache.tools.ant.types.Path
//import grails.util.BuildSettingsHolder
//
//eventTestCompileStart = { type ->
//    File targetClassDir = new File(BuildSettingsHolder.settings.testClassesDir, type.relativeSourcePath)
//    ant.mkdir(dir:targetClassDir)
//
//    def commonPath = new Path(ant.project)
//    commonPath.setLocation new File("test/common")
//
//    Path testClasspath = ant.project.getReference('grails.test.classpath')
//    testClasspath.append commonPath
//
//    ant."${ type.name == 'unit' ? 'testc' : 'groovyc'}"(destdir: targetClassDir, classpath: testClasspath,
//            verbose: true, listfiles: true) {
//        javac(classpath: testClasspath, debug: "yes")
//        src(path: commonPath)
//    }
//}