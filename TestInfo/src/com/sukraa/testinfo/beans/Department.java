package com.sukraa.testinfo.beans;

public class Department {

	private String deptCode, deptName, desc;
	private int testCount;
	
	public Department(){
		
	}
	
	public Department(String deptCode, String deptName, int testCount, String desc){
		this.deptCode = deptCode;
		this.deptName = deptName;
		this.testCount = testCount;
		this.desc = desc;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public int getTestCount() {
		return testCount;
	}

	public void setTestCount(int testCount) {
		this.testCount = testCount;
	}
	
	public String getDesc(){
		return desc;
	}
	
	public void setDesc(String desc){
		this.desc = desc;
	}
	
	@Override
	public String toString(){
		return this.deptName;
	}
	
}
