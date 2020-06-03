package com.datamation.hmdsfa.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.datamation.hmdsfa.R;
import com.datamation.hmdsfa.api.ApiCllient;
import com.datamation.hmdsfa.api.ApiInterface;
import com.datamation.hmdsfa.controller.BankController;
import com.datamation.hmdsfa.controller.CustomerController;
import com.datamation.hmdsfa.controller.DayExpHedController;
import com.datamation.hmdsfa.controller.DayNPrdHedController;
import com.datamation.hmdsfa.controller.ExpenseController;
import com.datamation.hmdsfa.controller.FItenrDetController;
import com.datamation.hmdsfa.controller.FItenrHedController;
import com.datamation.hmdsfa.controller.FirebaseMediaController;
import com.datamation.hmdsfa.controller.FreeDebController;
import com.datamation.hmdsfa.controller.FreeDetController;
import com.datamation.hmdsfa.controller.FreeHedController;
import com.datamation.hmdsfa.controller.FreeItemController;
import com.datamation.hmdsfa.controller.FreeMslabController;
import com.datamation.hmdsfa.controller.FreeSlabController;
import com.datamation.hmdsfa.controller.InvDetController;
import com.datamation.hmdsfa.controller.InvoiceBarcodeController;
import com.datamation.hmdsfa.controller.IteaneryDebController;
import com.datamation.hmdsfa.controller.ItemBundleController;
import com.datamation.hmdsfa.controller.ItemController;
import com.datamation.hmdsfa.controller.OrderController;
import com.datamation.hmdsfa.controller.ReasonController;
import com.datamation.hmdsfa.controller.ReceiptDetController;
import com.datamation.hmdsfa.controller.ReferenceDetailDownloader;
import com.datamation.hmdsfa.controller.ReferenceSettingController;
import com.datamation.hmdsfa.controller.RouteController;
import com.datamation.hmdsfa.controller.RouteDetController;
import com.datamation.hmdsfa.controller.SalRepController;
import com.datamation.hmdsfa.controller.SalesPriceController;
import com.datamation.hmdsfa.controller.VATController;
import com.datamation.hmdsfa.dialog.CustomProgressDialog;
import com.datamation.hmdsfa.dialog.StockInquiryDialog;
import com.datamation.hmdsfa.helpers.NetworkFunctions;
import com.datamation.hmdsfa.helpers.SharedPref;
import com.datamation.hmdsfa.helpers.UploadTaskListener;
import com.datamation.hmdsfa.model.Bank;
import com.datamation.hmdsfa.model.CompanyBranch;
import com.datamation.hmdsfa.model.DayExpHed;
import com.datamation.hmdsfa.model.DayNPrdHed;
import com.datamation.hmdsfa.model.Debtor;
import com.datamation.hmdsfa.model.Expense;
import com.datamation.hmdsfa.model.FItenrDet;
import com.datamation.hmdsfa.model.FItenrHed;
import com.datamation.hmdsfa.model.FirebaseData;
import com.datamation.hmdsfa.model.FreeDeb;
import com.datamation.hmdsfa.model.FreeDet;
import com.datamation.hmdsfa.model.FreeHed;
import com.datamation.hmdsfa.model.FreeItem;
import com.datamation.hmdsfa.model.FreeMslab;
import com.datamation.hmdsfa.model.FreeSlab;
import com.datamation.hmdsfa.model.InvHed;
import com.datamation.hmdsfa.model.Item;
import com.datamation.hmdsfa.model.ItemBundle;
import com.datamation.hmdsfa.model.ItenrDeb;
import com.datamation.hmdsfa.model.Order;
import com.datamation.hmdsfa.model.Reason;
import com.datamation.hmdsfa.model.Route;
import com.datamation.hmdsfa.model.RouteDet;
import com.datamation.hmdsfa.model.SalRep;
import com.datamation.hmdsfa.model.SalesPrice;
import com.datamation.hmdsfa.model.User;
import com.datamation.hmdsfa.model.VatMaster;
import com.datamation.hmdsfa.model.apimodel.ReadJsonList;
import com.datamation.hmdsfa.settings.TaskType;
import com.datamation.hmdsfa.utils.NetworkUtil;
import com.datamation.hmdsfa.utils.UtilityContainer;
import com.datamation.hmdsfa.vansale.UploadVanSales;
import com.datamation.hmdsfa.view.DayExpenseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***@Auther - rashmi**/

public class FragmentToolsOld extends Fragment implements View.OnClickListener, UploadTaskListener {

    private Context context = getActivity();
    User loggedUser;
    View view;
    int count = 0;
    Animation animScale;
    ImageView imgSync, imgUpload, imgPrinter, imgDatabase, imgStockDown, imgStockInq, imgSalesRep, imgTour, imgDayExp, imgImage, imgVideo;
    NetworkFunctions networkFunctions;
    SharedPref pref;
    List<String> resultList;
    LinearLayout layoutTool;
    private long timeInMillis;
    private Handler mHandler;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    ArrayList<FirebaseData> imgList, vdoList;
    ApiInterface apiInterface;

    ArrayList<FirebaseData> imgUrlList;
    ArrayList<FirebaseData> vdoUrlList;
    FirebaseData fd;

    DatabaseReference rootRef;
    FirebaseMediaController fmc;

