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
			//��
			result.setStatus(1);
		}else{
			//����
			result.setStatus(0);
		}
		result.setData(returnList);
		return result;
	}

	public List<RecordResult> findAllRecord() {
		//�Ȼ�ȡreocrd�����Ϣ
//		List<Record> recordList = recordDao.findAll();
		//��record�е�userIdת��id
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
			result.setMsg("�û�������");
		}else{
			if(password.equals(user.getPassword())){
				result.setStatus(0);
				result.setMsg("��¼�ɹ�");
				result.setData(user);
			}else{
				result.setStatus(1);
				result.setMsg("�������");
			}
		}
		return result;
	}

	public Result updateTable(String goodsName) {
		System.out.println(goodsName);
		Goods goods = goodsDao.findGoodsByName(goodsName);
		System.out.println("���������"+goods);
		Result result = new Result();
		if(goods==null){
			result.setStatus(1);
			result.setMsg("�������������");
		}else{
			result.setStatus(0);
			result.setMsg("�����ɹ�");
			result.setData(goods);
		}
		return result;
	}

	public Result export(int userId, List<Detail> details, String[] units, String personInCharge) {
		Result result = new Result();
		//1����goods�м�����Ʒ����������Ʒ���㣬ȡ����������
		//�ȴ�goods��ȡ��������Ʒ����Ϣ
		List<Goods> goodsList = goodsDao.findAll();
		for(int i=0;i<details.size();i++){
			Detail detail = details.get(i);
			//Ѱ�Ҷ�Ӧ����Ʒ
			for(int j=0;j<goodsList.size();j++){
				if(detail.getGoodsID()==goodsList.get(j).getId()){
					Goods goods = goodsList.get(j);
					//�˴�����ƷID�Ѿ�ƥ��
					/*
					 * ����λ
					 * 1.��һ�Ƚϣ�ȷ�����ĸ���λ
					 * 2.�����ƥ�䣬������
					 * 3.�����unit2������hex
					 */
					String currentUnit = units[i];
//					System.out.println("��λ��"+currentUnit);
//					System.out.println("unit:"+goods.getUnit());
//					System.out.println("unit2:"+goods.getUnit2());
					if(currentUnit.equals(goods.getUnit())){
						
					}else if(currentUnit.equals(goods.getUnit2())){
						detail.setNumber(detail.getNumber()*goods.getHex());
						details.get(i).setUnitprice(details.get(i).getUnitprice()/goods.getHex());
					}else{
						result.setStatus(1);
						result.setMsg("��λ����ȷ");
						return result;
					}
					if(detail.getNumber()>goods.getNumber()){
						System.out.println(goods.getName()+"����");
						//��Ʒ���㣬��Ƥ
						result.setStatus(1);
						result.setMsg(goods.getName()+"����");
						return result;
					}
//					break;
				}
			}
		}
		//�����ȶ�goods����в���
		//ʹ��goods��������ȡ��Ӧ�޸ĵ�����,ͬʱ�����ܼ�
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
		//2����record����ӽ��׼�¼����¼type=1
		//ͬ����¼��record��
		Record updateRecord = new Record();
		updateRecord.setType(1);
		updateRecord.setDate(details.get(0).getIndiction());
		updateRecord.setDetailIndiction(details.get(0).getIndiction());
		updateRecord.setPrice(totalAmount);
		updateRecord.setUserId(userId);
		updateRecord.setPersonInCharge(personInCharge);
		recordDao.insert(updateRecord);
		
		//3����detail�н����н�����ϸ��¼����
		detailDao.insert(details);
		//4������Ǯ
		possessionDao.export(totalAmount);
		result.setStatus(0);
		result.setMsg("�������");
		return result;
	}

	//�����׼�¼����Ϊexcel
	public XSSFWorkbook exportExcelInfo(String indiction) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, IntrospectionException, ParseException{
		System.out.println(indiction);
		//�ȴ����ݿ��ȡ����
		List<DetailResult> resultList = detailDao.findByIndiction(indiction);
		for(int i=0;i<resultList.size();i++){
			System.out.println(resultList.get(i));
		}
		Map<Integer,List<ExcelBean>> map=new LinkedHashMap<Integer, List<ExcelBean>>();
		//���ñ�����
		List<ExcelBean> excel=new ArrayList<ExcelBean>();
		excel.add(new ExcelBean("��Ʒ��","name",0));
		excel.add(new ExcelBean("��λ", "unit", 0));
		excel.add(new ExcelBean("����","number",0));
		excel.add(new ExcelBean("����","unitprice",0));
		excel.add(new ExcelBean("���","amount",0));
		map.put(0, excel);
		//���ñ���
		String sheetName = "test";
		XSSFWorkbook workbook = ExcelUtil.createExcelFile(DetailResult.class, resultList, map, sheetName);
		return workbook;
	}
	
	public Result checkRecord(String indiction) {
		//����indiction����detail
		List<DetailResult> returnList = detailDao.findByIndiction(indiction);
		for(int i=0;i<returnList.size();i++){
			System.out.println(returnList.get(i));
		}
		//������detail��id��Ҫת����
		Result result = new Result();
		result.setStatus(0);
		result.setMsg("�����ɹ�");
		result.setData(returnList);
		return result;
	}

	public Result newGoods(Goods newGoods) {
		Result result = new Result(); 
		//���жϸ���Ʒ�Ƿ��Ѿ�����
		Goods findResult = goodsDao.findGoodsByName(newGoods.getName());
		if(findResult==null){
			//������������Ʒ�ӵ�goods���м���
			goodsDao.insert(newGoods);
			result.setStatus(0);
			result.setMsg("������Ʒ�ɹ�");
			return result;
		}else{
			result.setStatus(1);
			result.setMsg("����Ʒ�Ѵ���");
			return result;
		}
	}

	public Result importGoods(int userId, List<Detail> details, String[] units, String personInCharge) {
		for(int i=0;i<units.length;i++){
			System.out.println(units[i]);
		}
		Result result = new Result();
		//ȡ������goods
		List<Goods> goodsList = goodsDao.findAll();
		//1.����goods������,��Ҫ�����ܼ�
		double totalAmount = 0;
		for(int i=0;i<details.size();i++){
			//�Ƚ��л���λ
			for(int j=0;j<goodsList.size();j++){
				if(goodsList.get(j).getId()==details.get(i).getGoodsID()){
					Goods goods = goodsList.get(j);
					//�˴���Ʒ�Ѿ�ƥ��
					//�Ե�λ���бȽ�
					String currentUnit = units[i];//��ǰ��λ
					String unit = goods.getUnit();
					String unit2 = goods.getUnit2();
					if(currentUnit.equals(unit)){
						//��unitƥ��
						continue;
					}else if(currentUnit.equals(unit2)){
						//��unit2ƥ��,��ԭ�����������Խ���,�����۳��Խ���
						details.get(i).setNumber(details.get(i).getNumber()*goods.getHex());
						details.get(i).setUnitprice(details.get(i).getUnitprice()/goods.getHex());
					}else{
						//�������
						result.setStatus(1);
						result.setMsg("�������");
						return result;
					}
				}
			}
			//�����ﵥλ�����Ѿ��ܾ�
			//��goods��������������
			Goods goods = new Goods();
			goods.setId(details.get(i).getGoodsID());
			goods.setNumber(details.get(i).getNumber());
			goodsDao.addNumberById(goods);
			totalAmount+=details.get(i).getNumber()*details.get(i).getUnitprice();
		}
		//2.����record
		Record record = new Record();
		record.setDetailIndiction(details.get(0).getIndiction());
		record.setType(-1);
		record.setPrice(totalAmount);
		record.setUserId(userId);
		record.setPersonInCharge(personInCharge);
		recordDao.insert(record);
		//3.����detail
		detailDao.insert(details);
		//4������Ǯ
		possessionDao.importGoods(totalAmount);
		result.setStatus(0);
		result.setMsg("����ɹ�");
		return result;
	}

	public Result updateUserInfo(User user) {
		Result result = new Result();
		userDao.update(user);
		result.setStatus(0);
		result.setMsg("�޸ĳɹ�");
		return result;
	}

	public Result selectGoods(String condition, int pageNum, int numInPage) {
		Result result = new Result();
		Pattern p = Pattern.compile(condition);
		//�Ȼ�ȡ���е�goods
		List<Goods> goodsList = this.findAllGoods();
		//��������������ƥ����
		List<Goods> resultList = new ArrayList<Goods>();
		//����goodsList
		Matcher m;
		for(int i=0;i<goodsList.size();i++){
			//ȡ����Ʒ
			Goods goods = goodsList.get(i);
			//����
			String goodsName = goods.getName();
			//����
			m = p.matcher(goodsName);
			if(m.find()){
				//���ƥ�䵽���������뷵��������
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
			result.setMsg("�������������");
		}else{
			//�˴���ҳ����������
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
			result.setMsg("�������");
			result.setData(resultList2);
			//ʹ��status�������Ƿ�Ϊĩβҳ
		}
		return result;
	}

	public Result exportWithExcel(int userId, InputStream in, MultipartFile file, String personInCharge) throws Exception{
		String filename = file.getOriginalFilename();
		String extension = filename.substring(filename.indexOf('.'));
		if(extension.equalsIgnoreCase(".xls")||extension.equalsIgnoreCase(".xlsx")){
			Workbook work = ExcelUtil.getWorkbook(in, file.getOriginalFilename());
			List<Object> colNames = ExcelUtil.getColNameList(work);
			List<String> unitsList = new ArrayList<String>();//���ڴ�ŵ�λ
			boolean hasUnit,hasUnitPrice;
			//�ȵõ�ÿһ����ʲô����
			int goodsNameCell = -1;//��Ʒ��
			int unitCell = -1;//��λ
			int numberCell = -1;//����
			int unitpriceCell = -1;//����
			for(int i=0;i<colNames.size();i++){
				String temp = colNames.get(i).toString();
				if(temp.equals("��Ʒ��")){
					goodsNameCell = i;
				}else if(temp.equals("����")){
					unitpriceCell = i;
				}else if(temp.equals("����")){
					numberCell = i;
				}else if(temp.equals("��λ")){
					unitCell = i;
				}
			}
			//���жϸ�ʽ�Ƿ����Ҫ��,��Ʒ���������ǲ���ȱ�ٵ�
			if(goodsNameCell==-1||numberCell==-1){
				Result result = new Result();
				result.setStatus(1);
				result.setMsg("����excel��ʽ����ȷ");
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
			//�Ƚ�����ת����Detail���飬Ȼ���������ݴ���export�������ɴﵽĿ�ģ���ҪList<Detail>,int amount,int userId
			//�����û���������������goodname����Ҫ�Ƚ�goodnameת����goodsID
			//���ļ�������ת���������ʽ
			List<List<Object>> listob = ExcelUtil.getBankListByExcel(work);
			List<Detail> details = new ArrayList<Detail>();
			List<Double> amounts = new ArrayList<Double>();
			//��ȡʱ��
			Date date = new Date();
			String pattern = "yyyymmddhhmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			String indiction = sdf.format(date);
			//����
			for(int i=0;i<listob.size();i++){
				//��ȡ��һ��,��򵥵�����Ҫ��Ϊ��Ʒ��������������
				List<Object> row = listob.get(i);
//			System.out.println("rowSize��"+row.size());
				//����goodsName��ȡID
				String goodsName = String.valueOf(row.get(goodsNameCell));
				Goods goods = goodsDao.findGoodsByName(String.valueOf(row.get(goodsNameCell)));
				if(goods==null){
					//����Ϊ��
					if(goodsName.equals("")||goodsName==null){
						continue;
					}else{
						Result result = new Result();
						result.setStatus(1);
						result.setMsg(goodsName+"������");
						return result;
					}
				}
				Detail detail = new Detail();
				//��λ
				if(hasUnit==false){
					//û�е�λ��ʹ�õ�λ1
					unitsList.add(goods.getUnit());
				}else{
					//�е�λ,��excel�еĵ�λ�������鼴��
					unitsList.add(String.valueOf(row.get(unitCell)));
				}
				//�½�Detail
				int goodsID = goods.getId();
				//����
				if(hasUnitPrice){
					//�е�����ʹ��excel�еĵ���
					detail.setUnitprice(Double.parseDouble(String.valueOf(row.get(unitpriceCell))));
				}else{
					//û�е�����ʹ��Ĭ�ϵ���
					detail.setUnitprice(goods.getDefaultPrice());
				}
				detail.setGoodsID(goodsID);
				detail.setNumber(Double.parseDouble(String.valueOf(row.get(numberCell))));
				detail.setIndiction(indiction);
				detail.setAmount(detail.getNumber()*detail.getUnitprice());
				//��detail����details
				details.add(detail);
				//������
				double amount = detail.getNumber()*detail.getUnitprice();
				amounts.add(amount);
			}
			//����λ����arrayListת����String[]����
			String[] units = new String[unitsList.size()];
			for(int i=0;i<unitsList.size();i++){
				units[i] = unitsList.get(i);
			}
			return this.export(userId, details, units, personInCharge);
		}else{
			Result result = new Result();
			result.setStatus(1);
			result.setMsg("�����ļ���ʽ");
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
			//��ȡ��ǰʱ��
			Date date = new Date();
			String pattern = "yyyymmddhhmmss";
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			String indiction = sdf.format(date);
			//�Ȼ�ȡ���
			Workbook work = ExcelUtil.getWorkbook(in, file.getOriginalFilename());
			//���ݱ��õ�ÿһ�д���ʲô
			List<Object> colName = ExcelUtil.getColNameList(work);
			int goodsNameCell = -1;
			int unitCell = -1;
			int unitpriceCell = -1;
			int numberCell = -1;
			for(int i=0;i<colName.size();i++){
				String temp = colName.get(i).toString();
				if(temp.equals("��Ʒ��")){
					goodsNameCell = i;
				}else if(temp.equals("��λ")){
					unitCell = i;
				}else if(temp.equals("����")){
					numberCell = i;
				}else if(temp.equals("����")){
					unitpriceCell = i;
				}
			}
			//�ж�excel��ʽ�Ƿ������Ʒ���������ǲ��ɻ�ȱ�ģ��������ҲҪ��¼��
			if(goodsNameCell==-1||numberCell==-1||unitpriceCell==-1){
				Result result = new Result();
				result.setStatus(1);
				result.setMsg("excel�����ʽ����ȷ");
				return result;
			}
			List<List<Object>> listob = ExcelUtil.getBankListByExcel(work);
			//��λ
			if(unitCell==-1){
				hasUnit = false;
			}else{
				hasUnit = true;
			}
			//����
			for(int i=0;i<listob.size();i++){
				//�õ�һ��
				List<Object> row = listob.get(i);
				//ȡ����Ʒ����������Ʒ��ȡ����Ʒ
				String goodsName = String.valueOf(row.get(goodsNameCell)).trim();
				Goods goods = goodsDao.findGoodsByName(goodsName);
				if(goods==null){
					if(!goodsName.equals("")){
						//��������Ʒ,���û�е�λ��������
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
				//�Ե�λ���в���
				if(hasUnit){
					//�е��뵥λ��ֱ�ӽ���λ���뼴��
					unitList.add(String.valueOf(row.get(unitCell)));
				}else{
					//û�е�λ��ʹ��unit
					unitList.add(goods.getUnit());
				}
				//���ݸ���ȡ����Ϣ
				Detail detail = new Detail();
				detail.setGoodsID(goods.getId());
				detail.setNumber(Double.parseDouble(String.valueOf(row.get(numberCell))));
				detail.setUnitprice(Double.parseDouble(String.valueOf(row.get(unitpriceCell))));
				detail.setAmount(detail.getNumber()*detail.getUnitprice());
				detail.setIndiction(indiction);
				
				details.add(detail);
			}
			//����λ����ת����String[]
			String[] units = new String[unitList.size()];
			for(int i=0;i<unitList.size();i++){
				units[i] = unitList.get(i);
			}
			return importGoods(userId, details, units ,personInCharge);
		}else{
			Result result = new Result();
			result.setStatus(1);
			result.setMsg("�����ļ���ʽ");
			return result;
		}
	}

	public Result updateGoods(Goods goods) {
		Result result = new Result();
		goodsDao.updateGoodsInfo(goods);
		result.setStatus(0);
		result.setMsg("�޸ĳɹ�");
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
		result.setMsg("�����ɹ�");
		result.setData(recordResultList);
		return result;
	}

	public Result getUserNumber() {
		Result result = new Result();
		int count = userDao.getUserNumber();
		System.out.println("count:"+count);
		result.setStatus(0);
		result.setMsg("�����ɹ�");
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
		//1������indiction��detail�еļ�¼ȫ��ɾ��
		detailDao.deleteByIndiction(indiction);
		//2�����µ�detial����¼�����ݿ�
		detailDao.insert(details);
		//3���޸�record���ܼ�
		double totalAmount = 0;
		for(int i=0;i<details.size();i++){
			totalAmount+=details.get(i).getAmount();
		}
		UpdateRecordVO ur = new UpdateRecordVO();
		ur.setDetailIndiciton(indiction);
		ur.setPrice(totalAmount);
		recordDao.updatePriceByIndiction(ur);
		result.setStatus(0);
		result.setMsg("�޸ĳɹ�");
		return result;
	}

	public Result deleteRecord(String indiction) {
		Result result = new Result();
		//1������indiction��detail���е�ȫ��ɾ��
		detailDao.deleteByIndiction(indiction);
		//2������indiction�õ�record�е�price�����ʽ�ָ�
		Record record = recordDao.findWithIndiction(indiction);
		//�ж�����
		System.out.println("type:"+record.getType());
		System.out.println("price:"+record.getPrice());
		if(record.getType()==1){
			//���������������Ǯ
			possessionDao.undoExport(record.getPrice());
		}else{
			possessionDao.undoImport(record.getPrice());
		}
		//3������indictionɾ��record
		recordDao.deleteByIndiction(indiction);
		result.setStatus(0);
		result.setMsg("ɾ���ɹ�");
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
		//����
		for(int j=0;j<recordResults.size();j++){
			System.out.println(recordResults.get(j));
		}
		result.setData(recordResults);
		result.setMsg("�����ɹ�");
		return result;
	}

	public Result addUser(User user) {
		userDao.addUser(user);
		Result result = new Result();
		result.setStatus(0);
		result.setMsg("�����ɹ�");
		return result;
	}

	public Result deleteUser(int id) {
		Result result = new Result();
		userDao.deleteUser(id);
		result.setStatus(0);
		result.setMsg("�����ɹ�");
		return result;
	}

	public Result goodsDetail(int goodsId) {
		Result result = new Result();
		List<GoodsDetailResult> DetailResults = detailDao.goodsDetail(goodsId);
		for(int i=0;i<DetailResults.size();i++){
			System.out.println(DetailResults.get(i));
		}
		result.setStatus(0);
		result.setMsg("��ѯ�ɹ�");
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
			result.setMsg("û���ҵ����");
		}else{
			result.setStatus(0);
			result.setMsg("�����ɹ�");
			result.setData(resultList);
		}
		return result;
	}
}
