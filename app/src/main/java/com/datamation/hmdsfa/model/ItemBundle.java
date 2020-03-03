package com.datamation.hmdsfa.model;


import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class ItemBundle {

    @SerializedName("DocumentNo")
    private String DocumentNo;
    @SerializedName("Barcode")
    private String Barcode;
    @SerializedName("Description")
    private String Description;
    @SerializedName("ItemNo")
    private String ItemNo;
    @SerializedName("VariantCode")
    private String VariantCode;
    @SerializedName("VariantColour")
    private String VariantColour;
    @SerializedName("VariantSize")
    private String VariantSize;
    @SerializedName("Quantity")
    private int Quantity;

    public String getDocumentNo() {
        return DocumentNo;
    }

    public void setDocumentNo(String documentNo) {
        DocumentNo = documentNo;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getVariantCode() {
        return VariantCode;
    }

    public void setVariantCode(String variantCode) {
        VariantCode = variantCode;
    }

    public String getVariantColour() {
        return VariantColour;
    }

    public void setVariantColour(String variantColour) {
        VariantColour = variantColour;
    }

    public String getVariantSize() {
        return VariantSize;
    }

    public void setVariantSize(String variantSize) {
        VariantSize = variantSize;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }
}