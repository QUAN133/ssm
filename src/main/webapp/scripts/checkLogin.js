/**
 * 
 */
$(function(){
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
})