<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="scripts/importPage.js"></script>
<style>
.center{
	text-align: center;
}
.myTr{
}
.controlWidth{
	width:100px;
}
.firstRow{
	height:50px;
	font-size:20px;
}
.inputControl{
	width:400px;
	display: inline;
	margin:5px 0
}
img{
	margin:10px;
}

img:hover{
	cursor:pointer
}
</style>
<div class="modal fade" id="importModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<div class="modal-title">
					请选择excel文件
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				</div>
			</div>
			<form enctype="multipart/form-data" id="excelImportForm">
				<div class="modal-body">
					<label>输入姓名以确认：</label>
					<input type="text" name="personInCharge" id="personInCharge2" class="form-control inputControl"/>
					<input type="file" name="file"/>
				</div>
				<div class="modal-footer">
						<input class="myButton" type="button" id="excelSubmit" value="确定提交"/>
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</form>
		</div>
	</div>
</div>
<body>
	<!-- 增加商品种类modal -->
	<div class="modal fade" id="myModal">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<div class="modal-title">
						增加商品种类
						<button class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
				</div>
				<div class="modal-body">
					<form id="newGoodsForm">
						<div>
							<label class="controlWidth">商品名称</label>
							<input type="text" name="goodsName" id="goodsName" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">品牌</label>
							<input type="text" name="brand" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">单位</label>
							<input type="text" name="unit" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">单位2</label>
							<input type="text" name="unit2" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">进制</label>
							<input type="text" name="hex" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">默认单价</label>
							<input type="text" name="defaultPrice" class="inputControl form-control"/>
						</div>
						<div>
							<label class="controlWidth">提醒数量</label>
							<input type="text" name="remindNum" class="inputControl form-control"/>
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<button class="myButton" id="newGoodsSubmit">确定提交</button>
				</div>
			</div>
		</div>
	</div>

	<form id="importForm">
		
		<table class="table table-striped" id="myTable">
			<tr class="firstRow">
				<th>商品名</th>
				<th>品牌</th>
				<th>单位</th>
				<th>数量</th>
				<th>单价</th>
				<th>总价</th>
			</tr>
			<tr class="myTr">
				<td>
					<select name="goodsName">
						<c:forEach items="${goodsList }" var="good">
							<option>${good.name }</option>
						</c:forEach>
					</select>
				</td>
				<td>
					<span>品牌</span>
				</td>
				<td>
					<select name="unit"></select>
				</td>
				<td>
					<input type="text" name="number"/>
					<input type="hidden" name="goodsID"/>
				</td>
				<td><input type="text" name="unitprice"/></td>
				<td><input type="text" name="amount"></td>
			</tr>
		</table>
		<div class="form-group center" style="margin:10px">
			<label for="personInCharge">输入姓名以确认：</label>
			<input type="text" id="personInCharge" name="personInCharge" class="form-control inputControl" style="width:200px">
			<img alt="导入Excel文件" src="images/excel.jpg"  height="40px" name="check" data-toggle="modal" data-target="#importModal">
		</div>
	</form>
	<div class="center">
		<img alt="增加商品种类" src="images/addtype.jpg"  height="40px" data-toggle="modal" data-target="#myModal">
		<img alt="新增一行" src="images/add.jpg"  height="40px" id="addRow">
		
		<img alt="确定提交" src="images/tick.jpg"  height="40px" id="importSubmit">
	</div>
</body>
</html>