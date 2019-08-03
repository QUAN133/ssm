package main.java.entity;

public class Goods {
	private int id;
	private String name;
	private String brand;
	private String unit;
	private String unit2;
	private double hex;
	private double defaultPrice;
	private double number;
	private double remindNum;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getUnit2() {
		return unit2;
	}
	public void setUnit2(String unit2) {
		this.unit2 = unit2;
	}
	public double getHex() {
		return hex;
	}
	public void setHex(double hex) {
		this.hex = hex;
	}
	public double getDefaultPrice() {
		return defaultPrice;
	}
	public void setDefaultPrice(double defaultPrice) {
		this.defaultPrice = defaultPrice;
	}
	public double getNumber() {
		return number;
	}
	public void setNumber(double number) {
		this.number = number;
	}
	public double getRemindNum() {
		return remindNum;
	}
	public void setRemindNum(double remindNum) {
		this.remindNum = remindNum;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	@Override
	public String toString() {
		return "Goods [id=" + id + ", name=" + name + ", brand=" + brand + ", unit=" + unit + ", unit2=" + unit2
				+ ", hex=" + hex + ", defaultPrice=" + defaultPrice + ", number=" + number + ", remindNum=" + remindNum
				+ "]";
	}
}
