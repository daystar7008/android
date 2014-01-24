package com.sukraa.testinfo.beans;

import java.io.Serializable;
import java.util.List;

public class Profile implements Serializable {

	private static final long serialVersionUID = 4152516955670173935L;
	
	private String profileCode;
	private String profileName;
	private double amount;
	private String remarks;
	private List<Test> tests;
	
	public String getProfileCode() {
		return profileCode;
	}
	
	public void setProfileCode(String profileCode) {
		this.profileCode = profileCode;
	}
	
	public String getProfileName() {
		return profileName;
	}
	
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String getRemarks() {
		return remarks;
	}
	
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public List<Test> getTests() {
		return tests;
	}
	
	public void setTests(List<Test> tests) {
		this.tests = tests;
	}
	
}
