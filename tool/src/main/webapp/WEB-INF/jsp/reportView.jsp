<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
	<form:form id="reportView" method="POST">
		<div class="navIntraTool">
                        <a href="" id="Export"><spring:message code="export.csv"/></a>&nbsp;
                        <a href="" id="Return"><spring:message code="return.cert.list"/></a>
                </div>
		<h2><spring:message code="report.header" arguments="${cert.name}"/></h2>
		<br>
		<br>
		<!-- TODO: put this in the c:choose -->
		<p><spring:message code="report.blurb"/></p>
		<table class="listHier" width="500px" cellspacing="2px" summary="Report">
			<thead align="center">
				<tr>
					<!-- the columns need to be in a c:for -->
					<!-- create the headers in an array from java code -->
					<th><spring:message code="report.table.header.name"/></th>
					<th><spring:message code="report.table.header.userid"/></th>
					<!-- TODO: this is western specific -->
					<th><spring:message code="report.table.header.employeenum"/></th>
					<th><spring:message code="report.table.header.issuedate"/></th>
					<c:forEach items="${headers}" var="tableHeader">
						<th>${tableHeader}</th>
					</c:forEach>
				</tr>
			</thead>
		</table> 
		<!--
		<c:choose>
		<c:when test="${empty reportList}">
			TODO
		</c:when>
		<c:otherwise>
		</c:otherwise>
		</c:choose>
		-->
	</form:form>
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
