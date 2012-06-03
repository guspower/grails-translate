package com.energizedwork.grails.validation

import org.springframework.validation.Errors
import grails.validation.ValidationErrors
import org.codehaus.groovy.grails.validation.Constraint

class ObjectValidator {

    private Object target
    private Map propertyToConstraints = [:]

    Closure constraints
    Errors errors

    boolean validate(Object target) {
        this.target = target
        errors = new ValidationErrors(target)

        buildConstraints()
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
        Class.forName("org.codehaus.groovy.grails.validation.${constraintName.capitalize()}Constraint")
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
