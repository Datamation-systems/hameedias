package com.datamation.hmdsfa.barcode;


import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.hmdsfa.R;
import com.datamation.hmdsfa.adapter.FreeIssueAdapter;
import com.datamation.hmdsfa.adapter.FreeIssueAdapterNew;
import com.datamation.hmdsfa.adapter.FreeItemsAdapter;
import com.datamation.hmdsfa.adapter.InvDetAdapter;
import com.datamation.hmdsfa.adapter.InvoiceFreeItemAdapter;
import com.datamation.hmdsfa.adapter.ItemAdapter;
import com.datamation.hmdsfa.adapter.ItemBundleAdapter;
import com.datamation.hmdsfa.adapter.NewProduct_Adapter;
import com.datamation.hmdsfa.adapter.OrderDetailsAdapter;
import com.datamation.hmdsfa.adapter.OrderFreeItemAdapter;
import com.datamation.hmdsfa.adapter.PreOrderAdapter;
import com.datamation.hmdsfa.controller.InvDetController;
import com.datamation.hmdsfa.controller.InvHedController;
import com.datamation.hmdsfa.controller.ItemBundleController;
import com.datamation.hmdsfa.controller.ItemController;
import com.datamation.hmdsfa.controller.ItemPriController;
import com.datamation.hmdsfa.controller.OrdFreeIssueController;
import com.datamation.hmdsfa.controller.OrderController;
import com.datamation.hmdsfa.controller.OrderDetailController;
import com.datamation.hmdsfa.controller.OrderDiscController;
import com.datamation.hmdsfa.controller.PreProductController;
import com.datamation.hmdsfa.controller.ProductController;
import com.datamation.hmdsfa.controller.SalRepController;
import com.datamation.hmdsfa.controller.TaxDetController;
import com.datamation.hmdsfa.discount.Discount;
import com.datamation.hmdsfa.freeissue.FreeIssue;
import com.datamation.hmdsfa.freeissue.FreeIssueModified;
import com.datamation.hmdsfa.helpers.BluetoothConnectionHelper;
import com.datamation.hmdsfa.helpers.PreSalesResponseListener;
import com.datamation.hmdsfa.helpers.SharedPref;
import com.datamation.hmdsfa.model.Customer;
import com.datamation.hmdsfa.model.FreeItemDetails;
import com.datamation.hmdsfa.model.InvDet;
import com.datamation.hmdsfa.model.InvHed;
import com.datamation.hmdsfa.model.Item;
import com.datamation.hmdsfa.model.ItemBundle;
import com.datamation.hmdsfa.model.ItemFreeIssue;
import com.datamation.hmdsfa.model.OrdFreeIssue;
import com.datamation.hmdsfa.model.Order;
import com.datamation.hmdsfa.model.OrderDetail;
import com.datamation.hmdsfa.model.PreProduct;
import com.datamation.hmdsfa.model.Product;
import com.datamation.hmdsfa.settings.ReferenceNum;
import com.datamation.hmdsfa.vansale.VanSalesOrderDetails;
import com.datamation.hmdsfa.view.ActivityVanSalesBR;
import com.datamation.hmdsfa.view.BarCodeReaderActivity;
import com.datamation.hmdsfa.view.VanSalesActivity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BRInvoiceDetailFragment extends Fragment{
    /*rashmi - hameedias barcode scan modification - 2020-03-02*/
    View view;
    int totPieces = 0;
    int seqno = 0;
    ListView lv_order_det, lvFree;
    Spinner spnScanType;
    ArrayList<InvDet> orderList;
    SharedPref mSharedPref;
    String RefNo, locCode;
    ActivityVanSalesBR mainActivity;
    MyReceiver r;
    ArrayList<Product> productList = null, selectedItemList = null;
    ArrayList<ItemBundle> itemArrayList = null;
    ImageButton ibtProduct, ibtDiscount;
    private  SweetAlertDialog pDialog;
    private InvHed selectedInvHed;
    TextView textStatus;
    EditText etSearchField;
    ThreadConnectBTdevice threadConnectBTdevice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        seqno = 0;
        totPieces = 0;
        view = inflater.inflate(R.layout.activity_bar_code_reader, container, false);
        mSharedPref = SharedPref.getInstance(getActivity());
        locCode = new SharedPref(getActivity()).getGlobalVal("KeyLocCode");
        selectedInvHed = new InvHedController(getActivity()).getActiveInvhed();
        RefNo = new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumVal));
        lv_order_det = (ListView) view.findViewById(R.id.lvProducts_Inv);
        textStatus = view.findViewById(R.id.tvStatus_main);
        etSearchField = view.findViewById(R.id.etSearchField);
        spnScanType = (Spinner) view.findViewById(R.id.spnScan);
        itemArrayList = new ArrayList<>();
        setHasOptionsMenu(true);

        ArrayList<String> strList = new ArrayList<String>();
        strList.add("ITEM WISE");
        strList.add("BUNDLE WISE");

        final ArrayAdapter<String> txnTypeAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.return_spinner_item, strList);
        txnTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnScanType.setAdapter(txnTypeAdapter);

        showData();

        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//


        etSearchField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                Log.v("ENTER CODE","Working.... ");
