package main.java.controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import main.java.entity.*;
import main.java.service.IService;
import main.java.util.DetailResult;
import main.java.util.RecordResult;
import main.java.util.Result;

@org.springframework.stereotype.Controller
public class Controller {
	@Autowired
	IService service;
	HttpSession session = null;
	private int numInPage = 10;
	
	//登录注销等操作--------------------------------------------------------------------------------------
	//登录
	@RequestMapping("login")
	@ResponseBody
	public Result login(HttpServletRequest request){
		//获取登录信息
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		//验证
		Result result = service.login(username,password);
		if(result.getStatus()==0){
			//需要先将登录信息加入session
			session = request.getSession();
			User user = (User)result.getData();
			if(!user.isCanQuery()){
				result.setStatus(1);
				result.setData(null);
				result.setMsg("该用户权限不足，请联系管理员");
			}
			session.setAttribute("loginInfo", user);
		}
		this.possession(request);
		return result;
	}
	//注销登录
	@RequestMapping("logout")
	public String logout(HttpServletRequest request){
		session = request.getSession();
		session.removeAttribute("loginInfo");
		session.removeAttribute("posssession");
		return "../../index";
	}
	//检查登录信息
	@RequestMapping("checkLogin")
	@ResponseBody
	public Result checkLogin(HttpServletRequest request){
		session = request.getSession();
		Result result = new Result();
		User loginInfo = (User) session.getAttribute("loginInfo");
		if(loginInfo==null){
			//未登录
			result.setStatus(1);
			result.setMsg("请先登录");
		}else{
			//已登录
			result.setStatus(0);
		}
		return result;
	}
	//库存查询-------------------------------------------------------------------------------------------
	@RequestMapping("checkGoods")
	public String checkGoods(HttpServletRequest request){
		List<Goods> goodsList = service.findAllGoodsWithPage(1, numInPage);
		request.setAttribute("operation", "0");
		request.setAttribute("pageNum", 1);
		request.setAttribute("goodsList", goodsList);
		return "goodsPage";
	}
	
