package com.jkys.data;

import java.io.Serializable;

public class BaseResult implements Serializable {
	
	protected String action;
	
	protected int code;
	
	protected String message;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isResultSuccess() {
		return code / 1000 == 2;
	}

	public boolean isLogout() {
		return code / 1000 == 4;
	}

}
