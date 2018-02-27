package com.youli.companybigwenjuan.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youli.companybigwenjuan.R;
import com.youli.companybigwenjuan.entity.AdminInfo;
import com.youli.companybigwenjuan.entity.NaireInfo;
import com.youli.companybigwenjuan.util.MyOkHttpUtils;
import com.youli.companybigwenjuan.util.SharedPreferencesUtils;
import com.youli.companybigwenjuan.util.TextViewUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Response;

/**
 * Created by liutao on 2018/1/30.
 *
 * 调查类型
 */

public class InvestTypeActivity extends BaseActivity implements View.OnClickListener{

    private final int SUCCESS=10000;
    private final int NODATA=10001;
    private final int SUCCESS_ADMIN=10002;
    private final int NODATA_ADMIN=10003;
    private final int PROBLEM=10004;
    private final int OVERTIME=10005;//登录超时
    private  ProgressDialog progressDialog;

    private List<NaireInfo> naireInfo=new ArrayList<>();//问卷信息

    private AdminInfo adminInfo;//登录账号的信息

    private Context mContext=this;

    private LocationManager locationManager;
    private String provider;
    private ImageView ivNew,ivOld;//新调查，已调查
    private ImageView ivComInfo;
    private Handler gpsHandler;//用来检测gps是否打开

    private int getGPSTime=5000;//获取GPS经纬度的时间间隔，默认是500ms

    public  static String jDuStr,wDuStr;//要上传的经度和纬度


    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {

            dismissMyProgressDialog(mContext);

            switch (msg.what){

                case SUCCESS_ADMIN://登录账号信息

                    adminInfo=(AdminInfo) msg.obj;

                    ivComInfo.setEnabled(true);
                Log.e("2018-2-1","名字=="+adminInfo.getNAME());

                    getNetWorkData();//获取问卷信息

                    break;

                case NODATA_ADMIN:

                    break;

                case SUCCESS:

                    naireInfo.clear();
                    naireInfo.addAll((List<NaireInfo>)msg.obj);

                    ivNew.setEnabled(true);
                    ivOld.setEnabled(true);
                    break;

                case PROBLEM:

                    Toast.makeText(mContext, "网络不给力", Toast.LENGTH_SHORT).show();

                    break;
                case NODATA:


                    break;
                case OVERTIME:

                    Intent i=new Intent(mContext,OvertimeDialogActivity.class);
                    startActivity(i);

                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_type);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        gpsHandler=new Handler();
        gpsHandler.post(r);
        gpsHandler.post(rGps);

        initViews();
    }

