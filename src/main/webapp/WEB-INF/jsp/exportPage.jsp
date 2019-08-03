<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="scripts/exportPage.js"></script>
<style>
select{
	width:200px;
}
.center{
	text-align:center;
}
.mytd{
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
<body>
<div class="modal fade" id="exportModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<div class="modal-title">
					请选择excel文件
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				</div>
			</div>
			<form action="excelExportGoods.do" method="post" enctype="multipart/form-data" id="excelExportForm">
				<div class="modal-body">
					
					<label>输入姓名以确认：</label>
					<input type="text" name="personInCharge" class="form-control inputControl">
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
<form action="exportGoods.do" id="myForm">
	
	
	<table class="table table-striped" id="table">
		<tr class="firstRow">
			<th>商品名</th>
			<th>品牌</th>
			<th>单位</th>
			<th>数量</th>
			<th>单价</th>
			<th>金额</th>
			<th></th>
		</tr>
		<tr class="mytd">
			<td>
				<select name="select">
				<c:forEach items="${goodsList }" var="good">
				<option>${good.name }</option>
				</c:forEach>
				</select>
			</td>
			<td>
				<span>品牌</span>
			</td>
			<td>
				<select name="unit">
				</select>
			</td>
			<td>
				<input type="text" name="number"/>
				<input type="hidden" name="goodsID"/>
			</td>
			<td><input type="text" name="price" /></td>
			<td><input type="text" name="amount"/></td>
		</tr>
	</table>
	<div class="form-group center" style="margin:10px">
		<label for="PersonInCharge">输入姓名以确认：</label>
		<input type="text" id="PersonInCharge" name="PersonInCharge" class="form-control inputControl" style="width:200px">
		<img alt="导入Excel文件" src="images/excel.jpg"  height="40px" name="check" data-toggle="modal" data-target="#exportModal">
	</div>
	<div class="center">
		<img alt="新增一行" src="images/add.jpg"  height="40px" id="addRow">
		<img alt="确定提交" src="images/tick.jpg"  height="40px" id="submit">
	</div>
</form>
</div>
</body>
</html>