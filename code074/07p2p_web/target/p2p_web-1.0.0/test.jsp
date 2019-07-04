<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<h4>${financeAccount.availableMoney}</h4><br>
<c:forEach items="${bidInfoList}" var="bil">
    <h4>${bil.loanInfo.productName}</h4>
</c:forEach>

</body>
</html>