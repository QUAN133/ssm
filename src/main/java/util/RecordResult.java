package main.java.util;

public class RecordResult {
	private int id;
	private int type;
	private String date;
	private double price;
	private String detailIndiction;
	private String username;
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "RecordResult [id=" + id + ", type=" + type + ", date=" + date + ", price=" + price
				+ ", detailIndiction=" + detailIndiction + ", username=" + username + "]";
	}
	public String getPersonInCharge() {
		return personInCharge;
	}
	public void setPersonInCharge(String personInCharge) {
		this.personInCharge = personInCharge;
	}
}
