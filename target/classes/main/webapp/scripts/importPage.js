/**
 * 
 */
$(function(){
	updateImportTable($("select[name='goodsName']"));
	$myTable = $("#myTable");
	$myTr = $(".myTr");
	$("#addRow").click(function(){
		addRow();
	})
	//增加物品种类
	$("#newGoodsSubmit").click(function(){
		newGoods();
	})
	//提交入货
	$("#importSubmit").click(function(){ 
		var personInCharge = $("#personInCharge").val();
		if(personInCharge==""){
			alert("请输入负责人");
		}else{
			submitForm();
		}
	})
	//选项改变
	$("select[name='goodsName']").change(function(){
		updateImportTable($(this));
	})
	//数量失去焦点
	$("[name='number']").blur(function(){
		var result = letterToNum($(this).val());
		$(this).val(result);
		calculate($(this).parent().prev().prev().prev().children());
	})
	//单价失去焦点
	$("[name='unitprice']").blur(function(){
		var result = letterToNum($(this).val());
		$(this).val(result);
		calculate($(this).parent().prev().prev().prev().prev().children());
	})
	//提交excel
	$("#excelSubmit").click(function(){
		var personInCharge = $("#personInCharge2").val();
		if(personInCharge==""){
			alert("请输入负责人")
		}else{
			uploadExcel();
		}
	})
})
function uploadExcel(){
	var formData = new FormData($("#excelImportForm")[0]);
	$.ajax({
		url:base_path+"/importWithExcel.do",
		type:"post",
		data:formData,
		async: false,  //是否异步，默认true
        cache: false,  //是否缓存，默认true
        contentType: false,  
        processData: false, 
		success:function(result){
			if(result.status==0){
				alert(result.msg);
				$('#importModal').modal('hide');
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
function addRow(){
	$newLi = $myTr.clone(true);
	$myTable.append($newLi);
}

function newGoods(){
	$.ajax({
		url:base_path+"/newGoods.do",
		type:"post",
		data:$("#newGoodsForm").serialize(),
		success:function(result){
			if(result.status==0){
				alert(result.msg);
				$('#myModal').modal('hide');
				addOption();
			}else{
				alert(result.msg);
			}
		},
		error:function(){
			alert("请正确输入");
		}
	})
}

function calculate(target){
	//传进来的select
	var num = target.parent().next().next().next().children().val();
	var price = target.parent().next().next().next().next().children().val();
	var amount = target.parent().next().next().next().next().next().children().get(0);
	amount.value = num*price;
}

function updateImportTable(target){
	var unitSelect = target.parent().next().next().children();
	var brandInfo = target.parent().next().children().get(0);
	var goodsIDInput = target.parent().next().next().next().children().get(1);//id
	var numberInput = target.parent().next().next().next().children().get(0);//数量
	var price = target.parent().next().next().next().next().children().get(0);//价格
	var goodsName = target.val();
	$.ajax({
		url:base_path+"/updateImportTable.do",
		type:"post",
		data:{
			"goodsName":goodsName
		},
		success:function(result){
			if(result.status==0){
				var brand = result.data.brand;
				var unit = result.data.unit;
				var unit2 = result.data.unit2;
				var goodsID = result.data.id;
				console.log(brand);
				brandInfo.innerHTML = brand;
				unitSelect.empty();
				unitSelect.append("<option>"+unit+"</option>");
				if(unit2!=null){
					unitSelect.append("<option>"+unit2+"</option>");
				}
				goodsIDInput.value=goodsID;
				numberInput.value=1;
				price.value=0;
				calculate(target);
			}
		},
		error:function(){
			alert("bug");
		}
	})
}

function submitForm(){
	$.ajax({
		url:base_path+"/importGoods.do",
		type:"post",
		data:$("#importForm").serialize(),
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

function addOption(name){
	var temp = $("#goodsName").val();
	$newLi = "<option>"+temp+"</option>";
	$("select").each(function(){
		$(this).append($newLi);
	})
}