    boolean isAnyActiveImages = false;
    boolean isAnyActiveVideos = false;
    boolean isImageFitToScreen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_management_tools, container, false);
        pref = SharedPref.getInstance(getActivity());

        animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_scale);
        layoutTool = (LinearLayout) view.findViewById(R.id.layoutTool);
        imgTour = (ImageView) view.findViewById(R.id.imgTourInfo);
        imgStockInq = (ImageView) view.findViewById(R.id.imgStockInquiry);
        imgSync = (ImageView) view.findViewById(R.id.imgSync);
        imgUpload = (ImageView) view.findViewById(R.id.imgUpload);
        imgStockDown = (ImageView) view.findViewById(R.id.imgDownload);
        imgPrinter = (ImageView) view.findViewById(R.id.imgPrinter);
        imgDatabase = (ImageView) view.findViewById(R.id.imgSqlite);
        imgSalesRep = (ImageView) view.findViewById(R.id.imgSalrep);
        imgDayExp = (ImageView) view.findViewById(R.id.imgDayExp);
        imgImage = (ImageView) view.findViewById(R.id.imgImage);
        imgVideo = (ImageView) view.findViewById(R.id.imgVideo);

        fmc = new FirebaseMediaController(getActivity());
        mHandler = new Handler(Looper.getMainLooper());
        imgList = new ArrayList<FirebaseData>();
        vdoList = new ArrayList<FirebaseData>();

        rootRef = FirebaseDatabase.getInstance().getReference();

        getImgDataFromFirebase(rootRef);
        getVdoDataFromFirebase(rootRef);

        isAnyActiveImages = new InvDetController(getActivity()).isAnyActiveOrders();
        isAnyActiveVideos = new ReceiptDetController(getActivity()).isAnyActiveReceipt();

        if (isAnyActiveImages) {
            imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_image));
        } else {
            imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_image));
        }

        if (isAnyActiveVideos) {
            imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video));
        } else {
            imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video));
        }
        networkFunctions = new NetworkFunctions(getActivity());
        imgTour.setOnClickListener(this);
        imgStockInq.setOnClickListener(this);
        imgSync.setOnClickListener(this);
        imgUpload.setOnClickListener(this);
        imgStockDown.setOnClickListener(this);
        imgPrinter.setOnClickListener(this);
        imgDatabase.setOnClickListener(this);
        imgSalesRep.setOnClickListener(this);
        imgDayExp.setOnClickListener(this);
        imgImage.setOnClickListener(this);
        imgVideo.setOnClickListener(this);
        resultList = new ArrayList<>();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        timeInMillis = System.currentTimeMillis();

        Log.d("FRAGMENT_TOOL", "IMAGE_FLAG: " + pref.getImageFlag());


        if (fmc.getAllMediaforCheckIfIsExist("IMG") > 0) {
            imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_img_notification));
        } else {
            imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_image));
        }

        if (fmc.getAllMediaforCheckIfIsExist("VDO") > 0) {
            imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video_notification));
        } else {
            imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video));
        }

        return view;
    }

    private void getVdoDataFromFirebase(DatabaseReference rootRef) {
        DatabaseReference chatSpaceRef = rootRef.child("Videos");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    try
                    {
                        int flag = ds.child("FLAG").getValue(Integer.class);
                        String mType = ds.child("M_TYPE").getValue(String.class);
                        List<Object> repCodeList = (List<Object>) ds.child("REPCODE").getValue();
                        String url = ds.child("URL").getValue(String.class);
                        if(repCodeList.size()>0)
                            if (repCodeList.contains(pref.getLoginUser().getRepCode()) && (flag == 0)) {
                                FirebaseData fd = new FirebaseData();
                                fd.setMEDIA_FLAG(flag + "");
                                fd.setMEDIA_URL(url);
                                fd.setMEDIA_TYPE(mType);
                                vdoList.add(fd);
                                Log.d("*TAG", url + "," + flag + "," + repCodeList + "" + pref.getLoginUser().getRepCode() + ", " + mType);
                            }
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getActivity(),"Video Media Problem....",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("*ERR", databaseError + "");
            }
        };
        chatSpaceRef.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgTourInfo:
                imgTour.startAnimation(animScale);
                UtilityContainer.mLoadFragment(new FragmentMarkAttendance(), getActivity());
                break;

            case R.id.imgStockInquiry:
                imgStockInq.startAnimation(animScale);//
                new StockInquiryDialog(getActivity());
                break;

            case R.id.imgSync:
                imgSync.startAnimation(animScale);
                Log.d("Validate Secondary Sync", ">>Mac>> " + pref.getMacAddress().trim() + " >>URL>> " + pref.getBaseURL() + " >>DB>> " + pref.getDistDB());
                try {
                    if(NetworkUtil.isNetworkAvailable(getActivity())) {
                        new Validate(pref.getMacAddress().trim(), pref.getBaseURL(), pref.getDistDB()).execute();
                    }else{
                        Toast.makeText(getActivity(),"No internet connection",Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e){
                    Log.e(">>>> Secondary Sync",e.toString());
                }
                break;

            case R.id.imgUpload:
                imgUpload.startAnimation(animScale);
                syncDialog(getActivity());
                break;

            case R.id.imgDownload:
                imgStockDown.startAnimation(animScale);
                UtilityContainer.mLoadFragment(new FragmentCategoryWiseDownload(), getActivity());
                break;

            case R.id.imgPrinter:
                imgPrinter.startAnimation(animScale);
                UtilityContainer.mPrinterDialogbox(getActivity());
                break;

            case R.id.imgSqlite:
                imgDatabase.startAnimation(animScale);
                UtilityContainer.mSQLiteDatabase(getActivity());
                break;

            case R.id.imgSalrep:
                imgSalesRep.startAnimation(animScale);
                ViewRepProfile();
                break;

            case R.id.imgDayExp:
                imgDayExp.startAnimation(animScale);
                Intent intent = new Intent(getActivity(), DayExpenseActivity.class);
                startActivity(intent);
                break;

            case R.id.imgImage:
                imgImage.startAnimation(animScale);
                imgUrlList = fmc.getAllMediafromDb("IMG");
                ViewImageList();
                break;

            case R.id.imgVideo:
                imgVideo.startAnimation(animScale);
                vdoUrlList = fmc.getAllMediafromDb("VDO");
                ViewVideoList();
                break;

        }

    }


    public void ViewImageList() {
        final Dialog imageDialog = new Dialog(getActivity());
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        imageDialog.setCancelable(false);
        imageDialog.setCanceledOnTouchOutside(false);
        imageDialog.setContentView(R.layout.whatsapp_image_layout);

        LinearLayout parentLayout = (LinearLayout) imageDialog.findViewById(R.id.image_layout);


        if (imgUrlList != null) {
            for (FirebaseData fd : imgUrlList) {
//            for (int i = 0; i < imgUrlList.size(); i++) {
                try {
                    final ImageView imageButton = new ImageView(getActivity());
                    final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, 400);
                    lp.setMargins(20, 20, 20, 20);
                    imageButton.setLayoutParams(lp);

                    Glide.with(this)
                            .load(fd.getMEDIA_URL())
                            .into(imageButton);

                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isImageFitToScreen) {
                                isImageFitToScreen = false;
                                imageButton.setLayoutParams(lp);
                                imageButton.setAdjustViewBounds(true);
                            } else {
                                isImageFitToScreen = true;
                                imageButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                        }
                    });

                    parentLayout.addView(imageButton);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } else {
            Toast.makeText(getActivity(), "No Images to show!", Toast.LENGTH_SHORT).show();
        }

        imageDialog.findViewById(R.id.got_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int result = fmc.createOrUpdateFirebaseData(imgList, 1);
                if (result > 0) {
                    Toast.makeText(getActivity(), "Image flag updated", Toast.LENGTH_SHORT).show();
                }

                if (fmc.getAllMediaforCheckIfIsExist("IMG") > 0) {
                    imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_img_notification));
                } else {
                    imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_image));
                }
                imageDialog.dismiss();
            }
        });

        imageDialog.show();
    }


    public void ViewVideoList() {
        final Dialog videoDialog = new Dialog(getActivity());
        videoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        videoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        videoDialog.setCancelable(false);
        videoDialog.setCanceledOnTouchOutside(false);
        //videoDialog.setContentView(R.layout.whatsapp_video_responsive_layout);
        videoDialog.setContentView(R.layout.whatsapp_video_layout);
        LinearLayout parentLayout = (LinearLayout) videoDialog.findViewById(R.id.video_layout);

        for (FirebaseData fd : vdoUrlList) {
            try {
                final fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard videoView = new fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard(getActivity());
                final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(475, 250);
                lp.setMargins(20, 20, 20, 20);
                videoView.setLayoutParams(lp);
                String pathName = "" + fd.getMEDIA_URL();
                videoView.setUp(pathName, "SWADESHI");
                videoView.ivThumb.setImageDrawable(getResources().getDrawable(R.drawable.video));
                parentLayout.addView(videoView);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        videoDialog.findViewById(R.id.got_it).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vdoresult = fmc.createOrUpdateFirebaseData(vdoList, 1);
                if (vdoresult > 0) {
                    Toast.makeText(getActivity(), "Video flag updated", Toast.LENGTH_SHORT).show();
                }
                if (fmc.getAllMediaforCheckIfIsExist("VDO") > 0) {
                    imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video_notification));
                } else {
                    imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video));
                }
                videoDialog.dismiss();
            }
        });
        videoDialog.show();
    }

    public void ViewRepProfile() {
        final Dialog repDialog = new Dialog(getActivity());
        repDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        repDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        repDialog.setCancelable(false);
        repDialog.setCanceledOnTouchOutside(false);
        repDialog.setContentView(R.layout.rep_profile);

        //initializations
        TextView repname = (TextView) repDialog.findViewById(R.id.repname);
        final TextView repcode = (TextView) repDialog.findViewById(R.id.repcode);
        final TextView repPrefix = (TextView) repDialog.findViewById(R.id.repPrefix);
        // final TextView locCode = (TextView) repDialog.findViewById(R.id.target);
        final EditText repemail = (EditText) repDialog.findViewById(R.id.email);
        final TextView areaCode = (TextView) repDialog.findViewById(R.id.areaCode);
        final TextView dealCode = (TextView) repDialog.findViewById(R.id.dealclode);
        //  areaCode.setText(loggedUser.getRoute());
        final SalRep rep = new SalRepController(getActivity()).getSaleRepDet(new SalRepController(getActivity()).getCurrentRepCode());
        repname.setText(rep.getNAME());
        repcode.setText(rep.getRepCode());
        repPrefix.setText(rep.getPREFIX());
        repemail.setText(rep.getEMAIL());

        //close
        repDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repemail.length() > 0) {
                    if (isEmailValid(repemail.getText().toString())) {
                        ArrayList<SalRep> salRepslist = new ArrayList<>();
                        rep.setEMAIL(repemail.getText().toString().trim());
                        salRepslist.add(rep);
                        new SalRepController(getActivity()).createOrUpdateSalRep(salRepslist);
                        repDialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Invalid email address, Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    repDialog.dismiss();
                }
            }
        });
        repDialog.show();
    }

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches())
            return true;
        else
            return false;
    }

    private void syncDialog(final Context context) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Are you sure, Do you want to Upload Data?")
                .positiveColor(ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button))
                .positiveText("Yes")
                .negativeColor(ContextCompat.getColor(getActivity(), R.color.material_alert_negative_button))
                .negativeText("No, Exit")
                .callback(new MaterialDialog.ButtonCallback() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        boolean connectionStatus = NetworkUtil.isNetworkAvailable(context);
                        if (connectionStatus == true) {
//                            try { // new customer upload 2019-10-17MMS
//                                ArrayList<SalRep> fblist = new ArrayList<>();
//                                SalRep salRep = new SalRep();
//                                salRep.setCONSOLE_DB(SharedPref.getInstance(context).getConsoleDB().trim());
//                                salRep.setDIST_DB(SharedPref.getInstance(context).getDistDB().trim());
//                                salRep.setRepCode(SharedPref.getInstance(context).getLoginUser().getRepCode());
//                                salRep.setFirebaseTokenID(SharedPref.getInstance(context).getFirebaseTokenKey());
//                                fblist.add(salRep);
//                                if (fblist.size() <= 0)
//                                    Toast.makeText(getActivity(), "No firebase records to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadFirebaseTokenKey(getActivity(), FragmentTools.this,fblist).execute(fblist);
////                                    new UploadAttendance(getActivity(), FragmentTools.this, attendList).execute(fblist);
//                                    Log.v(">>8>>", "Upload new firebase records finish" + fblist);
//                                }
//                            } catch (Exception e) {
//                                Log.v("Excp in sync attendance", e.toString());
//                            }
//                            try { // new customer upload 2019-10-17MMS
//                                AttendanceController attendanceController = new AttendanceController(getActivity());
//                                ArrayList<Attendance> attendList = attendanceController.getUnsyncedTourData();
//                                if (attendList.size() <= 0)
//                                    Toast.makeText(getActivity(), "No Attendance Records to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadAttendance(getActivity(), FragmentTools.this, attendList).execute(attendList);
//                                    Log.v(">>8>>", "Upload new Attendance execute finish");
//                                }
//                            } catch (Exception e) {
//                                Log.v("Excp in sync attendance", e.toString());
//                            }
//                            try { // new customer upload 2019-10-17MMS
//                                NewCustomerController customerDS = new NewCustomerController(getActivity());
//                                ArrayList<NewCustomer> newCustomers = customerDS.getAllNewCustomersForSync();
//                                if (newCustomers.size() <= 0)
//                                    Toast.makeText(getActivity(), "No Customer Records to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadNewCustomer(getActivity(), FragmentTools.this, newCustomers).execute(newCustomers);
//                                    Log.v(">>8>>", "Upload new customer execute finish");
//                                }
//                            } catch (Exception e) {
//                                Log.v("Exception in sync order", e.toString());
//                            }

//                            try {//existing update debtors upload - 2019-12-16
//                                CustomerController customerDS = new CustomerController(getActivity());
//                                ArrayList<Debtor> updExistingDebtors = customerDS.getAllUpdatedDebtors();
//                                if (updExistingDebtors.size() <= 0)
//                                    Toast.makeText(getActivity(), "No updated debtors to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadEditedDebtors(getActivity(), FragmentTools.this, updExistingDebtors).execute(updExistingDebtors);
//                                    Log.v(">>>>", "Updated debtors are uploaded");
//                                }
//                            } catch (Exception e) {
//                                Log.v("Excptn upld edted dbtrs", e.toString());
//                            }
//                            try {// debtor image uploads 2019-11-01MMS
//                                CustomerController customerDS = new CustomerController(getActivity());
//                                ArrayList<Debtor> imgUpdDebtors = customerDS.getAllImagUpdatedDebtors();
//                                if (imgUpdDebtors.size() <= 0)
//                                    Toast.makeText(getActivity(), "No Debtors business images to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadDebtorImges(getActivity(), FragmentTools.this, imgUpdDebtors).execute(imgUpdDebtors);
//                                    Log.v(">>8>>", "Debtor business images uploded");
//                                }
//                            } catch (Exception e) {
//                                Log.v("Exception business img", e.toString());
//                            }
//                            try {// debtor uploads 2019-10-21MMS
//                                CustomerController customerDS = new CustomerController(getActivity());
//                                ArrayList<Debtor> debtors = customerDS.getAllDebtorsToCordinatesUpdate();
//                                if (debtors.size() <= 0)
//                                    Toast.makeText(getActivity(), "No Debtor cordinates to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadDebtorCordinates(getActivity(), FragmentTools.this, debtors).execute(debtors);
//                                    Log.v(">>8>>", "Debtor cordinates uploded");
//                                }
//                            } catch (Exception e) {
//                                Log.v("Exception in sync De", e.toString());
//                            }
//                            try { // upload pre sale order
//                                OrderController orderHed = new OrderController(getActivity());
//                                ArrayList<Order> ordHedList = orderHed.getAllUnSyncOrdHed();
////                    /* If records available for upload then */
//                                if (ordHedList.size() <= 0)
//                                    Toast.makeText(getActivity(), "No Pre Sale Records to upload !", Toast.LENGTH_LONG).show();
//                                else {
//
//                                    new UploadPreSales(getActivity(), FragmentTools.this).execute(ordHedList);
//                                    Log.v(">>8>>", "UploadPreSales execute finish");
//                                    // new ReferenceNum(getActivity()).NumValueUpdate(getResources().getString(R.string.NumVal));
//                                }
//                            } catch (Exception e) {
//                                Log.v("Exception in sync order", e.toString());
//                            }
                            try {//Van sale upload - 2020-03-24-rashmi
                                //InvHedController hedDS = new InvHedController(getActivity());
                                InvoiceBarcodeController hedDS = new InvoiceBarcodeController(getActivity());
                                ArrayList<InvHed> invHedList = hedDS.getAllUnsynced();
//                    /* If records available for upload then */
                                if (invHedList.size() <= 0)
                                    Toast.makeText(getActivity(), "No Van Sale Records to upload !", Toast.LENGTH_LONG).show();
                                else{
                                    new UploadVanSales(getActivity(), FragmentToolsOld.this).execute(invHedList);
                                    Log.v(">>8>>","UploadPreSales execute finish");
                                }
                            }catch(Exception e){
                                Log.v("Exception in sync order",e.toString());
                            }
//                            try//Sales return upload -  2020-01-28-kaveesha
//                            {
//                                SalesReturnController retHed = new SalesReturnController(getActivity());
//                                ArrayList<FInvRHed> retHedList = retHed.getAllUnsyncedWithInvoice();
//                                if(retHedList.size() <= 0)
//                                {
//                                    Toast.makeText(getActivity(), "No Non Productive Records to upload !", Toast.LENGTH_LONG).show();
//                                }else
//                                {
//                                    new UploadSalesReturn(getActivity(),FragmentTools.this,"insertReturns").execute(retHedList);
//                                    Log.v(">>8>>","Upload sales return execute finish");
//                                }
//                            }
//                            catch (Exception e)
//                            {
//                                Log.v("Exception in sync return" , e.toString());
//                            }
//                            try { // upload Non productive 2019-10-23MMS
//                                DayNPrdHedController npHed = new DayNPrdHedController(getActivity());
//                                ArrayList<DayNPrdHed> npHedList = npHed.getUnSyncedData();
//                                if (npHedList.size() <= 0)
//                                    Toast.makeText(getActivity(), "No Non Productive Records to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadNonProd(getActivity(), FragmentTools.this).execute(npHedList);
//                                    Log.v(">>8>>", "Upload non productive execute finish");//
//                                }
//                            } catch (Exception e) {
//                                Log.v("Exception in sync order", e.toString());
//                            }
//                            try { // upload dAY eXPENSE
//                                DayExpHedController exHed = new DayExpHedController(getActivity());
//                                ArrayList<DayExpHed> exHedList = exHed.getUnSyncedData();
////                    /* If records available for upload then */
//                                if (exHedList.size() <= 0)
//                                    Toast.makeText(getActivity(), "No Expense Records to upload !", Toast.LENGTH_LONG).show();
//                                else {
//                                    new UploadExpenses(getActivity(), FragmentTools.this).execute(exHedList);
//                                    Log.v(">>8>>", "Upload expense execute finish");
//                                }
//                            } catch (Exception e) {
//                                Log.v("Exception in sync order", e.toString());
//                            }
                        } else
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .build();
        materialDialog.setCanceledOnTouchOutside(false);
        materialDialog.show();
    }

    public void mDevelopingMessage(String message, String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.info);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void syncMasterDataDialog(final Context context) {
        MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                .content("Are you sure, Do you want to Sync Master Data?")
                .positiveColor(ContextCompat.getColor(getActivity(), R.color.material_alert_positive_button))
                .positiveText("Yes")
                .negativeColor(ContextCompat.getColor(getActivity(), R.color.material_alert_negative_button))
                .negativeText("No, Exit")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        boolean connectionStatus = NetworkUtil.isNetworkAvailable(getActivity());
                        if (connectionStatus == true) {
                            if (isAllUploaded()) {
                                dialog.dismiss();
                                try {
                                    new secondarySync(SharedPref.getInstance(getActivity()).getLoginUser().getRepCode()).execute();
                                    SharedPref.getInstance(getActivity()).setGlobalVal("SyncDate", dateFormat.format(new Date(timeInMillis)));
                                } catch (Exception e) {
                                    Log.e("## ErrorIn2ndSync ##", e.toString());
                                }
                            } else {
                                Toast.makeText(context, "Please Upload All Transactions", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .build();
        materialDialog.setCanceledOnTouchOutside(false);
        materialDialog.show();
    }

    private boolean isAllUploaded() {
        Boolean allUpload = false;

        OrderController orderHed = new OrderController(getActivity());
        ArrayList<Order> ordHedList = orderHed.getAllUnSyncOrdHed();

        DayNPrdHedController npHed = new DayNPrdHedController(getActivity());
        ArrayList<DayNPrdHed> npHedList = npHed.getUnSyncedData();

        DayExpHedController exHed = new DayExpHedController(getActivity());
        ArrayList<DayExpHed> exHedList = exHed.getUnSyncedData();

        ArrayList<Debtor> imgUpdDebtors = new CustomerController(getActivity()).getAllImagUpdatedDebtors();
        // ArrayList<ReceiptHed> rcptHedList = receipts.getAllCompletedRecHed();

        if (ordHedList.isEmpty() && npHedList.isEmpty() && exHedList.isEmpty()) {
            allUpload = true;
        } else {
            allUpload = false;
        }

        return allUpload;
    }

    private void getImgDataFromFirebase(DatabaseReference rootRef) {
        DatabaseReference chatSpaceRef = rootRef.child("Images");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    try
                    {
                        int flag = ds.child("FLAG").getValue(Integer.class);
                        String mType = ds.child("M_TYPE").getValue(String.class);
                        List<String> repCodeList = (List<String>) ds.child("REPCODE").getValue();
                        String url = ds.child("URL").getValue(String.class);
                        if(repCodeList.size()>0)
                            if (repCodeList.contains(pref.getLoginUser().getRepCode()) && (flag == 0)) {
                                fd = new FirebaseData();
                                fd.setMEDIA_FLAG(flag + "");
                                fd.setMEDIA_URL(url);
                                fd.setMEDIA_TYPE(mType);
                                imgList.add(fd);

                                Log.d("*TAG", url + "," + flag + "," + repCodeList + "" + pref.getLoginUser().getRepCode() + " " + mType);
                            }
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getActivity(),"Image Media Problem....",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("*ERR", databaseError + "");
            }
        };
        chatSpaceRef.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public void onTaskCompleted(List<String> list) {
        resultList.addAll(list);
        String msg = "";
        for (String s : list) {
            msg += s;
        }
        resultList.clear();
        mUploadResult(msg);
    }

    //**********************secondary sysnc start***********************************************/
     private class secondarySync extends AsyncTask<String, Integer, Boolean> {
        int totalRecords = 0;
        CustomProgressDialog pdialog;
        private String repcode;
        private List<String> errors = new ArrayList<>();
        private Handler mHandler;

        public secondarySync(String repCode) {
            this.repcode = repCode;
            this.pdialog = new CustomProgressDialog(getActivity());
            mHandler = new Handler(Looper.getMainLooper());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog = new CustomProgressDialog(getActivity());
            mHandler = new Handler(Looper.getMainLooper());
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Authenticating...");
            pdialog.show();
        }
        @Override
        protected Boolean doInBackground(String... arg0) {
            apiInterface = ApiCllient.getClient(getActivity()).create(ApiInterface.class);
            mHandler = new Handler(Looper.getMainLooper());
            try {
                if (SharedPref.getInstance(getActivity()).getLoginUser() != null && SharedPref.getInstance(getActivity()).isLoggedIn()) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Downloading firebase media data...");
//                        }
//                    });
//                    Log.d("**$#*", "getImgDataFromFirebase: " + imgList);
//                    if (imgList.size() > 0) {
//                        int existImgRes = fmc.getAllIfIsExist(imgList);
//                        if (existImgRes > 0) {
//                            fmc.createOrUpdateFirebaseData(imgList, 0);
//                        } else {
//                            fmc.deleteAll("IMG");
//                            fmc.createOrUpdateFirebaseData(imgList, 0);
//                        }
//                    }
///*****************controls**********************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing firebase media data...");
//                        }
//                    });
//                    Log.d("*newvdoList", "doInBackground: " + vdoList);
//                    if (vdoList.size() > 0) {
//                        int existVdoRes = fmc.getAllIfIsExist(vdoList);
//                        if (existVdoRes > 0) {
//                            fmc.createOrUpdateFirebaseData(vdoList, 0);
//                        } else {
//                            fmc.deleteAll("VDO");
//                            fmc.createOrUpdateFirebaseData(vdoList, 0);
//                        }
//                    }

                    /*****************itenary detdeb**********************************************************************/

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Prices downloaded\nDownloading iteanery details...");
                        }
                    });
                    // Processing itenarydetdeb
                    try {
                        Calendar c = Calendar.getInstance();
                        int cyear = c.get(Calendar.YEAR);
                        int cmonth = c.get(Calendar.MONTH) + 1;
                        DecimalFormat df_month = new DecimalFormat("00");
                        String curmonth = df_month.format((double) cmonth);
                        Log.d(">>>>curmonth",">>>"+curmonth);
                        Call<ReadJsonList> resultCall = apiInterface.getIteaneryDeb(pref.getDistDB(),repcode,""+cyear,""+df_month.format((double) cmonth));

                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {


                                if(response.body() != null) {
                                    final IteaneryDebController itenaryDebController = new IteaneryDebController(getActivity());
                                    itenaryDebController.deleteAll();
                                    final ArrayList<ItenrDeb> itenaryDebList = new ArrayList<ItenrDeb>();
                                    for (int i = 0; i < response.body().getIteaneryDebList().size(); i++) {
                                        itenaryDebList.add(response.body().getIteaneryDebList().get(i));
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Iteanerydeb)...");
                                            itenaryDebController.InsertOrReplaceItenrDeb(itenaryDebList);
                                            pdialog.setMessage("Processed (Iteanerydeb)...");

                                                       }
                                    });

                                }else{
                                    errors.add("ItenrDeb response is null");
                                }
                            }

                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }

