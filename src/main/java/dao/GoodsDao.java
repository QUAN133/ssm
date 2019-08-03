package main.java.dao;

import java.util.List;

import main.java.entity.Goods;
import main.java.util.PageRecordVO;

public interface GoodsDao {
	public List<Goods> findAll();

	public Goods findGoodsByName(String goodsName);

	public void reduceNumberByID(Goods goods);

	public void insert(Goods newGoods);

	public void addNumberById(Goods good);

	public void updateGoodsInfo(Goods goods);

	public List<Goods> findAllGoodsWithPage(PageRecordVO pg);
}
