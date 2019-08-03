/**
 * 
 */
$(function() {
	$("#login").click(function(){
		var username=$("#username").val();
		var password=$("#password").val();
		//提交
		$.ajax({
			url:base_path+"/login.do",
			type:"post",
			data:{
				"username":username,
				"password":password
			},
			success:function(result){
				if(result.status==0){
					alert(result.msg);
					window.location.href=base_path+"/goMain.do";
				}else{
					alert(result.msg);
				}
			},
			error:function(){
				alert("bug");
			}
		});
	});
});