package com.datamation.hmdsfa.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.hmdsfa.R;
import com.datamation.hmdsfa.controller.DayExpHedController;
import com.datamation.hmdsfa.controller.DayNPrdHedController;
import com.datamation.hmdsfa.controller.FreeDebController;
import com.datamation.hmdsfa.controller.FreeDetController;
import com.datamation.hmdsfa.controller.FreeHedController;
import com.datamation.hmdsfa.controller.FreeItemController;
import com.datamation.hmdsfa.controller.ItemController;
import com.datamation.hmdsfa.controller.ItemLocController;
import com.datamation.hmdsfa.controller.ItemPriceController;
import com.datamation.hmdsfa.controller.LocationsController;
import com.datamation.hmdsfa.controller.OrderController;
import com.datamation.hmdsfa.controller.OutstandingController;
import com.datamation.hmdsfa.controller.RouteController;
import com.datamation.hmdsfa.controller.RouteDetController;
import com.datamation.hmdsfa.controller.SalesPriceController;
import com.datamation.hmdsfa.dialog.CustomProgressDialog;
import com.datamation.hmdsfa.helpers.NetworkFunctions;
import com.datamation.hmdsfa.helpers.SharedPref;
import com.datamation.hmdsfa.model.DayExpHed;
import com.datamation.hmdsfa.model.DayNPrdHed;
import com.datamation.hmdsfa.model.FddbNote;
import com.datamation.hmdsfa.model.FreeDeb;
import com.datamation.hmdsfa.model.FreeDet;
import com.datamation.hmdsfa.model.FreeHed;
import com.datamation.hmdsfa.model.FreeItem;
import com.datamation.hmdsfa.model.Item;
import com.datamation.hmdsfa.model.ItemLoc;
import com.datamation.hmdsfa.model.ItemPri;
import com.datamation.hmdsfa.model.Locations;
import com.datamation.hmdsfa.model.Order;
import com.datamation.hmdsfa.model.Route;
import com.datamation.hmdsfa.model.RouteDet;
import com.datamation.hmdsfa.model.SalesPrice;
import com.datamation.hmdsfa.utils.NetworkUtil;
import com.datamation.hmdsfa.view.ActivityHome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class FragmentCategoryWiseDownload extends Fragment {

    private View view;
    private TextView downItems,downFree,downRoute,downOutstanding;
    NetworkFunctions networkFunctions;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_download, container, false);

        getActivity().setTitle("Category Wise Download");
        networkFunctions = new NetworkFunctions(getActivity());
        //initializations
         downItems       = (TextView) view.findViewById(R.id.items_download);
         downFree        = (TextView) view.findViewById(R.id.free_download);
         downRoute       = (TextView) view.findViewById(R.id.route_download);
         downOutstanding = (TextView) view.findViewById(R.id.outstanding_download);

        downItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        boolean connectionStatus = NetworkUtil.isNetworkAvailable(getActivity());
        if (connectionStatus == true) {

            if (isAllUploaded(getActivity())) {

                try {
                    new itemsDownload(SharedPref.getInstance(getActivity()).getLoginUser().getRepCode()).execute();
                } catch (Exception e) {
                    Log.e("## ErrorInItemDown ##", e.toString());
                }
            } else {
                Toast.makeText(getActivity(), "Please Upload All Transactions", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }

    }
});

        downFree.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        boolean connectionStatus = NetworkUtil.isNetworkAvailable(getActivity());
        if (connectionStatus == true) {

        if (isAllUploaded(getActivity())) {

        try {
            new salespriceDownload(SharedPref.getInstance(getActivity()).getLoginUser().getRepCode()).execute();
            //new freeDownload(SharedPref.getInstance(getActivity()).getLoginUser().getRepCode()).execute();
        } catch (Exception e) {
            Log.e("## ErrorInItemDown ##", e.toString());
        }
        } else {
        Toast.makeText(getActivity(), "Please Upload All Transactions", Toast.LENGTH_LONG).show();
        }
        } else {
        Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
        }
        });

        downRoute.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
            boolean connectionStatus = NetworkUtil.isNetworkAvailable(getActivity());
            if (connectionStatus == true) {

                if (isAllUploaded(getActivity())) {

                    try {
                        new routeDownload(SharedPref.getInstance(getActivity()).getLoginUser().getRepCode()).execute();
                    } catch (Exception e) {
                        Log.e("## ErrorInItemDown ##", e.toString());
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Upload All Transactions", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
            }
        });

        downOutstanding.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
            boolean connectionStatus = NetworkUtil.isNetworkAvailable(getActivity());
            if (connectionStatus == true) {

                if (isAllUploaded(getActivity())) {

                    try {
                        new outstandingDownload(SharedPref.getInstance(getActivity()).getLoginUser().getRepCode()).execute();
                    } catch (Exception e) {
                        Log.e("## ErrorInItemDown ##", e.toString());
                    }
                } else {
                    Toast.makeText(getActivity(), "Please Upload All Transactions", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
            }
             }
        });
        //DISABLED BACK NAVIGATION
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("", "keyCode: " + keyCode);
                ActivityHome.navigation.setVisibility(View.VISIBLE);

                if( keyCode == KeyEvent.KEYCODE_BACK ) {
                    Toast.makeText(getActivity(), "Back button disabled!", Toast.LENGTH_SHORT).show();
                    return true;
                }else if ((keyCode == KeyEvent.KEYCODE_HOME)) {

                    getActivity().finish();

                    return true;

                } else {
                    return false;
                }
            }
        });


        return view;
    }


    private boolean isAllUploaded(Context context) {
        Boolean allUpload = false;

        OrderController orderHed = new OrderController(context);
        ArrayList<Order> ordHedList = orderHed.getAllUnSyncOrdHed();

        DayNPrdHedController npHed = new DayNPrdHedController(context);
        ArrayList<DayNPrdHed> npHedList = npHed.getUnSyncedData();

        DayExpHedController exHed = new DayExpHedController(context);
        ArrayList<DayExpHed> exHedList = exHed.getUnSyncedData();

        if (ordHedList.isEmpty() && npHedList.isEmpty() && exHedList.isEmpty()) {
            allUpload = true;
        } else {
            allUpload = false;
        }

        return allUpload;
    }

    //item download asynctask
    private class itemsDownload extends AsyncTask<String, Integer, Boolean> {
        CustomProgressDialog pdialog;
        private String repcode;

        public itemsDownload(String repCode) {
            this.repcode = repCode;
            this.pdialog = new CustomProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new CustomProgressDialog(getActivity());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Downloading Items...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                if (SharedPref.getInstance(getActivity()).getLoginUser() != null && SharedPref.getInstance(getActivity()).isLoggedIn()) {

                    /*****************Item Loc*****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Downloading item data...");
                        }
                    });

                    /*****************Item*****************************************************************************/

                    String item = "";
                    try {
                        item = networkFunctions.getItems(repcode);
                        // Log.d(LOG_TAG, "OUTLETS :: " + outlets);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }

                    // Processing item price
                    try {
                        JSONObject itemJSON = new JSONObject(item);
                        JSONArray itemJSONArray = itemJSON.getJSONArray("fItemsResult");
                        ArrayList<Item> itemList = new ArrayList<Item>();
                        ItemController itemController = new ItemController(getActivity());
                        for (int i = 0; i < itemJSONArray.length(); i++) {
                            itemList.add(Item.parseItem(itemJSONArray.getJSONObject(i)));
                        }
                        itemController.InsertOrReplaceItems(itemList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end item **********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Download complete...");
                        }
                    });
                    return true;
                } else {
                    //errors.add("Please enter correct username and password");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();

                return false;
            } catch (JSONException e) {
                e.printStackTrace();

                return false;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            pdialog.setMessage("Finalizing item data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            }
        }
    }

    //salesprice download asynctask
    private class salespriceDownload extends AsyncTask<String, Integer, Boolean> {
        CustomProgressDialog pdialog;
        private String repcode;

        public salespriceDownload(String repcode) {
            this.repcode = repcode;
            this.pdialog = new CustomProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new CustomProgressDialog(getActivity());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Downloading SalesPrice...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                if (SharedPref.getInstance(getActivity()).getLoginUser() != null && SharedPref.getInstance(getActivity()).isLoggedIn()) {

                    /*****************Sales Price****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Downloading Sales Price Data...");
                        }
                    });

                    String salespri = "";
                    try {
                        salespri = networkFunctions.getSalesPrice();
                        // Log.d(LOG_TAG, "OUTLETS :: " + outlets);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }


                    try {
                        JSONObject salesPriceJSON = new JSONObject(salespri);
                        JSONArray salesPriJSONJSONArray = salesPriceJSON.getJSONArray("SalesPriceResult");
                        ArrayList<SalesPrice> salesPriList = new ArrayList<SalesPrice>();
                        SalesPriceController salesPriceController = new SalesPriceController(getActivity());
                        for (int i = 0; i < salesPriJSONJSONArray.length(); i++) {
                            Log.d(">>>", ">>>" + i);
                            salesPriList.add(SalesPrice.parseSalespri(salesPriJSONJSONArray.getJSONObject(i)));
                        }
                        Log.d(">>>", "size :" + salesPriList.size());
                        salesPriceController.InsertOrReplaceSalesPrice(salesPriList);
                    } catch (JSONException | NumberFormatException e) {
                        Log.d(">>>", "error in fragment :" + e.toString());

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end sales price **********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Download complete...");
                        }
                    });
                    return true;
                } else {
                    //errors.add("Please enter correct username and password");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();

                return false;
            } catch (JSONException e) {
                e.printStackTrace();

                return false;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            pdialog.setMessage("Finalizing Sales Price data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            }
        }
    }

    //free download asynctask

    private class freeDownload extends AsyncTask<String, Integer, Boolean> {
        CustomProgressDialog pdialog;
        private String repcode;

        public freeDownload(String repCode) {
            this.repcode = repCode;
            this.pdialog = new CustomProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new CustomProgressDialog(getActivity());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Downloading Free...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                if (SharedPref.getInstance(getActivity()).getLoginUser() != null && SharedPref.getInstance(getActivity()).isLoggedIn()) {

                    /*****************FreeHed**********************************************************************/
                    String freehed = "";
                    try {
                        freehed = networkFunctions.getFreeHed(repcode);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }


                    // Processing freehed
                    try {
                        JSONObject freeHedJSON = new JSONObject(freehed);
                        JSONArray freeHedJSONArray = freeHedJSON.getJSONArray("FfreehedResult");
                        ArrayList<FreeHed> freeHedList = new ArrayList<FreeHed>();
                        FreeHedController freeHedController = new FreeHedController(getActivity());
                        for (int i = 0; i < freeHedJSONArray.length(); i++) {
                            freeHedList.add(FreeHed.parseFreeHed(freeHedJSONArray.getJSONObject(i)));
                        }
                        freeHedController.createOrUpdateFreeHed(freeHedList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end freeHed**********************************************************************/
                    /*****************Freedet**********************************************************************/
                    String freedet = "";
                    try {
                        freedet = networkFunctions.getFreeDet();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }

                    try {
                        JSONObject freedetJSON = new JSONObject(freedet);
                        JSONArray freedetJSONArray = freedetJSON.getJSONArray("FfreedetResult");
                        ArrayList<FreeDet> freedetList = new ArrayList<FreeDet>();
                        FreeDetController freedetController = new FreeDetController(getActivity());
                        for (int i = 0; i < freedetJSONArray.length(); i++) {
                            freedetList.add(FreeDet.parseFreeDet(freedetJSONArray.getJSONObject(i)));
                        }
                        freedetController.createOrUpdateFreeDet(freedetList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end freedet**********************************************************************/
                    /*****************freedeb**********************************************************************/
                    String freedeb = "";
                    try {
                        freedeb = networkFunctions.getFreeDebs();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }


                    FreeDebController freedebController = new FreeDebController(getActivity());
                    freedebController.deleteAll();
                    // Processing freedeb
                    try {
                        JSONObject freedebJSON = new JSONObject(freedeb);
                        JSONArray freedebJSONArray = freedebJSON.getJSONArray("FfreedebResult");
                        ArrayList<FreeDeb> freedebList = new ArrayList<FreeDeb>();

                        for (int i = 0; i < freedebJSONArray.length(); i++) {
                            freedebList.add(FreeDeb.parseFreeDeb(freedebJSONArray.getJSONObject(i)));
                        }
                        freedebController.createOrUpdateFreeDeb(freedebList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end freedeb**********************************************************************/
                    /*****************Freeslab**********************************************************************/
                    String freeitem = "";
                    try {
                        freeitem = networkFunctions.getFreeItems();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }

                    // Processing freeItem
                    try {
                        JSONObject freeitemJSON = new JSONObject(freeitem);
                        JSONArray freeitemJSONArray = freeitemJSON.getJSONArray("fFreeItemResult");
                        ArrayList<FreeItem> freeitemList = new ArrayList<FreeItem>();
                        FreeItemController freeitemController = new FreeItemController(getActivity());
                        for (int i = 0; i < freeitemJSONArray.length(); i++) {
                            freeitemList.add(FreeItem.parseFreeItem(freeitemJSONArray.getJSONObject(i)));
                        }
                        freeitemController.createOrUpdateFreeItem(freeitemList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end freeItem**********************************************************************/

                    return true;
                } else {
                    //errors.add("Please enter correct username and password");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();

                return false;
            } catch (JSONException e) {
                e.printStackTrace();

                return false;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            pdialog.setMessage("Finalizing free data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            }
        }
    }

    //route download asynctask
    private class routeDownload extends AsyncTask<String, Integer, Boolean> {
        CustomProgressDialog pdialog;
        private String repcode;

        public routeDownload(String repCode) {
            this.repcode = repCode;
            this.pdialog = new CustomProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new CustomProgressDialog(getActivity());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Downloading routes...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                if (SharedPref.getInstance(getActivity()).getLoginUser() != null && SharedPref.getInstance(getActivity()).isLoggedIn()) {

                    /*****************Item Loc*****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Downloading route data...");
                        }
                    });

                    String route = "";
                    try {
                        route = networkFunctions.getRoutes(repcode);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (routes)...");
                        }
                    });

                    // Processing route
                    try {
                        JSONObject routeJSON = new JSONObject(route);
                        JSONArray routeJSONArray = routeJSON.getJSONArray("fRouteResult");
                        ArrayList<Route> routeList = new ArrayList<Route>();
                        RouteController routeController = new RouteController(getActivity());
                        for (int i = 0; i < routeJSONArray.length(); i++) {
                            routeList.add(Route.parseRoute(routeJSONArray.getJSONObject(i)));
                        }
                        routeController.createOrUpdateFRoute(routeList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end route**********************************************************************/
                    /*****************Route det**********************************************************************/

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Last invoices downloaded\nDownloading route details...");
                        }
                    });

                    String routedet = "";
                    try {
                        routedet = networkFunctions.getRouteDets(repcode);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (routes)...");
                        }
                    });

                    // Processing route
                    try {
                        JSONObject routeJSON = new JSONObject(routedet);
                        JSONArray routeJSONArray = routeJSON.getJSONArray("fRouteDetResult");
                        ArrayList<RouteDet> routeList = new ArrayList<RouteDet>();
                        RouteDetController routeController = new RouteDetController(getActivity());
                        for (int i = 0; i < routeJSONArray.length(); i++) {
                            routeList.add(RouteDet.parseRoute(routeJSONArray.getJSONObject(i)));
                        }
                        routeController.InsertOrReplaceRouteDet(routeList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    /*****************end route det**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Download complete...");
                        }
                    });
                    return true;
                } else {
                    //errors.add("Please enter correct username and password");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();

                return false;
            } catch (JSONException e) {
                e.printStackTrace();

                return false;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            pdialog.setMessage("Finalizing item data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            }
        }
    }

    //outstanding download asynctask
    private class outstandingDownload extends AsyncTask<String, Integer, Boolean> {
        CustomProgressDialog pdialog;
        private String repcode;

        public outstandingDownload(String repCode) {
            this.repcode = repCode;
            this.pdialog = new CustomProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new CustomProgressDialog(getActivity());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Downloading outstanding...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            try {
                if (SharedPref.getInstance(getActivity()).getLoginUser() != null && SharedPref.getInstance(getActivity()).isLoggedIn()) {

                    /*****************Item Loc*****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Downloading outstanding data...");
                        }
                    });

                    /*****************fddbnote*****************************************************************************/

                    String fddbnote = "";
                    try {
                        fddbnote = networkFunctions.getFddbNotes(repcode);
                        // Log.d(LOG_TAG, "OUTLETS :: " + outlets);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw e;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (outstanding details)...");
                        }
                    });

                    // Processing fddbnote
                    try {
                        JSONObject fddbnoteJSON = new JSONObject(fddbnote);
                        JSONArray fddbnoteJSONArray = fddbnoteJSON.getJSONArray("fDdbNoteWithConditionResult");
                        ArrayList<FddbNote> fddbnoteList = new ArrayList<FddbNote>();
                        OutstandingController outstandingController = new OutstandingController(getActivity());
                        for (int i = 0; i < fddbnoteJSONArray.length(); i++) {
                            fddbnoteList.add(FddbNote.parseFddbnote(fddbnoteJSONArray.getJSONObject(i)));
                        }
                        outstandingController.createOrUpdateFDDbNote(fddbnoteList);
                    } catch (JSONException | NumberFormatException e) {

//                        ErrorUtil.logException("LoginActivity -> Authenticate -> doInBackground() # Process Routes and Outlets",
//                                e, routes, BugReport.SEVERITY_HIGH);

                        throw e;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Download complete...");
                        }
                    });
                    return true;
                } else {
                    //errors.add("Please enter correct username and password");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();

                return false;
            } catch (JSONException e) {
                e.printStackTrace();

                return false;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            pdialog.setMessage("Finalizing item data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }

            }
        }
    }
}