//
//                ArrayList<Item> aList = new ItemController(getActivity())
                if(spnScanType.getSelectedItemPosition() == 0) {
//                        .getAllItem(etSearchField.getText().toString());
                    ItemBundle item = new ItemBundleController(getActivity())
                            .getItem(etSearchField.getText().toString());
                    Log.v("ENTERED CODE", "itemcode " + etSearchField.getText().toString());
                    // for (Item item: aList ) {
                    Log.v("code :", ">> " + item.getItemNo());
                    etSearchField.setText("");
                    etSearchField.setFocusable(true);
                    // }
                    if(item.getBarcode()!= null) {
                        itemArrayList.add(item);
                    }else{
                        Toast.makeText(getActivity(),"No matching item",Toast.LENGTH_LONG).show();
                    }

                    lv_order_det.setAdapter(new ItemAdapter(getActivity(), itemArrayList));
                }else{

                    ArrayList<ItemBundle> itemBundle = new ItemBundleController(getActivity())
                            .getItemsInBundle(etSearchField.getText().toString());
                    if(itemBundle.size()>0) {
                        BundleItemsDialogBox(itemBundle);

                    }else{
                        Toast.makeText(getActivity(),"No matching bundle",Toast.LENGTH_LONG).show();
                    }
                    etSearchField.setText("");
                    etSearchField.setFocusable(true);

                }
                return false;
            }
        });
        etSearchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                        || keyCode == KeyEvent.KEYCODE_TAB) {
                    // handleInputScan();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (etSearchField != null) {
                                etSearchField.requestFocus();
                            }
                        }
                    }, 10); // Remove this Delay Handler IF requestFocus(); works just fine without delay
                    return true;
                }
                return false;
            }
        });
//        ibtProduct.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                new LoardingProductFromDB().execute();
//            }
//        });



        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//

//        ibtDiscount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                calculateFreeIssue(SharedPref.getInstance(getActivity()).getSelectedDebCode());
//            }
//        });

        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//

        lv_order_det.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                new InvDetController(getActivity()).restFreeIssueData(RefNo);
                //new OrdFreeIssueDS(getActivity()).ClearFreeIssues(RefNo);
                newDeleteOrderDialog(position);
                return true;
            }
        });

        //*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*//*
        if(!new BluetoothConnectionHelper(getActivity()).isSupportBluetooth()){
            Toast.makeText(getActivity(),"FEATURE_BLUETOOTH NOT SUPPORTED", Toast.LENGTH_LONG).show();
            connectionError();
        }

        if(!new BluetoothConnectionHelper(getActivity()).isBluetoothHardwareSupport()){
            Toast.makeText(getActivity(),"Bluetooth is not supported on this hardware platform", Toast.LENGTH_LONG).show();
            connectionError();
        }

        return view;

    }
    private boolean BundleItemsDialogBox(final ArrayList<ItemBundle> itemDetails) {

        final ArrayList<ItemBundle> itemBundles;
       // final ArrayList<String> saleItemList;
        //final String FIRefNo = itemDetails.getRefno();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.free_issues_items_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Items Inside Bundle");
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.lv_free_issue);
       // final ListView listViewsale = (ListView) promptView.findViewById(R.id.lv_free_issue_items);
       // final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
       // final TextView itemNamefree = (TextView) promptView.findViewById(R.id.tv_free_issued_item_name);
       // final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

       // freeQty.setText("Free Quantity : " + itemDetails.getFreeQty());
       //  itemNamefree.setText("Free Products : ");


//        saleItemList = itemsDS.getFreeIssueItemDetailsByItem(itemDetails.getSaleItemList());
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, saleItemList);
//        listViewsale.setAdapter(adapter);
        listView.setAdapter(new ItemBundleAdapter(getActivity(), itemDetails));


        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for(ItemBundle items : itemDetails) {
                    itemArrayList.add(items);
                }
                lv_order_det.setAdapter(new ItemAdapter(getActivity(), itemArrayList));
