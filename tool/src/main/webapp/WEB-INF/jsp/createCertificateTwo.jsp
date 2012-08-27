<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
	<form:form id="createCertFormTwo" modelAttribute="certificateToolState" action="second.form">
		<h3><spring:message code="form.text.instruction"/></h3	>
		<p><spring:message code="form.text.fields.description"/></p>
		<div id="submitError" class="alertMessage" style="display:none"></div>
		<c:if test="${statusMessageKey != null}" >
			<div id="statusMessageKey" class="alertMessage" >
				<spring:message code="${statusMessageKey}"/>
			</div>
		</c:if>
		<c:if test="${errorMessage != null}" >
			<div id="errorMessage" class="alertMessage" >
				<spring:message code="${errorMessage}"/>
			</div>
		</c:if>
		<div style="position:relative; display:inline-block; margin-left:20px">
			<div id="tabledata" style="position:relative; float:left; max-width:30%; display:block">
				<table id="tFList" class="listHier lines nolines" summary="Template Fields">
					<thead>
						<tr>
							<th><spring:message code="form.label.field"/></th>
							<th><spring:message code="form.label.value"/><span class="reqStarInline">*</span></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${certificateToolState.templateFields}" var="tField" varStatus="index">
							<tr>
								<td>${tField.key}</td>
								<!--bbailla2-->
								<!--<td><form:input path="templateFields['${tField.key}']"/></td>-->
								<td>
									<form:select path="templateFields['${tField.key}']">
										<form:options items="${certificateToolState.predifinedFields}"/>
									</form:select>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div style="display:block; position:relative; margin:5px">
			<input id="back" type="button" value="<spring:message code="form.submit.back" />" />&nbsp;
			<!-- bbailla2 <input id="save" type="button" value="<spring:message code="form.submit.saveProgress"/>"/>&nbsp;-->
			<input id="next" type="button" value="<spring:message code="form.submit.next"/>"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="cancel" type="button" value="<spring:message code="form.submit.cancel"/>"/>
			<form:hidden path="submitValue" />
		</div>
	</div>
	</form:form>
</div>

<script type="text/javascript">

	$(document).ready(function() {

		loaded();
		
		$("#back").click(function(){
			back();
		});
		
		/* bbailla2 $("#save").click(function() {
			save();
		});*/
	
		$("#next").click(function() {
			next();
		});
		
		$("#cancel").click(function() {
			cancel();
		});
	
	});
	
	function back()
	{
		$("#submitValue").val("back");
		$("#createCertFormTwo").submit();
	}
	
	/* bbailla2 function save()
	{
		if(validateForm())
		{
			$("#submitValue").val("save");
			$("#createCertFormTwo").submit();
		}
	}*/
	
	function next()
	{
		if(validateForm())
		{
			$("#submitValue").val("next");
			$("#createCertFormTwo").submit();
		}
	}
	
	function cancel()
	{
		$("#submitValue").val("cancel");
		$("#createCertFormTwo").submit();
	}
	
	function validateForm() 
	{
		$(".alertMessage").hide();
		var error = false;
		var errHtml = "";

		//bbailla2
		//if(!$("input:text").val()) {
		if(!$("select").val()) {
			errHtml = errHtml + "<spring:message code="form.error.fieldValue"/>" + "</br>" ;
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
