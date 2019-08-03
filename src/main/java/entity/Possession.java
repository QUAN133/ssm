package main.java.entity;

public class Possession {
	private double money;
	private double earning;
	private double consume;
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public double getEarning() {
		return earning;
	}
	public void setEarning(double earning) {
		this.earning = earning;
	}
	public double getConsume() {
		return consume;
	}
	public void setConsume(double consume) {
		this.consume = consume;
	}
	@Override
	public String toString() {
		return "Possession [money=" + money + ", earning=" + earning + ", consume=" + consume + "]";
	}
}
