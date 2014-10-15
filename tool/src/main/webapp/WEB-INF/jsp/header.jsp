<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<% response.setContentType("text/html; charset=UTF-8"); %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
   <head>
      <title><%= (String)request.getAttribute("_title")%></title>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <meta http-equiv="Content-Style-Type" content="text/css" />
      <link href="/osp-common-tool/css/eport.css" type="text/css" rel="stylesheet" media="all" />
      <%--link href="<c:out value="${sakai_skin_base}" />" type="text/css" rel="stylesheet" media="all" /--%>
      <%--link href="<c:out value="${sakai_skin}" />" type="text/css" rel="stylesheet" media="all" /--%>
<% 
org.sakaiproject.component.api.ServerConfigurationService scs = (org.sakaiproject.component.api.ServerConfigurationService) org.sakaiproject.component.cover.ComponentManager.get(org.sakaiproject.component.api.ServerConfigurationService.class);
String skinRepo = scs.getString( "skin.repo" );
String skin = scs.getString( "skin.default" );
String templates = scs.getString( "portal.templates", "neoskin" );
String prefix = scs.getString( "portal.neoprefix", "neo-" );
if ( "neoskin".equals( templates ) && !skin.startsWith( prefix ))
{
	skin = prefix + skin;
}
skin = skinRepo + "/" + skin;
%>
      <link media="all" href="<%= skinRepo %>/tool_base.css" rel="stylesheet" type="text/css" />
      <link media="all" href="<%= skin %>/tool.css" rel="stylesheet" type="text/css" />
      <link media="all" href="WEB-INF/css/jquery-ui.css" rel="stylesheet" type="text/css" />
      <link media="all" href="WEB-INF/css/certification.css" rel="stylesheet" type="text/css" />
      <script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js"></script>
      <script type="text/javascript" language="JavaScript" src="/osp-common-tool/js/eport.js"></script>
      <script type="text/javascript" language="JavaScript" src="WEB-INF/js/jquery-1.8.3.js"></script>
      <script type="text/javascript" language="JavaScript" src="WEB-INF/js/jquery-ui.js"></script>
      <script type="text/javascript" language="Javascript" src="/library/js/spinner.js"></script>
      <%
          String panelId = request.getParameter("panel");
          if (panelId == null) {
             panelId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
          }
      %>

      <script language="javascript">
         function resetHeight() 
         {
            setMainFrameHeightWithMax('<%= org.sakaiproject.util.Validator.escapeJavascript(panelId)%>', -1);
         }

         function loaded() {
            resetHeight();
            parent.updCourier(doubleDeep, ignoreCourier);
            if (parent.resetHeight) {
               parent.resetHeight();
            }
         }
      </script>
   </head>

<body >
    <div class="portletBody">
       <c:if test="${not empty requestScope.panelId}"><div class="ospEmbedded"></c:if>
