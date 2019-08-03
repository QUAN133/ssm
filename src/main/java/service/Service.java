package main.java.service;

import java.beans.IntrospectionException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import main.java.dao.*;
import main.java.entity.*;
import main.java.util.DetailResult;
import main.java.util.ExcelUtil;
import main.java.util.GoodsDetailResult;
import main.java.util.PageRecordVO;
import main.java.util.RecordResult;
import main.java.util.Result;
import main.java.util.SearchRecordVO;
import main.java.util.UpdateRecordVO;
@org.springframework.stereotype.Service
public class Service implements IService{
	@Autowired
	DetailDao detailDao;
	@Autowired
	GoodsDao goodsDao;
	@Autowired
	PossessionDao possessionDao;
	@Autowired
	RecordDao recordDao;
	@Autowired
	UserDao userDao;
	public List<Goods> findAllGoods() {
		return goodsDao.findAll();
	}
	
	public List<Goods> findAllGoodsWithPage(int pageNum, int numInPage){
		PageRecordVO pg = new PageRecordVO();
		pg.setPageNum(pageNum);
		pg.setNumInPage(numInPage);
		return goodsDao.findAllGoodsWithPage(pg);
	}

	public Result selectGoodsWithPage(int pageNum, int numInPage) {
		Result result = new Result();
		PageRecordVO pg = new PageRecordVO();
		pg.setPageNum(pageNum);
		pg.setNumInPage(numInPage);
		List<Goods> returnList = goodsDao.findAllGoodsWithPage(pg);
		
		List<Goods> goodsList = goodsDao.findAll();
		while((pageNum-1)*numInPage>goodsList.size()-1){
			pageNum--;
		}
		if(pageNum*numInPage>=goodsList.size()){
			//够
			result.setStatus(1);
		}else{
			//不够
			result.setStatus(0);
		}
		result.setData(returnList);
		return result;
	}

	public List<RecordResult> findAllRecord() {
		//先获取reocrd表的信息
//		List<Record> recordList = recordDao.findAll();
		//将record中的userId转成id
		List<RecordResult> recordResultList = recordDao.findRecordWithUser();
		for(int i=0;i<recordResultList.size();i++){
			System.out.println(recordResultList.get(i));
		}
		return recordResultList;
	}
	
	public List<User> findAllUser() {
		return userDao.findAll();
	}
	
	public Result login(String username,String password){
		User user = userDao.findByUsername(username);
		Result result = new Result();
		if(user==null){
			result.setStatus(1);
			result.setMsg("用户不存在");
		}else{
			if(password.equals(user.getPassword())){
				result.setStatus(0);
				result.setMsg("登录成功");
				result.setData(user);
			}else{
				result.setStatus(1);
				result.setMsg("密码错误");
			}
		}
		return result;
	}

	public Result updateTable(String goodsName) {
		System.out.println(goodsName);
		Goods goods = goodsDao.findGoodsByName(goodsName);
		System.out.println("搜索结果："+goods);
		Result result = new Result();
		if(goods==null){
			result.setStatus(1);
			result.setMsg("搜索结果不存在");
		}else{
			result.setStatus(0);
			result.setMsg("搜索成功");
			result.setData(goods);
		}
		return result;
	}