/*****************company details**********************************************************************/

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (Company details)...");
                        }
                    });



                    // Processing controls
                    try{
                        Call<ReadJsonList> resultCall = apiInterface.getControlResult(pref.getDistDB());
                        UtilityContainer.download(getActivity(),resultCall, TaskType.ItenrDeb);

                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getControlResult(pref.getDistDB());
//                        UtilityContainer.download(getActivity(),resultCall, TaskType.Controllist);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    final ArrayList<Control> controlList = new ArrayList<Control>();
//                                    for (int i = 0; i < response.body().getControlResult().size(); i++) {
//                                        controlList.add(response.body().getControlResult().get(i));
//                                    }
//                                    final CompanyDetailsController companyController = new CompanyDetailsController(getActivity());
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            pdialog.setMessage("Processing downloaded data (Company Details)...");
//                                            companyController.createOrUpdateFControl(controlList);
//                                            pdialog.setMessage("Processed (Company Details)...");
//
//                                        }
//                                    });
//
//                                }else{
//                                    errors.add("Control response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
/*****************outlets**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Downloading Customers...");
                        }
                    });
                    CustomerController customerController = new CustomerController(getActivity());
                    customerController.deleteAll();
                    // Processing outlets
                    try{
                        Call<ReadJsonList> resultCall = apiInterface.getDebtorResult(pref.getDistDB(),repcode);
                        UtilityContainer.download(getActivity(),resultCall, TaskType.Controllist);

                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getDebtorResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    final ArrayList<Debtor> debtorList = new ArrayList<Debtor>();
//                                    for (int i = 0; i < response.body().getDebtorResult().size(); i++) {
//                                        debtorList.add(response.body().getDebtorResult().get(i));
//                                    }
//                                    final CustomerController customerController = new CustomerController(getActivity());
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            pdialog.setMessage("Processing downloaded data (Customers)...");
//                                            customerController.InsertOrReplaceDebtor(debtorList);
//                                            pdialog.setMessage("Processed (Customers)...");
//
//                                        }
//                                    });
//
//                                }else{
//                                    errors.add("Control response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }



//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Customers downloaded\nDownloading Near Customers...");
//                        }
//                    });
//                    /*****************end Customers**********************************************************************/
//                    // ----------------Near Customer-------------------- Nuwan ------------- 17/10/2019--------------------------
//                    // Processing outlets
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getNearDebtorResult(pref.getDistDB());
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    ArrayList<NearDebtor> nDebList = new ArrayList<NearDebtor>();
//                                    for (int i = 0; i < response.body().getNearDebtorResult().size(); i++) {
//                                        nDebList.add(response.body().getNearDebtorResult().get(i));
//                                    }
//                                    NearCustomerController nCustomerController = new NearCustomerController(getActivity());
//                                    nCustomerController.InsertOrReplaceNearDebtor(nDebList);
//                                }else{
//                                    errors.add("NearDebtor response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Near Customers downloaded\nDownloading Company Settings...");
//                        }
//                    });
                    // --------------------------------------------------------------------------------------------------
                    /*****************Settings*****************************************************************************/
                    // Processing company settings
                    final ReferenceSettingController settingController = new ReferenceSettingController(getActivity());
                    settingController.deleteAll();
                    try{
                        Call<ReadJsonList> resultCall = apiInterface.getCompanySettingResult(pref.getDistDB());
                        UtilityContainer.download(getActivity(),resultCall, TaskType.Customers);

                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getCompanySettingResult(pref.getDistDB());
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//
//                                if(response.body() != null) {
//                                    final ArrayList<CompanySetting> settingList = new ArrayList<CompanySetting>();
//                                    for (int i = 0; i < response.body().getCompanySettingResult().size(); i++) {
//                                        settingList.add(response.body().getCompanySettingResult().get(i));
//                                    }
//                                    final ReferenceSettingController settingController = new ReferenceSettingController(getActivity());
//                                    mHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//
//                                            pdialog.setMessage("Processing downloaded data (Settings)...");
//                                            settingController.createOrUpdateFCompanySetting(settingList);
//                                            pdialog.setMessage("Processed (Settings)...");
//
//                                        }
//                                    });
//
//                                }else {
//                                    errors.add("CompanySetting response is null");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
                    /*****************end Settings**********************************************************************/
/*****************Branches*****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (setting details)...");
                        }
                    });
                    // Processing Branches
                    ReferenceDetailDownloader branchController = new ReferenceDetailDownloader(getActivity());
                    branchController.deleteAll();
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getCompanyBranchResult(pref.getDistDB(),repcode);
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<CompanyBranch> settingList = new ArrayList<CompanyBranch>();
                                    for (int i = 0; i < response.body().getCompanyBranchResult().size(); i++) {
                                        settingList.add(response.body().getCompanyBranchResult().get(i));
                                    }
                                    final ReferenceDetailDownloader settingController = new ReferenceDetailDownloader(getActivity());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (references)...");
                                            settingController.createOrUpdateFCompanyBranch(settingList);
                                            pdialog.setMessage("Processed (references)...");

                                        }
                                    });

                                }else{
                                    errors.add("CompanyBranch response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {

                        throw e;
                    }
                    /*****************end Branches**********************************************************************/
                    /*****************ItemBundle*****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (ItemBundle details)...");
                        }
                    });
                    // Processing Branches
                    final ItemBundleController bundleController = new ItemBundleController(getActivity());
                    bundleController.deleteAll();
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getItemBundle(pref.getDistDB(),repcode);
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<ItemBundle> bundleList = new ArrayList<ItemBundle>();
                                    for (int i = 0; i < response.body().getItemBundleResult().size(); i++) {
                                        bundleList.add(response.body().getItemBundleResult().get(i));
                                    }
                                    final ItemBundleController bundleController = new ItemBundleController(getActivity());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Item bundle)...");
                                            bundleController.InsertOrReplaceItemBundle(bundleList);
                                            pdialog.setMessage("Processed (Item bundle)...");

                                        }
                                    });

                                }else{
                                    Log.d(">>ItmBundleRes", ">> is null");

                                    errors.add("ItemBundle response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {

                        throw e;
                    }
                    /*****************end ItemBundle**********************************************************************/

/*****************VAT*****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (VAT details)...");
                        }
                    });
                    // Processing Branches
                    final VATController vatController = new VATController(getActivity());
                    vatController.deleteAll();
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getVATResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<VatMaster> vatList = new ArrayList<VatMaster>();
                                    for (int i = 0; i < response.body().getVatMasterList().size(); i++) {
                                        vatList.add(response.body().getVatMasterList().get(i));
                                    }
                                    final VATController vatController = new VATController(getActivity());

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (VAT)...");
                                            vatController.InsertOrReplaceVAT(vatList);
                                            pdialog.setMessage("Processed (VAT)...");

                                        }
                                    });
                                }else{
                                    Log.d(">>vat Res", ">> is null");

                                    errors.add("vat response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {

                        throw e;
                    }
                    /*****************end VAT**********************************************************************/



                    /*****************Item Loc*****************************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (item location details)...");
//                        }
//                    });

                    // Processing itemLocations
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getItemLocResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    ArrayList<ItemLoc> itemLocList = new ArrayList<ItemLoc>();
//                                    for (int i = 0; i < response.body().getItemLocResult().size(); i++) {
//                                        itemLocList.add(response.body().getItemLocResult().get(i));
//                                    }
//                                    ItemLocController locController = new ItemLocController(getActivity());
//                                    locController.InsertOrReplaceItemLoc(itemLocList);
//                                }else{
//                                    errors.add("ItemLocation response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//
//                        throw e;
//                    }
//                    /*****************end Item Loc**********************************************************************/
//                    /*****************Locations*****************************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (location details)...");
//                        }
//                    });
//                    LocationsController locController = new LocationsController(getActivity());
//                    locController.deleteAll();
//                    // Processing itemLocations
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getLocationsResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    ArrayList<Locations> locList = new ArrayList<Locations>();
//                                    for (int i = 0; i < response.body().getLocationsResult().size(); i++) {
//                                        locList.add(response.body().getLocationsResult().get(i));
//                                    }
//                                    LocationsController locController = new LocationsController(getActivity());
//                                    locController.createOrUpdateFLocations(locList);
//                                }else{
//                                    errors.add("Locations response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//
//                        throw e;
//                    }
                    /*****************itemPrices*****************************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (item price details)...");
//                        }
//                    });
//                    ItemPriceController priceController = new ItemPriceController(getActivity());
//                    priceController.deleteAllItemPri();
//                    // Processing itemPrices
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getItemPriResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//
//                                if(response.body() != null) {
//                                    ArrayList<ItemPri> itemPriceList = new ArrayList<ItemPri>();
//                                    for (int i = 0; i < response.body().getItemPriResult().size(); i++) {
//                                        itemPriceList.add(response.body().getItemPriResult().get(i));
//                                    }
//                                    ItemPriceController priceController = new ItemPriceController(getActivity());
//                                    priceController.InsertOrReplaceItemPri(itemPriceList);
//                                }else{
//                                    errors.add("ItemPrice response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
                    /*****************end item prices**********************************************************************/
                    /*****************Items*****************************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (item details)...");
                        }
                    });
                    final ItemController itemController = new ItemController(getActivity());
                    itemController.deleteAll();
                    // Processing Items
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getItemsResult(pref.getDistDB(),repcode);
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<Item> itemList = new ArrayList<Item>();
                                    for (int i = 0; i < response.body().getItemsResult().size(); i++) {
                                        itemList.add(response.body().getItemsResult().get(i));
                                    }
                                    final ItemController itemController = new ItemController(getActivity());

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Items)...");
                                            itemController.InsertOrReplaceItems(itemList);
                                            pdialog.setMessage("Processed (Items)...");

                                        }
                                    });
                                }else{
                                    errors.add("Item response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
                    /*****************end Items **********************************************************************/
                    //                    /*****************reasons**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Items downloaded\nDownloading reasons...");
                        }
                    });
                    // Processing reasons
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getReasonResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<Reason> reasonList = new ArrayList<Reason>();
                                    for (int i = 0; i < response.body().getReasonResult().size(); i++) {
                                        reasonList.add(response.body().getReasonResult().get(i));
                                    }
                                    final ReasonController reasonController = new ReasonController(getActivity());

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Reasons)...");
                                            reasonController.createOrUpdateReason(reasonList);
                                            pdialog.setMessage("Processed (Reasons)...");

                                        }
                                    });
                                }else{
                                    errors.add("Reason response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
                    /*****************end reasons**********************************************************************/
                    /*****************fddbnote*****************************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Reason downloaded\nDownloading outstanding details...");
//                        }
//                    });
//                    // Processing fddbnote
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getOutstandingResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    OutstandingController outstandingController = new OutstandingController(getActivity());
//                                    outstandingController.deleteAll();
//                                    ArrayList<FddbNote> fddbnoteList = new ArrayList<FddbNote>();
//                                    for (int i = 0; i < response.body().getOutstandingResult().size(); i++) {
//                                        fddbnoteList.add(response.body().getOutstandingResult().get(i));
//                                    }
//                                    outstandingController.createOrUpdateFDDbNote(fddbnoteList);
//                                }else{
//                                    errors.add("Outstanding response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("outstanding downloaded\nDownloading banks...");
                        }
                    });
//                    /*****************expenses**********************************************************************/

                    // Processing route
                    try {
                       // ApiInterface apiInterface = ApiCllient.getClient(getActivity()).create(ApiInterface.class);
                        Call<ReadJsonList> resultCall = apiInterface.getBankResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final BankController bankController = new BankController(getActivity());
                                    final ArrayList<Bank> bankList = new ArrayList<Bank>();
                                    for (int i = 0; i < response.body().getBankResult().size(); i++) {
                                        bankList.add(response.body().getBankResult().get(i));
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Banks)...");
                                            bankController.createOrUpdateBank(bankList);
                                            pdialog.setMessage("Processed (Banks)...");

                                        }
                                    });

                                }else{
                                    errors.add("Bank response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }
                    /*****************end banks**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (expenses)...");
                        }
                    });
                    // Processing expense
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getExpenseResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<Expense> expensesList = new ArrayList<Expense>();
                                    for (int i = 0; i < response.body().getExpenseResult().size(); i++) {
                                        expensesList.add(response.body().getExpenseResult().get(i));
                                    }
                                    final ExpenseController expenseController = new ExpenseController(getActivity());

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Expenses)...");
                                            expenseController.createOrUpdateFExpense(expensesList);
                                            pdialog.setMessage("Processed (Expenses)...");

                                        }
                                    });
                                }else{
                                    errors.add("Expense response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
                    /*****************end expenses**********************************************************************/
                    /*****************Route**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Expenses downloaded\nDownloading route details...");
                        }
                    });
                    // Processing route
                    final RouteController routeController = new RouteController(getActivity());
                    routeController.deleteAll();
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getRouteResult(pref.getDistDB(),repcode);
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<Route> routeList = new ArrayList<Route>();
                                    for (int i = 0; i < response.body().getRouteResult().size(); i++) {
                                        routeList.add(response.body().getRouteResult().get(i));
                                    }
                                    final RouteController routeController = new RouteController(getActivity());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Routes)...");
                                            routeController.createOrUpdateFRoute(routeList);
                                            pdialog.setMessage("Processed (Routes)...");

                                        }
                                    });

                                }else{
                                    errors.add("Route response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }
                    /*****************end route**********************************************************************/
                    /*****************ItemBundle**********************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Route downloaded\nDownloading ItemBundle details...");
//                        }
//                    });
//                    // Processing route
//                    ItemBundleController itmbndlController = new ItemBundleController(getActivity());
//                    itmbndlController.deleteAll();
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getItemBundle(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    ArrayList<ItemBundle> itemList = new ArrayList<ItemBundle>();
//                                    for (int i = 0; i < response.body().getItemBundleResult().size(); i++) {
//                                        itemList.add(response.body().getItemBundleResult().get(i));
//                                    }
//                                    ItemBundleController itmbndlController = new ItemBundleController(getActivity());
//                                    itmbndlController.createOrUpdateItemBundle(itemList);
//                                }else{
//                                    errors.add("ItemBundle response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//
//                        throw e;
//                    }
                    /*****************end ItemBundle**********************************************************************/
                    /*****************last 3 invoice heds**********************************************************************/

//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (last invoices)...");
//                        }
//                    });
//                    // Processing lastinvoiceheds
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getLastThreeInvHedResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                FInvhedL3Controller invoiceHedController = new FInvhedL3Controller(getActivity());
//                                invoiceHedController.deleteAll();
//                                ArrayList<FInvhedL3> invoiceHedList = new ArrayList<FInvhedL3>();
//                                if(response.body() != null) {
//
//                                    for (int i = 0; i < response.body().getLastThreeInvHedResult().size(); i++) {
//                                        invoiceHedList.add(response.body().getLastThreeInvHedResult().get(i));
//                                    }
//                                    invoiceHedController.createOrUpdateFinvHedL3(invoiceHedList);
//                                }else{
//                                    errors.add("LastThreeInvHed response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
                    /*****************end lastinvoiceheds**********************************************************************/
                    /*****************last 3 invoice dets**********************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (invoices)...");
//                        }
//                    });
//                    // Processing lastinvoiceheds
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getLastThreeInvDetResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                FinvDetL3Controller invoiceDetController = new FinvDetL3Controller(getActivity());
//                                invoiceDetController.deleteAll();
//                                ArrayList<FinvDetL3> invoiceDetList = new ArrayList<FinvDetL3>();
//                                if(response.body() != null) {
//                                    for (int i = 0; i < response.body().getLastThreeInvDetResult().size(); i++) {
//                                        invoiceDetList.add(response.body().getLastThreeInvDetResult().get(i));
//                                    }
//                                    invoiceDetController.createOrUpdateFinvDetL3(invoiceDetList);
//                                }else{
//                                    errors.add("LastThreeInvDet response is null");
//                                }
//                            }
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//
//                        throw e;
//                    }
                    /*****************end lastinvoicedets**********************************************************************/
                    /*****************Route det**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Last invoices downloaded\nDownloading route details...");
                        }
                    });
                    RouteDetController routeDetController = new RouteDetController(getActivity());
                    routeDetController.deleteAll();
                    // Processing route
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getRouteDetResult(pref.getDistDB(),repcode);
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<RouteDet> routeList = new ArrayList<RouteDet>();
                                    for (int i = 0; i < response.body().getRouteDetResult().size(); i++) {
                                        routeList.add(response.body().getRouteDetResult().get(i));
                                    }
                                    final RouteDetController routeController = new RouteDetController(getActivity());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Routes)...");
                                            routeController.InsertOrReplaceRouteDet(routeList);
                                            pdialog.setMessage("Processed (Routes)...");

                                        }
                                    });

                                }else{
                                    errors.add("RouteDetail response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }
                    /*****************end route det**********************************************************************/
                    /*****************towns**********************************************************************/
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Expenses downloaded\nDownloading town details...");
//                        }
//                    });
//                    TownController townController = new TownController(getActivity());
//                    townController.deleteAll();
//                    // Processing towns
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getTownResult(pref.getDistDB());
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//                                if(response.body() != null) {
//                                    ArrayList<Town> townList = new ArrayList<Town>();
//                                    for (int i = 0; i < response.body().getTownResult().size(); i++) {
//                                        townList.add(response.body().getTownResult().get(i));
//                                    }
//                                    TownController townController = new TownController(getActivity());
//                                    townController.createOrUpdateFTown(townList);
//                                }else{
//                                    errors.add("Town response is null");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
                    /*****************end towns**********************************************************************/
                    /*****************Freeslab**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (free)...");
                        }
                    });
                    // Processing freeslab
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getFreeSlabResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final FreeSlabController freeslabController = new FreeSlabController(getActivity());
                                    freeslabController.deleteAll();
                                    final ArrayList<FreeSlab> freeslabList = new ArrayList<FreeSlab>();
                                    for (int i = 0; i < response.body().getFreeSlabResult().size(); i++) {
                                        freeslabList.add(response.body().getFreeSlabResult().get(i));
                                    }

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Freeslab)...");
                                            freeslabController.createOrUpdateFreeSlab(freeslabList);
                                            pdialog.setMessage("Processed (Freeslab)...");

                                        }
                                    });

                                }else{
                                    errors.add("FreeSlab response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
                    /*****************end freeSlab**********************************************************************/
                    /*****************freeMslab**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (free)...");
                        }
                    });
                    // Processing freeMslab
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getFreeMSlabResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final FreeMslabController freeMslabController = new FreeMslabController(getActivity());
                                    freeMslabController.deleteAll();
                                    final ArrayList<FreeMslab> freeMslabList = new ArrayList<FreeMslab>();
                                    for (int i = 0; i < response.body().getFreeMslabResult().size(); i++) {
                                        freeMslabList.add(response.body().getFreeMslabResult().get(i));
                                    }

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Freemslab)...");
                                            freeMslabController.createOrUpdateFreeMslab(freeMslabList);
                                            pdialog.setMessage("Processed (Freemslab)...");

                                        }
                                    });
                                }else{
                                    errors.add("FreeMslab response is null");
                                }
                            }

                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }
                    /*****************end freeMSlab**********************************************************************/
                    /*****************FreeHed**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (free)...");
                        }
                    });
                    // Processing freehed
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getFreehedResult(pref.getDistDB(),repcode);
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final FreeHedController freeHedController = new FreeHedController(getActivity());
                                    freeHedController.deleteAll();
                                    final ArrayList<FreeHed> freeHedList = new ArrayList<FreeHed>();
                                    for (int i = 0; i < response.body().getFreeHedResult().size(); i++) {
                                        freeHedList.add(response.body().getFreeHedResult().get(i));
                                    }

                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Freehed)...");
                                            freeHedController.createOrUpdateFreeHed(freeHedList);
                                            pdialog.setMessage("Processed (Freehed)...");

                                        }
                                    });
                                }else{
                                    errors.add("FreeHed response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }
                    /*****************end freeHed**********************************************************************/
                    /*****************Freedet**********************************************************************/

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (free)...");
                        }
                    });

                    // Processing freedet
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getFreeDetResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {

                                if(response.body() != null) {
                                    final FreeDetController freedetController = new FreeDetController(getActivity());
                                    freedetController.deleteAll();
                                    final ArrayList<FreeDet> freedetList = new ArrayList<FreeDet>();
                                    for (int i = 0; i < response.body().getFreeDetResult().size(); i++) {
                                        freedetList.add(response.body().getFreeDetResult().get(i));
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (Freedet)...");
                                            freedetController.createOrUpdateFreeDet(freedetList);
                                            pdialog.setMessage("Processed (Freedet)...");

                                        }
                                    });

                                }else{
                                    errors.add("FreeDetail response is null");
                                }
                            }

                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }
                    /*****************end freedet**********************************************************************/
                    /*****************freedeb**********************************************************************/

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (free)...");
                        }
                    });

                    // Processing freedeb
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getFreedebResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {

                                if(response.body() != null) {
                                    final FreeDebController freedebController = new FreeDebController(getActivity());
                                    freedebController.deleteAll();
                                    final ArrayList<FreeDeb> freedebList = new ArrayList<FreeDeb>();
                                    for (int i = 0; i < response.body().getFreeDebResult().size(); i++) {
                                        freedebList.add(response.body().getFreeDebResult().get(i));
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (freedeb)...");
                                            freedebController.createOrUpdateFreeDeb(freedebList);
                                            pdialog.setMessage("Processed (freedeb)...");

                                        }
                                    });

                                }else{
                                    errors.add("FreeDeb response is null");
                                }
                            }

                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
                    /*****************end freedeb**********************************************************************/
                    /*****************freeItem**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Processing downloaded data (free)...");
                        }
                    });


                    // Processing freeItem
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getFreeitemResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final FreeItemController freeitemController = new FreeItemController(getActivity());
                                    freeitemController.deleteAll();
                                    final ArrayList<FreeItem> freeitemList = new ArrayList<FreeItem>();
                                    for (int i = 0; i < response.body().getFreeItemResult().size(); i++) {
                                        freeitemList.add(response.body().getFreeItemResult().get(i));
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (freeitem)...");
                                            freeitemController.createOrUpdateFreeItem(freeitemList);
                                            pdialog.setMessage("Processed (freeitem)...");

                                        }
                                    });

                                }else{
                                    errors.add("FreeItem response is null");
                                }
                            }

                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }
                    /*****************end freeItem**********************************************************************/
                    /*****************discdeb**********************************************************************/


