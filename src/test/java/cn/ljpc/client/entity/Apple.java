package cn.ljpc.client.entity;

import cn.ljpc.client.annotation.MyField;

public class Apple {
	@MyField(description = "苹果id", length = 100)
	protected int id;
	protected String name;

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

}
