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
<h4>正在跳转至支付界面  load........</h4>
${result}
</body>
</html>