    private void initViews(){


        ivNew=findViewById(R.id.iv_wdc_type);
        ivNew.setOnClickListener(this);

        ivOld=findViewById(R.id.iv_ydc_type);
        ivOld.setOnClickListener(this);


        ivComInfo=findViewById(R.id.iv_com_info);
        ivComInfo.setOnClickListener(this);
        getAdminInfo();//获取登录账户的信息





    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()){

            case R.id.iv_wdc_type://新调查
                i=new Intent(mContext,NaireDetailActivity.class);
                 i.putExtra("type","1");//1代表新调查
                 i.putExtra("wenjuan",(Serializable)naireInfo);
                 startActivity(i);

                break;
            case R.id.iv_ydc_type://已调查

                i=new Intent(mContext,YdcListActivity.class);
                i.putExtra("wenjuan",(Serializable)naireInfo);
                startActivity(i);

                break;

            case R.id.iv_com_info:

                i=new Intent(mContext,AdminInfoActivity.class);
                i.putExtra("ADMININFO",adminInfo);
                startActivity(i);

                break;
        }

    }

    private void getAdminInfo(){

        showMyProgressDialog(mContext);

        //http://183.194.4.58:89/Json/Get_Staff.aspx
        new Thread(


                new Runnable() {
                    @Override
                    public void run() {

                        String meetUrl= MyOkHttpUtils.BaseUrl+"/Json/Get_Staff.aspx";

                        Response response=MyOkHttpUtils.okHttpGet(meetUrl);

                        Message msg=Message.obtain();

                        if(response!=null){

                            try {
                                String meetStr=response.body().string();

                                if(!TextUtils.equals(meetStr,"")&&!TextUtils.equals(meetStr,"[]")){

                                    Gson gson=new Gson();
                                    msg.obj=gson.fromJson(meetStr,AdminInfo.class);
                                    msg.what=SUCCESS_ADMIN;
                                }else{
                                    msg.what=NODATA_ADMIN; //没有数据
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                msg.what=OVERTIME;

                            }

                        }else{

                            msg.what=PROBLEM;

                        }

                        mHandler.sendMessage(msg);

                    }
                }


        ).start();

    }

    private void getNetWorkData(){

        showMyProgressDialog(mContext);

        //http://183.194.4.58:82/Json/Get_Qa_Detil_Special.aspx
        new Thread(


                new Runnable() {
                    @Override
                    public void run() {

                        String meetUrl= MyOkHttpUtils.BaseUrl+"/Json/Get_Qa_Detil_Special.aspx";

                        Response response=MyOkHttpUtils.okHttpGet(meetUrl);

                        Message msg=Message.obtain();

                        if(response!=null){

                            try {
                                String meetStr=response.body().string();

                                if(!TextUtils.equals(meetStr,"")&&!TextUtils.equals(meetStr,"[]")){

                                    Gson gson=new Gson();
                                    msg.obj=gson.fromJson(meetStr,new TypeToken<List<NaireInfo>>(){}.getType());
                                    msg.what=SUCCESS;
                                }else{
                                     msg.what=NODATA; //没有数据
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                msg.what=OVERTIME;

                            }

                        }else{

                            msg.what=PROBLEM;

                        }

                        mHandler.sendMessage(msg);

                    }
                }


        ).start();


    }

    private void showMyProgressDialog(Context context){

        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("正在加载中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissMyProgressDialog(Context context){

        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }

    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定退出吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //移除Handler
                gpsHandler.removeCallbacks(r);
                gpsHandler.removeCallbacks(rGps);
                ActivityController.finishAll();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    Runnable rGps=new Runnable() {
        @Override
        public void run() {


            gpsHandler.postDelayed(this,getGPSTime);//刷新gps

            getAddress();

        }
    };

    private void getAddress(){

        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
//            Toast.makeText(MainActivity.this, "no Location provider to use",
//                    Toast.LENGTH_SHORT).show();
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            getGPSTime=1000*60*5;//5分钟
            //显示位置
            showLocations(location);

        }else{

            getGPSTime=5000;//500ms

            String spJdu= SharedPreferencesUtils.getString("jDu");//sp取经度
            String spWdu=SharedPreferencesUtils.getString("wDu");//sp取纬度


            Log.e("2018-1-3111","经度="+spJdu);
            Log.e("2018-1-3111","纬度="+spWdu);

            if(!TextUtils.equals(spJdu,"")&&!TextUtils.equals(spWdu,"")) {

//                tvJdu.setText("经度:" + (new Double(Double.parseDouble(spJdu))).intValue());//String先转Double，再转int
//                tvWdu.setText("纬度:" + (new Double(Double.parseDouble(spWdu))).intValue());//String先转Double，再转int
//                tvJdu.setText("经度:" + spJdu);//String先转Double，再转int
//                tvWdu.setText("纬度:" + spWdu);//String先转Double，再转int
            }
            jDuStr=spJdu;//要上传的经度
            wDuStr=spWdu;//要上传的纬度
            Log.e("2018-01-3111","经度="+jDuStr);
            Log.e("2018-01-3111","纬度="+wDuStr);
        }
        locationManager.requestLocationUpdates(provider, getGPSTime, 0, locationListener);
        //绑定监听状态
        locationManager.addGpsStatusListener(listener);//可以获取卫星的数量
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //更新当前位置
            showLocations(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void showLocations(Location location) {

//        tvJdu.setText("经度:"+location.getLongitude());
//        tvWdu.setText("纬度:"+location.getLatitude());
//
//        tvGdu.setText("高度:"+location.getAltitude()+"米");


        jDuStr=location.getLongitude()+"";//要上传的经度
        wDuStr=location.getLatitude()+"";//要上传的纬度
        Log.e("2018-01-31","经度="+jDuStr);
        Log.e("2018-01-31","纬度="+wDuStr);
        Log.e("2018-1-3","经度="+location.getLongitude());
        Log.e("2018-1-3","纬度="+location.getLatitude());

        SharedPreferencesUtils.putString("jDu",String.valueOf(location.getLongitude()));//sp存经度
        SharedPreferencesUtils.putString("wDu",String.valueOf(location.getLatitude()));//sp存纬度
    }

    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    //  Log.i("2017-12-17", "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    //  Log.i("2017-12-17", "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus=locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    Log.e("2018-1-3", "搜索到："+count+"颗卫星");
                    SharedPreferencesUtils.putString("wx",String.valueOf(count));//卫星
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    // Log.i("2017-12-17", "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    //  Log.i("2017-12-17", "定位结束");
                    break;
            }
        };
    };

    Runnable r=new Runnable() {
        @Override
        public void run() {
            gpsHandler.postDelayed(this,2000);//2秒钟检测一次gps
            if(isOPen(getApplicationContext())){
                //  Toast.makeText(getApplicationContext(),"GPS可用",Toast.LENGTH_SHORT).show();
            }else{
                //  Toast.makeText(getApplicationContext(),"GPS不可用",Toast.LENGTH_SHORT).show();
                //移除Handler
                gpsHandler.removeCallbacks(r);
                Intent i=new Intent(mContext,OvertimeDialogActivity.class);
                i.putExtra("type","gps");
                startActivity(i);

            }
        }
    };

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    private  boolean isOPen(final Context context) {

        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gps) {
            return true;
        }

        return false;
    }

}
