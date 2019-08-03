<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="scripts/updateRecord.js"></script>
<style>
.buttons{
	text-align:center;
}
</style>

<body>
<div style="margin:10px">
	<label>订单号：</label>
	<label>${indiction }</label>
</div>
<form action="" id="myForm">
<input type="hidden" name="indiction" value="${indiction }">
<table class="table table-bordered" id="myTable">
	<tr>
		<th>商品名</th>
		<th>品牌</th>
		<th>单位</th>
		<th>数量</th>
		<th>单价</th>
		<th>总价</th>
	</tr>
	<c:forEach items="${DetailList }" var="detail">
		<tr id="myTr">
			<td>
				<input type="hidden" name="goodsID" value="${detail.goodsID }"/>
				<input type="hidden" value="${detail.name }">
				<select name="select">
					<c:forEach items="${goodsList }" var="goods">
						<option>${goods.name }</option>
					</c:forEach>
				</select>
			</td>
			<td>
				<span>${detail.brand }</span>
			</td>
			<td>
				<select name="unit">
				</select>
			</td>
			<td>
				<input type="text" name="number" value="${detail.number }"/>
			</td>
			<td><input type="text" name="price" value="${detail.unitprice }"/></td>
			<td><input type="text" name="amount" value="${detail.amount }"/></td>
		</tr>
	</c:forEach>
</table>
</form>
<div class="buttons">
	<input class="myButton" type="button" value="增加一行" id="addRow"/>
	<input class="myButton" type="button" value="确定提交" id="submitForm"/>
</div>
</body>
</html>