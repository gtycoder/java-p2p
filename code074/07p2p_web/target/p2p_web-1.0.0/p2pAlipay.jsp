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
<h4>跳转load</h4>
<form action="http://localhost:8083/p2p_pay/api/alipay" method="post">
    <input type="hidden" name="out_trade_no" value="${out_trade_no}">
    <input type="hidden" name="total_amount" value="${total_amount}">
    <input type="hidden" name="subject" value="${subject}">
</form>
<script>document.forms[0].submit()</script>

</body>
</html>