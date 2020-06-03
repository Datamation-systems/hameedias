package com.datamation.hmdsfa.model;

import com.google.gson.annotations.SerializedName;

public class ItenrDeb {

	@SerializedName("DebCode")
	private String DebCode;
	@SerializedName("RefNo")
	private String RefNo;
	@SerializedName("RouteCode")
	private String RouteCode;
	@SerializedName("TxnDate")
	private String TxnDate;

	public String getDebCode() {
		return DebCode;
	}

	public void setDebCode(String debCode) {
		DebCode = debCode;
	}

	public String getRefNo() {
		return RefNo;
	}

	public void setRefNo(String refNo) {
		RefNo = refNo;
	}

	public String getRouteCode() {
		return RouteCode;
	}

	public void setRouteCode(String routeCode) {
		RouteCode = routeCode;
	}

	public String getTxnDate() {
		return TxnDate;
	}

	public void setTxnDate(String txnDate) {
		TxnDate = txnDate;
	}
}