<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
	<form:form id="createCertFormOne" modelAttribute="certificateToolState" action="first.form" enctype="multipart/form-data">
		<div>
			<h3><spring:message code="form.text.instruction"/></h3>
			<p><spring:message code="form.text.create.description"/></p>
		</div>
		<div id="submitError" class="alertMessage" style="display:none"></div>
		<c:if test="${statusMessageKey != null}" >
			<div id="statusMessageKey" class="alertMessage" >
				<spring:message code="${statusMessageKey}"/>
			</div>
		</c:if>
		<c:if test="${errorMessage != null}" >
			<div id="errorMessage" class="alertMessage" >
				<spring:message code="${errorMessage}" />
			</div>
		</c:if>
		<div style="position:relative; margin-left:20px">
		<table>
			<tbody>
				<tr>
					<td><form:label path="certificateDefinition.name"><B><spring:message code="form.label.name" /></B><span class="reqStarInline">*</span></form:label></td>
					<td><form:input id="name" path="certificateDefinition.name"/></td>
				</tr>
				<tr>
					<td><form:label path="certificateDefinition.description"><B><spring:message code="form.label.description" /></B></form:label></td>
					<td><form:textarea cssStyle="resize:none; width:350px; height:100px" path="certificateDefinition.description"/></td>
				</tr>
				
				<tr>
					<td><form:label path="certificateDefinition.expiryOffset" ><B><spring:message code="form.label.expiryOffset1" /></B></form:label></td>
					<td>
						<form:input maxlength="5" size="5" path="certificateDefinition.expiryOffset" id="expiryOffset" />
						<form:label path="certificateDefinition.expiryOffset" ><B><spring:message code="form.label.expiryOffset2" /></B></form:label>
					</td>
				</tr>
				<tr>
					<td><form:label path="certificateDefinition.documentTemplate"><B><spring:message code="form.label.templatefile" /><B><span class="reqStarInline">*</span></form:label></td>
                    <td>
                        <c:if test="${certificateToolState.certificateDefinition.documentTemplate != null}">
                                <spring:message code="form.label.currentFile"/>
                                <c:out value="${certificateToolState.certificateDefinition.documentTemplate.name}"/><br/>
                                <form:hidden id="currentTemplate" path="certificateDefinition.documentTemplate.id" />
                        </c:if>
                        <input type="file" name="data" />
                        <span style="font-size : xx-small;"><spring:message code="form.label.mimeTypes" arguments="${certificateToolState.mimeTypes}"/></span>
                    </td>
				</tr>
			</tbody>
			
		</table>
		</div>
		<div style="margin:5px">
			<input id="back" type="button" disabled="disabled" value="<spring:message code="form.submit.back" />" />&nbsp;
			<!-- bbailla2 <input id="save" type="button" value="<spring:message code="form.submit.saveProgress"/>"/>&nbsp;-->
			<input id="next" type="button" value="<spring:message code="form.submit.next"/>"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="cancel" type="button" value="<spring:message code="form.submit.cancel"/>"/>
			<form:hidden path="submitValue" />
		</div>
	</form:form>	
<script type="text/javascript">

	$(document).ready(function() {

		loaded();
		
		/* bbailla2 $("#save").click(function() {
			save();
		});*/
	
		$("#next").click(function() {
			next();
		});
		
		$("#cancel").click(function() {
			cancel();
		});
		
		$("textarea").resize(function() {
			loaded();
		});
	});
	
	/* bbailla2 function save() {
		if(validateForm()) {
			$("#submitValue").val("save");
			$("#createCertFormOne").submit();
		}
	}*/
	
	function cancel() {
		$("#submitValue").val("cancel");
		$("#createCertFormOne").submit();
	}
	
	function next() {
		if(validateForm()) {
			$("#submitValue").val("next");
			$("#createCertFormOne").submit();
		}
	}
	
	function validateForm() {
		$(".alertMessage").hide();
		var error = false;
		var errHtml = "";
		
		if(!$("#name").val()) {
			errHtml = errHtml + "<spring:message code="form.error.namefield"/>" + "</br>" ;
			error = true;
		}
		
		// bjones86 - expiry offset check
		if( $( "#expiryOffset" ).val() )
		{
			value = $( "#expiryOffset" ).val();
			if( value.indexOf( "-" ) != -1 || ( parseFloat( value ) != parseInt( value ) ) || isNaN( value ) )
			{
				error = true;
				errHtml = errHtml + "<spring:message code="form.error.expiryOffset.notNumber"/>" + "</br>";
			}
		}
		
		if(!$("input:file").val() && !$("#currentTemplate").val()) {
			errHtml = errHtml + "<spring:message code="form.error.templateField"/>" + "</br>" ;
			error = true;
		}
		
		if(error)
		{
			$("#submitError").html(errHtml).show();
			resetHeight();
			return false;
		}
		else
		{
			return true;
		}
	}

</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
