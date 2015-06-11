package org.aars.android.beans;

import java.io.Serializable;

public class Transaction implements Serializable {

	private static final long serialVersionUID = -5684590468976606403L;

	public enum Type {
		INCOME, EXPENSE;
	}

	private Type type;
	private int id;
	private String date;
	private String name;
	private int amount;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
}