	public Result export(int userId, List<Detail> details, String[] units, String personInCharge) {
		Result result = new Result();
		//1、在goods中减少商品数量，若商品不足，取消后续交易
		//先从goods中取回所有商品的信息
		List<Goods> goodsList = goodsDao.findAll();
		for(int i=0;i<details.size();i++){
			Detail detail = details.get(i);
			//寻找对应的商品
			for(int j=0;j<goodsList.size();j++){
				if(detail.getGoodsID()==goodsList.get(j).getId()){
					Goods goods = goodsList.get(j);
					//此处的商品ID已经匹配
					/*
					 * 化单位
					 * 1.逐一比较，确定是哪个单位
					 * 2.如果不匹配，报错撤回
					 * 3.如果是unit2，乘以hex
					 */
					String currentUnit = units[i];
//					System.out.println("单位："+currentUnit);
//					System.out.println("unit:"+goods.getUnit());
//					System.out.println("unit2:"+goods.getUnit2());
					if(currentUnit.equals(goods.getUnit())){
						
					}else if(currentUnit.equals(goods.getUnit2())){
						detail.setNumber(detail.getNumber()*goods.getHex());
						details.get(i).setUnitprice(details.get(i).getUnitprice()/goods.getHex());
					}else{
						result.setStatus(1);
						result.setMsg("单位不正确");
						return result;
					}
					if(detail.getNumber()>goods.getNumber()){
						System.out.println(goods.getName()+"不足");
						//商品不足，收皮
						result.setStatus(1);
						result.setMsg(goods.getName()+"不足");
						return result;
					}
//					break;
				}
			}
		}
		//现在先对goods表进行操作
		//使用goods数组来存取对应修改的数据,同时计算总价
		double totalAmount = 0;
		List<Goods> updateGoodsList = new ArrayList<Goods>();
		for(int i=0;i<details.size();i++){
			Detail detail = details.get(i);
			Goods goods = new Goods();
			goods.setId(detail.getGoodsID());
			goods.setNumber(detail.getNumber());
			totalAmount+=detail.getNumber()*detail.getUnitprice();
			updateGoodsList.add(goods);
		}
		for(int i=0;i<updateGoodsList.size();i++){
			goodsDao.reduceNumberByID(updateGoodsList.get(i));
		}
		//2、在record中添加交易记录并记录type=1
		//同样记录在record中
		Record updateRecord = new Record();
		updateRecord.setType(1);
		updateRecord.setDate(details.get(0).getIndiction());
		updateRecord.setDetailIndiction(details.get(0).getIndiction());
		updateRecord.setPrice(totalAmount);
		updateRecord.setUserId(userId);
		updateRecord.setPersonInCharge(personInCharge);
		recordDao.insert(updateRecord);
		
		//3、在detail中将所有交易明细记录下来
		detailDao.insert(details);
		//4、减少钱
		possessionDao.export(totalAmount);
		result.setStatus(0);
		result.setMsg("出货完成");
		return result;
	}

	//将交易记录导出为excel
	public XSSFWorkbook exportExcelInfo(String indiction) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, IntrospectionException, ParseException{
		System.out.println(indiction);
		//先从数据库获取数据
		List<DetailResult> resultList = detailDao.findByIndiction(indiction);
		for(int i=0;i<resultList.size();i++){
			System.out.println(resultList.get(i));
		}
		Map<Integer,List<ExcelBean>> map=new LinkedHashMap<Integer, List<ExcelBean>>();
		//设置标题栏
		List<ExcelBean> excel=new ArrayList<ExcelBean>();
		excel.add(new ExcelBean("商品名","name",0));
		excel.add(new ExcelBean("单位", "unit", 0));
		excel.add(new ExcelBean("数量","number",0));
		excel.add(new ExcelBean("单价","unitprice",0));
		excel.add(new ExcelBean("金额","amount",0));
		map.put(0, excel);
		//设置表名
		String sheetName = "test";
		XSSFWorkbook workbook = ExcelUtil.createExcelFile(DetailResult.class, resultList, map, sheetName);
		return workbook;
	}
	
	public Result checkRecord(String indiction) {
		//根据indiction检索detail
		List<DetailResult> returnList = detailDao.findByIndiction(indiction);
		for(int i=0;i<returnList.size();i++){
			System.out.println(returnList.get(i));
		}
		//该类中detail的id需要转化成
		Result result = new Result();
		result.setStatus(0);
		result.setMsg("操作成功");
		result.setData(returnList);
		return result;
	}

	public Result newGoods(Goods newGoods) {
		Result result = new Result(); 
		//先判断该商品是否已经存在
		Goods findResult = goodsDao.findGoodsByName(newGoods.getName());
		if(findResult==null){
			//将传过来的商品加到goods表中即可
			goodsDao.insert(newGoods);
			result.setStatus(0);
			result.setMsg("新增商品成功");
			return result;
		}else{
			result.setStatus(1);
			result.setMsg("该商品已存在");
			return result;
		}
	}