//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (discount)...");
//                        }
//                    });
//
//                    // Processing discdeb
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getDiscDebResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//
//
//                                if(response.body() != null) {
//                                    DiscdebController discdebController = new DiscdebController(getActivity());
//                                    discdebController.deleteAll();
//                                    ArrayList<Discdeb> discdebList = new ArrayList<Discdeb>();
//                                    for (int i = 0; i < response.body().getDiscDebResult().size(); i++) {
//                                        discdebList.add(response.body().getDiscDebResult().get(i));
//                                    }
//
//                                    discdebController.createOrUpdateDiscdeb(discdebList);
//                                }else{
//                                    errors.add("Discdeb response is null");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//
//                        throw e;
//                    }
                    /*****************end discdeb**********************************************************************/
                    /*****************discdet**********************************************************************/

//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (discount)...");
//                        }
//                    });
//
//                    // Processing discdet
//
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getDiscDetResult(pref.getDistDB());
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//
//                                if(response.body() != null) {
//                                    DiscdetController discdetController = new DiscdetController(getActivity());
//                                    discdetController.deleteAll();
//                                    ArrayList<Discdet> discdetList = new ArrayList<Discdet>();
//                                    for (int i = 0; i < response.body().getDiscDetResult().size(); i++) {
//                                        discdetList.add(response.body().getDiscDetResult().get(i));
//                                    }
//
//                                    discdetController.createOrUpdateDiscdet(discdetList);
//                                }else{
//                                    errors.add("Discdet response is null");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//
//                        throw e;
//                    }
                    /*****************end discdet**********************************************************************/
                    /*****************discshed**********************************************************************/


