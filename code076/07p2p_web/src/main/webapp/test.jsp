<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2019/5/12
  Time: 17:12
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>
<head>
    <base href="<%=basePath%>">
    <title>practice</title>
</head>
<body>
<h2>Hello jsp!</h2>
<br>

<h4>${historyAvgRate}</h4><br>
<h4>${sumUserCount}</h4><br>

</body>
</html>