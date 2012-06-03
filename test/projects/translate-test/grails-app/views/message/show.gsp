
<%@ page import="grails.plugin.translate.Message" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'message.label', default: 'Message')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-message" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-message" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list message">
			
				<g:if test="${messageInstance?.code}">
				<li class="fieldcontain">
					<span id="code-label" class="property-label"><g:message code="message.code.label" default="Code" /></span>
					
						<span class="property-value" aria-labelledby="code-label"><g:fieldValue bean="${messageInstance}" field="code"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${messageInstance?.language}">
				<li class="fieldcontain">
					<span id="language-label" class="property-label"><g:message code="message.language.label" default="Language" /></span>
					
						<span class="property-value" aria-labelledby="language-label"><g:fieldValue bean="${messageInstance}" field="language"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${messageInstance?.country}">
				<li class="fieldcontain">
					<span id="country-label" class="property-label"><g:message code="message.country.label" default="Country" /></span>
					
						<span class="property-value" aria-labelledby="country-label"><g:fieldValue bean="${messageInstance}" field="country"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${messageInstance?.variant}">
				<li class="fieldcontain">
					<span id="variant-label" class="property-label"><g:message code="message.variant.label" default="Variant" /></span>
					
						<span class="property-value" aria-labelledby="variant-label"><g:fieldValue bean="${messageInstance}" field="variant"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${messageInstance?.text}">
				<li class="fieldcontain">
					<span id="text-label" class="property-label"><g:message code="message.text.label" default="Text" /></span>
					
						<span class="property-value" aria-labelledby="text-label"><g:fieldValue bean="${messageInstance}" field="text"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${messageInstance?.id}" />
					<g:link class="edit" action="edit" id="${messageInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
