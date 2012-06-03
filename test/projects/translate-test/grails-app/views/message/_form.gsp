<%@ page import="grails.plugin.translate.Message" %>



<div class="fieldcontain ${hasErrors(bean: messageInstance, field: 'code', 'error')} ">
	<label for="code">
		<g:message code="message.code.label" default="Code" />
		
	</label>
	<g:textField name="code" value="${messageInstance?.code}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageInstance, field: 'language', 'error')} ">
	<label for="language">
		<g:message code="message.language.label" default="Language" />
		
	</label>
	<g:textField name="language" value="${messageInstance?.language}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageInstance, field: 'country', 'error')} ">
	<label for="country">
		<g:message code="message.country.label" default="Country" />
		
	</label>
	<g:textField name="country" value="${messageInstance?.country}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageInstance, field: 'variant', 'error')} ">
	<label for="variant">
		<g:message code="message.variant.label" default="Variant" />
		
	</label>
	<g:textField name="variant" value="${messageInstance?.variant}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: messageInstance, field: 'text', 'error')} ">
	<label for="text">
		<g:message code="message.text.label" default="Text" />
		
	</label>
	<g:textField name="text" value="${messageInstance?.text}"/>
</div>

