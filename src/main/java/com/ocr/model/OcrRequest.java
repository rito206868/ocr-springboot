package com.ocr.model;

import java.io.Serializable;

public class OcrRequest implements Serializable{

	/**
	 * POJO for JSON store
	 */

	private static final long serialVersionUID = -5418629617091486891L;
	
	private String token;
	private String type;

	public OcrRequest() {

	}

	public OcrRequest(String token, String type) {
		super();
		this.token = token;
		this.type = type;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
