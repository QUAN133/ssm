package main.java.dao;

import java.util.List;

import main.java.entity.Record;
import main.java.util.PageRecordVO;
import main.java.util.RecordResult;
import main.java.util.SearchRecordVO;
import main.java.util.UpdateRecordVO;

public interface RecordDao {

	void insert(Record updateRecord);

	List<Record> findAll();

	List<RecordResult> findRecordWithUser();

	List<RecordResult> findWithCondition(SearchRecordVO vo);

	void updatePriceByIndiction(UpdateRecordVO ur);

	void deleteByIndiction(String indiction);

	Record findWithIndiction(String indiction);

	List<RecordResult> findRecordWithPage(PageRecordVO vo);

	List<RecordResult> findRecordResultWithIndiction(String indiction);
 
}
