<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
	<form:form id="reportView" method="POST">
		<div class="navIntraTool">
				<a href="${toolUrl}/reportView.form?certId=${cert.id}&export=true" id="export"><spring:message code="export.csv"/></a>&nbsp;
				<a href="" id="return"><spring:message code="return.cert.list"/></a>
                </div>
		<h2><spring:message code="report.header" arguments="${cert.name}" htmlEscape="true"/></h2>
		
		<c:forEach items="${errors}" var="error">
			<div class="alertMessage">
				${error}
			</div>
		</c:forEach>

		<p id="requirementsHead" style="background: url(/library/skin/neo-default/images/tab-arrow-up.gif) no-repeat left; display:inline; padding-left:17px; cursor:pointer;">
			<b><spring:message code="report.requirements"/></b>
		</p>
		<div id="requirementsPanel">
			<ul style="margin-bottom:0px;">
				<c:forEach items="${requirements}" var="requirement">
					<li>${requirement}</li>
				</c:forEach>
			</ul>
		</div>

		<c:if test="${expiryOffset != null}">
			<p><spring:message code="report.disclaimer" arguments="${expiryOffset}" /></p>
		</c:if>

		<p id="displayOptionsHead" style="background: url(/library/skin/neo-default/images/tab-arrow-down-active.gif) no-repeat left; display:inline; padding-left:17px; cursor:hand; cursor:pointer">
			<b>Display Options</b>
		</p>
		<div id="displayOptionsPanel">
			<div style="display:inline-block; background-color:#ddd; padding:10px">
				<span style="float:left;"> Show:  </span>
				<div style="display:inline-block; margin-left: 1em;">
					<input type="radio" name="show" value="all" onchange="$('#dateRange').css('display','none');" checked>All participants (uni18n)</input><br/>
					<input type="radio" name="show" value="unawarded" onchange="$('#dateRange').css('display','none');">Unawarded participants only (uni18n)</input><br/>
					<input type="radio" name="show" value="awarded" onchange="$('#dateRange').css('display','inline');">Awarded participants only (uni18n)</input><br/>
					<div id="dateRange" style="display:none;">
						<br/>
						Show results where the 
						<select id="filterDateType">
							<option value="issueDate">Issue Date</option>
							<option value="expiryDate">Expiry Date</option>
						</select> 
						is between <input id="startDate" type="text"/> and <input id="endDate" type="text"/>
					</div>
				</div>
				<br/>
				<input id="historical" type="checkbox" value="historical">Display records for users who are no longer participants of this site (uni18n)</input>
				<br/>
				<br/>
				<div style="float:right;">
					<input id="filterApply" type="submit" value="Apply"/>
					<input id="filterReset" type="submit" value="Reset"/>
				</div>
			</div>
			<br/>
		</div>
		<br/>

		<p class="viewNav"><spring:message code="report.blurb"/></p>


                        <div class="listNav">
                                <div class="pager">
                                        <span style="align:center"><spring:message code="form.pager.showing"/>&nbsp;<c:out value="${firstElement}" />&nbsp;&#045;&nbsp;<c:out value="${lastElement}" />&nbsp;of&nbsp;${reportList.nrOfElements}</span><br/>
                                        <c:choose>
                                                <c:when test="${!reportList.firstPage}">
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
                                                <c:when test="${!reportList.lastPage}">
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


		<table id="reporttable" class="listHier" width="500px" cellspacing="2px" summary="Report" certificateid="${cert.id}">
			<thead align="center">
				<tr>
					<!-- the columns need to be in a c:for -->
					<!-- create the headers in an array from java code -->
					<th><spring:message code="report.table.header.name"/></th>
					<th><spring:message code="report.table.header.userid"/></th>
					<th><spring:message code="report.table.header.role"/></th>
					<c:forEach items="${userPropHeaders}" var="prop">
						<th>${prop}</th>
					</c:forEach>
					<th><spring:message code="report.table.header.issuedate"/></th>
					<c:forEach items="${critHeaders}" var="crit">
						<th>${crit}</th>
					</c:forEach>
					<th><spring:message code="report.table.header.awarded"/></th>
				</tr>
			</thead>

			<tbody align="left">
			<c:forEach var="row" items="${reportList.pageList}">
				<tr>
					<td>${row.name}</td>
					<td>${row.userId}</td>
					<td>${row.role}</td>
					<c:forEach var="prop" items="${row.extraProps}">
						<td>${prop}</td>
					</c:forEach>
					<td>${row.issueDate}</td>
					<c:forEach var="criterionCell" items="${row.criterionCells}">
						<td>${criterionCell}</td>
					</c:forEach>
					<td>${row.awarded}</td>
				</tr>
			</c:forEach>
		</table> 
		<%--
		<c:choose>
		<c:when test="${empty reportList}">
			TODO
		</c:when>
		<c:otherwise>
		</c:otherwise>
		</c:choose>
		--%>
	</form:form>

	<script type="text/javascript">

		$(document).ready(function() 
		{
			loaded();

			var requirementsExpanded=true;
			$("#requirementsHead").click(function()
			{
				$("#requirementsPanel").slideToggle(200);
				requirementsExpanded=!requirementsExpanded;
				if (requirementsExpanded)
				{
					$("#requirementsHead").css("background","url(/library/skin/neo-default/images/tab-arrow-up.gif) no-repeat left");
				}
				else
				{
					$("#requirementsHead").css("background","url(/library/skin/neo-default/images/tab-arrow-down-active.gif) no-repeat left");
				}
				resetHeight();
			});
			$("#displayOptionsPanel").hide();

			var displayExpanded=false;
			$("#displayOptionsHead").click(function()
			{
				$("#displayOptionsPanel").slideToggle(200);
				displayExpanded=!displayExpanded;
				if (displayExpanded)
				{
					$("#displayOptionsHead").css("background","url(/library/skin/neo-default/images/tab-arrow-up.gif) no-repeat left");
				}
				else
				{
					$("#displayOptionsHead").css("background","url(/library/skin/neo-default/images/tab-arrow-down-active.gif) no-repeat left");
				}
				resetHeight();
			});

			$("#startDate").datepicker();
			$("#endDate").datepicker();
			
			$("#return").click( function() {
				location.href="list.form";
				return false;
			});

			var id = $("#reporttable").attr("certificateid");

			$("#first").click( function() {
				location.href="reportView.form?certId=" + id + "&page=first";
				return false;
			});
			
			$("#prev").click( function() {
				location.href="reportView.form?certId=" + id + "&page=previous";
				return false;
			});
			
			$("#next").click( function() {
				location.href="reportView.form?certId=" + id + "&page=next";
				return false;
			});
			
			$("#last").click( function() {
				location.href="reportView.form?certId=" + id + "&page=last";
				return false;
			});
			
			$("#pageSize").change( function() {
				location.href="reportView.form?certId=" + id + "&pageSize=" + $("#pageSize option:selected").val() +" &pageNo=" + $("#pageNo").val();
				return false;
			});

			$("#filterApply").click( function() {
				var filterType = $("input[name='show']:checked").val();
				var filterDateType = $("#filterDateType option:selected").val();
				var filterStartDate = $("#startDate").val();
				var filterEndDate = $("#endDate").val();
				var filterHistorical = $("#historical").prop('checked');
				location.href="reportViewFilter.form?certId=" + id + "&filterType=" + filterType + "&filterDateType=" + filterDateType + "&filterStartDate=" + filterStartDate + "&filterEndDate=" + filterEndDate + "&filterHistorical=" + filterHistorical;
				return false;
			});

			$("#filterReset").click( function() {
				location.href="reportViewFilter.form?certId=" + id;
				return false;
			});
		});
	</script>		
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
