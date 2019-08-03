package main.java.dao;

import main.java.entity.Possession;

public interface PossessionDao {

	void export(double totalAmount);

	void importGoods(double totalAmount);

	Possession getPossession();

	void undoImport(double price);

	void undoExport(double price);
	
}