//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (discount)...");
//                        }
//                    });
//
//                    // Processing disched
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getDiscHedResult(pref.getDistDB(),repcode);
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//
//                                if(response.body() != null) {
//                                    DischedController dischedController = new DischedController(getActivity());
//                                    dischedController.deleteAll();
//                                    ArrayList<Disched> dischedList = new ArrayList<Disched>();
//                                    for (int i = 0; i < response.body().getDiscHedResult().size(); i++) {
//                                        dischedList.add(response.body().getDiscHedResult().get(i));
//                                    }
//                                    dischedController.createOrUpdateDisched(dischedList);
//                                }else{
//                                    errors.add("Disched response is null");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//
//                        throw e;
//                    }
                    /*****************end disched**********************************************************************/
                    /*****************discslab**********************************************************************/


//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            pdialog.setMessage("Processing downloaded data (discount)...");
//                        }
//                    });
//
//                    // Processing discslab
//                    try {
//                        Call<ReadJsonList> resultCall = apiInterface.getDiscSlabResult(pref.getDistDB());
//                        resultCall.enqueue(new Callback<ReadJsonList>() {
//                            @Override
//                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
//
//                                if(response.body() != null) {
//                                    DiscslabController discslabController = new DiscslabController(getActivity());
//                                    discslabController.deleteAll();
//                                    ArrayList<Discslab> discslabList = new ArrayList<Discslab>();
//                                    for (int i = 0; i < response.body().getDiscSlabResult().size(); i++) {
//                                        discslabList.add(response.body().getDiscSlabResult().get(i));
//                                    }
//                                    discslabController.createOrUpdateDiscslab(discslabList);
//                                }else{
//                                    errors.add("Disched response is null");
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
//                                errors.add(t.toString());
//                            }
//                        });
//                    } catch (Exception e) {
//                        errors.add(e.toString());
//                        throw e;
//                    }
                    /*****************end discslab**********************************************************************/
             getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Prices downloaded\nDownloading iteanery details...");
                        }
                    });