	public Result importGoods(int userId, List<Detail> details, String[] units, String personInCharge) {
		for(int i=0;i<units.length;i++){
			System.out.println(units[i]);
		}
		Result result = new Result();
		//取得所有goods
		List<Goods> goodsList = goodsDao.findAll();
		//1.增加goods的数量,需要计算总价
		double totalAmount = 0;
		for(int i=0;i<details.size();i++){
			//先进行化单位
			for(int j=0;j<goodsList.size();j++){
				if(goodsList.get(j).getId()==details.get(i).getGoodsID()){
					Goods goods = goodsList.get(j);
					//此处商品已经匹配
					//对单位进行比较
					String currentUnit = units[i];//当前单位
					String unit = goods.getUnit();
					String unit2 = goods.getUnit2();
					if(currentUnit.equals(unit)){
						//与unit匹配
						continue;
					}else if(currentUnit.equals(unit2)){
						//与unit2匹配,将原本的数量乘以进制,将单价除以进制
						details.get(i).setNumber(details.get(i).getNumber()*goods.getHex());
						details.get(i).setUnitprice(details.get(i).getUnitprice()/goods.getHex());
					}else{
						//输入错误
						result.setStatus(1);
						result.setMsg("输入错误");
						return result;
					}
				}
			}
			//到这里单位问题已经拒绝
			//对goods的数量进行增加
			Goods goods = new Goods();
			goods.setId(details.get(i).getGoodsID());
			goods.setNumber(details.get(i).getNumber());
			goodsDao.addNumberById(goods);
			totalAmount+=details.get(i).getNumber()*details.get(i).getUnitprice();
		}
		//2.增加record
		Record record = new Record();
		record.setDetailIndiction(details.get(0).getIndiction());
		record.setType(-1);
		record.setPrice(totalAmount);
		record.setUserId(userId);
		record.setPersonInCharge(personInCharge);
		recordDao.insert(record);
		//3.增加detail
		detailDao.insert(details);
		//4、减少钱
		possessionDao.importGoods(totalAmount);
		result.setStatus(0);
		result.setMsg("入货成功");
		return result;
	}

	public Result updateUserInfo(User user) {
		Result result = new Result();
		userDao.update(user);
		result.setStatus(0);
		result.setMsg("修改成功");
		return result;
	}

	public Result selectGoods(String condition, int pageNum, int numInPage) {
		Result result = new Result();
		Pattern p = Pattern.compile(condition);
		//先获取所有的goods
		List<Goods> goodsList = this.findAllGoods();
		//定义数组来储存匹配结果
		List<Goods> resultList = new ArrayList<Goods>();
		//遍历goodsList
		Matcher m;
		for(int i=0;i<goodsList.size();i++){
			//取出商品
			Goods goods = goodsList.get(i);
			//名字
			String goodsName = goods.getName();
			//正则
			m = p.matcher(goodsName);
			if(m.find()){
				//如果匹配到结果，则加入返回数组中
				resultList.add(goods);
				continue;
			}
			String brand = goods.getBrand();
			m = p.matcher(brand);
			if(m.find()){
				resultList.add(goods);
				continue;
			}
		}
		if(resultList.size()==0){
			result.setStatus(1);
			result.setMsg("搜索结果不存在");
		}else{
			//此处对页数进行限制
			List<Goods> resultList2 = new ArrayList<Goods>();
			
			while((pageNum-1)*numInPage>resultList.size()-1){
				pageNum--;
			}
			for(int i=(pageNum-1)*numInPage;i<=pageNum*numInPage-1&&i<resultList.size();i++){
				resultList2.add(resultList.get(i));
				if(i==resultList.size()-1){
					result.setStatus(1);
				}
			}
			if(result.getStatus()!=1){
				result.setStatus(0);
			}
			result.setMsg("搜索完成");
			result.setData(resultList2);
			//使用status来区分是否为末尾页
		}
		return result;
	}

