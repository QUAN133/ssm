package main.java.entity;

public class Record {
	private int id;
	private int type;
	private String date;
	private double price;
	private String detailIndiction;
	private int userId;
	private String personInCharge;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public void setDate(String string) {
		this.date = string;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getDetailIndiction() {
		return detailIndiction;
	}
	public void setDetailIndiction(String detailIndiction) {
		this.detailIndiction = detailIndiction;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getPersonInCharge() {
		return personInCharge;
	}
	public void setPersonInCharge(String personInCharge) {
		this.personInCharge = personInCharge;
	}
	@Override
	public String toString() {
		return "Record [id=" + id + ", type=" + type + ", date=" + date + ", price=" + price + ", detailIndiction="
				+ detailIndiction + ", userId=" + userId + ", personInCharge=" + personInCharge + "]";
	}
}
