/**
 * 
 */
$(function(){
	//修改数量信息
	updateNumber();
	//判断是否为管理员
	checkPower();
	//点击修改时修改表格信息
	$("[name='fix']").click(function(){
		fix($(this));
	})
	//提交修改
	$("#updateSubmit").click(function(){
		$.ajax({
			url:base_path+"/updateUserInfo.do",
			type:"post",
			data:$("#updateForm").serialize(),
			success:function(result){
				if(result.status==0){
					alert(result.msg);
					$('#myModal').modal('hide');
					reflashTable();
				}else{
					alert(result.msg);
				}
			},
			error:function(){
				alert("bug");
			}
		})
	})
	//添加用户
	$("#addUserComfirm").click(function(){
		$.ajax({
			url:base_path+"/addUser.do",
			type:"post",
			data:$("#newUserForm").serialize(),
			success:function(result){
				if(result.status==0){
					alert(result.msg);
					$('#addUserModal').modal('hide');
					reflashTable();
				}
			},
			error:function(){
				alert("bug");
			}
		})
	})
	//删除用户
	$("[name='delete']").click(function(){
		deleteUser($(this));
	})
})
//判断权限
function checkPower(){
	var role = $("#role").val();
	if(role=="normal"){
		$("#addUser").attr("disabled","disabled");
		$("[name='fix']").each(function(){
			$(this).attr("disabled","disabled");
		})
		$("[name='delete']").each(function(){
			$(this).attr("disabled","disabled");
		})
	}
}

//修改弹窗信息
function fix(button){
	//获取id
	var userId = button.prev().val();
	//获取username
	var username = button.parent().prev().prev().html();
	//power
	var canQuery = button.parent().prev().children()[0].checked;
	var canInsert = button.parent().prev().children()[1].checked;
	var canUpdate = button.parent().prev().children()[2].checked;
	var isManager = button.parent().prev().children()[3].checked;
	console.log("canQuery:"+canQuery);
	console.log("canInsert:"+canInsert);
	console.log("canUpdate:"+canUpdate);
	console.log("isManager:"+isManager);
	$("#userId").val(userId);
	$("#username").val(username);
	if(canQuery){
		$("#canQuery")[0].checked=true;
	}else{
		$("#canQuery")[0].checked=false;
	}
	if(canInsert){
		$("#canInsert")[0].checked=true;
	}else{
		$("#canInsert")[0].checked=false;
	}
	if(canUpdate){
		$("#canUpdate")[0].checked=true;
	}else{
		$("#canUpdate")[0].checked=false;
	}
	if(isManager){
		$("#isManager")[0].checked=true;
	}else{
		$("#isManager")[0].checked=false;
	}
}

//删除用户信息
function deleteUser(button){
	//获取id
	var id = button.prev().prev().val();
	$.ajax({
		url:base_path+"/deleteUser.do",
		type:"post",
		data:{
			"id":id
		},
		success:function(result){
			if(result.status==0){
				alert(result.msg);
				reflashTable();
			}
		},
		error:function(){
			alert("bug");
		}
	})
}
//修改总用户数量
function updateNumber(){
	$.ajax({
		url:base_path+"/getUserNumber.do",
		type:"post",
		success:function(result){
			if(result.status==0){
				$("#number").html(result.data);
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function reflashTable(){
	$table = $("#myTable");
	$.ajax({
		url:base_path+"/reflashTable.do",
		type:"post",
		success:function(result){
			if(result.status==0){
				//清空表格
				$table.empty();
				var colName = "<tr><th>用户名</th><th>权限</th><th>修改</th></tr>";
				$table.append(colName);
				let i=0;
				var button = "<button class=\"btn btn-default\" name=\"fix\" data-toggle=\"modal\" data-target=\"#myModal\">修改</button>";
				while(true){
					if(result.data[i]==null){
						break;
					}
					let id = result.data[i].id;
					let name = result.data[i].username.toString();
					let canQuery = result.data[i].canQuery;
					let canInsert = result.data[i].canInsert;
					let canUpdate = result.data[i].canUpdate;
					let role = result.data[i].role;
					let queryCheckBox,insertCheckBox,updateCheckBox,managerCheckBox;
					if(canQuery==true){
						queryCheckBox = `<input type="checkbox" checked="checked">查看`;
					}else{
						queryCheckBox = `<input type="checkbox">查看`;
					}
					if(canInsert==true){
						insertCheckBox = `<input type="checkbox" checked="checked">插入`;
					}else{
						insertCheckBox = `<input type="checkbox">插入`;
					}
					if(canUpdate==true){
						updateCheckBox = `<input type="checkbox" checked="checked">修改`;
					}else{
						updateCheckBox = `<input type="checkbox">修改`;
					}
					if(role=="normal"){
						managerCheckBox = `<input type="checkbox">管理员`;
					}else{
						managerCheckBox = `<input type="checkbox" checked="checked">管理员`;
					}
					let newLi = `<tr><td>`+name+`</td><td>`
					+queryCheckBox+insertCheckBox+updateCheckBox+managerCheckBox+
					`</td><td>
						<input type="hidden" name="id" value="`+id+`">
						<button class="btn btn-default" name="fix" data-toggle="modal" data-target="#myModal">修改</button>
						<button class="btn btn-danger" name="delete">删除</button>
					</td></tr>`;
					$table.append(newLi);
					i++;
				}
				$("[name='fix']").click(function(){
					fix($(this));
				})
			}else{
				alert(result.msg);
			}
		},
		error:function(){
			alert("bug");
		}
	})
}