//                for (ItemFreeIssue itemFreeIssue : itemFreeIssues) {
//
//                    if (Integer.parseInt(itemFreeIssue.getAlloc()) > 0) {
//
//                        seqno++;
//                        OrderDetail ordDet = new OrderDetail();
//                        OrderDetailController detDS = new OrderDetailController(getActivity());
//                        ArrayList<OrderDetail> ordList = new ArrayList<OrderDetail>();
//
//                        ordDet.setFORDERDET_ID("0");
//                        ordDet.setFORDERDET_AMT("0");
//                        ordDet.setFORDERDET_BALQTY(itemFreeIssue.getAlloc());
//                        ordDet.setFORDERDET_BAMT("0");
//                        ordDet.setFORDERDET_BDISAMT("0");
//                        ordDet.setFORDERDET_BPDISAMT("0");
//                        String unitPrice = new ItemPriController(getActivity()).getProductPriceByCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mSharedPref.getSelectedDebtorPrilCode());
//                        ordDet.setFORDERDET_BSELLPRICE("0");
//                        ordDet.setFORDERDET_BTSELLPRICE("0.00");
//                        ordDet.setFORDERDET_DISAMT("0");
//                        ordDet.setFORDERDET_SCHDISPER("0");
//                        ordDet.setFORDERDET_FREEQTY("0");
//                        ordDet.setFORDERDET_ITEMCODE(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
//                        ordDet.setFORDERDET_PDISAMT("0");
//                        ordDet.setFORDERDET_PRILCODE("WSP001");
//                        ordDet.setFORDERDET_QTY(itemFreeIssue.getAlloc());
//                        ordDet.setFORDERDET_PICE_QTY(itemFreeIssue.getAlloc());
//                        ordDet.setFORDERDET_CASES("0");//because free issue not issued cases wise-2019-10-21 menaka said
//                        ordDet.setFORDERDET_TYPE("FD");
//                        ordDet.setFORDERDET_RECORDID("");
//                        ordDet.setFORDERDET_REFNO(RefNo);
//                        ordDet.setFORDERDET_SELLPRICE("0");
//                        ordDet.setFORDERDET_SEQNO(seqno + "");
//                        ordDet.setFORDERDET_TAXAMT("0");
//                        ordDet.setFORDERDET_TAXCOMCODE(new ItemController(getActivity()).getTaxComCodeByItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
//                        //ordDet.setFTRANSODET_TAXCOMCODE(new ItemsDS(getActivity()).getTaxComCodeByItemCodeFromDebTax(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mainActivity.selectedDebtor.getFDEBTOR_CODE()));
//                        ordDet.setFORDERDET_TIMESTAMP_COLUMN("");
//                        ordDet.setFORDERDET_TSELLPRICE("0.00");
//                        ordDet.setFORDERDET_TXNDATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//                        ordDet.setFORDERDET_TXNTYPE("27");
//                        ordDet.setFORDERDET_IS_ACTIVE("1");
//                        ordDet.setFORDERDET_LOCCODE(mSharedPref.getGlobalVal("PrekeyLocCode").trim());
//                        ordDet.setFORDERDET_COSTPRICE(new ItemController(getActivity()).getCostPriceItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
//                        ordDet.setFORDERDET_BTAXAMT("0");
//                        ordDet.setFORDERDET_IS_SYNCED("0");
//                        ordDet.setFORDERDET_QOH("0");
//                        ordDet.setFORDERDET_SCHDISC("0");
//                        ordDet.setFORDERDET_BRAND_DISC("0");
//                        ordDet.setFORDERDET_BRAND_DISPER("0");
//                        ordDet.setFORDERDET_COMP_DISC("0");
//                        ordDet.setFORDERDET_COMP_DISPER("0");
//                        ordDet.setFORDERDET_DISCTYPE("");
//                        ordDet.setFORDERDET_PRICE("0.00");
//                        ordDet.setFORDERDET_ORG_PRICE("0");
//                        ordDet.setFORDERDET_DISFLAG("0");
//                        ordDet.setFORDERDET_REACODE("");
//
//                        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*OrdFreeIssue table update*-*-*-*-*-*-*-*-*-*-*-*-*-*/
//
//                        OrdFreeIssue ordFreeIssue = new OrdFreeIssue();
//                        ordFreeIssue.setOrdFreeIssue_ItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
//                        ordFreeIssue.setOrdFreeIssue_Qty(itemFreeIssue.getAlloc());
//                        ordFreeIssue.setOrdFreeIssue_RefNo(FIRefNo);
//                        ordFreeIssue.setOrdFreeIssue_RefNo1(RefNo);
//                        ordFreeIssue.setOrdFreeIssue_TxnDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//                        new OrdFreeIssueController(getActivity()).UpdateOrderFreeIssue(ordFreeIssue);
//
//                        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*/
//
//                        ordList.add(ordDet);
//
//                        if (detDS.createOrUpdateOrdDet(ordList) > 0) {
//                            Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
//                            showData();
//
////                            lvFree.setAdapter(null);
////                            ArrayList<OrderDetail> freeList=new OrderDetailController(getActivity()).getAllFreeIssue(RefNo);
////                            lvFree.setAdapter(new OrderFreeItemAdapter(getActivity(), freeList));
//                        }
//                    }
//                }

                dialog.cancel();//2020-03-10
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
        return true;
    }
    //------------------------------------------------------------------------------------------------------------------------------------
    public class LoardingProductFromDB extends AsyncTask<Object, Object, ArrayList<Product>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Fetch Data Please Wait.");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected ArrayList<Product> doInBackground(Object... objects) {

            if (new ProductController(getActivity()).tableHasRecords()) {
                productList = new ProductController(getActivity()).getAllItems("","SA");//rashmi 2018-10-26
            } else {

                new ProductController(getActivity()).insertIntoProductAsBulk("MS","WSP001");

                if(selectedInvHed!=null) {

                    ArrayList<InvDet> invDetArrayList = selectedInvHed.getInvDetArrayList();
                    if (invDetArrayList != null) {
                        for (int i = 0; i < invDetArrayList.size(); i++) {
                            String tmpItemName = invDetArrayList.get(i).getFINVDET_ITEM_CODE();
                            String tmpQty = invDetArrayList.get(i).getFINVDET_QTY();
                            //Update Qty in  fProducts_pre table
                            int count = new ProductController(getActivity()).updateProductQtyfor(tmpItemName, tmpQty);
                            if (count > 0) {

                                Log.d("InsertOrUpdate", "success");
                            } else {
                                Log.d("InsertOrUpdate", "Failed");
                            }

                        }
                    }
                }
                //----------------------------------------------------------------------------
            }
            productList = new ProductController(getActivity()).getAllItems("","SA");//rashmi -2018-10-26
            return productList;
        }


        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            super.onPostExecute(products);

            if(pDialog.isShowing()){
                pDialog.dismiss();
            }
            ProductDialogBox();
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void calculateFreeIssue(String debCode) {
        /* GET CURRENT ORDER DETAILS FROM TABLE */
        ArrayList<InvDet> dets = new InvDetController(getActivity()).getSAForFreeIssueCalc(RefNo);
        /* CLEAR ORDERDET TABLE RECORD IF FREE ITEMS ARE ALREADY ADDED. */
        new InvDetController(getActivity()).restFreeIssueData(RefNo);
        /* Clear free issues in OrdFreeIss */
        new OrdFreeIssueController(getActivity()).ClearFreeIssues(RefNo);

        Discount issue = new Discount(getActivity());
        FreeIssue freeIssue = new FreeIssue(getActivity());
        /* Get discounts for assorted items */
        ArrayList<ArrayList<InvDet>> metaOrdList = issue.SortInvDiscount(dets, debCode);

        Log.d("PRE_SALES_ORDER_DETAILS", "LIST_SIZE: " + metaOrdList.size());

        /* Iterate through for discounts for items */
        for (ArrayList<InvDet> OrderList : metaOrdList) {

            double totAmt = 0;
            String discPer,discType,discRef;
            double freeVal = Double.parseDouble(OrderList.get(0).getFINVDET_B_AMT());
            if(OrderList.get(0).getFINVDET_SCHDISPER() != null)
                discPer = OrderList.get(0).getFINVDET_SCHDISPER();
            else
                discPer = "";
            if(OrderList.get(0).getFINVDET_DISCTYPE() != null)
                discType = OrderList.get(0).getFINVDET_DISCTYPE();
            else
                discType = "";
            if(OrderList.get(0).getFINVDET_DISC_REF() != null)
                discRef = OrderList.get(0).getFINVDET_DISC_REF();
            else
                discRef = "";


            OrderList.get(0).setFINVDET_B_AMT("0");

            for (InvDet det : OrderList)
                totAmt += Double.parseDouble(det.getFINVDET_B_SELL_PRICE()) * (Double.parseDouble(det.getFINVDET_QTY()));

            for (InvDet det : OrderList) {
                det.setFINVDET_SCHDISPER(discPer);
                det.setFINVDET_DISCTYPE(discType);
                det.setFINVDET_DISC_REF(discRef);

                double disc;
                /*
                 * For value, calculate amount portion & for percentage ,
                 * calculate percentage portion
                 */
                disc = (freeVal / totAmt) * Double.parseDouble(det.getFINVDET_B_SELL_PRICE()) * (Double.parseDouble(det.getFINVDET_QTY()));

                new InvDetController(getActivity()).updateDiscount(det, disc, det.getFINVDET_DISCTYPE());
            }
        }
        ArrayList<FreeItemDetails> list = freeIssue.getFreeItemsByInvoiceItem(dets, "");
        for (FreeItemDetails freeItemDetails : list) {
            freeIssueDialogBox(freeItemDetails);
        }

    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    private boolean freeIssueDialogBox(final FreeItemDetails itemDetails) {

        final ArrayList<ItemFreeIssue> itemFreeIssues;
        final String FIRefNo = itemDetails.getRefno();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.free_issues_items_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Free Issue Schemes");
        alertDialogBuilder.setView(promptView);

        final ListView listView = (ListView) promptView.findViewById(R.id.lv_free_issue);
        final TextView itemName = (TextView) promptView.findViewById(R.id.tv_free_issue_item_name);
        final TextView freeQty = (TextView) promptView.findViewById(R.id.tv_free_qty);

        freeQty.setText("Free Quantity : " + itemDetails.getFreeQty());
        itemName.setText("Product : " + new ItemController(getActivity()).getItemNameByCode(itemDetails.getFreeIssueSelectedItem()));

        final ItemController itemsDS = new ItemController(getActivity());
        itemFreeIssues = itemsDS.getAllFreeItemNameByRefno(itemDetails.getRefno());
        listView.setAdapter(new FreeIssueAdapterNew(getActivity(), itemFreeIssues));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            int avaliableQty = 0;

            @Override
            public void onItemClick(AdapterView<?> parent, View view2, final int position, long id) {

                if (itemDetails.getFreeQty() > 0) {

                    ItemFreeIssue freeIssue = itemFreeIssues.get(position);
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

                    View promptView = layoutInflater.inflate(R.layout.set_free_issue_dialog, null);

                    final TextView leftQty = (TextView) promptView.findViewById(R.id.tv_free_item_left_qty);
                    final EditText enteredQty = (EditText) promptView.findViewById(R.id.et_free_qty);

                    leftQty.setText("Free Items Left = " + itemDetails.getFreeQty());

                    enteredQty.addTextChangedListener(new TextWatcher() {
                        public void afterTextChanged(Editable s) {

                            if (enteredQty.getText().toString().equals("")) {

                                leftQty.setText("Free Items Left = " + itemDetails.getFreeQty());

                            } else {
                                avaliableQty = itemDetails.getFreeQty() - Integer.parseInt(enteredQty.getText().toString());

                                if (avaliableQty < 0) {
                                    enteredQty.setText("");
                                    leftQty.setText("Free Items Left = " + itemDetails.getFreeQty());
                                    Toast.makeText(getActivity(), "You don't have enough sufficient quantity to order", Toast.LENGTH_SHORT).show();
                                } else {
                                    leftQty.setText("Free Items Left = " + avaliableQty);
                                }
                            }
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                    });

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle(freeIssue.getItems().getFITEM_ITEM_NAME());
                    alertDialogBuilder.setView(promptView);

                    alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            itemDetails.setFreeQty(avaliableQty);
                            freeQty.setText("Free Qty you have : " + itemDetails.getFreeQty());

                            itemFreeIssues.get(position).setAlloc(enteredQty.getText().toString());
                            listView.clearTextFilter();
                            listView.setAdapter(new FreeIssueAdapterNew(getActivity(), itemFreeIssues));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertD = alertDialogBuilder.create();
                    alertD.show();
                } else {
                    Toast.makeText(getActivity(), "You don't have enough sufficient quantity to order", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                for (ItemFreeIssue itemFreeIssue : itemFreeIssues) {

                    if (Integer.parseInt(itemFreeIssue.getAlloc()) > 0) {

                        seqno++;
                        InvDet ordDet = new InvDet();
                        InvDetController detDS = new InvDetController(getActivity());
                        ArrayList<InvDet> ordList = new ArrayList<InvDet>();

                        ordDet.setFINVDET_AMT("0");
                        ordDet.setFINVDET_BAL_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFINVDET_B_AMT("0");
                        ordDet.setFINVDET_BRAND_DISC("0");
                        //ordDet.setFINVDET_BP("0");
                        String unitPrice = new ItemPriController(getActivity()).getProductPriceByCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mSharedPref.getSelectedDebtorPrilCode());
                        ordDet.setFINVDET_B_SELL_PRICE("0");
                        ordDet.setFINVDET_BT_SELL_PRICE("0.00");
                        ordDet.setFINVDET_DIS_AMT("0");
                        ordDet.setFINVDET_SCHDISPER("0");
                        ordDet.setFINVDET_FREEQTY("0");
                        ordDet.setFINVDET_ITEM_CODE(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        //ordDet.setFINVDET_PDISAMT("0");
                        ordDet.setFINVDET_PRIL_CODE(SharedPref.getInstance(getActivity()).getSelectedDebtorPrilCode());

                        ordDet.setFINVDET_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFINVDET_PICE_QTY(itemFreeIssue.getAlloc());
                        ordDet.setFINVDET_TYPE("FI");
                        ordDet.setFINVDET_RECORD_ID("");
                        ordDet.setFINVDET_REFNO(RefNo);
                        ordDet.setFINVDET_SELL_PRICE("0");
                        ordDet.setFINVDET_SEQNO(seqno + "");
                        ordDet.setFINVDET_TAX_AMT("0");
                        ordDet.setFINVDET_TAX_COM_CODE(new ItemController(getActivity()).getTaxComCodeByItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
                        // ordDet.setFINVDET_TAX_COMCODE(new ItemsDS(getActivity()).getTaxComCodeByItemCodeFromDebTax(itemFreeIssue.getItems().getFITEM_ITEM_CODE(), mainActivity.selectedDebtor.getFDEBTOR_CODE()));
                        //   ordDet.setFINVDET_TIMESTAMP_COLUMN("");
                        ordDet.setFINVDET_T_SELL_PRICE("0.00");
                        ordDet.setFINVDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        ordDet.setFINVDET_TXN_TYPE("27");
                        ordDet.setFINVDET_IS_ACTIVE("1");
                        ordDet.setFINVDET_LOCCODE(mSharedPref.getGlobalVal("PrekeyLocCode").trim());
                        //  ordDet.setFINVDET_COST_PRICE(new ItemController(getActivity()).getCostPriceItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE()));
                        ordDet.setFINVDET_BT_TAX_AMT("0");
                        ordDet.setFINVDET_IS_SYNCED("0");
                        ordDet.setFINVDET_QOH("0");
                        ordDet.setFINVDET_DISCTYPE("");
                        ordDet.setFINVDET_PRICE("0.00");
                        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*OrdFreeIssue table update*-*-*-*-*-*-*-*-*-*-*-*-*-*/

                        OrdFreeIssue ordFreeIssue = new OrdFreeIssue();
                        ordFreeIssue.setOrdFreeIssue_ItemCode(itemFreeIssue.getItems().getFITEM_ITEM_CODE());
                        ordFreeIssue.setOrdFreeIssue_Qty(itemFreeIssue.getAlloc());
                        ordFreeIssue.setOrdFreeIssue_RefNo(FIRefNo);
                        ordFreeIssue.setOrdFreeIssue_RefNo1(RefNo);
                        ordFreeIssue.setOrdFreeIssue_TxnDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        new OrdFreeIssueController(getActivity()).UpdateOrderFreeIssue(ordFreeIssue);

                        /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*/

                        ordList.add(ordDet);

                        if (detDS.createOrUpdateInvDet(ordList) > 0) {
                            Toast.makeText(getActivity(), "Added successfully", Toast.LENGTH_SHORT).show();
                            //showData();

                            lvFree.setAdapter(null);
                            ArrayList<InvDet> freeList=new InvDetController(getActivity()).getAllFreeIssue(RefNo);
                            lvFree.setAdapter(new InvoiceFreeItemAdapter(getActivity(), freeList));
                        }
                    }
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
        return true;
    }
    public void showData() {
        selectedInvHed = new InvHedController(getActivity()).getActiveInvhed();
        try {
            orderList = new InvDetController(getActivity()).getAllInvDet(selectedInvHed.getFINVHED_REFNO());
//            ArrayList<InvDet> freeList = new InvDetController(getActivity()).getAllFreeIssue(selectedInvHed.getFINVHED_REFNO());
//            lv_order_det.setAdapter(new InvDetAdapter(getActivity(), orderList));//2019-07-07 till error free
           // lvFree.setAdapter(new FreeItemsAdapter(getActivity(), freeList));
        } catch (NullPointerException e) {
            Log.v("SA Error", e.toString());
        }
    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public double getAvailableTotal(double discVal, ArrayList<InvDet> OrderList) {
        double avQTY = 0;

        for (InvDet mOrdDet : OrderList) {
            avQTY = avQTY + Double.parseDouble(mOrdDet.getFINVDET_DIS_AMT());
            System.out.println(mOrdDet.getFINVDET_DIS_AMT() + " - " + mOrdDet.getFINVDET_ITEM_CODE());
        }

        return (discVal - avQTY);

    }
    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void mToggleTextbox() {
        if(!new BluetoothConnectionHelper(getActivity()).isSupportBluetooth()){
            Toast.makeText(getActivity(),"FEATURE_BLUETOOTH NOT SUPPORTED", Toast.LENGTH_LONG).show();
            connectionError();
        }

        if(!new BluetoothConnectionHelper(getActivity()).isBluetoothHardwareSupport()){
            Toast.makeText(getActivity(),"Bluetooth is not supported on this hardware platform", Toast.LENGTH_LONG).show();
            connectionError();
        }
        showData();
    }

    @Override
    public void onStart() {
        super.onStart();
        new BluetoothConnectionHelper(getActivity()).enableBluetooth(getActivity());
        try {
            //MAC ADDRESS BT AUTO CONNECT.
            threadConnectBTdevice = new ThreadConnectBTdevice(new BluetoothConnectionHelper(getActivity()).getDevice());
            threadConnectBTdevice.start();

        }catch ( Exception ex ){
            Toast.makeText(getActivity(),ex.toString(), Toast.LENGTH_LONG).show();
            connectionError();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        new BluetoothConnectionHelper(getActivity()).enableBluetooth(getActivity());
        try {
            //MAC ADDRESS BT AUTO CONNECT.
            threadConnectBTdevice = new ThreadConnectBTdevice(new BluetoothConnectionHelper(getActivity()).getDevice());
            threadConnectBTdevice.start();

        }catch ( Exception ex ){
            Toast.makeText(getActivity(),ex.toString(), Toast.LENGTH_LONG).show();
            connectionError();
        }
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
        if(threadConnectBTdevice!=null){
            threadConnectBTdevice.cancel();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(threadConnectBTdevice!=null){
            threadConnectBTdevice.cancel();
        }
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r, new IntentFilter("TAG_DETAILS"));
    }
    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void ProductDialogBox() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.product_dialog_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final ListView lvProducts = (ListView) promptView.findViewById(R.id.lv_product_list);
        final SearchView search = (SearchView) promptView.findViewById(R.id.et_search);

        lvProducts.clearTextFilter();
        productList = new ProductController(getActivity()).getAllItems("","SA");//rashmi -2018-10-26
        lvProducts.setAdapter(new NewProduct_Adapter(getActivity(), productList));

        alertDialogBuilder.setCancelable(false).setNegativeButton("DONE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                selectedItemList = new ProductController(getActivity()).getSelectedItems("SA");
                updateInvoiceDet(selectedItemList);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                productList = new ProductController(getActivity()).getAllItems(query,"SA");//Rashmi 2018-10-26
                lvProducts.setAdapter(new NewProduct_Adapter(getActivity(), productList));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                productList.clear();
                productList = new ProductController(getActivity()).getAllItems(newText,"SA");//rashmi-2018-10-26
                lvProducts.setAdapter(new NewProduct_Adapter(getActivity(), productList));
                return true;
            }
        });
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void newDeleteOrderDialog(final int position) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Confirm Deletion !");
        alertDialogBuilder.setMessage("Do you want to delete this item ?");
        alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                new ProductController(getActivity()).updateProductQty(orderList.get(position).getFINVDET_ITEM_CODE(), "0");
                new InvDetController(getActivity()).mDeleteProduct(selectedInvHed.getFINVHED_REFNO(), orderList.get(position).getFINVDET_ITEM_CODE());
                android.widget.Toast.makeText(getActivity(), "Deleted successfully!", android.widget.Toast.LENGTH_SHORT).show();
                showData();

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mToggleTextbox();
        }
    }

    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void updateInvoiceDet(final ArrayList<Product> list) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Updating products...");
                pDialog.setCancelable(false);
                pDialog.show();
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                int i = 0;
                new InvDetController(getActivity()).mDeleteRecords(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumVal)));

                for (Product product : list) {
                    i++;
                    mUpdateInvoice("0", product.getFPRODUCT_ITEMCODE(), product.getFPRODUCT_QTY(), product.getFPRODUCT_PRICE(), i + "", product.getFPRODUCT_QOH(),product.getFPRODUCT_CHANGED_PRICE());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(pDialog.isShowing()){
                    pDialog.dismiss();
                }

                showData();
            }

        }.execute();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_barcode, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;

            case R.id.menu_item2: { // REFRESH BLUETOOTH CONNECTION
                textStatus.setText("Status : Connected.");
                textStatus.setBackgroundResource(R.color.Green);
                try {
                    threadConnectBTdevice.cancel();
                }catch ( Exception e ){
                    e.printStackTrace();
                }finally {
                    try {
                        threadConnectBTdevice = new ThreadConnectBTdevice(new BluetoothConnectionHelper(getActivity()).getDevice());
                        threadConnectBTdevice.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            }

            return true;


        }
        return(super.onOptionsItemSelected(item));
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void mUpdateInvoice(String id, String itemCode, String Qty, String price, String seqno, String qoh, String changedPrice) {

        ArrayList<InvDet> arrList = new ArrayList<>();
        double amt = 0.0;
        if(changedPrice.equals("0.0") || changedPrice.equals("0")) {
            amt = Double.parseDouble(price) * Double.parseDouble(Qty);
        }else{
            amt = Double.parseDouble(changedPrice) * Double.parseDouble(Qty);
        }
        InvDet invDet = new InvDet();
        invDet.setFINVDET_B_AMT(String.format("%.2f", amt));
        invDet.setFINVDET_B_SELL_PRICE(price);
        invDet.setFINVDET_BT_SELL_PRICE(price);
        invDet.setFINVDET_DIS_AMT("0");
        invDet.setFINVDET_DIS_PER("0");
        invDet.setFINVDET_ITEM_CODE(itemCode);
        invDet.setFINVDET_PRIL_CODE(SharedPref.getInstance(getActivity()).getSelectedDebtorPrilCode());
        invDet.setFINVDET_QTY(Qty);
        invDet.setFINVDET_PICE_QTY(Qty);
        invDet.setFINVDET_TYPE("SA");
        invDet.setFINVDET_BT_TAX_AMT("0");
        invDet.setFINVDET_RECORD_ID("");
        invDet.setFINVDET_SEQNO(seqno + "");
        invDet.setFINVDET_T_SELL_PRICE(String.format("%.2f", amt / Double.parseDouble(invDet.getFINVDET_QTY())));
        invDet.setFINVDET_BT_SELL_PRICE(String.format("%.2f", amt / Double.parseDouble(invDet.getFINVDET_QTY())));
        invDet.setFINVDET_REFNO(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.VanNumVal)));
        invDet.setFINVDET_BRAND_DISCPER("0");
        invDet.setFINVDET_BRAND_DISC("0");
        invDet.setFINVDET_COMDISC("0");
        invDet.setFINVDET_TXN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        invDet.setFINVDET_TXN_TYPE("22");
        invDet.setFINVDET_IS_ACTIVE("1");
        invDet.setFINVDET_QOH(qoh);
        invDet.setFINVDET_DISVALAMT("0");
        invDet.setFINVDET_PRICE(price);
        invDet.setFINVDET_CHANGED_PRICE(changedPrice);
        invDet.setFINVDET_AMT(String.format("%.2f", amt));
        invDet.setFINVDET_BAL_QTY(Qty);
        arrList.add(invDet);
        new InvDetController(getActivity()).createOrUpdateInvDet(arrList);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;
            try {
                Method m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
            }

            catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectionError();
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    //textStatus.setText("STATUS : CONNECTION ERROR!");
                    e1.printStackTrace();
                }

            }

            if(success){
                //connect successful
                final String msgconnected = "Connected.";

                getActivity().runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        textStatus.setText("Status : Connected.");
                        textStatus.setBackgroundResource(R.color.Green);
                    }});

            }else{
                //fail
                Log.e("<<ERROR>>", "Failed to connect.");
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        connectionError();
                    }});
                //connectionError();
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    private void connectionError(){
       // textStatus.setText("STATUS : BLUETOOTH CONNECTION ERROR!");
        textStatus.setBackgroundResource(R.color.blue_c);
    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/


}
