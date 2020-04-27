package com.ocr.model;

import java.io.Serializable;

public class OcrResponse implements Serializable {

	/**
	 * POJO for JSON store
	 */

	private static final long serialVersionUID = 5201934327356000929L;

	private String name;
	private String dob;
	private String uid;

	public OcrResponse() {

	}

	public OcrResponse(String name, String dob, String uid) {
		super();
		this.name = name;
		this.dob = dob;
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
