package main.java.entity;

public class User {
	private int id;
	private String username;
	private String password;
	private boolean canQuery;
	private boolean canInsert;
	private boolean canUpdate;
	private String role;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isCanQuery() {
		return canQuery;
	}
	public void setCanQuery(boolean canQuery) {
		this.canQuery = canQuery;
	}
	public boolean isCanInsert() {
		return canInsert;
	}
	public void setCanInsert(boolean canInsert) {
		this.canInsert = canInsert;
	}
	public boolean isCanUpdate() {
		return canUpdate;
	}
	public void setCanUpdate(boolean canUpdate) {
		this.canUpdate = canUpdate;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", canQuery=" + canQuery
				+ ", canInsert=" + canInsert + ", canUpdate=" + canUpdate + ", role=" + role + "]";
	}
}
