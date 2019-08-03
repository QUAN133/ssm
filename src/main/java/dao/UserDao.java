package main.java.dao;

import java.util.List;

import main.java.entity.User;

public interface UserDao {

	User findByUsername(String username);

	List<User> findAll();

	void update(User user);

	int getUserNumber();

	void addUser(User user);

	void deleteUser(int id);

}
