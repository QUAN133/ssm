<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="scripts/recordPage.js"></script>
<style>
.control {
	width: 100px
}

#conditionForm {
	margin: 10px;
}

.firstRow {
	height: 50px;
	font-size: 20px;
}

.inputControl {
	width: 400px;
	display: inline;
	margin: 5px 0
}
</style>
<body>
	<div class="modal fade" id="detailModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<div class="modal-title">
						交易详情
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
				</div>
				<div class="modal-body">
					<table class="table table-bordered">
						<tbody id="DetailTable">
							<tr>
								<th>商品名</th>
								<th>品牌</th>
								<th>数量</th>
								<th>单位</th>
								<th>金额</th>
							</tr>
						</tbody>
					</table>
					<label>总价：</label> <label id="totalAmount"></label>
				</div>
				<div class="modal-footer">
					<form action="exportExcel.do">
						<input type="hidden" id="indiction" name="indiction" /> <input
							type="submit" class="myButton" id="exportExcel" value="导出为Excel" />
					</form>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" id="canUpdate" value="${loginInfo.canUpdate }"/>
	<h3 style="color: #3A3A3A">订单查询</h3>
	<div class="form-group" style="margin: 10px">
		<input type="text" placeholder="根据订单号查询" id="indictionInput"
			class="form-control inputControl"> <input type="button"
			class="myButton" id="findWithIndiction" value="搜索">
	</div>
	<form action="" id="conditionForm">
		<input type="hidden" id="pageNum2" name="pageNum" value="1"> <label>类型：</label>
		<select name="type">
			<option value="0">全部</option>
			<option value="1">出货</option>
			<option value="-1">入货</option>
		</select> <label class="control"></label> <label>开始时间：</label> <input
			type="date" name="startTime"> <label class="control"></label>
		<label>截至时间：</label> <input type="date" name="endTime"> <label
			class="control"></label> <label>排序：</label> <select name="sort">
			<option value="date ASC">时间</option>
			<option value="date DESC">时间倒序</option>
			<option value="userId ASC">操作者</option>
			<option value="userId DESC">操作者倒序</option>
		</select> <label class="control"></label> <input type="button" class="myButton"
			value="确定" id="submit">
	</form>
	<table class="table table-bordered">
		<tbody id="myTr">
			<tr class="firstRow">
				<th>订单号</th>
				<th>种类</th>
				<th>日期</th>
				<th>价格</th>
				<th>负责人</th>
				<th>操作者</th>
				<th></th>
			</tr>
			<c:forEach items="${recordList }" var="record">
				<tr>
					<td>${record.detailIndiction }</td>
					<td class="type">${record.type }</td>
					<td>${record.date }</td>
					<td>${record.price }</td>
					<td>${record.personInCharge }</td>
					<td>${record.username }</td>
					<td>
						<form action="updateRecord.do" target="content">
							<input type="hidden" value="${record.price }" /> <input
								type="hidden" name="indiction"
								value="${record.detailIndiction }" /> <a type="button"
								name="check" data-toggle="modal" data-target="#detailModal">查看明细</a>
							<a class="fix">修改</a> <a name="delete">删除</a>
						</form>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div id="pageControl">
		<input type="hidden" id="operation" value="0"/> 
		<input class="myButton" type="button" id="prevPage" value="<<" />
		<input type="text" id="pageNum" value="${pageNum }"/>
		<input class="myButton" type="button" id="go" value="跳转"/>
		<input class="myButton" type="button" id="nextPage" value=">>" />
	</div>
</body>
</html>