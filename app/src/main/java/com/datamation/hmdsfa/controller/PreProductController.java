package com.datamation.hmdsfa.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.datamation.hmdsfa.helpers.DatabaseHelper;
import com.datamation.hmdsfa.model.PreProduct;

import java.util.ArrayList;

public class PreProductController {

    Context context;
    private SQLiteDatabase dB;
    private DatabaseHelper dbHelper;
    // Pre Product Table
    // rashmi - 2019-12-19 move from database_helper , because of reduce coding in database helper*******************************************************************************

    public static final String TABLE_FPRODUCT_PRE = "fProducts_pre";
    public static final String FPRODUCT_ID_PRE = "id";
    public static final String FPRODUCT_ITEMCODE_PRE = "itemcode_pre";
    public static final String FPRODUCT_ITEMNAME_PRE = "itemname_pre";
    public static final String FPRODUCT_PRICE_PRE = "price_pre";
    public static final String FPRODUCT_QOH_PRE = "qoh_pre";
    public static final String FPRODUCT_QTY_PRE = "qty_pre";
    public static final String FPRODUCT_BAL_QTY = "BalQty";
    public static final String FPRODUCT_REACODE = "ReaCode";
    public static final String FPRODUCT_CASE_PRE = "case_pre";
    public static final String FPRODUCT_UNITS = "units";
    public static final String FPRODUCT_TYPE = "type";

    public static final String CREATE_FPRODUCT_PRE_TABLE = "CREATE  TABLE IF NOT EXISTS " + TABLE_FPRODUCT_PRE + " ("
            + FPRODUCT_ID_PRE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FPRODUCT_ITEMCODE_PRE + " TEXT, "
            + FPRODUCT_ITEMNAME_PRE + " TEXT, "
            + FPRODUCT_PRICE_PRE + " TEXT, "
           // + ProductController.FPRODUCT_CHANGED_PRICE + " TEXT, "
            + FPRODUCT_QOH_PRE + " TEXT, "
            + FPRODUCT_CASE_PRE + " TEXT, "
            + FPRODUCT_UNITS + " TEXT, "
            + FPRODUCT_REACODE + " TEXT, "
            + FPRODUCT_TYPE + " TEXT, "
            + FPRODUCT_QTY_PRE + " TEXT); ";
    public static final String INDEX_PRODUCTS = "CREATE UNIQUE INDEX IF NOT EXISTS ui_fProducts_pre ON fProducts_pre (itemcode_pre,itemname_pre,type);";

    public PreProductController(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        dB = dbHelper.getWritableDatabase();
    }

    public boolean tableHasRecords() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        boolean result = false;
        Cursor cursor = null;

        try {
            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FPRODUCT_PRE, null);

