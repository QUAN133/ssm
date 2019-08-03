package main.java.util;

public class GoodsDetailResult {
	private String indiction;
	private String personInCharge;
	private int type;
	private String date;
	private int number;
	private int unitprice;
	private int amount;
	public String getIndiction() {
		return indiction;
	}
	public void setIndiction(String indiction) {
		this.indiction = indiction;
	}
	public String getPersonInCharge() {
		return personInCharge;
	}
	public void setPersonInCharge(String personInCharge) {
		this.personInCharge = personInCharge;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getUnitprice() {
		return unitprice;
	}
	public void setUnitprice(int unitprice) {
		this.unitprice = unitprice;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "GoodsDetailResult [indiction=" + indiction + ", personInCharge=" + personInCharge + ", type=" + type
				+ ", date=" + date + ", number=" + number + ", unitprice=" + unitprice + ", amount=" + amount + "]";
	}
}
