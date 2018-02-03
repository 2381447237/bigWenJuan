package com.youli.companybigwenjuan.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.youli.companybigwenjuan.R;
import com.youli.companybigwenjuan.util.MyOkHttpUtils;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by liutao on 2018/2/2.
 *
 * //修改密码
 */

public class ModifyPassword extends BaseActivity implements View.OnClickListener{

    private Context mContext = ModifyPassword.this;

    private final int SUCCESS = 10001;
    private final int FAIL = 10002;

    private Button confirmBtn, cancelBtn;
    private EditText editorg1;//原密码
    private EditText editorg2;//新密码
    private EditText editorg3;//确认新密码
    private String pwdOrgStr, pwdNewStr, pwdNewAgaStr;


    private Handler mHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SUCCESS:
//                  Toast.makeText(mContext, "密码修改成功！", Toast.LENGTH_SHORT).show();
//                  finish();

                    Intent i=new Intent(mContext,OvertimeDialogActivity.class);
                    i.putExtra("type","pwd");
                    startActivity(i);

                    break;
                case FAIL:

                    Toast.makeText(mContext, "密码修改失败！", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initView();
    }

    private void initView(){
        confirmBtn= (Button) findViewById(R.id.btn_change_pwd_confirm);
        confirmBtn.setOnClickListener(this);
        cancelBtn= (Button) findViewById(R.id.btn_change_pwd_cancel);
        cancelBtn.setOnClickListener(this);
        editorg1= (EditText) findViewById(R.id.et_change_pwd_original);
        editorg2= (EditText) findViewById(R.id.et_change_pwd_new);
        editorg3= (EditText) findViewById(R.id.et_change_pwd_new_again);

    }

    @Override
    public void onClick(View v) {
        pwdOrgStr=editorg1.getText().toString().trim();
        pwdNewStr=editorg2.getText().toString().trim();
        pwdNewAgaStr=editorg3.getText().toString().trim();

        switch (v.getId()){
            case R.id.btn_change_pwd_confirm:
                if (TextUtils.equals(pwdOrgStr,"")){
                    Toast.makeText(mContext,"原密码不能为空！",Toast.LENGTH_SHORT).show();
                    editorg1.requestFocus();
                    return;
                }
                if (TextUtils.equals(pwdNewStr,"")){
                    Toast.makeText(mContext,"新密码不能为空！",Toast.LENGTH_SHORT).show();
                    editorg2.requestFocus();
                    return;
                }
                if (TextUtils.equals(pwdNewAgaStr,"")){
                    Toast.makeText(mContext,"确认新密码不能为空！",Toast.LENGTH_SHORT).show();
                    editorg3.requestFocus();
                    return;
                }
                if (!TextUtils.equals(pwdNewStr,pwdNewAgaStr)){
                    Toast.makeText(mContext,"新密码和确认新密码不同！",Toast.LENGTH_SHORT).show();
                    editorg2.setText("");
                    editorg3.setText("");
                    editorg2.requestFocus();
                    return;
                }

                changPwdDialog();

                break;
            case R.id.btn_change_pwd_cancel:
                finish();
                break;
        }
    }


    private void changPwdDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("您确定要修改密码吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                //修改密码
                                changPwd();
                            }
                        }
                ).start();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    /*
    *http://web.youli.pw:89/Json/Set_Pwd.aspx?pwd=123&new_pwd=321
    返回true，修改成功，否则，修改失败；
    * */
    private void changPwd(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = MyOkHttpUtils.BaseUrl + "/Json/Set_Pwd.aspx?pwd="+pwdOrgStr+"&new_pwd="+pwdNewStr;

                Response response = MyOkHttpUtils.okHttpGet(url);

                try {
                    String resStr = response.body().string();

                    Message msg = Message.obtain();
                    Log.e("2018-2-2","yrl"+url);
                    Log.e("2018-2-2","========"+resStr);

                    if (TextUtils.equals(resStr, "True")) {

                        msg.what = SUCCESS;

                    } else {

                        msg.what = FAIL;

                    }

                    mHandle.sendEmptyMessage(msg.what);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();

    }
}
