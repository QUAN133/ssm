<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="scripts/userPage.js"></script>
<style>
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
</style>
<body>
	<div class="modal fade" id="addUserModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<div class="modal-title">
						添加用户
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
					</div>
				</div>
				<div class="modal-body">
					<form id="newUserForm">
						<div>
							<label class="controlWidth">用户名:</label>
							<input type="text" name="username" class="form-control inputControl"/>
						</div>
						<div>
							<label class="controlWidth">密码:</label>
							<input type="text" name="password" class="form-control inputControl"/>
						</div>
						<div>
							<label class="controlWidth">权限:</label>
							<input type="checkbox" name="canQuery">查看
							<input type="checkbox" name="canInsert">插入
							<input type="checkbox" name="canUpdate">修改
							<input type="checkbox" name="role">管理员
						</div>
					</form>
				</div>
				<div class="modal-footer">
					<input type="button" id="addUserComfirm" class="btn btn-primary" value="确定添加"/>
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
		</div>
	</div>
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<form id="updateForm">
					<div class="modal-header">
						<div class="modal-title">
							修改用户信息
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
						</div>
					</div>
					<div class="modal-body">
							<input type="hidden" name="userId" id="userId">
							<div>
								<label class="controlWidth">用户名：</label>
								<input type="text" name="username" id="username" class="form-control inputControl"/>
							</div>
							<div>
								<label class="controlWidth">权利：</label>
								<input type="checkbox" value="canQuery" id="canQuery" name="canQuery"/>查看
								<input type="checkbox" value="canInsert" id="canInsert" name="canInsert"/>插入
								<input type="checkbox" value="canUpdate" id="canUpdate" name="canUpdate"/>修改
								<input type="checkbox" value="isManager" id="isManager" name="isManager"/>管理员
							</div>
					</div>
					<div class="modal-footer">
						<input type="button" id="updateSubmit" class="btn btn-primary" value="确定修改"/>
	<!-- 					<button id="updateSubmit" class="btn btn-primary">确定修改</button> -->
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<div>
	<h3 style="color:#3A3A3A">用户管理</h3>
		<label>用户数量：</label>
		<label id="number"></label>
<!-- 		<input class="btn btn-primary" type="button" value="添加用户" id="addUser"> -->
		<button class="myButton" name="fix" data-toggle="modal" data-target="#addUserModal">添加用户</button>
	</div>
	<input type="hidden" id="role" value="${loginInfo.role }">
	<table class="table table-bordered">
		<tbody id="myTable">
			<tr class="firstRow">
				<th>用户名</th>
				<th>权限</th>
				<th>修改</th>
			</tr>
			<c:forEach items="${userList }" var="user">
				<tr>
					<td>${user.username }</td>
					<td>
						<c:choose>
							<c:when test="${user.canQuery =='true'}">
								<input type="checkbox" checked="checked" onclick="return false;"/>
							</c:when>
							<c:otherwise>
								<input type="checkbox" onclick="return false;"/>
							</c:otherwise>
						</c:choose>
						查看
						<c:choose>
							<c:when test="${user.canInsert =='true'}">
								<input type="checkbox" checked="checked" onclick="return false;"/>
							</c:when>
							<c:otherwise>
								<input type="checkbox" onclick="return false;"/>
							</c:otherwise>
						</c:choose>
						插入
						<c:choose>
							<c:when test="${user.canUpdate =='true'}">
								<input type="checkbox" checked="checked" onclick="return false;"/>
							</c:when>
							<c:otherwise>
								<input type="checkbox" onclick="return false;"/>
							</c:otherwise>
						</c:choose>
						修改
						<c:choose>
							<c:when test="${user.role=='normal' }">
								<input type="checkbox" onclick="return false;">
							</c:when>
							<c:otherwise>
								<input type="checkbox" checked="checked" onclick="return false;">
							</c:otherwise>
						</c:choose>
						管理员
					</td>
					<td>
						<input type="hidden" name="id" value="${user.id }">
						<button class="myButton" name="fix" data-toggle="modal" data-target="#myModal">修改</button>
						<button class="myButton" name="delete">删除</button>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>