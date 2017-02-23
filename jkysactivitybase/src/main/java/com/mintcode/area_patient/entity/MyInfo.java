package com.mintcode.area_patient.entity;

import java.io.Serializable;


public class MyInfo implements Serializable {

	private String avatar;
	
	private String name;
	
	private int sex;
	
	private String mobile;
	
	private long bday;
	
	private long modified;
	
	private String nickname;
	
	public long getModified() {
		return modified;
	}

	public void setModified(long modified) {
		this.modified = modified;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public long getBday() {
		return bday;
	}

	public void setBday(long bday) {
		this.bday = bday;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	
	
}
