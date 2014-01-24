package com.sukraa.testinfo.beans;

import java.io.Serializable;

public class Test implements Serializable {

	private static final long serialVersionUID = -4180163966544477150L;
	
	private String deptCode;
	private String testCode;
	private String testName;
	private double amount;
	private double unit;
	private String remarks;
	private String desc;
	private String referenceValue;
	private String criticalValue;
	
	public String getDeptCode() {
		return deptCode;
	}
	
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	
	public String getTestCode() {
		return testCode;
	}
	
	public void setTestCode(String testCode) {
		this.testCode = testCode;
	}
	
	public String getTestName() {
		return testName;
	}
	
	public void setTestName(String testName) {
		this.testName = testName;
	}
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public double getUnit() {
		return unit;
	}
	
	public void setUnit(double unit) {
		this.unit = unit;
	}

	public String getRemarks(){
		return remarks;
	}
	
	public void setRemarks(String remarks){
		this.remarks = remarks;
	}
	
	public String getDesc(){
		return desc;
	}
	
	public void setDesc(String desc){
		this.desc = desc;
	}
	
	public String getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}

	public String getCriticalValue() {
		return criticalValue;
	}

	public void setCriticalValue(String criticalValue) {
		this.criticalValue = criticalValue;
	}
	
	@Override
	public String toString(){
		return this.testName;
	}

}
