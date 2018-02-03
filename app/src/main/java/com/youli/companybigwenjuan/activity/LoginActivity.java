package com.youli.companybigwenjuan.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youli.companybigwenjuan.R;
import com.youli.companybigwenjuan.util.MyOkHttpUtils;
import com.youli.companybigwenjuan.util.ProgressDialogUtils;
import com.youli.companybigwenjuan.util.SharedPreferencesUtils;
import com.youli.companybigwenjuan.util.UpdateManager;

import java.io.IOException;

import okhttp3.Response;


//事业单位大调研调查问卷
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext=this;

    private final int SUCCEED_LOGIN=10000;//登录1按钮
    private final int  PROBLEM=10004;

    private LocationManager locationManager;

    private  ProgressDialog progressDialog;

    private EditText etUserName,etPwd;//用户名，密码
    private Button btnLogin;//登录按钮

    private String nameStr,pwdStr;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            dismissMyProgressDialog(mContext);
            switch (msg.what){

                case SUCCEED_LOGIN:

                    if(TextUtils.equals("true", (String)msg.obj)){

                        SharedPreferencesUtils.putString("userName",nameStr);

                                        Intent i=new Intent(mContext,InvestTypeActivity.class);
                                          startActivity(i);

                                          finish();
                    }else if(TextUtils.equals("false", (String)msg.obj)){
                        Toast.makeText(LoginActivity.this,"用户名或密码不正确",Toast.LENGTH_SHORT).show();
                    }

                    break;



                case PROBLEM:



                        Toast.makeText(LoginActivity.this, "网络不给力", Toast.LENGTH_SHORT).show();


                    break;


            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 更新apk
        UpdateManager manager = new UpdateManager(LoginActivity.this);
        manager.checkUpdate();

        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        initViews();

    }

    private void initViews(){

        etUserName=findViewById(R.id.et_username_login);
        etPwd=findViewById(R.id.et_password_login);
        String localUserName = SharedPreferencesUtils.getString("userName");
        if (!TextUtils.equals("",localUserName)) {
            etUserName.setText(localUserName);
            etPwd.requestFocus();
        }
        btnLogin=findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_login:

                nameStr=etUserName.getText().toString().trim();
                pwdStr=etPwd.getText().toString().trim();

                if(TextUtils.equals("",nameStr)||TextUtils.equals("",pwdStr)){
                    Toast.makeText(this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                }else{

                    if ((locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) != true)) {
                        Toast.makeText(LoginActivity.this, "请打开GPS定位！",
                                Toast.LENGTH_SHORT).show();
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                        startActivity(callGPSSettingIntent);
                    }else {
                        //登录
                        login(nameStr, pwdStr);
                    }
                }



                break;

        }

    }

    private void login(final String name, final String password){

        showMyProgressDialog(mContext);

        new Thread(

                new Runnable() {
                    @Override
                    public void run() {

                        String  url= MyOkHttpUtils.BaseUrl + "/login.aspx?username=" + name + "&password=" + password;

                        Log.e("2018-1-30","登录="+url);

                        Response response=MyOkHttpUtils.okHttpGet(url);

                        //获得cookies
                        if(response!=null) {
                            if (response.header("Set-Cookie") != null) {
                                String cookies = response.header("Set-Cookie").toString();

                                String mycookies=cookies.substring(0,cookies.indexOf(";"));
                                SharedPreferencesUtils.putString("cookies", mycookies);

                            }
                        }
                        Message msg=Message.obtain();
                        try {
                            if(response!=null) {
                                msg.obj = response.body().string();
                                msg.what=SUCCEED_LOGIN;
                                mHandler.sendMessage(msg);
                            }else{
                                msg.what=PROBLEM;
                                mHandler.sendMessage(msg);

                            }
                        } catch (IOException e) {

                            msg.what=PROBLEM;
                            mHandler.sendMessage(msg);

                            e.printStackTrace();
                        }

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

}
