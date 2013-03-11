<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
		<form:form id="certList" method="POST">
		<c:choose>
		<c:when test="${certList.nrOfElements == 0}">
			<p class="instruction">
				<spring:message code="instructions.student"/>
			</p>
			<h3 class="instruction" style="text-align:center">
		 		<spring:message code="form.text.emptycertlist"/>
			</h3>
		</c:when>
		<c:otherwise>
        <c:forEach items="${unmetCriteria}" var="condition">
            <div id="unmetConditions" class="alertMessage">
                <spring:message code="error.unmet" arguments="${condition.expression}"/>
            </div>
        </c:forEach>
	    <c:if test="${errorMessage != null}" >
	        <div id="errorMessage" class="alertMessage" >
	            <spring:message code="${errorMessage}" arguments="${errorArgs}"/>
	        </div>
	    </c:if>
	<div class="instruction">
		<p><spring:message code="instructions.student"/></p>
	</div>
 		<div class="listNav">
			<div class="pager">
				<span style="align:center">showing&nbsp;<c:out value="${firstElement}" />&nbsp;&#045;&nbsp;<c:out value="${lastElement}" />&nbsp;of&nbsp;${certList.nrOfElements}</span></br>
				<c:choose>
				<c:when test="${!certList.firstPage}">
					<input type="button" id="first" value="<spring:message code="pagination.first"/>" />&nbsp;
					<input type="button" id="prev" value="<spring:message code="pagination.previous"/>" />
				</c:when>
				<c:otherwise>
					<input type="button" id="nofirst" value="<spring:message code="pagination.first"/>" disabled="disabled" />&nbsp;
					<input type="button" id="noPrev" value="<spring:message code="pagination.previous"/>" disabled="disabled" />
				</c:otherwise>
				</c:choose>
				<input type="hidden" id="pageNo" value="${pageNo}" />
				<select id="pageSize">
				<c:forEach items="${pageSizeList}" var="list">
					<c:choose>
					<c:when test="${list > 200}">
						<option value="${list}" <c:if test="${pageSize eq list}">selected="selected"</c:if>><spring:message code="form.label.showall" /></option>
					</c:when>
					<c:otherwise>
						<option value="${list}" <c:if test="${pageSize eq list}">selected="selected"</c:if>><spring:message code="form.label.show" arguments="${list}" /></option>
					</c:otherwise>
					</c:choose>
				</c:forEach>
				</select>
				<c:choose>
					<c:when test="${!certList.lastPage}">
						<input type="button" id="next" value="<spring:message code="pagination.next"/>" />&nbsp;
						<input type="button" id="last" value="<spring:message code="pagination.last"/>" />
					</c:when>
					<c:otherwise>
						<input type="button" id="noNext" value="<spring:message code="pagination.next"/>" disabled="disabled"/>
						<input type="button" id="noLast" value="<spring:message code="pagination.last"/>" disabled="disabled"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<table id="cList" class="listHier lines nolines" cellspacing="0" cellpadding="0" border="0" summary="Certificates">
			<thead>
				<tr>
				  <th><spring:message code="form.label.certificate"/></th>
                  <th><spring:message code="form.label.certificate.description"/></th>
				  <th><spring:message code="form.label.requirements"/></th>
				  <th><spring:message code="form.label.viewcert"/></th>
				</tr>
			</thead>
			<tbody>
	        	<c:forEach var="cert" items="${certList.pageList}">
	            <tr>
	            	<td>
	                	<c:out value="${cert.name}"></c:out>
	                </td>
                    <td>
                        <c:out value="${cert.description}"></c:out>
                    </td>


            <c:choose>
		<c:when test="${certRequirementList[cert.id] != null}">
			<td>
				<c:choose>
					<c:when test="${cert.progressHidden}">
						<span class="instruction">
							<spring:message code="form.label.requirements.hidden"/>
						</span>
					</c:when>
					<c:otherwise>
						<ul style="margin-top:0px; padding-left:14px">
							<c:forEach items="${certRequirementList[cert.id]}" var="req">
								<li>${req.key}</li>
								<ul>
									<li>${req.value}</li>
								</ul>
							</c:forEach>
						</ul>
					</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:choose>
					<c:when test="${certIsAwarded[cert.id]}">
						<a id="viewCert${cert.id}" href="${toolUrl}/print.form?certId=${cert.id}"><spring:message code="form.submit.print"/></a>
					</c:when>
					<c:otherwise>
						<spring:message code="form.submit.na"/>
					</c:otherwise>
				</c:choose>
			</td>
		</c:when>
            </c:choose>

	          	</tr>
	       		</c:forEach>
			</tbody>
		</table>
		</div>
		</c:otherwise>
		</c:choose>
		</form:form>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			
            loaded();
           
            $("#first").click( function() {
				location.href="list.form?page=first";
				return false;
			});
			
			$("#prev").click( function() {
				location.href="list.form?page=previous";
				return false;
			});
			
			$("#next").click( function() {
				location.href="list.form?page=next";
				return false;
			});
			
			$("#last").click( function() {
				location.href="list.form?page=last";
				return false;
			});
			
			$("#pageSize").change( function() {
				location.href="list.form?pageSize=" + $("#pageSize option:selected").val() +" &pageNo=" + $("#pageNo").val();
				return false;
			});
		});
		
	</script>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