//**************************************************itenaryhed**********************************************************
                    final FItenrHedController itenaryHedController = new FItenrHedController(getActivity());
                    itenaryHedController.deleteAll();
                    // Processing itenaryhed
                    try {
                        Calendar c = Calendar.getInstance();
                        int cyear = c.get(Calendar.YEAR);
                        int cmonth = c.get(Calendar.MONTH) + 1;
                        DecimalFormat df_month = new DecimalFormat("00");
                        Call<ReadJsonList> resultCall = apiInterface.getItenrHedResult(pref.getDistDB(),repcode,""+cyear,""+df_month.format((double) cmonth));
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {

                                if(response.body() != null) {
                                    final FItenrHedController itenaryHedController = new FItenrHedController(getActivity());
                                    final ArrayList<FItenrHed> itenaryHedList = new ArrayList<FItenrHed>();
                                    for (int i = 0; i < response.body().getItenrHedResult().size(); i++) {
                                        itenaryHedList.add(response.body().getItenrHedResult().get(i));
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (itenaryhed)...");
                                            itenaryHedController.createOrUpdateFItenrHed(itenaryHedList);
                                            pdialog.setMessage("Processed (itenaryhed)...");

                                        }
                                    });

                                }else{
                                    errors.add("ItenrHed response is null");
                                }
                            }

                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }

                    /*****************itenary det**********************************************************************/

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Prices downloaded\nDownloading iteanery details...");
                        }
                    });
                    // Processing itenarydet
                    try {
                        Calendar c = Calendar.getInstance();
                        int cyear = c.get(Calendar.YEAR);
                        int cmonth = c.get(Calendar.MONTH) + 1;
                        DecimalFormat df_month = new DecimalFormat("00");
                        Call<ReadJsonList> resultCall = apiInterface.getItenrDetResult(pref.getDistDB(),repcode,""+cyear,""+df_month.format((double) cmonth));

                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {


                                if(response.body() != null) {
                                    final FItenrDetController itenaryDetController = new FItenrDetController(getActivity());
                                    itenaryDetController.deleteAll();
                                    final ArrayList<FItenrDet> itenaryDetList = new ArrayList<FItenrDet>();
                                    for (int i = 0; i < response.body().getItenrDetResult().size(); i++) {
                                        itenaryDetList.add(response.body().getItenrDetResult().get(i));
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (itenarydet)...");
                                            itenaryDetController.createOrUpdateFItenrDet(itenaryDetList);
                                            pdialog.setMessage("Processed (itenarydet)...");

                                        }
                                    });

                                }else{
                                    errors.add("ItenrDet response is null");
                                }
                            }

                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());

                        throw e;
                    }

                    /*****************SalesPrice**********************************************************************/
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Downloading SalesPeice....");
                        }
                    });
                    // Processing SalesPrice
                    try {
                        Call<ReadJsonList> resultCall = apiInterface.getSalesPriceResult(pref.getDistDB());
                        resultCall.enqueue(new Callback<ReadJsonList>() {
                            @Override
                            public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                                if(response.body() != null) {
                                    final ArrayList<SalesPrice> salesPri_list = new ArrayList<SalesPrice>();
                                    for (int i = 0; i < response.body().getSalesPriceResult().size(); i++) {
                                        salesPri_list.add(response.body().getSalesPriceResult().get(i));
                                    }
                                    final SalesPriceController salepriController = new SalesPriceController(getActivity());
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            pdialog.setMessage("Processing downloaded data (salesprice)...");
                                            salepriController.InsertOrReplaceSalesPrice(salesPri_list);
                                            pdialog.setMessage("Processed (salesprice)...");

                                        }
                                    });

                                }else{
                                    errors.add("SalesPrice response is null");
                                }
                            }
                            @Override
                            public void onFailure(Call<ReadJsonList> call, Throwable t) {
                                errors.add(t.toString());
                            }
                        });
                    } catch (Exception e) {
                        errors.add(e.toString());
                        throw e;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pdialog.setMessage("Completed...");
                        }
                    });

                    /*****************end iteanerydet**********************************************************************/
                    return true;
                } else {
                    errors.add("SharedPref.getInstance(getActivity()).getLoginUser() = null OR !SharedPref.getInstance(getActivity()).isLoggedIn()");
                    Log.d("ERROR>>>>>", "Login USer" + SharedPref.getInstance(getActivity()).getLoginUser().toString() + " IS LoggedIn --> " + SharedPref.getInstance(getActivity()).isLoggedIn());
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                errors.add(e.toString());

                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean result) {
            super.onPostExecute(result);

            pdialog.setMessage("Finalizing data");
            pdialog.setMessage("Download Completed..");
            if (result) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
                showErrorText("Successfully Synchronized");
            } else {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
                StringBuilder sb = new StringBuilder();
                if (errors.size() == 1) {
                    sb.append(errors.get(0));
                    showErrorText(sb.toString());
                } else if(errors.size() == 0) {
                    sb.append("Following errors occurred");
                    for (String error : errors) {
                        sb.append("\n - ").append(error);
                        showErrorText(sb.toString());
                    }
                }
                //showErrorText(sb.toString());
            }
            if (fmc.getAllMediaforCheckIfIsExist("IMG") > 0) {
                imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_img_notification));
            } else {
                imgImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_image));
            }

            if (fmc.getAllMediaforCheckIfIsExist("VDO") > 0) {
                imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video_notification));
            } else {
                imgVideo.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_video));
            }
        }
    }

    private void showErrorText(String s) {
        Toast.makeText(getActivity(), "" + s, Toast.LENGTH_LONG).show();

    }

    /////////////***********************secondory sync finish***********************************/
    /*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void mUploadResult(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Upload Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    private class Validate extends AsyncTask<String, Integer, Boolean> {
        int totalRecords = 0;
        CustomProgressDialog pdialog;
        private String macId, url, db;

        public Validate(String macId, String url, String db) {
            this.macId = macId;
            this.url = url;
            this.db = db;
            this.pdialog = new CustomProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            pdialog.setMessage("Validating...");
            pdialog.show();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {

            try {

                try {
                    ApiInterface apiInterface = ApiCllient.getClient(getActivity()).create(ApiInterface.class);
                    Call<ReadJsonList> resultCall = apiInterface.getSalRepResult(pref.getDistDB(),macId);
                    resultCall.enqueue(new Callback<ReadJsonList>() {
                        @Override
                        public void onResponse(Call<ReadJsonList> call, Response<ReadJsonList> response) {
                            ArrayList<SalRep> repList = new ArrayList<SalRep>();
                            for (int i = 0; i < response.body().getSalRepResult().size(); i++) {
                                repList.add(response.body().getSalRepResult().get(i));
                            }
                            new SalRepController(getActivity()).createOrUpdateSalRep(repList);

                            if(repList.size()>0){
                                networkFunctions.setUser(repList.get(0));
                                pref.storeLoginUser(repList.get(0));
                            }

                        }

                        @Override
                        public void onFailure(Call<ReadJsonList> call, Throwable t) {
                            Log.d(">>>Error in failure",t.toString());
                        }
                    });

                        pref = SharedPref.getInstance(getActivity());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pdialog.setMessage("Authenticated...");
                            }
                        });

                        return true;

                } catch (Exception e) {
                    Log.e("networkFunctions ->", "IOException -> " + e.toString());
                    throw e;
                }



            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (pdialog.isShowing())
                pdialog.cancel();
            // pdialog.cancel();
            if (result) {
                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                syncMasterDataDialog(getActivity());
            } else {
                Toast.makeText(getActivity(), "Invalid Mac Id", Toast.LENGTH_LONG).show();
            }
        }
    }
}