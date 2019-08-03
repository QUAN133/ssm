package main.java.service;

import java.beans.IntrospectionException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import main.java.entity.*;
import main.java.util.DetailResult;
import main.java.util.RecordResult;
import main.java.util.Result;

public interface IService {
	public List<Goods> findAllGoods();
	
	public List<Goods> findAllGoodsWithPage(int pageNum, int numInPage);

	public Result login(String username, String password);

	public Result updateTable(String goodName);

	public Result export(int userId, List<Detail> details, String[] units, String personInCharge);

	public List<RecordResult> findAllRecord();

	public Result checkRecord(String indiction);

	public Result newGoods(Goods newGoods);

	public Result importGoods(int userId, List<Detail> details, String[] units, String personInCharge);

	public List<User> findAllUser();

	public Result updateUserInfo(User user);

	public Result selectGoods(String condition, int pageNum, int numInPage);

	public XSSFWorkbook exportExcelInfo(String indiction) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException, IntrospectionException, ParseException;
	
	public Result exportWithExcel(int userId, InputStream in, MultipartFile file, String personInCharge) throws Exception;
	
	public Result importWithExcel(int userId, InputStream in, MultipartFile file, String personInCharge) throws Exception;

	public Result updateGoods(Goods goods);

	public Result selectRecord(String type, String startTime, String endTime, String sort, int pageNum, int numInPage);

	public Result getUserNumber();

	public Result selectGoodsWithPage(int pageNum, int numInPage);

	public List<DetailResult> findDetailByIndiction(String indiction);

	public Result updateUnit(int goodsID);

	public Result updateRecord(List<Detail> details, String[] units, String indiction);

	public Result deleteRecord(String indiction);

	public Possession getPossession();

	public List<RecordResult> findRecordWithPageNum(int i, int numInPage);

	public Result selectRecordWithPageNum(int i, int numInPage);

	public Result addUser(User user);

	public Result deleteUser(int id);

	public Result goodsDetail(int goodsId);

	public Result findWithIndiction(String indiction);
}
