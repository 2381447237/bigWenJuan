package com.youli.companybigwenjuan.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.youli.companybigwenjuan.R;
import com.youli.companybigwenjuan.entity.AdminInfo;

import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by liutao on 2018/2/2.
 *
 * 登录账号信息界面
 */

public class AdminInfoActivity extends BaseActivity {

    private Context mContext=this;
    private TextView tvOperatorNo;//操作员工号
    private TextView tvName;//姓名
    private TextView tvPhone;//联系电话
    private TextView tvEmail;//电子邮箱
    private TextView tvState;//状态
    private TextView tvDepart;//所属部门
    private TextView tvStreet;//街道
    private RelativeLayout password_xg;

    private AdminInfo aInfo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_info);

        aInfo=(AdminInfo)getIntent().getSerializableExtra("ADMININFO");

        initViews();
    }

    private void initViews(){
        password_xg= (RelativeLayout) findViewById(R.id.rela_mmxg);
        password_xg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,ModifyPassword.class);//修改密码
                startActivity(intent);
            }
        });

        tvOperatorNo= (TextView) findViewById(R.id.tv_admin_info_operator_no);
        tvOperatorNo.setText(aInfo.getINPUT_CODE());
        tvName= (TextView) findViewById(R.id.tv_admin_info_name);
        tvName.setText(aInfo.getNAME());
        tvPhone= (TextView) findViewById(R.id.tv_admin_info_phone);
        tvPhone.setText(aInfo.getPHONE());
        tvEmail= (TextView) findViewById(R.id.tv_admin_info_email);
        tvEmail.setText(aInfo.getEMAIL());


        tvState= (TextView) findViewById(R.id.tv_admin_info_state);

        if(!aInfo.isSTOP()){
            tvState.setText("启用");
        }else{
            tvState.setText("停用");
        }


        tvDepart= (TextView) findViewById(R.id.tv_admin_info_department);
        tvDepart.setText(aInfo.getDEPT());
        tvStreet= (TextView) findViewById(R.id.tv_admin_info_street);
        tvStreet.setText(aInfo.getJD());



    }



}







