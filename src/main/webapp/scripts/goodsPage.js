$(function(){
	//设置提醒色
	remind();
	//GO
	$("#pageNum").blur(function(){
		$(this).val(letterToNum($(this).val()));
	})
	$("#go").click(function(){
		alert("go");
		//获取pageNum
		//根据operation进行分页操作
		var operation = $("#operation").val();
		var pageNum = $("#pageNum").val();
		if(operation==0){
			page(pageNum);
		}else{
			search(pageNum);
		}
	})
	//分页
	$("#nextPage").click(function(){
		var operation = $("#operation").val();
		var pageNum = $("#pageNum").val();
		pageNum++;
		if(operation==0){
			page(pageNum);
		}else{
			search(pageNum);
		}
	})
	$("#prevPage").click(function(){
		var operation = $("#operation").val();
		var pageNum = $("#pageNum").val();
		pageNum--;
		if(pageNum<=0){
			return;
		}
		if(operation==0){
			page(pageNum);
		}else{
			search(pageNum);
		}
	})
	//搜索
	$("#submitCondition").click(function(){
		search(1);
	})
	//点击修改时修改modal中信息
	addUpdateEvent();
	//提交修改
	$("#fixGoods").click(function(){
		//先进行修改
		submitFix();
		//隐藏
		$('#fixModal').modal('hide');
		//对页面进行刷新
		$("#condition").val("");
		search(1);
	})
})

function addUpdateEvent(){
	$("[name='fix']").click(fixAction = function(){
		var canUpdate = $("#canUpdate").val();
		console.log("canUpdate:"+canUpdate);
		console.log(canUpdate=="false");
		if(canUpdate=="false"){
			alert("没有权限");
			return;
		}
		console.log("?");
		//先获取商品id
		var goodsId = $(this).prev().val();
		//更改modal中的goodsId
		$("#goodsId").val(goodsId);
		updateForm(goodsId);
	})
	$("[name='check']").click(function(){
		//获取id
		var goodsId = $(this).prev().prev().val();
		//修改modal中的商品信息
		var goodsName = $(this).parent().prev().prev().prev().prev().prev().prev().html();
		var brand = $(this).parent().prev().prev().prev().prev().prev().html();
		$("#goodsInfo").html(goodsName+"("+brand+")&nbsp&nbsp&nbsp&nbsp的交易详情");
		//ajax查询详情
		$.ajax({
			url:base_path+"/goodsDetail.do",
			type:"post",
			data:{
				"goodsId":goodsId
			},
			success:function(result){
				if(result.status==0){
					//先清空表格
					$("#goodsDetailTable").empty();
					//加入表头
					var colName = 
					`<tr>
						<th>订单号</th>
						<th>负责人</th>
						<th>类型</th>
						<th>日期</th>
						<th>数量</th>
						<th>单价</th>
						<th>金额</th>
					</tr>`;
					$("#goodsDetailTable").append(colName);
					var i = 0;
					let indiction;
					let personInCharge;
					let type,type2;
					let date;
					let number;
					let unitprice;
					let amount;
					while(true){
						if(result.data[i]==null){
							break;
						}
						//先获取数据
						indiction = result.data[i].indiction;
						personInCharge = result.data[i].personInCharge;
						type = result.data[i].type;
						date = result.data[i].date;
						number = result.data[i].number;
						unitprice = result.data[i].unitprice;
						amount = result.data[i].amount;
						if(type==1){
							type2="出货";
						}else{
							type2="入货";
						}
						let row = 
						`<tr>
							<td>`+ indiction +`</td>
							<td>`+ personInCharge +`</td>
							<td>`+ type2 +`</td>
							<td>`+ date +`</td>
							<td>`+ number +`</td>
							<td>`+ unitprice +`</td>
							<td>`+ amount +`</td>
						</tr>`;
						$("#goodsDetailTable").append(row);
						i++;
					}
					if(i==0){
						$("#info").html("没有找到对应的交易记录");
					}
				}
			},
			error:function(){
				alert("bug");
			}
		})
	})
}

function page(pageNum){//输入目标页面
	$.ajax({
		url:base_path+"/selectGoodsWithPage.do",
		type:"post",
		data:{
			"pageNum":pageNum
		},
		success:function(result){
			reflashTable(result);
			//需要设置pageNum
			$("#pageNum").val(pageNum);
			if(result.status==0){
				$("#nextPage").show();
			}else{
				$("#nextPage").hide();
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function updateForm(id){
	$.ajax({
		url:base_path+"/updateForm.do",
		type:"post",
		data:{
			"id":id
		},
		success:function(result){
			if(result.status==0){
				//设置值
				$("#goodsName").val(result.data.name);
				$("#brand").val(result.data.brand);
				$("#unit").val(result.data.unit);
				$("#unit2").val(result.data.unit2);
				$("#hex").val(result.data.hex);
				$("#unitprice").val(result.data.defaultPrice);
				$("#remindNumber").val(result.data.remindNum);
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function submitFix(){
	$.ajax({
		url:base_path+"/fixGoods.do",
		type:"post",
		data:$("#fixForm").serialize(),
		success:function(result){
			if(result.status==0){
				alert(result.msg);
			}else{
				alert(result.msg);
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function remind(){
	//获取所有tr标签
	$("#myTable tr").each(function(){
		var remindNumber = $(this).children().get(6).innerHTML;
		var number = $(this).children().get(5).innerHTML;
		if(number-remindNumber<0){
			$(this).children(":first").children().attr("checked","checked");
		}
	})
}

function search(pageNum){
	var condition = $("#condition").val();
	$.ajax({
		url:base_path+"/selectGoods.do",
		type:"post",
		data:{
			"condition":condition,
			"pageNum":pageNum
		},
		success:function(result){
			reflashTable(result);
			$("#operation").val("1");
			$("#pageNum").val(pageNum);
			if(result.status==0){
				$("#nextPage").show();
			}else{
				$("#nextPage").hide();
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function reflashTable(result){
	$table = $("#myTable");
	//先清空表格
	$table.empty();
	//加入列名
	let colName = "<tr class=\"firstRow\"" +
			"><th>库存不足</th>" +
			"<th>商品名</th>" +
			"<th>品牌</th>" +
			"<th>单位</th>" +
			"<th>单价</th>" +
			"<th>数量</th>" +
			"<th>提醒数量</th>" +
			"<th>操作</th></tr>";
	$table.append(colName);
	var i=0;
	while(true){
		if(result.data[i]==null){
			break;
		}
		let id = result.data[i].id;
		let name = result.data[i].name;
		let brand = result.data[i].brand;
		let unit = result.data[i].unit;
		let defaultPrice = result.data[i].defaultPrice;
		let number = result.data[i].number;
		let remindNum = result.data[i].remindNum;
		let button = "<a name=\"fix\">修改</a>";
		let button2 = `<a type="button" name="check" data-toggle="modal" data-target="#checkModal" name="fix">查询相关交易</a>`;
		//对表格进行操作,增加一行
		let newLi = "<tr><td><input type=\"checkbox\" onclick=\"return false;\"/></td>" +
				"<td>"+name+"</td>" +
				"<td>"+brand+"</td>" +
				"<td>"+unit+"</td>" +
				"<td>"+defaultPrice+"</td>" +
				"<td>"+number+"</td>" +
				"<td>"+remindNum+"</td>" +
				"<td><input type=\"hidden\" value=\""+id+"\">"
				+button+"</td></tr>";
		$table.append(newLi);
		i++;
	}
	remind();
	addUpdateEvent();
}
