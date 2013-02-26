<%@ include file="/WEB-INF/jsp/include.jsp" %>
<jsp:include page="/WEB-INF/jsp/header.jsp"/>
<script type="text/javascript" src="/library/js/jquery/cookie/jquery.cookie.js"></script>
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

		<p id="requirementsHead" style="background: url(WEB-INF/images/down_arrow.gif) no-repeat left; display:inline; padding-left:17px; cursor:pointer;">
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

		<p id="displayOptionsHead" style="background: url(WEB-INF/images/down_arrow.gif) no-repeat left; display:inline; padding-left:17px; cursor:hand; cursor:pointer">
			<b><spring:message code="report.filter.head"/></b>
		</p>
		<div id="displayOptionsPanel">
			<div style="display:inline-block; background-color:#ddd; padding:10px">
				<span style="float:left;"> <spring:message code="report.filter.show"/>  </span>
				<div style="display:inline-block; margin-left: 1em;">
					<input id="rdAll" type="radio" name="show" value="all" onchange="$('#dateRange').css('display','none');" checked><spring:message code="report.filter.all"/></input><br/>
					<input id="idUnawarded" type="radio" name="show" value="unawarded" onchange="$('#dateRange').css('display','none');"><spring:message code="report.filter.unawarded"/></input><br/>
					<input id="idAwarded" type="radio" name="show" value="awarded" onchange="$('#dateRange').css('display','inline');"><spring:message code="report.filter.awarded"/></input><br/>
					<div id="dateRange" style="display:none;">
						<br/>
						<spring:message code="report.filter.awarded.1"/>
						<select id="filterDateType">
							<option value="issueDate"><spring:message code="report.filter.issuedate"/></option>
							<option value="expiryDate"><spring:message code="report.filter.expirydate"/></option>
						</select> 
						<spring:message code="report.filter.awarded.2"/> 
						<input id="startDate" type="text" style="background: url(WEB-INF/images/calendar.gif) #FFF no-repeat right; padding-right: 17px; width: 10em"/> 
						<spring:message code="report.filter.awarded.3"/>
						<input id="endDate" type="text" style="background: url(WEB-INF/images/calendar.gif) #FFF no-repeat right; padding-right: 17px; width: 10em"/>
					</div>
				</div>
				<br/>
				<input id="historical" type="checkbox" value="historical"><spring:message code="report.filter.historical"/></input>
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

		<input id="certificateId" type="button" style="display:none" value="${cert.id}"></input>

		<table id="reporttable" class="listHier" width="500px" cellspacing="2px" summary="Report">
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

			/*If the user expands/collapses elements, use cookies to keep track of this*/
			if ($.cookie("requirementsExpanded") == "false")
			{
				$("#requirementsPanel").hide();
				$("#requirementsHead").css("background","url(WEB-INF/images/right_arrow.gif) no-repeat left");
			}

			$("#requirementsHead").click(function()
			{
				$("#requirementsPanel").slideToggle(200, function() { resetHeight(); } );
				if ($.cookie('requirementsExpanded') == "false")
				{
					$("#requirementsHead").css("background","url(WEB-INF/images/down_arrow.gif) no-repeat left");
					$.cookie('requirementsExpanded', 'true');
				}
				else
				{
					$("#requirementsHead").css("background","url(WEB-INF/images/right_arrow.gif) no-repeat left");
					$.cookie('requirementsExpanded', 'false');
				}
			});

			if (!$.cookie('displayOptionsExpanded'))
			{
				$.cookie('displayOptionsExpanded',"false");
			}

			if ($.cookie('displayOptionsExpanded') == "false")
			{
				$("#displayOptionsHead").css("background","url(WEB-INF/images/right_arrow.gif) no-repeat left");
				$("#displayOptionsPanel").hide();
			}

			$("#displayOptionsHead").click(function()
			{
				$("#displayOptionsPanel").slideToggle(200, function() { resetHeight(); } );
				if ($.cookie('displayOptionsExpanded') == "false")
				{
					$("#displayOptionsHead").css("background","url(WEB-INF/images/down_arrow.gif) no-repeat left");
					$.cookie('displayOptionsExpanded', "true");
				}
				else
				{
					$("#displayOptionsHead").css("background","url(WEB-INF/images/right_arrow.gif) no-repeat left");
					$.cookie('displayOptionsExpanded', "false");
				}
			});

			$("#startDate").datepicker();
			$("#endDate").datepicker();
			$("#startDate").datepicker( "option", "dateFormat", "mm-dd-yy" );
			$("#endDate").datepicker( "option", "dateFormat", "mm-dd-yy" );

			/*Use cookies to keep track of the user's display options*/
			<c:choose>
				<c:when test="${useDefaultDisplayOptions == true}">
					/*We're using the defaults, so set the cookies*/
					var filterType = $("input[name='show']:checked").val();
					var filterDateType = $("#filterDateType option:selected").val();
					var filterStartDate = $("#startDate").val();
					var filterEndDate = $("#endDate").val();
					var filterHistorical = $("#historical").prop('checked');

					$.cookie("filterType", filterType);
					$.cookie("filterDateType", filterDateType);
					$.cookie("filterStartDate", filterStartDate);
					$.cookie("filterEndDate", filterEndDate);
					$.cookie("filterHistorical", filterHistorical);
				</c:when>
				<c:otherwise>
					/*We're not using the defaults, so use cookies*/
					var filterType = $.cookie("filterType");
					var filterDateType = $.cookie("filterDateType");
					var filterStartDate = $.cookie("filterStartDate");
					var filterEndDate = $.cookie("filterEndDate");
					var filterHistorical = $.cookie("filterHistorical");

					/*do a click event - this way the css on the dateRange will be applied*/
					$("input[name=show][value=" + filterType + "]").click();
					$("#filterDateType").val(filterDateType);
					$("#startDate").val(filterStartDate);
					$("#endDate").val(filterEndDate);
					if (filterHistorical == "true")
					{
						$("#historical").attr("checked", "checked");
					}
				</c:otherwise>
			</c:choose>

			
			$("#return").click( function() {
				location.href="list.form";
				return false;
			});

			var id = $("#certificateId").val();

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

				$.cookie("filterType", filterType);
				$.cookie("filterDateType", filterDateType);
				$.cookie("filterStartDate", filterStartDate);
				$.cookie("filterEndDate", filterEndDate);
				$.cookie("filterHistorical", filterHistorical);

				location.href="reportViewFilter.form?certId=" + id + "&filterType=" + filterType + "&filterDateType=" + filterDateType + "&filterStartDate=" + filterStartDate + "&filterEndDate=" + filterEndDate + "&filterHistorical=" + filterHistorical;
				return false;
			});

			$("#filterReset").click( function() {
				location.href="reportView.form?certId=" + id;
				return false;
			});
		});
	</script>		
<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
