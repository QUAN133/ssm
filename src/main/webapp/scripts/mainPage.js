$(function(){
	$().ready(function(){
		$.ajax({
			url:base_path+"/checkLogin.do",
			type:"post",
			success:function(result){
				if(result.status!=0){
					window.location.href=base_path;
				}
			},
			error:function(){
				alert("bug");
			}
		})
		//注销
		$("#logOut").click(function(){
			window.parent.location.href=base_path+"/logout.do";
		})
		//预约医生
		$("#checkGoods").click(function () {
			$(this).css("background-color","#FFFFFF").css("color","#000000");
			$("#UserManage").css("background-color","gray").css("color","#FFFFFF");
			$("#exportPage").css("background-color","gray").css("color","#FFFFFF");
			$("#importPage").css("background-color","gray").css("color","#FFFFFF");
			$("#recordPage").css("background-color","gray").css("color","#FFFFFF");
			$("#content").load("http://localhost:8080/sale/checkGoods.do",function () {
				$("#controlList").css("height","100%");
			});
		})
		//查看预约
		$("#exportPage").click(function () {
			$(this).css("background-color","#FFFFFF").css("color","#000000");
			$("#UserManage").css("background-color","gray").css("color","#FFFFFF");
			$("#checkGoods").css("background-color","gray").css("color","#FFFFFF");
			$("#importPage").css("background-color","gray").css("color","#FFFFFF");
			$("#recordPage").css("background-color","gray").css("color","#FFFFFF");
			$("#content").load("http://localhost:8080/sale/exportPage.do",function () {
				$("#controlList").css("height","100%");
			});
		})
		//个人信息
		$("#importPage").click(function () {
			$(this).css("background-color","#FFFFFF").css("color","#000000");
			$("#UserManage").css("background-color","gray").css("color","#FFFFFF");
			$("#checkGoods").css("background-color","gray").css("color","#FFFFFF");
			$("#exportPage").css("background-color","gray").css("color","#FFFFFF");
			$("#recordPage").css("background-color","gray").css("color","#FFFFFF");
			$("#content").load("http://localhost:8080/sale/importPage.do",function () {
				$("#controlList").css("height","100%");
			});
		})
		//科室管理
		$("#recordPage").click(function () {
			$(this).css("background-color","#FFFFFF").css("color","#000000");
			$("#UserManage").css("background-color","gray").css("color","#FFFFFF");
			$("#checkGoods").css("background-color","gray").css("color","#FFFFFF");
			$("#exportPage").css("background-color","gray").css("color","#FFFFFF");
			$("#importPage").css("background-color","gray").css("color","#FFFFFF");
			$("#content").load("http://localhost:8080/sale/recordPage.do",function () {
				$("#controlList").css("height","100%");
			});
		})
		//医生管理
		$("#UserManage").click(function () {
			$(this).css("background-color","#FFFFFF").css("color","#000000");
			$("#recordPage").css("background-color","gray").css("color","#FFFFFF");
			$("#checkGoods").css("background-color","gray").css("color","#FFFFFF");
			$("#exportPage").css("background-color","gray").css("color","#FFFFFF");
			$("#importPage").css("background-color","gray").css("color","#FFFFFF");
			$("#content").load("http://localhost:8080/sale/UserManage.do",function () {
				$("#controlList").css("height","100%");
			});
		})
	})
})

