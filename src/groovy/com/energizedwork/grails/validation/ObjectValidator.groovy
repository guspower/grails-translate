package com.energizedwork.grails.validation

import org.springframework.validation.Errors
import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.validation.Constraint
import grails.util.Holders

class ObjectValidator {

    private Object target
    private Map propertyToConstraints = [:]

    Closure constraints
    Errors errors

    boolean validate(Object input) {
        errors = new ValidationErrors(input)

        if(target?.class != input.class) {
            target = input
            buildConstraints()
        } else {
            target = input
        }

        runConstraints()
        addErrorsToTargetIfPossible()

        !errors.hasErrors()
    }

    private addErrorsToTargetIfPossible() {
        if (errors.hasErrors() && target.hasProperty('errors')) {
            if (target.errors) {
                target.errors.addAllErrors(errors)
            } else {
                target.errors = errors
            }
        }
    }

    private buildConstraints() {
        constraints.delegate = this
        constraints()
    }

    private Class findConstraintClass(String constraintName) {
        Class result
        
        String className = "org.codehaus.groovy.grails.validation.${constraintName.capitalize()}Constraint"
        try {
            result = Class.forName(className)
        } catch(ClassNotFoundException notFound) {
            result = Holders.grailsApplication.classLoader.loadClass(className)
        }

        result
    }

    private runConstraints() {
        propertyToConstraints.each { String propertyName, List constraintList ->
            constraintList.each { Constraint constraint ->
                constraint.validate(target, target."$propertyName", errors)
            }
        }
    }

    def methodMissing(String name, args) {
        def constraints = args[0].collect { String constraintName, parameter ->
            Constraint constraint = findConstraintClass(constraintName).newInstance()
            constraint.parameter = parameter
            constraint.propertyName = name
            constraint.owningClass = target.class
            constraint
        }
        propertyToConstraints."$name" = constraints
    }

}
