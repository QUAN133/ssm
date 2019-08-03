<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品销售系统</title>
<link rel="stylesheet" href="bootstrap/bootstrap.min.css" />
<script type="text/javascript" src="scripts/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="bootstrap/bootstrap.min.js"></script>
<script type="text/javascript" src="scripts/basevalue.js"></script>
<script type="text/javascript" src="scripts/checkLogin.js"></script>
<script type="text/javascript" src="scripts/LetterToNum.js"></script>
<script type="text/javascript" src="scripts/mainPage.js"></script>
</head>
<style>
.myButton { 
	color:rgb(255, 255, 255);
	font-size:15px;
	padding-top:5px;
	padding-bottom:5px;
	padding-left:15px;
	padding-right:15px;
	border-width:0px;
	border-color:gray;
	border-style:solid;
	border-radius:5px;
	background-color:gray;
}
.myButton:hover{
	color:#ffffff;
	background-color:#3B3B3B;
	border-color:#3B3B3B;
}
html, body {
	height: 100%;
	
}

ul {
	list-style: none;
	padding: 25px
}

#Info{
	float:right
}

.controlMargin {
	margin-top: 10px;
	margin-left: 25px;
	margin-right: 25px;
}

.mydiv{
	background-color:#202020;
	color:#ffffff
}

#controlList {
	position:absolute;
	left:0;
	width: 200px;
	height: 100%;
	background-color:gray
	
}

#right {
	height: 100%;
	margin-left:200px
}

.myPanel{
	margin-top:10px;
}

.myBtn {
	line-height: 40px;
	height: 40px;
	width: 100%;
	color: #FFFFFF;
	font-size: 16px;
	font-weight: normal;
	font-family: Arial;
	border: 1px solid gray;
	-moz-box-shadow: inset 0px 0px 0px 0px gray;
	-webkit-box-shadow: inset 0px 0px 0px 0px gray;
	box-shadow: inset 0px 0px 0px 0px gray;
	text-align: center;
	display: inline-block;
	text-decoration: none;
	background-color:gray;
}

.myBtn:hover {
	background-color: #FFFFFF;
	color:black;
	cursor: pointer;
}

.logout {
	color: #ffffff;
}

.logout:hover {
	color:#ffffff
	cursor: pointer;
}
</style>
<body>
	<div class="panel-default mydiv">
		<div class="panel-body">
			<label>商品销售系统</label>
			<div id="Info">
				<label id="userInfo">欢迎你</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<a class="logout" id="logOut"><span class="glyphicon glyphicon-off" aria-hidden="true"></span>&nbsp;&nbsp;注销</a>
			</div>
		</div>
	</div>
	<div id="controlList">
		<div>
			<a class="myBtn" id="checkGoods">库存查询</a>
			<c:if test="${loginInfo.canInsert eq true }">
				<a class="myBtn" id="exportPage">出货</a>
				<a class="myBtn" id="importPage">入货</a>
			</c:if>
			<a class="myBtn" id="recordPage">交易记录</a>
			<c:if test="${loginInfo.role eq 'manager' }">
				<a class="myBtn" id="UserManage">员工管理</a>
			</c:if>
		</div>
	</div>
	<div id="right">
		
		<div id="content">
		</div>
	</div>
</body>
</html>