            if (cursor.getCount() > 0)
                result = true;
            else
                result = false;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            dB.close();

        }

        return result;

    }

    public ArrayList<PreProduct> getAllItems(String newText) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        ArrayList<PreProduct> list = new ArrayList<>();
        try {
            //cursor = dB.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FPRODUCT_PRE + " WHERE itemcode || itemname LIKE '%" + newText + "%' and TxnType = '"+txntype+"' ORDER BY QOH DESC", null);
            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FPRODUCT_PRE + " WHERE itemcode_pre || itemname_pre LIKE '%" + newText + "%' group by itemcode_pre", null);

            while (cursor.moveToNext()) {
                PreProduct product = new PreProduct();
                product.setPREPRODUCT_ID(cursor.getString(cursor.getColumnIndex(FPRODUCT_ID_PRE)));
                product.setPREPRODUCT_ITEMCODE(cursor.getString(cursor.getColumnIndex(FPRODUCT_ITEMCODE_PRE)));
                product.setPREPRODUCT_ITEMNAME(cursor.getString(cursor.getColumnIndex(FPRODUCT_ITEMNAME_PRE)));
                product.setPREPRODUCT_PRICE(cursor.getString(cursor.getColumnIndex(FPRODUCT_PRICE_PRE)));
                product.setPREPRODUCT_QOH(cursor.getString(cursor.getColumnIndex(FPRODUCT_QOH_PRE)));
                product.setPREPRODUCT_QTY(cursor.getString(cursor.getColumnIndex(FPRODUCT_QTY_PRE)));
//                product.setPREPRODUCT_TXN_TYPE(cursor.getString(cursor.getColumnIndex(DatabaseHelper.FPRODUCT_TXNTYPE)));
                list.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            dB.close();
        }

        return list;
    }

    public void updateProductQty(String itemCode, String qty) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            ContentValues values = new ContentValues();
            values.put(FPRODUCT_QTY_PRE, qty);
            dB.update(TABLE_FPRODUCT_PRE, values, FPRODUCT_ITEMCODE_PRE + " =?", new String[]{String.valueOf(itemCode)});

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dB.close();
        }
    }
    public void updateReason(String itemCode, String reason, String type) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            ContentValues values = new ContentValues();
            values.put(FPRODUCT_REACODE, reason);
            dB.update(TABLE_FPRODUCT_PRE, values,  FPRODUCT_ITEMCODE_PRE
                    + " = '" + itemCode + "' and "+FPRODUCT_TYPE + " = '" + type + "' ", null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dB.close();
        }
    }
    public void updateProductCase(String itemCode, String cases, String type) {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            ContentValues values = new ContentValues();
            values.put(FPRODUCT_CASE_PRE, cases);
            dB.update(TABLE_FPRODUCT_PRE, values,  FPRODUCT_ITEMCODE_PRE
                    + " = '" + itemCode + "' and "+FPRODUCT_TYPE + " = '" + type + "' ",null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dB.close();
        }
    }
    public int updateQuantities(String itemCode,String qty) {
        int count = 0;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {

            ContentValues values = new ContentValues();
            values.put(FPRODUCT_QTY_PRE, qty);
            count=(int)dB.update(TABLE_FPRODUCT_PRE, values,FPRODUCT_ITEMCODE_PRE + " =?", new String[]{String.valueOf(itemCode)});


//            String selectQuery = "SELECT * FROM " + TABLE_FPRODUCT_PRE + " WHERE " + FPRODUCT_ITEMCODE_PRE
//                    + " = '" + itemCode + "' and "+FPRODUCT_TYPE + " = '" + type + "' ";
//
//            cursor = dB.rawQuery(selectQuery, null);
//
//            ContentValues values = new ContentValues();
//            values.put(FPRODUCT_ITEMCODE_PRE, itemCode);
//            values.put(FPRODUCT_ITEMNAME_PRE, itemname);
//            values.put(FPRODUCT_PRICE_PRE, price);
//            values.put(FPRODUCT_QOH_PRE, qoh);
//            values.put(FPRODUCT_QTY_PRE, qty);
//            values.put(FPRODUCT_CASE_PRE, cases);
//            values.put(DatabaseHelper.REFNO, refno);
//            values.put(FPRODUCT_UNITS, units);
//            values.put(FPRODUCT_TYPE, type);
//
//
//            int cn = cursor.getCount();
//            if (cn > 0) {
//                count = dB.update(TABLE_FPRODUCT_PRE, values, FPRODUCT_ITEMCODE_PRE
//                        + " = '" + itemCode + "' and "+FPRODUCT_TYPE + " = '" + type + "'",
//                        null);
//            } else {
//                count = (int) dB.insert(TABLE_FPRODUCT_PRE, null, values);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dB.close();
        }
        return count;
    }
    public int updateReason(String itemCode,String itemname, String price, String qoh, String qty, String cases, String refno, String units, String type, String code) {
        int count = 0;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + TABLE_FPRODUCT_PRE + " WHERE " + FPRODUCT_ITEMCODE_PRE
                    + " = '" + itemCode + "' and "+FPRODUCT_TYPE + " = '" + type + "' and "+FPRODUCT_REACODE + " = '" + code + "' ";

            cursor = dB.rawQuery(selectQuery, null);

            ContentValues values = new ContentValues();
            values.put(FPRODUCT_ITEMCODE_PRE, itemCode);
            values.put(FPRODUCT_ITEMNAME_PRE, itemname);
            values.put(FPRODUCT_PRICE_PRE, price);
            values.put(FPRODUCT_QOH_PRE, qoh);
            values.put(FPRODUCT_QTY_PRE, qty);
            values.put(FPRODUCT_CASE_PRE, cases);
            values.put(DatabaseHelper.REFNO, refno);
            values.put(FPRODUCT_UNITS, units);
            values.put(FPRODUCT_TYPE, type);
            values.put(FPRODUCT_REACODE, code);



            int cn = cursor.getCount();
            if (cn > 0) {
                count = dB.update(TABLE_FPRODUCT_PRE, values, FPRODUCT_ITEMCODE_PRE
                                + " = '" + itemCode + "' and "+FPRODUCT_TYPE + " = '" + type + "' and "+FPRODUCT_REACODE + " = '" + code + "'",
                        null);
            } else {
                count = (int) dB.insert(TABLE_FPRODUCT_PRE, null, values);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dB.close();
        }
        return count;
    }

    public int updateProductQtyFor(String itemCode, String qty) {
        int count = 0;
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try {

            ContentValues values = new ContentValues();
            values.put(FPRODUCT_QTY_PRE, qty);
            count=(int)  dB.update(TABLE_FPRODUCT_PRE, values, FPRODUCT_ITEMCODE_PRE + " =?", new String[]{String.valueOf(itemCode)});

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dB.close();
        }
        return count;
    }

    public ArrayList<PreProduct> getSelectedItems() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        Cursor cursor = null;
        ArrayList<PreProduct> list = new ArrayList<>();
        try {
            cursor = dB.rawQuery("SELECT * FROM " + TABLE_FPRODUCT_PRE + " WHERE  qty_pre<>'0'", null);

            while (cursor.moveToNext()) {
                PreProduct product = new PreProduct();
                product.setPREPRODUCT_ID(cursor.getString(cursor.getColumnIndex(FPRODUCT_ID_PRE)));
                product.setPREPRODUCT_ITEMCODE(cursor.getString(cursor.getColumnIndex(FPRODUCT_ITEMCODE_PRE)));
                product.setPREPRODUCT_ITEMNAME(cursor.getString(cursor.getColumnIndex(FPRODUCT_ITEMNAME_PRE)));
                product.setPREPRODUCT_PRICE(cursor.getString(cursor.getColumnIndex(FPRODUCT_PRICE_PRE)));
                product.setPREPRODUCT_QOH(cursor.getString(cursor.getColumnIndex(FPRODUCT_QOH_PRE)));
                product.setPREPRODUCT_QTY(cursor.getString(cursor.getColumnIndex(FPRODUCT_QTY_PRE)));

                list.add(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            dB.close();
        }

        return list;
    }

    public void mClearTables() {

        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }
        try {
            dB.delete(TABLE_FPRODUCT_PRE, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dB.close();
        }
    }

    public void insertIntoProductAsBulkForPre(String LocCode, String prillcode)
    {
        if (dB == null) {
            open();
        } else if (!dB.isOpen()) {
            open();
        }

        try
        {

            if(prillcode.equals(null) || prillcode.isEmpty())
            {
                String insertQuery1;
                insertQuery1 = "INSERT INTO fProducts_pre (itemcode_pre,itemname_pre,price_pre,qoh_pre,qty_pre)\n" +
                        "SELECT \n" +
                        "itm.ItemCode AS ItemCode , \n" +
                        "itm.ItemName AS ItemName ,  \n" +
                        "IFNULL(pri.Price,0.0) AS Price , \n" +
//                        "IFNULL(pri.MinPrice,0.0) AS MinPrice , \n" +//commented on 2019-07-08 because price is 0.0
//                        "IFNULL(pri.MaxPrice,0.0) AS MaxPrice ,\n" +//commented on 2019-07-08 because price is 0.0
                        "loc.QOH AS QOH , \n" +
                        "\"0.0\" AS ChangedPrice , \n" +
                        "\"SA\" AS TxnType , \n" +
                        "\"0\" AS Qty \n" +
                        "FROM fItem itm\n" +
                        "INNER JOIN fItemLoc loc ON loc.ItemCode = itm.ItemCode \n" +
                        "LEFT JOIN fItemPri pri ON pri.ItemCode = itm.ItemCode \n" +
                        "AND pri.PrilCode = itm.PrilCode\n" +
                        "WHERE loc.LocCode = '"+LocCode+"'\n" +
                        //   "AND pri.Price > 0\n" +//commented on 2019-07-08 because price is 0.0
                        "GROUP BY itm.ItemCode ORDER BY QOH DESC";

                dB.execSQL(insertQuery1);
            }
            else
            {
                String insertQuery2;
                insertQuery2 = "INSERT INTO fProducts_pre (itemcode_pre,itemname_pre,price_pre,qoh_pre,qty_pre)\n" +
                        "SELECT \n" +
                        "itm.ItemCode AS ItemCode , itm.ItemName AS ItemName ,  \n" +
                        "IFNULL(pri.Price,0.0) AS Price , " +
//                        "IFNULL(pri.MinPrice,0.0) AS MinPrice , \n" +//commented on 2019-07-08 because price is 0.0
//                        "IFNULL(pri.MaxPrice,0.0) AS MaxPrice ,\n" +//commented on 2019-07-08 because price is 0.0
                        "loc.QOH AS QOH , \"0\" AS Qty FROM fItem itm\n" +
//                        "loc.QOH AS QOH , \"0.0\" AS ChangedPrice , \"SA\" AS TxnType , \"0\" AS Qty FROM fItem itm\n" +
                        "INNER JOIN fItemLoc loc ON loc.ItemCode = itm.ItemCode \n" +
                        "LEFT JOIN fItemPri pri ON pri.ItemCode = itm.ItemCode \n" +
                        "AND pri.PrilCode = '"+prillcode+"'\n" +
                        "WHERE loc.LocCode = '"+LocCode+"'\n" +
                        //    "AND pri.Price > 0\n" +//commented on 2019-07-08 because price is 0.0
                        "GROUP BY itm.ItemCode ORDER BY QOH DESC";

                dB.execSQL(insertQuery2);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if(dB.isOpen())
            {
                dB.close();
            }
        }
    }
}
