package com.sukraa.testinfo.beans;

public class Item {

	private String itemName;
	private ItemType type;
	
	public enum ItemType {
		DEPARTMENT, TEST, PROFILE, TEST_IN_PROFILE, FAVORITE;
	}
	
	public Item(String itemName, ItemType type){
		this.itemName = itemName;
		this.type = type;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public ItemType getType() {
		return type;
	}

	public void setType(ItemType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return itemName;
	}
	
}
