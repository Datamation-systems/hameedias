package com.datamation.hmdsfa.controller;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.datamation.hmdsfa.helpers.DatabaseHelper;
import com.datamation.hmdsfa.helpers.SharedPref;
import com.datamation.hmdsfa.model.Discdeb;
import com.datamation.hmdsfa.model.Disched;
import com.datamation.hmdsfa.model.Discount;
import com.datamation.hmdsfa.model.Discslab;
import com.datamation.hmdsfa.model.InvDet;
import com.datamation.hmdsfa.model.ItemBundle;
import com.datamation.hmdsfa.model.OrderDetail;
import com.datamation.hmdsfa.model.SalesPrice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DiscountController {

    private SQLiteDatabase dB;
    private DatabaseHelper DbHelper;
    Context context;
    private String TAG = "DiscountController";

    // table
    public static final String TABLE_DISCOUNT  = "discount";
    // table attributes
    public static final String  DISCOUNT_DEBCODE = "DebCode";
    public static final String  DISCOUNT_DEBNAME= "DebName";
    public static final String  DISCOUNT_LOCCODE = "LocCode";
    public static final String  DISCOUNT_PRODUCT_DIS = "ProductDis";
    public static final String  DISCOUNT_PRODUCT_CASH_DIS = "ProductCashDis";
    public static final String  DISCOUNT_PRODUCT_GROUP= "ProductGroup";
    public static final String  DISCOUNT_REPCODE = "RepCode";

    // create String
    public static final String CREATE_TABLE_DISCOUNT = "CREATE  TABLE IF NOT EXISTS " + TABLE_DISCOUNT + " ("

            + DISCOUNT_PRODUCT_CASH_DIS + " TEXT, "    + DISCOUNT_REPCODE + " TEXT, " + DISCOUNT_PRODUCT_GROUP + " TEXT, "      + DISCOUNT_DEBCODE + " TEXT, " + DISCOUNT_DEBNAME + " TEXT, " + DISCOUNT_LOCCODE + " TEXT, " + DISCOUNT_PRODUCT_DIS + " TEXT); ";



    public DiscountController(Context context) {
        this.context = context;
        DbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException
    {
        dB = DbHelper.getWritableDatabase();
    }

    public void InsertOrReplaceDiscount(ArrayList<Discount> list) {
        Log.d("InsertOrReplaceSalesPri", "" + list.size());
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {
            dB.beginTransactionNonExclusive();
            String sql = "INSERT OR REPLACE INTO " + TABLE_DISCOUNT + " (DebCode,DebName,LocCode,ProductDis,ProductGroup,RepCode,ProductCashDis) " + " VALUES (?,?,?,?,?,?,?)";

            SQLiteStatement stmt = dB.compileStatement(sql);

            for (Discount discount : list) {
                stmt.bindString(1, discount.getDebCode());
                stmt.bindString(2, discount.getDebName());
                stmt.bindString(3, discount.getLocCode());
                stmt.bindString(4, discount.getProductDis());
                stmt.bindString(5, discount.getProductGroup());
                stmt.bindString(6, discount.getRepCode());
                stmt.bindString(7, discount.getProductCashDis());
                stmt.execute();
                stmt.clearBindings();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            dB.setTransactionSuccessful();
            dB.endTransaction();
            dB.close();
        }

    }
    public int IsDiscountCustomer(String debcode) {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;


        try {

            String selectQuery = "SELECT * FROM " + TABLE_DISCOUNT + " WHERE " + DISCOUNT_DEBCODE + " = '" + debcode + "'";

            cursor = dB.rawQuery(selectQuery, null);

            ContentValues values = new ContentValues();

            //  values.put(DatabaseHelper.FINVHED_IS_ACTIVE, "0");

            int cn = cursor.getCount();
            count = cn;

//            if (cn > 0) {
//                count = dB.update(DatabaseHelper.TABLE_FINVHED, values, DatabaseHelper.REFNO + " =?", new String[]{String.valueOf(refno)});
//            } else {
//                count = (int) dB.insert(DatabaseHelper.TABLE_FINVHED, null, values);
//            }

        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }
        return count;

    }
    public int deleteAll() {

        int count = 0;

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            cursor = dB.rawQuery("SELECT * FROM " + TABLE_DISCOUNT, null);
            count = cursor.getCount();
            if (count > 0) {
                int success = dB.delete(TABLE_DISCOUNT, null, null);
                Log.v("Success", success + "");
            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return count;

    }

    public ArrayList<Discount> getDiscountInfo(String debcode) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        Discount discount = new Discount();

        String selectQuery = "select * from discount where DebCode = '" + debcode + "'";

        Cursor cursor = dB.rawQuery(selectQuery, null);
        ArrayList<Discount> discounts = new ArrayList<>();
        try {

            while (cursor.moveToNext()) {
                discount.setProductDis(cursor.getString(cursor.getColumnIndex(DISCOUNT_PRODUCT_DIS)));
                discount.setProductGroup(cursor.getString(cursor.getColumnIndex(DISCOUNT_PRODUCT_GROUP)));
                discounts.add(discount);
            }
        } catch (Exception e) {
            Log.v(TAG + " Exception", e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discounts;
    }
    public Discount getSchemeByItemCode(String productgroup,String debcode,String discountClmIndex) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }


        // commented due to date format issue and M:D:Y format is available in DB
        //String selectQuery = "select * from fdisched where refno in (select refno from fdiscdet where itemcode='" + itemCode + "') and date('now') between vdatef and vdatet";
        String selectQuery = "select * from discount where ProductGroup  = '" + productgroup + "'  and DebCode = '" + debcode + "'";

        Discount discount = new Discount();
        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {

                discount.setProductDis(cursor.getString(cursor.getColumnIndex(discountClmIndex)));

            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return discount;
    }

    //rashmi 20200615
    public ArrayList<InvDet> updateInvDiscount(ArrayList<InvDet> ordArrList, String debcode) {

        ArrayList<InvDet> newMetaList = new ArrayList<InvDet>();

            /* For each invoice object inside ordeArrList ArrayList */
            for (InvDet mTranSODet : ordArrList) {
                ItemBundle item = new ItemBundleController(context).getItem(mTranSODet.getFINVDET_ITEM_CODE());
                Discount discountdets = null;
//                String productgroup = new ItemBundleController(context).getProductGroup(mTranSODet.getFINVDET_ITEM_CODE(),mTranSODet.getFINVDET_BARCODE());
                String productgroup = new ItemController(context).getItemGroupByCode(mTranSODet.getFINVDET_ITEM_CODE(),debcode);

                if(new SharedPref(context).getGlobalVal("KeyPayType").equals("CASH")){
                    if(productgroup.equals("")) {
                        discountdets = getSchemeByItemCode("OTHERS", debcode, "ProductCashDis");
                    }else{
                        discountdets = getSchemeByItemCode(productgroup, debcode, "ProductCashDis");
                    }
                }else{
                    if(productgroup.equals("")) {
                         discountdets = getSchemeByItemCode("OTHERS",debcode,"ProductDis");
                    }else{
                        discountdets = getSchemeByItemCode(productgroup,debcode,"ProductDis");
                    }
                }


                if (discountdets.getProductDis() != null) {
                                            /* Update table directly */
                        double discPrice = ((Double.parseDouble(mTranSODet.getFINVDET_SELL_PRICE()) / 100) * (Double.parseDouble(discountdets.getProductDis())));
                        mTranSODet.setFINVDET_SCHDISPER(discountdets.getProductDis());
                        mTranSODet.setFINVDET_DIS_PER(discountdets.getProductDis());
                        mTranSODet.setFINVDET_DIS_AMT(String.valueOf(discPrice* (Double.parseDouble(mTranSODet.getFINVDET_QTY()))));
                        if(new CustomerController(context).getCustomerVatStatus(new SharedPref(context).getSelectedDebCode()).trim().equals("VAT"))
                            mTranSODet.setFINVDET_B_SELL_PRICE(String.valueOf((Double.parseDouble(mTranSODet.getFINVDET_SELL_PRICE())) - discPrice));//pass for calculate tax forqow
                        else
//                            mTranSODet.setFINVDET_B_SELL_PRICE(String.valueOf(Double.parseDouble(mTranSODet.getFINVDET_SELL_PRICE())));//pass for calculate tax forqow
                            mTranSODet.setFINVDET_B_SELL_PRICE(String.valueOf((Double.parseDouble(mTranSODet.getFINVDET_SELL_PRICE())) - discPrice));//pass for calculate tax forqow
                    }else{
                        mTranSODet.setFINVDET_SCHDISPER("0");
                        mTranSODet.setFINVDET_DIS_PER("0");
                        mTranSODet.setFINVDET_DIS_AMT("0");
                        mTranSODet.setFINVDET_B_SELL_PRICE(mTranSODet.getFINVDET_B_SELL_PRICE());//pass for calculate tax forqow

                }
                    newMetaList.add(mTranSODet);

                }




        return newMetaList;

    }
    public ArrayList<OrderDetail> updateOrdDiscount(ArrayList<OrderDetail> ordArrList, String debcode) {

        ArrayList<OrderDetail> newMetaList = new ArrayList<OrderDetail>();

        /* For each invoice object inside ordeArrList ArrayList */
        for (OrderDetail mTranSODet : ordArrList) {
            Discount discountdets = null;
//Original 09-11-2020            String productgroup = new ItemBundleController(context).getProductGroup(mTranSODet.getFORDERDET_ITEMCODE(),mTranSODet.getFORDERDET_BARCODE()); //2020-07-15 by rashmi
            //Original 09-11-2020             String productgroup = new ItemBundleController(context).getProductGroup(mTranSODet.getFORDERDET_ITEMCODE(),debcode); //2020-11-09 by Menaka
            String productgroup = new ItemController(context).getItemGroupByCode(mTranSODet.getFORDERDET_ITEMCODE(),debcode);

            if(new SharedPref(context).getGlobalVal("KeyPayType").equals("CASH")){
                if(productgroup.equals("")) {
                    discountdets = getSchemeByItemCode("OTHERS", debcode, "ProductCashDis");
                }else{
                    discountdets = getSchemeByItemCode(productgroup, debcode, "ProductCashDis");
                }
            }else{
                if(productgroup.equals("")) {
                    discountdets = getSchemeByItemCode("OTHERS",debcode,"ProductDis");
                }else{
                    discountdets = getSchemeByItemCode(productgroup,debcode,"ProductDis");
                }
            }


            if (discountdets.getProductDis() != null) {
                /* Update table directly */
                double discPrice = ((Double.parseDouble(mTranSODet.getFORDERDET_SELLPRICE()) / 100) * (Double.parseDouble(discountdets.getProductDis())));
                mTranSODet.setFORDERDET_SCHDISPER(discountdets.getProductDis());
                mTranSODet.setFORDERDET_DISPER(discountdets.getProductDis());
                mTranSODet.setFORDERDET_DISAMT(String.valueOf(discPrice* (Double.parseDouble(mTranSODet.getFORDERDET_QTY()))));
                if(new CustomerController(context).getCustomerVatStatus(new SharedPref(context).getSelectedDebCode()).trim().equals("VAT"))
                    mTranSODet.setFORDERDET_BSELLPRICE(String.valueOf((Double.parseDouble(mTranSODet.getFORDERDET_SELLPRICE())) - discPrice));//pass for calculate tax forqow
                else
                    mTranSODet.setFORDERDET_BSELLPRICE(String.valueOf(Double.parseDouble(mTranSODet.getFORDERDET_SELLPRICE())- discPrice));//pass for calculate tax forqow
            }else{
                mTranSODet.setFORDERDET_SCHDISPER("0");
                mTranSODet.setFORDERDET_DISPER("0");
                mTranSODet.setFORDERDET_DISAMT("0");
                mTranSODet.setFORDERDET_BSELLPRICE(mTranSODet.getFORDERDET_BSELLPRICE());//pass for calculate tax forqow

            }
            newMetaList.add(mTranSODet);

        }

        return newMetaList;

    }

//    // ---------------------------------- kaveesha -------------- 20/05/2021 -------------------------------------------------------------------
//    public ArrayList<InvDet> getDiscountItems(String payType, String debcode) {
//
//        ArrayList<InvDet> newMetaList = new ArrayList<InvDet>();
//        Discount discountdets = null;
//
//             if(payType.equals("CA")){
//                    discountdets = getSchemeByType( debcode, "ProductCashDis");
//            }else{
//                    discountdets = getSchemeByType(debcode,"ProductDis");
//            }
//
//            InvDet invDet = new InvDet();
//
//            if (discountdets.getProductDis() != null) {
//
//                invDet.setFINVDET_DIS_PER(discountdets.getProductDis());
//            }
//            newMetaList.add(invDet);
//
//        return newMetaList;
//    }

    // ---------------------------- kaveesha ------------------ 20/05/2021 -------------------------------------------
    public ArrayList<Discount> getDiscountItems(String payType,String debcode) {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        String selectQuery = "select * from discount where DebCode = '" + debcode + "'";
        //String selectQuery = "select * from discount where ProductGroup  = '" + productgroup + "'  and DebCode = '" + debcode + "'";
        ArrayList<Discount> newMetaList = new ArrayList<Discount>();

        Cursor cursor = dB.rawQuery(selectQuery, null);

        try {
            while (cursor.moveToNext()) {
                Discount discount = new Discount();
                discount.setProductDis(cursor.getString(cursor.getColumnIndex(DISCOUNT_PRODUCT_DIS)));
                discount.setProductCashDis(cursor.getString(cursor.getColumnIndex(DISCOUNT_PRODUCT_CASH_DIS)));
                discount.setProductGroup(cursor.getString(cursor.getColumnIndex(DISCOUNT_PRODUCT_GROUP)));
                discount.setPayType(payType);
                newMetaList.add(discount);
            }
        } catch (Exception e) {

            Log.v(TAG + " Exception", e.toString());

        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dB.close();
        }

        return newMetaList;
    }


}
