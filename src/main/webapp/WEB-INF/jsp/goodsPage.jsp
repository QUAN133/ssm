<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="scripts/goodsPage.js"></script>
<style>
a{
	margin-right:10px;
}
#select{
	margin: 10px 10px;
}
#condition{
	margin-right: 10px;
}
.controlWidth{
	width:100px;
}
.inputControl{
	width:400px;
	display: inline;
	margin:5px 0
}
.firstRow{
	height:50px;
	font-size:20px;
}
</style>
<body>
	<div class="modal fade" id="fixModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<div class="modal-title">
						修改商品信息
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					</div>
				</div>
				<div class="modal-body">
					<form id="fixForm">
						<input type="hidden" name="goodsId" id="goodsId">
						<div>
							<label class="controlWidth" for="goodsName">商品名：</label>
							<input type="text" name="goodsName" id="goodsName" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">品牌：</label>
							<input type="text" name="brand" id="brand" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">单位：</label>
							<input type="text" name="unit" id="unit" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">单位2：</label>
							<input type="text" name="unit2" id="unit2" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">进制：</label>
							<input type="text" name="hex" id="hex" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">单价：</label>
							<input type="text" name="unitprice" id="unitprice" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">提醒数量：</label>
							<input type="text" name="remindNumber" id="remindNumber" class="inputControl form-control"/>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<input type="button" class="btn btn-info" id="fixGoods" value="确定修改"/>
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="checkModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<div class="modal-title">
						<span id="goodsInfo"></span>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					</div>
				</div>
				<div class="modal-body">
					
					<table class="table table-striped">
						<tbody id="goodsDetailTable">
							<tr>
								<th>订单号</th>
								<th>负责人</th>
								<th>类型</th>
								<th>日期</th>
								<th>数量</th>
								<th>单价</th>
								<th>金额</th>
							</tr>
						</tbody>
					</table>
					<span id="info"></span>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<input type="hidden" id="canUpdate" value="${loginInfo.canUpdate }"/>
<div id="select">
	<h3 style="color:#3A3A3A">库存查询</h3>
	<input type="text" id="condition" class="inputControl form-control" placeholder="请输入搜索条件"/>
	<input class="myButton" type="button" id="submitCondition" value="搜索"/>
</div>
<table class="table table-bordered mytable">
	<tbody id="myTable">
		<tr class="firstRow">
			<th>库存不足</th>
			<th>商品名</th>
			<th>品牌</th>
			<th>单位</th>
			<th>单价</th>
			<th>数量</th>
			<th>提醒数量</th>
			<th>操作</th>
		</tr>
		<c:forEach items="${goodsList}" var="good">
			<tr>
				<td><input type="checkbox" onclick="return false;"/></td>
				<td>${good.name }</td>
				<td>${good.brand }</td>
				<td>${good.unit }</td>
				<td>${good.defaultPrice }</td>
				<td>${good.number }</td>
				<td>${good.remindNum }</td>
				<td>
					<input type="hidden" value="${good.id }">
					<a name="fix">修改</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<input type="hidden" id="operation" value="${operation }">
<input class="myButton" type="button" id="prevPage" value="<<"/>
<input type="text" id="pageNum" value="${pageNum }">
<input class="myButton" type="button" id="go" value="跳转"/>
<input class="myButton" type="button" id="nextPage" value=">>"/>
</body>
</html>