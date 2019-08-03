/**
 * 
 */
$(function(){
	$td = $(".mytd");
	updateUnits();
	//设置所有选项的默认值
	updateOption();
	$("#addRow").click(function(){
		addRow();
	});
	//选项改变时
	$("[name='select']").change(function(){
		changeInfo($(this));
	})
	//数量改变时
	$("[name='number']").blur(function(){
		var result = letterToNum($(this).val());
		$(this).val(result);
		$amount = $(this).parent().next().next().children();
		calculate($amount);
	})
	//单价改变时
	$("[name='price']").blur(function(){
		var result = letterToNum($(this).val());
		$(this).val(result);
		$amount = $(this).parent().next().children();
		calculate($amount);
	})
	//添加一行
	$("#addRow").click(function(){
		$newLi = $("#myTr").clone(true);
		$("#myTable").append($newLi);
	})
	//提交表单
	$("#submitForm").click(function(){
		$.ajax({
			url:base_path+"/updateDetail.do",
			type:"post",
			data:$("#myForm").serialize(),
			success:function(result){
				if(result.status==0){
					alert(result.msg);
				}
			},
			error:function(){
				alert("bug");
			}
		})
	})
})

function updateUnits(){
	$("[name='unit']").each(function(){
		var select = $(this);
		var goodsID = $(this).parent().prev().prev().children().val();
		//根据goodsID，取得商品信息
		$.ajax({
			url:base_path+"/updateUnits.do",
			type:"post",
			data:{
				"goodsID":goodsID
			},
			success:function(result){
				if(result.status==0){
					let unit = result.data[0];
					let unit2 = result.data[1];
					let newLi1 = "<option>"+unit+"</option>";
					let newLi2 = "<option>"+unit2+"</option>";
					console.log(unit);
					console.log(unit2);
					console.log(newLi1);
					console.log(newLi2);
					select.empty();
					select.append(newLi1);
					if(unit2!=""){
						select.append(newLi2);
					}
				}
			},
			error:function(){
				alert("bug");
			}
		})
	})
}

function updateOption(){
	//获取所有select标签
	$("select").each(function(){
		//获取前面一个的值
		var name = $(this).prev().val();
		//根据name寻找对应的选项
		$(this).children().each(function(){
			if(name==$(this).html()){
				$(this).attr("selected","selected");
			}
		})
	})
}

function changeInfo(target){
	var name = target.val();
	var brandInfo = target.parent().next().children().get(0);
	var priceInput = target.parent().next().next().next().next().children().get(0);
	var unitSelect = target.parent().next().next().children();
	var numberInput = target.parent().next().next().next().children().get(0);
	var goodsIdInfo = target.parent().children().get(0);
	$amount = target.parent().next().next().next().next().next().children();
	$.ajax({
		url:base_path+"/updateExportTable.do",
		type:"post",
		data:{
			"name":name
		},
		success:function(result){
			if(result.status==0){
				var brand = result.data.brand;
				var defaultPrice = result.data.defaultPrice;
				var goodsID = result.data.id;
				var unit = result.data.unit;
				var unit2 = result.data.unit2;
				var hex = result.data.hex;
				//设置品牌
				brandInfo.innerHTML = brand;
				//设置单位
				unitSelect.empty();
				unitSelect.append("<option>"+unit+"</option>");
				if(unit2!=null){
					unitSelect.append("<option>"+unit2+"</option>");
				}
				//设置数量为1
				numberInput.value = 1;
				//设置goodsID
				goodsIdInfo.value = goodsID;
				//设置默认价格
				priceInput.value=defaultPrice;
				calculate($amount);
			}
		},
		error:function(){
		}
	})
	
}
function addRow(){
	$newNode = $td.clone(true);
	$("#table").append($newNode);
}
//自动计算金额
function calculate(target){
	//获取数量
	var num = target.parent().prev().prev().children().get(0).value;
	//获取单价
	var price = target.parent().prev().children().get(0).value;
	var amount = num*price;
	target.val(amount);
}