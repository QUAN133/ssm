/**
 * 
 */
$(function(){
	changeInfo($("[name='select']"));
	$td = $(".mytd");
	//控制添加一行
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
	
	//提交表单
	$("#submit").click(function(){
		//先判断是否已经输入负责人
		var personInCharge = $("#PersonInCharge").val();
		if(personInCharge==""){
			alert("请先输入负责人");
		}else{
			submitForm();
		}
	})
	//Excel
	$("#excelSubmit").click(function(){
		var personInCharge = $("#personInCharge2").val();
		if(personInCharge == ""){
			alert("请输入负责人");
		}else{
			uploadExcel();
		}
	})
});
function uploadExcel(){
	var formData = new FormData($("#excelExportForm")[0]);
	$.ajax({
		url:base_path+"/excelExportGoods.do",
		type:"post",
		data:formData,
		async: false,  //是否异步，默认true
        cache: false,  //是否缓存，默认true
        contentType: false,  
        processData: false, 
		success:function(result){
			if(result.status==0){
				alert(result.msg);
				$('#exportModal').modal('hide');
				window.parent.frames["mainTop"].location.reload();
			}else{
				alert(result.msg);
			}
		},
		error:function(){
			alert("bug");
		}
	})
}
//更新表格信息
function changeInfo(target){
	var name = target.val();
	var brandInfo = target.parent().next().children().get(0);
	var priceInput = target.parent().next().next().next().next().children().get(0);
	var unitSelect = target.parent().next().next().children();
	var numberInput = target.parent().next().next().next().children().get(0);
	var goodsIdInfo = target.parent().next().next().next().children().get(1);
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
//增加一行
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

//提交信息
function submitForm(){
	$.ajax({
		url:base_path+"/exportGoods.do",
		type:"post",
		data:$("#myForm").serialize(),
		success:function(result){
			if(result.status==0){
				alert(result.msg);
				window.parent.frames["mainTop"].location.reload();
			}else{
				alert(result.msg);
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function checkPower(){
	var canUpdate = $("#canUpdate").val();
	if(canUpdate=="false"){
		$(".fix").unbind("click",fixAction);
		$("[name='delete']").unbind("click",deleteAction);
		$(".fix").click(function(){
			alert("没有权限");
		})
		$("[name='delete']").click(function(){
			alert("没有权限");
		})
	}
}