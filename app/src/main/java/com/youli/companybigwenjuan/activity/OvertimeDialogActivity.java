package com.youli.companybigwenjuan.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.youli.companybigwenjuan.R;
import com.youli.companybigwenjuan.util.AlertDialogUtils;


/**
 * 作者: zhengbin on 2017/11/13.
 * <p>
 * 邮箱:2381447237@qq.com
 * <p>
 * github:2381447237
 *
 * 登录超时的对话框
 */

public class OvertimeDialogActivity extends BaseActivity{

    private AlertDialogUtils adu;
    private Context mContext=OvertimeDialogActivity.this;
    private String mark;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_overtime_dialog);

        mark=getIntent().getStringExtra("type");

         initViews(mark);
    }

    private void initViews(String mark){

        adu=new AlertDialogUtils(this, R.layout.overtime_dialog,R.style.dialog);

        adu.showAlertDialog();

        TextView tv= (TextView) adu.getAduView().findViewById(R.id.overtime_title_tv);
        if(TextUtils.equals(mark,"gps")){
            tv.setText("GPS未打开，请您重新登录！");
        }else if(TextUtils.equals(mark,"imei")){
            tv.setText("IMEI号码不正确，请您重新登录！");
        }else if(TextUtils.equals(mark,"state")){
            tv.setText("此账号已停用，请您重新登录！");
        }else if(TextUtils.equals(mark,"pwd")){
            tv.setText("密码修改成功，请您重新登录！");
        }


        Button btnSure= (Button) adu.getAduView().findViewById(R.id.btn_overtime_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adu.dismissAlertDialog();
                ActivityController.finishAll();
                Intent intent=new Intent(mContext,LoginActivity.class);
                startActivity(intent);
            }
        });

    }

}
