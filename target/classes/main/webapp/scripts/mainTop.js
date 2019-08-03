/**
 * 
 */
$(function(){
	$("#logout").click(function(){
		window.parent.location.href=base_path+"/logout.do";
	});
});