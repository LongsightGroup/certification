<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
	<form:form id="reportView" method="POST">
		<div class="navIntraTool">
                        <a href="" id="Export"><spring:message code="export.csv"/></a>&nbsp;
                        <a href="" id="Return"><spring:message code="return.cert.list"/></a>
                </div>
		<c:choose>
		<c:when test="${empty reportList}">
			TODO
		</c:when>
		<c:otherwise>
		</c:otherwise>
		</c:choose>
	</form:form>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
