package com.mintcode.area_patient.entity;

import java.io.Serializable;

public class Cpx implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double height;

	private double weight;

	private double bmi;

	private double waist;

	private double hip;

	private double sbp;

	private double dbp;

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getBmi() {
		if(this.weight>0&&this.height>0){
			this.bmi=(double) this.getWeight()
					/ (Math.pow((double) this.getHeight() * 0.01, 2.0));
		}else{
			this.bmi=0.0;
		}
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}

	public double getWaist() {
		return waist;
	}

	public void setWaist(double waist) {
		this.waist = waist;
	}

	public double getHip() {
		return hip;
	}

	public void setHip(double hip) {
		this.hip = hip;
	}

	public double getSbp() {
		return sbp;
	}

	public void setSbp(double sbp) {
		this.sbp = sbp;
	}

	public double getDbp() {
		return dbp;
	}

	public void setDbp(double dbp) {
		this.dbp = dbp;
	}


}
