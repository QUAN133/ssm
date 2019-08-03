<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="scripts/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="scripts/basevalue.js"></script>
<script type="text/javascript" src="scripts/mainTop.js"></script>
<script type="text/javascript" src="scripts/checkLogin.js"></script>
<link rel="stylesheet" href="bootstrap/bootstrap.min.css"/>
<script type="text/javascript" src="bootstrap/bootstrap.min.js"></script>
<style>
.info{
	float:right;
}
</style>
<body>
<label>商铺信息：  盈利：${possession.money }  入货支出：${possession.consume }  出货收入：${possession.earning }</label>
<label class="info">你的信息：${loginInfo.username}</label><br/>
<button class="btn btn-danger info" id="logout">注销</button>
</body>
</html>