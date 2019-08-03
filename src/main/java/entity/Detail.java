package main.java.entity;

public class Detail {
	private int id;
	private String indiction;
	private int goodsID;
	private double number;
	private double unitprice;
	private double amount;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIndiction() {
		return indiction;
	}
	public void setIndiction(String indiction) {
		this.indiction = indiction;
	}
	public int getGoodsID() {
		return goodsID;
	}
	public void setGoodsID(int goodsID) {
		this.goodsID = goodsID;
	}
	public double getNumber() {
		return number;
	}
	public void setNumber(double number) {
		this.number = number;
	}
	public double getUnitprice() {
		return unitprice;
	}
	public void setUnitprice(double unitprice) {
		this.unitprice = unitprice;
	}
	@Override
	public String toString() {
		return "Detail [id=" + id + ", indiction=" + indiction + ", goodsID=" + goodsID + ", number=" + number
				+ ", unitprice=" + unitprice + "]";
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
