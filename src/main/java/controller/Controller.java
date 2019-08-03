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
	
	//��¼ע���Ȳ���--------------------------------------------------------------------------------------
	//��¼
	@RequestMapping("login")
	@ResponseBody
	public Result login(HttpServletRequest request){
		//��ȡ��¼��Ϣ
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		//��֤
		Result result = service.login(username,password);
		if(result.getStatus()==0){
			//��Ҫ�Ƚ���¼��Ϣ����session
			session = request.getSession();
			User user = (User)result.getData();
			if(!user.isCanQuery()){
				result.setStatus(1);
				result.setData(null);
				result.setMsg("���û�Ȩ�޲��㣬����ϵ����Ա");
			}
			session.setAttribute("loginInfo", user);
		}
		this.possession(request);
		return result;
	}
	//ע����¼
	@RequestMapping("logout")
	public String logout(HttpServletRequest request){
		session = request.getSession();
		session.removeAttribute("loginInfo");
		session.removeAttribute("posssession");
		return "../../index";
	}
	//����¼��Ϣ
	@RequestMapping("checkLogin")
	@ResponseBody
	public Result checkLogin(HttpServletRequest request){
		session = request.getSession();
		Result result = new Result();
		User loginInfo = (User) session.getAttribute("loginInfo");
		if(loginInfo==null){
			//δ��¼
			result.setStatus(1);
			result.setMsg("���ȵ�¼");
		}else{
			//�ѵ�¼
			result.setStatus(0);
		}
		return result;
	}
	//����ѯ-------------------------------------------------------------------------------------------
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
	
	//������
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
				result.setMsg("���ҳɹ�");
				result.setData(goods);
				return result;
			}
		}
		result.setStatus(1);
		result.setData("�������������");
		return result;
	}
	
	//��Ʒ�޸�
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
	
	//��ѯĳ��Ʒ�Ľ�������
	@RequestMapping("goodsDetail")
	@ResponseBody
	public Result goodsDetail(HttpServletRequest request){
		int goodsId = Integer.parseInt(request.getParameter("goodsId"));
		System.out.println("goodsId:"+goodsId);
		return service.goodsDetail(goodsId);
	}
	
	//����--------------------------------------------------------------------------------------------------
	//ͨ��excel����
	@RequestMapping("excelExportGoods")
	@ResponseBody
	public Result excelExportGoods(HttpServletRequest request,Model model) throws Exception{
		String personInCharge = request.getParameter("personInCharge");
		System.out.println(personInCharge);
		//��ȡ������
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginInfo");
		int userId = user.getId();
		//��ȡ�ϴ����ļ�
		MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
		MultipartFile file = multipart.getFile("file");
		InputStream in = file.getInputStream();
		Result result = service.exportWithExcel(userId, in, file, personInCharge);
		in.close();
		this.possession(request);
		return result;
	}
	
	//ͨ��ҳ�������ύ������¼
	@RequestMapping("exportGoods")
	@ResponseBody
	public Result exportGoods(HttpServletRequest request){
		//��ȡ��������Ϣ
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
		//�Ȼ�ȡ��ǰʱ��
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
		//��details�����ݴ���ȥservice
		Result result = service.export(userId,details, units, personInCharge);
		this.possession(request);
		return result;
	}
	
	//���³����е�table��Ϣ
	@RequestMapping("updateExportTable")
	@ResponseBody
	public Result updateExportTable(HttpServletRequest request){
		System.out.println("------------------------------------");
		String goodsName = request.getParameter("name");
		System.out.println("��ȡ������Ʒ����"+goodsName);
		return service.updateTable(goodsName);
	}
	
	//��ת����������
	@RequestMapping("exportPage")
	public String inportPage(HttpServletRequest request){
		//��Ҫ����Ʒ�б���ȥ
		List<Goods> goodsList = service.findAllGoods();
		request.setAttribute("goodsList", goodsList);
		return "exportPage";
	}
	
	//����indicion������ϸ�Ľ�����Ϣ
	@RequestMapping("checkRecord")
	@ResponseBody
	public Result checkRecord(HttpServletRequest request){
		String indiction = request.getParameter("indiction");
		return service.checkRecord(indiction);
	}
	
	//���-----------------------------------------------------------------------------------------------------
	//��ת�����ҳ��
	@RequestMapping("importPage")
	public String importPage(HttpServletRequest request){
		//�����е���Ʒ���ʹ���ȥ����ѡ��
		List<Goods> goodsList = service.findAllGoods();
		request.setAttribute("goodsList", goodsList);
		return "importPage";
	}
	//������Ʒ����
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
	
	//ѡ����ʱ�����µ�λ��Ϣ
	@RequestMapping("updateImportTable")
	@ResponseBody
	public Result updateImportTable(HttpServletRequest request){
		String goodsName = request.getParameter("goodsName");
		return service.updateTable(goodsName);
	}
	
	//����ҳ�����Ϣ�����������¼
	@RequestMapping("importGoods")
	@ResponseBody
	public Result importGoods(HttpServletRequest request){
		//��ȡ��������Ϣ
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginInfo");
		int userId = user.getId();
		//��ȡ��Ϣ
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
		//����Ϣ����detail��
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
	
	//ʹ��excel�������
	@RequestMapping("importWithExcel")
	@ResponseBody
	public Result importWithExcel(HttpServletRequest request) throws Exception{
		String personInCharge = request.getParameter("personInCharge");
		
		//��ȡ�û���Ϣ
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginInfo");
		int userId = user.getId();
		//ת��
		MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
		//��ȡ�ļ�
		MultipartFile file = multipart.getFile("file");
		InputStream in = file.getInputStream();
		Result result = service.importWithExcel(userId, in, file,personInCharge);
		in.close();
		this.possession(request);
		return result;
	}
	

	
	
	//���׼�¼-------------------------------------------------------------------------------------------
	//��ת�����׼�¼ҳ��
	@RequestMapping("recordPage")
	public String recordPage(HttpServletRequest request){
		//��Ҫ�����׼�¼����ȥrecord
		List<RecordResult> recordList = service.findRecordWithPageNum(1,numInPage);
		request.setAttribute("recordList", recordList);
		request.setAttribute("pageNum", 1);
		return "recordPage";
	}
	
	//���ݴ�������ҳ�����ؽ����
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
	
	//������excel
	@RequestMapping("exportExcel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request,HttpServletResponse response){
		//�Ȼ�ȡindiction
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
		//��ȡ������
		String indiction = request.getParameter("indiction");
		//���ݶ����Ż�ȡ�ö��������м�¼
		List<DetailResult> detailList = service.findDetailByIndiction(indiction);
		//��ȡ������Ʒ����������˵�
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
	
	//ɾ�����׼�¼
	@RequestMapping("deleteRecord")
	@ResponseBody
	public Result deleteRecord(HttpServletRequest request){
		String indiction = request.getParameter("indiction");
		Result result = service.deleteRecord(indiction);
		this.possession(request);
		return result;
	}
	
	//���ݶ����Ų�ѯ
	@RequestMapping("findWithIndiction")
	@ResponseBody
	public Result findWithIndiction(HttpServletRequest request){
		String indiction = request.getParameter("indiction");
		return service.findWithIndiction(indiction);
	}
	
	//Ա������--------------------------------------------------------------------------------------------------
	//��ת���û��������
	@RequestMapping("UserManage")
	public String UserManage(HttpServletRequest request){
		//��Ҫ������user����Ϣ����ȥ
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
		//��Ҫ������user����Ϣ����ȥ
		List<User> userList = service.findAllUser();
		for(int i=0;i<userList.size();i++){
			userList.get(i).setPassword(null);
		}
		Result result = new Result();
		result.setStatus(0);
		result.setData(userList);
		return result;
	}
	
	//�����û���Ϣ
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
	
	//����û�
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
	
	//ɾ���û�
	@RequestMapping("deleteUser")
	@ResponseBody
	public Result deleteUser(HttpServletRequest request){
		int id = Integer.parseInt(request.getParameter("id"));
		System.out.println("id:"+id);
		return service.deleteUser(id);
	}
	
	//ҳ����ת---------------------------------------------------------------------------------------------
	//��ת����ҳ��
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
	//��������--------------------------------------------------------------
	public void possession(HttpServletRequest request){
		Possession possession = service.getPossession();
		HttpSession session = request.getSession();
		session.setAttribute("possession", possession);
	}
}