	@RequestMapping("selectGoodsWithPage")
	@ResponseBody
	public Result selectGoodsWithPage(HttpServletRequest request){
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));
		if(pageNum<1){
			pageNum=1;
		}
		return service.selectGoodsWithPage(pageNum, numInPage);
	}
	
	//库存检索
	@RequestMapping("selectGoods")
	@ResponseBody
	public Result selectGoods(HttpServletRequest request){
		String condition = request.getParameter("condition");
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));
		if(pageNum<1){
			pageNum=1;
		}
		Result result = service.selectGoods(condition, pageNum,numInPage);
		return result;
	}

	@RequestMapping("updateForm")
	@ResponseBody
	public Result updateForm(HttpServletRequest request){
		Result result = new Result();
		int id = Integer.parseInt(request.getParameter("id"));
		List<Goods> goodsList = service.findAllGoods();
		Goods goods;
		for(int i=0;i<goodsList.size();i++){
			goods = goodsList.get(i);
			if(id==goods.getId()){
				result.setStatus(0);
				result.setMsg("查找成功");
				result.setData(goods);
				return result;
			}
		}
		result.setStatus(1);
		result.setData("搜索结果不存在");
		return result;
	}
	
	//商品修改
	@RequestMapping("fixGoods")
	@ResponseBody
	public Result fixGoods(HttpServletRequest request){
		String id = request.getParameter("goodsId");
		String goodsName = request.getParameter("goodsName");
		String brand = request.getParameter("brand");
		String unit = request.getParameter("unit");
		String unit2 = request.getParameter("unit2");
		String hex = request.getParameter("hex");
		String unitprice = request.getParameter("unitprice");
		String remindNum = request.getParameter("remindNumber");
		Goods goods = new Goods();
		goods.setId(Integer.parseInt(id));
		goods.setName(goodsName);
		goods.setBrand(brand);
		goods.setUnit(unit);
		goods.setUnit2(unit2);
		goods.setHex(Double.parseDouble(hex));
		goods.setDefaultPrice(Double.parseDouble(unitprice));
		goods.setRemindNum(Double.parseDouble(remindNum));
		return service.updateGoods(goods);
	}
	
	//查询某商品的交易详情
	@RequestMapping("goodsDetail")
	@ResponseBody
	public Result goodsDetail(HttpServletRequest request){
		int goodsId = Integer.parseInt(request.getParameter("goodsId"));
		System.out.println("goodsId:"+goodsId);
		return service.goodsDetail(goodsId);
	}
	
	//出货--------------------------------------------------------------------------------------------------
	//通过excel出货
	@RequestMapping("excelExportGoods")
	@ResponseBody
	public Result excelExportGoods(HttpServletRequest request,Model model) throws Exception{
		String personInCharge = request.getParameter("personInCharge");
		System.out.println(personInCharge);
		//获取操作者
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginInfo");
		int userId = user.getId();
		//获取上传的文件
		MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
		MultipartFile file = multipart.getFile("file");
		InputStream in = file.getInputStream();
		Result result = service.exportWithExcel(userId, in, file, personInCharge);
		in.close();
		this.possession(request);
		return result;
	}
	
	//通过页面输入提交出货记录
	@RequestMapping("exportGoods")
	@ResponseBody
	public Result exportGoods(HttpServletRequest request){
		//获取操作者信息
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginInfo");
		int userId = user.getId();
		String[] units = request.getParameterValues("unit");
		String personInCharge = request.getParameter("PersonInCharge");
		System.out.println(personInCharge);
		String[] numbers = request.getParameterValues("number");
		String[] goodsIDs = request.getParameterValues("goodsID");
		String[] prices=request.getParameterValues("price");
		String[] amounts = request.getParameterValues("amount");
		List<Detail> details = new ArrayList<Detail>();
		//先获取当前时间
		Date date = new Date();
		String pattern = "yyyymmddhhmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String dateTime = sdf.format(date);
		for(int i=0;i<numbers.length;i++){
			Detail d = new Detail();
			d.setGoodsID(Integer.parseInt(goodsIDs[i]));
			d.setNumber(Double.parseDouble(numbers[i]));
			d.setUnitprice(Double.parseDouble(prices[i]));
			d.setIndiction(dateTime);
			d.setAmount(Double.parseDouble(amounts[i]));
			details.add(d);
		}
		//将details的数据传过去service
		Result result = service.export(userId,details, units, personInCharge);
		this.possession(request);
		return result;
	}
	
	//更新出货中的table信息
	@RequestMapping("updateExportTable")
	@ResponseBody
	public Result updateExportTable(HttpServletRequest request){
		System.out.println("------------------------------------");
		String goodsName = request.getParameter("name");
		System.out.println("获取到的商品名："+goodsName);
		return service.updateTable(goodsName);
	}
	
	//跳转到出货界面
	@RequestMapping("exportPage")
	public String inportPage(HttpServletRequest request){
		//需要将商品列表传过去
		List<Goods> goodsList = service.findAllGoods();
		request.setAttribute("goodsList", goodsList);
		return "exportPage";
	}
	
	//根据indicion返回详细的交易信息
	@RequestMapping("checkRecord")
	@ResponseBody
	public Result checkRecord(HttpServletRequest request){
		String indiction = request.getParameter("indiction");
		return service.checkRecord(indiction);
	}
	
	//入货-----------------------------------------------------------------------------------------------------
	//跳转到入货页面
	@RequestMapping("importPage")
	public String importPage(HttpServletRequest request){
		//将所有的商品类型传回去以做选项
		List<Goods> goodsList = service.findAllGoods();
		request.setAttribute("goodsList", goodsList);
		return "importPage";
	}
	//新增商品种类
	@RequestMapping("newGoods")
	@ResponseBody
	public Result newGoods(HttpServletRequest request){
		String goodsName = request.getParameter("goodsName");
		String brand = request.getParameter("brand");
		String unit = request.getParameter("unit");
		String unit2 = request.getParameter("unit2");
		String hex = request.getParameter("hex");
		String defaultPrice = request.getParameter("defaultPrice");
		String remindNum = request.getParameter("remindNum");
		Goods newGoods = new Goods();
		newGoods.setName(goodsName);
		newGoods.setBrand(brand);
		newGoods.setUnit(unit);
		newGoods.setUnit2(unit2);
		newGoods.setHex(Double.parseDouble(hex));
		newGoods.setDefaultPrice(Double.parseDouble(defaultPrice));
		newGoods.setRemindNum(Double.parseDouble(remindNum));
		newGoods.setNumber(0);
		return service.newGoods(newGoods);
	}
	
	//选项变更时，更新单位信息
	@RequestMapping("updateImportTable")
	@ResponseBody
	public Result updateImportTable(HttpServletRequest request){
		String goodsName = request.getParameter("goodsName");
		return service.updateTable(goodsName);
	}
	
	//根据页面的信息，增加入货记录
	@RequestMapping("importGoods")
	@ResponseBody
	public Result importGoods(HttpServletRequest request){
		//获取操作者信息
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginInfo");
		int userId = user.getId();
		//获取信息
		String personInCharge = request.getParameter("personInCharge");
		System.out.println(personInCharge);
		String[] units = request.getParameterValues("unit");
		String[] goodsIDs = request.getParameterValues("goodsID");
		String[] numbers = request.getParameterValues("number");
		String[] unitPrices = request.getParameterValues("unitprice");
		Date date = new Date();
		String pattern = "yyyymmddhhmmss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		String indiction = sdf.format(date);
		//将信息存入detail中
		List<Detail> details = new ArrayList<Detail>();
		for(int i=0;i<goodsIDs.length;i++){
			Detail detail = new Detail();
			detail.setGoodsID(Integer.parseInt(goodsIDs[i]));
			detail.setNumber(Double.parseDouble(numbers[i]));
			detail.setUnitprice(Double.parseDouble(unitPrices[i]));
			detail.setAmount(detail.getNumber()*detail.getUnitprice());
			detail.setIndiction(indiction);
			details.add(detail);
		}
		Result result = service.importGoods(userId,details,units,personInCharge);
		this.possession(request);
		return result;
	}
	
	//使用excel进行入货
	@RequestMapping("importWithExcel")
	@ResponseBody
	public Result importWithExcel(HttpServletRequest request) throws Exception{
		String personInCharge = request.getParameter("personInCharge");
		
		//获取用户信息
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginInfo");
		int userId = user.getId();
		//转型
		MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
		//获取文件
		MultipartFile file = multipart.getFile("file");
		InputStream in = file.getInputStream();
		Result result = service.importWithExcel(userId, in, file,personInCharge);
		in.close();
		this.possession(request);
		return result;
	}
	

	
	
	//交易记录-------------------------------------------------------------------------------------------
	//跳转到交易记录页面
	@RequestMapping("recordPage")
	public String recordPage(HttpServletRequest request){
		//需要将交易记录传过去record
		List<RecordResult> recordList = service.findRecordWithPageNum(1,numInPage);
		request.setAttribute("recordList", recordList);
		request.setAttribute("pageNum", 1);
		return "recordPage";
	}
	
	//根据传过来的页数返回结果集
	@RequestMapping("selectRecordsWithPage")
	@ResponseBody
	public Result selectRecordsWithPage(HttpServletRequest request){
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));
//		if(pageNum<1){
//			pageNum=1;
//		}
		System.out.println("pageNum:"+pageNum);
		return service.selectRecordWithPageNum(pageNum,numInPage);
	}
	
	//导出到excel
	@RequestMapping("exportExcel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request,HttpServletResponse response){
		//先获取indiction
		String indiction = request.getParameter("indiction");
		System.out.println("indiction:"+indiction);
		if(indiction!=null){
			response.reset();
			response.setHeader("Content-Disposition", "attachment;filename="+"1"+".xlsx");  
	        response.setContentType("application/vnd.ms-excel;charset=UTF-8");  
	        response.setHeader("Pragma", "no-cache");  
	        response.setHeader("Cache-Control", "no-cache");  
	        response.setDateHeader("Expires", 0);
	        XSSFWorkbook workbook = null;
	        OutputStream output = null;
	        BufferedOutputStream bufferedOutput = null;
	        try {
				workbook = service.exportExcelInfo(indiction);
				output = response.getOutputStream();
				bufferedOutput = new BufferedOutputStream(output);
				bufferedOutput.flush();
				workbook.write(bufferedOutput);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					bufferedOutput.close();
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@RequestMapping("selectRecord")
	@ResponseBody
	public Result selectRecord(HttpServletRequest request){
		String type=request.getParameter("type");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sort = request.getParameter("sort");
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));
		System.out.println("pageNum:"+pageNum);
		return service.selectRecord(type,startTime,endTime,sort,pageNum,numInPage);
	}
	
	@RequestMapping("updateRecord")
	public String updateRecord(HttpServletRequest request){
		//获取订单号
		String indiction = request.getParameter("indiction");
		//根据订单号获取该订单的所有记录
		List<DetailResult> detailList = service.findDetailByIndiction(indiction);
		//获取所有商品已完成下拉菜单
		List<Goods> goodsList = service.findAllGoods();
		request.setAttribute("goodsList", goodsList);
		request.setAttribute("DetailList", detailList);
		request.setAttribute("indiction", indiction);
		return "updateRecord";
	}
	
	@RequestMapping("updateUnits")
	@ResponseBody
	public Result updateUnits(HttpServletRequest request){
		int goodsID = Integer.parseInt(request.getParameter("goodsID"));
		return service.updateUnit(goodsID);
	}
	
	@RequestMapping("updateDetail")
	@ResponseBody
	public Result updateDetail(HttpServletRequest request){
		String indiction = request.getParameter("indiction");
		String[] goodsIDs = request.getParameterValues("goodsID");
		String[] units = request.getParameterValues("unit");
		String[] numbers = request.getParameterValues("number");
		String[] prices = request.getParameterValues("price");
		String[] amounts = request.getParameterValues("amount");
		List<Detail> details = new ArrayList<Detail>();
		for(int i=0;i<goodsIDs.length;i++){
			Detail detail = new Detail();
			detail.setGoodsID(Integer.parseInt(goodsIDs[i]));
			detail.setNumber(Double.parseDouble(numbers[i]));
			detail.setUnitprice(Double.parseDouble(prices[i]));
			detail.setIndiction(indiction);
			detail.setAmount(Double.parseDouble(amounts[i]));
			details.add(detail);
		}
		return service.updateRecord(details,units,indiction);
	}
	
	//删除交易记录
	@RequestMapping("deleteRecord")
	@ResponseBody
	public Result deleteRecord(HttpServletRequest request){
		String indiction = request.getParameter("indiction");
		Result result = service.deleteRecord(indiction);
		this.possession(request);
		return result;
	}
	
	//根据订单号查询
	@RequestMapping("findWithIndiction")
	@ResponseBody
	public Result findWithIndiction(HttpServletRequest request){
		String indiction = request.getParameter("indiction");
		return service.findWithIndiction(indiction);
	}
	
	//员工管理--------------------------------------------------------------------------------------------------
	//跳转到用户管理界面
	@RequestMapping("UserManage")
	public String UserManage(HttpServletRequest request){
		//需要将所有user的信息传过去
		List<User> userList = service.findAllUser();
		for(int i=0;i<userList.size();i++){
			userList.get(i).setPassword(null);
		}
		request.setAttribute("userList", userList);
		return "userPage";
	}
	
	@RequestMapping("reflashTable")
	@ResponseBody
	public Result reflashTable(HttpServletRequest request){
		//需要将所有user的信息传过去
		List<User> userList = service.findAllUser();
		for(int i=0;i<userList.size();i++){
			userList.get(i).setPassword(null);
		}
		Result result = new Result();
		result.setStatus(0);
		result.setData(userList);
		return result;
	}
	
	//更改用户信息
	@RequestMapping("updateUserInfo")
	@ResponseBody
	public Result updateUserInfo(HttpServletRequest request){
		String id = request.getParameter("userId");
		String username = request.getParameter("username");
		String canQueryResult = request.getParameter("canQuery");
		String canInsertResult = request.getParameter("canInsert");
		String canUpdateResult = request.getParameter("canUpdate");
		String isManager = request.getParameter("isManager");
		boolean canQuery,canInsert,canUpdate;
		String role;
		if(canQueryResult!=null){
			canQuery = true;
		}else{
			canQuery = false;
		}
		if(canInsertResult!=null){
			canInsert = true;
		}else{
			canInsert = false;
		}
		if(canUpdateResult!=null){
			canUpdate = true;
		}else{
			canUpdate = false;
		}
		if(isManager!=null){
			role="manager";
		}else{
			role="normal";
		}
		User user = new User();
		user.setId(Integer.parseInt(id));
		user.setUsername(username);
		user.setCanQuery(canQuery);
		user.setCanInsert(canInsert);
		user.setCanUpdate(canUpdate);
		user.setRole(role);
		Result result = service.updateUserInfo(user);
		return result;
	}
	
	@RequestMapping("getUserNumber")
	@ResponseBody
	public Result getUserNumber(){
		return service.getUserNumber();
	}
	
	//添加用户
	@RequestMapping("addUser")
	@ResponseBody
	public Result addUser(HttpServletRequest request){
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String canQueryResult = request.getParameter("canQuery");
		String canUpdateResult = request.getParameter("canUpdate");
		String canInsertResult = request.getParameter("canInsert");
		String roleResult = request.getParameter("role");
		boolean canQuery,canInsert,canUpdate;
		String role;
		if(canQueryResult!=null){
			canQuery = true;
		}else{
			canQuery = false;
		}
		if(canInsertResult!=null){
			canInsert = true;
		}else{
			canInsert = false;
		}
		if(canUpdateResult!=null){
			canUpdate = true;
		}else{
			canUpdate = false;
		}
		if(roleResult!=null){
			role="manager";
		}else{
			role="normal";
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setCanQuery(canQuery);
		user.setCanInsert(canInsert);
		user.setCanUpdate(canUpdate);
		user.setRole(role);
		return service.addUser(user);
	}
	
	//删除用户
	@RequestMapping("deleteUser")
	@ResponseBody
	public Result deleteUser(HttpServletRequest request){
		int id = Integer.parseInt(request.getParameter("id"));
		System.out.println("id:"+id);
		return service.deleteUser(id);
	}
	
	//页面跳转---------------------------------------------------------------------------------------------
	//跳转到主页面
	@RequestMapping("goMain")
	public String goMain(){
		return "mainPage";
	}
	@RequestMapping("mainLeft")
	public String mainLeft(){
		return "mainLeft";
	}
	@RequestMapping("mainTop")
	public String mainTop(){
		return "mainTop";
	}
	@RequestMapping("mainRight")
	public String mainRight(){
		return "mainRight";
	}
	//基本方法--------------------------------------------------------------
	public void possession(HttpServletRequest request){
		Possession possession = service.getPossession();
		HttpSession session = request.getSession();
		session.setAttribute("possession", possession);
	}
}
