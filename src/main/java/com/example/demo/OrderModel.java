package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {

	public int patientId;
	public int orderId;
	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String purchaseDate;
	public String patientContactNo;

	public int paymentStatus;

	public List<MedicineOrderDetails> medicineList = new ArrayList<MedicineOrderDetails>();

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	/*
	 * public int getOrderId() { return orderId; }
	 * 
	 * public void setOrderId(int orderId) { this.orderId = orderId; }
	 */
	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getPatientContactNo() {
		return patientContactNo;
	}

	public void setPatientContactNo(String patientContactNo) {
		this.patientContactNo = patientContactNo;
	}

	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public List<MedicineOrderDetails> getMedicineList() {
		return medicineList;
	}

	public void setMedicineList(List<MedicineOrderDetails> medicineList) {
		this.medicineList = medicineList;
	}
}