	public Result exportWithExcel(int userId, InputStream in, MultipartFile file, String personInCharge) throws Exception{
		String filename = file.getOriginalFilename();
		String extension = filename.substring(filename.indexOf('.'));
		if(extension.equalsIgnoreCase(".xls")||extension.equalsIgnoreCase(".xlsx")){
			Workbook work = ExcelUtil.getWorkbook(in, file.getOriginalFilename());
			List<Object> colNames = ExcelUtil.getColNameList(work);
			List<String> unitsList = new ArrayList<String>();//用于存放单位
			boolean hasUnit,hasUnitPrice;
			//先得到每一列是什么内容
			int goodsNameCell = -1;//商品名
			int unitCell = -1;//单位
			int numberCell = -1;//数量
			int unitpriceCell = -1;//单价
			for(int i=0;i<colNames.size();i++){
				String temp = colNames.get(i).toString();
				if(temp.equals("商品名")){
					goodsNameCell = i;
				}else if(temp.equals("单价")){
					unitpriceCell = i;
				}else if(temp.equals("数量")){
					numberCell = i;
				}else if(temp.equals("单位")){
					unitCell = i;
				}
			}
			//先判断格式是否符合要求,商品名、数量是不可缺少的
			if(goodsNameCell==-1||numberCell==-1){
				Result result = new Result();
				result.setStatus(1);
				result.setMsg("输入excel格式不正确");
				return result;
			}
			if(unitCell==-1){
				hasUnit=false;
			}else{
				hasUnit=true;
			}
			if(unitpriceCell==-1){
				hasUnitPrice=false;
			}else{
				hasUnitPrice=true;
			}
			//先将数据转化成Detail数组，然后将所有数据传给export方法即可达到目的，需要List<Detail>,int amount,int userId
			//由于用户传过来的数据有goodname，需要先将goodname转化成goodsID
			//将文件的数据转成数组的形式
			List<List<Object>> listob = ExcelUtil.getBankListByExcel(work);
			List<Detail> details = new ArrayList<Detail>();
			List<Double> amounts = new ArrayList<Double>();
			//获取时间
			Date date = new Date();
			String pattern = "yyyymmddhhmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			String indiction = sdf.format(date);
			//遍历
			for(int i=0;i<listob.size();i++){
				//获取到一行,最简单的数据要求为商品名，数量，单价
				List<Object> row = listob.get(i);
//			System.out.println("rowSize："+row.size());
				//根据goodsName获取ID
				String goodsName = String.valueOf(row.get(goodsNameCell));
				Goods goods = goodsDao.findGoodsByName(String.valueOf(row.get(goodsNameCell)));
				if(goods==null){
					//货物为空
					if(goodsName.equals("")||goodsName==null){
						continue;
					}else{
						Result result = new Result();
						result.setStatus(1);
						result.setMsg(goodsName+"不存在");
						return result;
					}
				}
				Detail detail = new Detail();
				//单位
				if(hasUnit==false){
					//没有单位，使用单位1
					unitsList.add(goods.getUnit());
				}else{
					//有单位,将excel中的单位放入数组即可
					unitsList.add(String.valueOf(row.get(unitCell)));
				}
				//新建Detail
				int goodsID = goods.getId();
				//单价
				if(hasUnitPrice){
					//有单价则使用excel中的单价
					detail.setUnitprice(Double.parseDouble(String.valueOf(row.get(unitpriceCell))));
				}else{
					//没有单价则使用默认单价
					detail.setUnitprice(goods.getDefaultPrice());
				}
				detail.setGoodsID(goodsID);
				detail.setNumber(Double.parseDouble(String.valueOf(row.get(numberCell))));
				detail.setIndiction(indiction);
				detail.setAmount(detail.getNumber()*detail.getUnitprice());
				//将detail放入details
				details.add(detail);
				//计算金额
				double amount = detail.getNumber()*detail.getUnitprice();
				amounts.add(amount);
			}
			//将单位数组arrayList转化成String[]数组
			String[] units = new String[unitsList.size()];
			for(int i=0;i<unitsList.size();i++){
				units[i] = unitsList.get(i);
			}
			return this.export(userId, details, units, personInCharge);
		}else{
			Result result = new Result();
			result.setStatus(1);
			result.setMsg("请检查文件格式");
			return result;
		}
	}

