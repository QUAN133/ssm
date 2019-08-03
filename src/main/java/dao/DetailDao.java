package main.java.dao;

import java.util.List;

import main.java.entity.*;
import main.java.util.DetailResult;
import main.java.util.GoodsDetailResult;

public interface DetailDao {
	public List<Detail> findAll();

	public void insert(List<Detail> details);

	public List<DetailResult> findByIndiction(String indiction);

	public void deleteByIndiction(String indiction);

	public List<GoodsDetailResult> goodsDetail(int goodsId);
}
