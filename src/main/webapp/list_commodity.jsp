<%--
  Created by IntelliJ IDEA.
  User: yyq20
  Date: 2023/9/11
  Time: 16:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Commodity List</title>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" />
    <link rel="stylesheet" href="css/list_commodity.css"/>
</head>
<body>
<c:set var="customer" value="${CUSTOMER}"/>
用户: ${customer.name}
<br/><br/>
余额: ￥${customer.balance}
<br/><br/>

<form action="ControllerServlet" method="GET">
    <input type="hidden" name="command" value="SEARCH"/>
    <div class="search-center-container">
        <div class="search-container">
            <input type="text" class="search-text" name="search_field"/>
            <input type="submit" class="material-symbols-outlined" value="search"/>
        </div>
    </div>
</form>

<div class="table-center-container">
    <table>
        <tr class="header-row">
            <th>商品名称</th>
            <th>价格</th>
            <th>库存</th>
            <th class="buy-header"></th>
        </tr>
        <c:forEach var="tempCommodity" items="${COMMODITY_LIST}">
            <tr class="data-row">
                <td>${tempCommodity.name}</td>
                <td>￥${tempCommodity.price}</td>
                <td>${tempCommodity.inventory}</td>
                <td>
                    <form action="ControllerServlet" method="GET" class="buy-button">
                        <input type="hidden" name="command" value="BUY"/>
                        <input type="hidden" name="itemID" value="${tempCommodity.id}"/>
                        <input type="submit" value="购买"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<br/><br/>
<form action="ControllerServlet" method="GET">
    <input type="hidden" name="command" value="RESET"/>
    <input type="submit" value="重置"/>
</form>
<br/>
<a href="ControllerServlet">返回主菜单</a>
</body>
</html>