	public Result importWithExcel(int userId, InputStream in, MultipartFile file, String personInCharge) throws Exception {
		String filename = file.getOriginalFilename();
		String extension = filename.substring(filename.indexOf('.'));
		if(extension.equalsIgnoreCase(".xls")||extension.equalsIgnoreCase(".xlsx")){
			
			ArrayList<String> unitList = new ArrayList<String>();
			ArrayList<Detail> details = new ArrayList<Detail>();
			boolean hasUnit;
			//获取当前时间
			Date date = new Date();
			String pattern = "yyyymmddhhmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			String indiction = sdf.format(date);
			//先获取表格
			Workbook work = ExcelUtil.getWorkbook(in, file.getOriginalFilename());
			//根据表格得到每一列代表什么
			List<Object> colName = ExcelUtil.getColNameList(work);
			int goodsNameCell = -1;
			int unitCell = -1;
			int unitpriceCell = -1;
			int numberCell = -1;
			for(int i=0;i<colName.size();i++){
				String temp = colName.get(i).toString();
				if(temp.equals("商品名")){
					goodsNameCell = i;
				}else if(temp.equals("单位")){
					unitCell = i;
				}else if(temp.equals("数量")){
					numberCell = i;
				}else if(temp.equals("单价")){
					unitpriceCell = i;
				}
			}
			//判断excel格式是否错误，商品名和数量是不可或缺的，入货单价也要求录入
			if(goodsNameCell==-1||numberCell==-1||unitpriceCell==-1){
				Result result = new Result();
				result.setStatus(1);
				result.setMsg("excel输入格式不正确");
				return result;
			}
			List<List<Object>> listob = ExcelUtil.getBankListByExcel(work);
			//单位
			if(unitCell==-1){
				hasUnit = false;
			}else{
				hasUnit = true;
			}
			//遍历
			for(int i=0;i<listob.size();i++){
				//得到一行
				List<Object> row = listob.get(i);
				//取得商品名，根据商品名取得商品
				String goodsName = String.valueOf(row.get(goodsNameCell)).trim();
				Goods goods = goodsDao.findGoodsByName(goodsName);
				if(goods==null){
					if(!goodsName.equals("")){
						//创建新商品,如果没有单位，则跳过
						if(hasUnit==false){
							continue;
						}
						goods = new Goods();
						goods.setName(goodsName);
						goods.setUnit(String.valueOf(row.get(unitCell)));
						goodsDao.insert(goods);
						goods = goodsDao.findGoodsByName(goodsName);
					}else{
						continue;
					}
				}
				//对单位进行操作
				if(hasUnit){
					//有导入单位，直接将单位传入即可
					unitList.add(String.valueOf(row.get(unitCell)));
				}else{
					//没有单位，使用unit
					unitList.add(goods.getUnit());
				}
				//根据该行取得信息
				Detail detail = new Detail();
				detail.setGoodsID(goods.getId());
				detail.setNumber(Double.parseDouble(String.valueOf(row.get(numberCell))));
				detail.setUnitprice(Double.parseDouble(String.valueOf(row.get(unitpriceCell))));
				detail.setAmount(detail.getNumber()*detail.getUnitprice());
				detail.setIndiction(indiction);
				
				details.add(detail);
			}
			//将单位数组转化成String[]
			String[] units = new String[unitList.size()];
			for(int i=0;i<unitList.size();i++){
				units[i] = unitList.get(i);
			}
			return importGoods(userId, details, units ,personInCharge);
		}else{
			Result result = new Result();
			result.setStatus(1);
			result.setMsg("请检查文件格式");
			return result;
		}
	}

	public Result updateGoods(Goods goods) {
		Result result = new Result();
		goodsDao.updateGoodsInfo(goods);
		result.setStatus(0);
		result.setMsg("修改成功");
		return result;
	}
	
	public Result selectRecord(String type, String startTime, String endTime, String sort, int pageNum, int numInPage) {
		Result result = new Result();
		SearchRecordVO vo = new SearchRecordVO();
		vo.setType(type);
		if(!startTime.equals("")){
			vo.setStartTime(startTime);
		}
		if(!endTime.equals("")){
			vo.setEndTime(endTime);
		}
		vo.setSort(sort);
		vo.setNumInPage(numInPage);
		List<RecordResult> recordResultList = new ArrayList<RecordResult>();
		do{
			if(pageNum<1){
				break;
			}
			vo.setPageNum(pageNum);
			recordResultList = recordDao.findWithCondition(vo);
			pageNum--;
		}while(recordResultList.isEmpty());
		for(int i=0;i<recordResultList.size();i++){
			System.out.println(recordResultList.get(i));
		}
		result.setStatus(++pageNum);
		result.setMsg("操作成功");
		result.setData(recordResultList);
		return result;
	}

	public Result getUserNumber() {
		Result result = new Result();
		int count = userDao.getUserNumber();
		System.out.println("count:"+count);
		result.setStatus(0);
		result.setMsg("操作成功");
		result.setData(count);
		return result;
	}

	public List<DetailResult> findDetailByIndiction(String indiction) {
		return detailDao.findByIndiction(indiction);
	}

