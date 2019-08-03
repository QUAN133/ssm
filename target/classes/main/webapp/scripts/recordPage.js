/**
 * 
 */
$(function(){
	//将-1和1改成入货和出货
	showType();
	//查看详细
	$("[name='check']").click(function(){
		resetTable();
		checkDetail($(this));
		updateTableInfo($(this).prev().prev().val());
	})
	//筛选
	$("#submit").click(function(){
		submitForm();
	})
	$("[name='delete']").click(deleteAction = function(){
		var canUpdate = $("#canUpdate").val();
		if(canUpdate=="false"){
			alert("没有权限");
			return;
		}
		var indiction = $(this).prev().prev().prev().val();
		$tr = $(this).parent().parent().parent();
		deleteRecord(indiction,$tr);
	})
	$(".fix").click(fixAction = function(){
		var canUpdate = $("#canUpdate").val();
		if(canUpdate=="false"){
			alert("没有权限");
			return;
		}
		var indiction = $(this).prev().prev().val();
		$("#content").load("http://localhost:8080/sale/updateRecord.do?indiction="+indiction);
	})
	//下一页
	$("#nextPage").click(function(){
		let operation = $("#operation").val();
		let pageNum = $("#pageNum").val();
		pageNum++;
		if(operation==0){
			page(pageNum);
		}else{
			$("#pageNum2").val(pageNum);
			submitForm();
		}
	})
	//上一页
	$("#prevPage").click(function(){
		let operation = $("#operation").val();
		let pageNum = $("#pageNum").val();
		pageNum--;
		if(pageNum<1){
			pageNum=1;
		}
		if(operation==0){
			page(pageNum);
		}else{
			$("#pageNum2").val(pageNum);
			submitForm();
		}
	})
	//根据订单号查询
	$("#findWithIndiction").click(function(){
		//获取搜索订单号
		var indiction = $("#indictionInput").val();
		if(indiction==""){
			alert("请先输入订单号");
			return;
		}
		$.ajax({
			url:base_path+"/findWithIndiction.do",
			type:"post",
			data:{
				"indiction":indiction
			},
			success:function(result){
				if(result.status==0){
					//根据结果集刷新页面
					reflashTable(result);
				}else{
					alert(result.msg);
					reflashTable(result);
				}
			},
			error:function(){
				alert("bug");
			}
		})
	})
})
function page(pageNum){//传过来的是目标页面
	$.ajax({
		url:base_path+"/selectRecordsWithPage.do",
		type:"post",
		data:{
			"pageNum":pageNum
		},
		success:function(result){
			reflashTable(result);
			let pageNum = result.status;
			$("#pageNum").val(pageNum);
		},
		error:function(){
			alert("bug");
		}
	})
}

function deleteRecord(indiction,tr){
	$.ajax({
		url:base_path+"/deleteRecord.do",
		type:"post",
		data:{
			"indiction":indiction
		},
		success:function(result){
			if(result.status==0){
				alert(result.msg);
				//删除当前行
				$tr.remove();
				window.parent.frames["mainTop"].location.reload();
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function submitForm(){
	$.ajax({
		url:base_path+"/selectRecord.do",
		type:"post",
		data:$("#conditionForm").serialize(),
		success:function(result){
			reflashTable(result);
			$("#pageNum").val(result.status);
			$("#pageNum2").val(result.status);
			$("#operation").val(1);
		},
		error:function(){
			alert("bug");
		}
	})
}

function reflashTable(result){
	//对表格进行操作
	//清空
	$("#myTr").empty();
	var colName = "<tr class=\"firstRow\">" +
			"<th>订单号</th>" +
			"<th>种类</th>" +
			"<th>日期</th>" +
			"<th>价格</th>" +
			"<th>负责人</th>" +
			"<th>操作者</th>" +
			"<th></th></tr>";
	//加入列名
	$("#myTr").append(colName);
	//循环加入每一行
	var i=0;
	while(true){
		if(result.data==null||result.data[i]==null){
			break;
		}
		let type = result.data[i].type;
		let date = result.data[i].date;
		let price = result.data[i].price;
		let username = result.data[i].username;
		let personInCharge = result.data[i].personInCharge;
		let detailIndiction = result.data[i].detailIndiction;
		let newLi = "<tr><td>"+detailIndiction+"</td>" +
				"<td class=\"type\">"+type+"</td>" +
				"<td>"+date+"</td>" +
				"<td>"+price+"</td>" +
				"<td>"+personInCharge+"</td>" +
				"<td>"+username+"</td>" +
				"<td><form action=\"updateRecord.do\">" +
				"<input type=\"hidden\" value=\""+price+"\"/>" +
				"<input type=\"hidden\" name=\"indiction\" value=\""+detailIndiction+"\"/>" +
				"<a type=\"button\" name=\"check\" data-toggle=\"modal\" data-target=\"#detailModal\">查看明细</a>" +
				"<a class=\"fix\">修改</a>" +
				"<a name=\"delete\">删除</a></form></td>";
		$("#myTr").append(newLi);
		i++;
	}
	showType();
	$("[name='check']").click(function(){
		resetTable();
		checkDetail($(this));
		updateTableInfo($(this).prev().prev().val());
	})
	$("[name='delete']").click(deleteAction = function(){
		var canUpdate = $("#canUpdate").val();
		if(canUpdate=="false"){
			alert("没有权限");
			return;
		}
		var indiction = $(this).prev().prev().prev().val();
		$tr = $(this).parent().parent().parent();
		deleteRecord(indiction,$tr);
	})
	$(".fix").click(fixAction = function(){
		var canUpdate = $("#canUpdate").val();
		if(canUpdate=="false"){
			alert("没有权限");
			return;
		}
		var indiction = $(this).prev().prev().val();
		$("#content").load("http://localhost:8080/sale/updateRecord.do?indiction="+indiction);
	})
	//控制分页显示
	if(i==0){
		$("#pageControl").hide();
	}else{
		$("#pageControl").show();
	}
}

function showType(){
	$(".type").each(function(){
		if($(this).html()==1){
			$(this).html("出货");
		}else{
			$(this).html("入货");
		}
	})
}

function resetTable(){
	$("#DetailTable").empty();
	var s = "<tr><th>商品名</th>" +
			"<th>品牌</th>" +
			"<th>单位</th>" +
			"<th>数量</th>" +
			"<th>单价</th>" +
			"<th>金额</th></tr>";
	$("#DetailTable").append(s);
}

function checkDetail(target){
	var indiction = target.prev().val();
		$.ajax({
		url:base_path+"/checkRecord.do",
		type:"post",
		data:{
			"indiction":indiction,
		},
		success:function(result){
			if(result.status==0){
				$("#indiction").val(indiction);
				var i=0;
				var name;
				var brand;
				var unit;
				var number;
				var unitprice;
				var amount;
				while(true){
					if(result.data[i]==null){
						break;
					}
					name=result.data[i].name;
					brand = result.data[i].brand;
					unit=result.data[i].unit;
					number=result.data[i].number;
					unitprice=result.data[i].unitprice;
					amount=result.data[i].amount;
					//生成新的dom节点
					var newLi = "<tr><td>"+name+"</td>" +
							"<td>"+brand+"</td>" +
							"<td>"+unit+"</td>" +
							"<td>"+number+"</td>" +
							"<td>"+unitprice+"</td>" +
							"<td>"+amount+"</td></tr>"
					$("#DetailTable").append(newLi);
					i++;
				}
			}else{
				alert("??");
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function updateTableInfo(target){//将总数传过来即可，不用ajax
	//直接设置值即可
	$("#totalAmount").html(target);
}