	public Result updateUnit(int goodsID) {
		Result result = new Result();
		List<Goods> goodsList = goodsDao.findAll();
		String[] units = new String[2];
		for(int i=0;i<goodsList.size();i++){
			Goods goods = goodsList.get(i);
			if(goods.getId()==goodsID){
				units[0] = goods.getUnit();
				units[1] = goods.getUnit2();
			}
		}
		result.setStatus(0);
		result.setData(units);
		return result;
	}

	public Result updateRecord(List<Detail> details, String[] units, String indiction) {
		Result result = new Result();
		//1、根据indiction将detail中的记录全部删除
		detailDao.deleteByIndiction(indiction);
		//2、将新的detial重新录入数据库
		detailDao.insert(details);
		//3、修改record中总价
		double totalAmount = 0;
		for(int i=0;i<details.size();i++){
			totalAmount+=details.get(i).getAmount();
		}
		UpdateRecordVO ur = new UpdateRecordVO();
		ur.setDetailIndiciton(indiction);
		ur.setPrice(totalAmount);
		recordDao.updatePriceByIndiction(ur);
		result.setStatus(0);
		result.setMsg("修改成功");
		return result;
	}

	public Result deleteRecord(String indiction) {
		Result result = new Result();
		//1、根据indiction将detail表中的全部删除
		detailDao.deleteByIndiction(indiction);
		//2、根据indiction得到record中的price并将资金恢复
		Record record = recordDao.findWithIndiction(indiction);
		//判断类型
		System.out.println("type:"+record.getType());
		System.out.println("price:"+record.getPrice());
		if(record.getType()==1){
			//出货，撤销后减少钱
			possessionDao.undoExport(record.getPrice());
		}else{
			possessionDao.undoImport(record.getPrice());
		}
		//3、根据indiction删除record
		recordDao.deleteByIndiction(indiction);
		result.setStatus(0);
		result.setMsg("删除成功");
		return result;
	}

	public Possession getPossession() {
		return possessionDao.getPossession();
	}

	public List<RecordResult> findRecordWithPageNum(int i, int numInPage) {
		// TODO Auto-generated method stub
		System.out.println("---------------------------------------------");
		System.out.println(i+":"+numInPage);
		PageRecordVO vo = new PageRecordVO();
		vo.setPageNum(i);
		vo.setNumInPage(numInPage);
		System.out.println(vo);
		System.out.println("pageNum:"+vo.getPageNum());
		System.out.println("numInPage:"+vo.getNumInPage());
		return recordDao.findRecordWithPage(vo);
	}

	public Result selectRecordWithPageNum(int i, int numInPage) {
		Result result = new Result();
		List<RecordResult> recordResults = new ArrayList<RecordResult>();
		do{
			if(i<=0){
				break;
			}
			recordResults = this.findRecordWithPageNum(i, numInPage);
			result.setStatus(i);
			i--;
			System.out.println(recordResults.isEmpty());
		}while(recordResults.isEmpty());
		//遍历
		for(int j=0;j<recordResults.size();j++){
			System.out.println(recordResults.get(j));
		}
		result.setData(recordResults);
		result.setMsg("操作成功");
		return result;
	}

	public Result addUser(User user) {
		userDao.addUser(user);
		Result result = new Result();
		result.setStatus(0);
		result.setMsg("操作成功");
		return result;
	}

	public Result deleteUser(int id) {
		Result result = new Result();
		userDao.deleteUser(id);
		result.setStatus(0);
		result.setMsg("操作成功");
		return result;
	}

	public Result goodsDetail(int goodsId) {
		Result result = new Result();
		List<GoodsDetailResult> DetailResults = detailDao.goodsDetail(goodsId);
		for(int i=0;i<DetailResults.size();i++){
			System.out.println(DetailResults.get(i));
		}
		result.setStatus(0);
		result.setMsg("查询成功");
		result.setData(DetailResults);
		return result;
	}

	public Result findWithIndiction(String indiction) {
		Result result = new Result();
		List<RecordResult> resultList = recordDao.findRecordResultWithIndiction(indiction);
		for(int i=0;i<resultList.size();i++){
			System.out.println(resultList.get(i));
		}
		if(resultList.isEmpty()){
			result.setStatus(1);
			result.setMsg("没有找到结果");
		}else{
			result.setStatus(0);
			result.setMsg("搜索成功");
			result.setData(resultList);
		}
		return result;
	}
}
