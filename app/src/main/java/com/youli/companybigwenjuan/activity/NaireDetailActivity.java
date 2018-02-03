package com.youli.companybigwenjuan.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.youli.companybigwenjuan.R;
import com.youli.companybigwenjuan.entity.AnswerInfo;
import com.youli.companybigwenjuan.entity.NaireAnswerInfo;
import com.youli.companybigwenjuan.entity.NaireInfo;
import com.youli.companybigwenjuan.util.MyOkHttpUtils;
import com.youli.companybigwenjuan.util.SharedPreferencesUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by liutao on 2018/1/30.
 *
 * 问卷详情界面
 */

public class NaireDetailActivity extends BaseActivity implements View.OnClickListener{

   // private boolean isTablet=true;//判断是平板，还是手机

    private Context mContext=this;
    private LinearLayout bigll;

    private TextView titleTv;

    private Button btnStart,btnNext,btnLast,btnAll,btnRestart,btnSubmit;

    private List<NaireInfo> questionDetailsList;//问卷的信息

    private List<NaireInfo> juanInfos = new ArrayList<>();//问卷的信息

    private List<NaireInfo> questionTitleList = new ArrayList<>();//问题
    private List<NaireInfo> answerInfo = new ArrayList<>();//选项

    private int tempQuestionIndex = 0;// 用于标识上一题的题号
    // 用于保存上一步回答的题目
    private List<Integer> tempList = new ArrayList<Integer>();
    private NaireInfo currentInfo;// 用于表示当前题
    private int index = 0;//问题在集合中的索引

    private List<RadioButton> radioButtons = new ArrayList<>();//单选的答案
    private List<CheckBox> this_CheckBoxs = new ArrayList<CheckBox>();//多选的答案
    // 保存小题的编辑框
    private List<EditText> editTexts = new ArrayList<EditText>();
    // 保存大题的编辑框
    private List<EditText> questionEditTexts = new ArrayList<EditText>();
    private List<TextView> questionTextViews = new ArrayList<TextView>();
    // 保存出生年月的编辑框
    private List<EditText> birthEditTexts = new ArrayList<EditText>();

    private byte [] shujuliu;//答案数据流
    // 保存所选的答案和题号
    private List<AnswerInfo> answerInfos2 = new ArrayList<AnswerInfo>();

    private List<NaireAnswerInfo> aInfo=new ArrayList<>();//上个界面传递过来的答案信息

    private String typeStr;//1代表新调查,2代表已调查

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naire_detail);

      //  isTablet=isTabletDevice(this);

        aInfo=(List<NaireAnswerInfo>)getIntent().getSerializableExtra("answerInfo");//上个界面传递过来的答案信息

        Log.e("2018-2-1","aInfo=="+aInfo);

         questionDetailsList=((List<NaireInfo>)getIntent().getSerializableExtra("wenjuan"));

         typeStr=getIntent().getStringExtra("type");

        initViews();



        if(TextUtils.equals("2",typeStr)){//已调查

            if(questionDetailsList.size()>0) {
                    juanInfos.addAll(questionDetailsList);
                }
            btnStart.setVisibility(View.GONE);
                seeAllQuestion();
        }


    }



    private void initViews(){
        titleTv=findViewById(R.id.title_tv);//标题
        bigll = (LinearLayout) findViewById(R.id.ll);//所有问题的父布局
        btnStart= (Button) findViewById(R.id.btn_start);//开始答题
        btnStart.setOnClickListener(this);
        btnNext= (Button) findViewById(R.id.btn_next);//下一题
        btnNext.setOnClickListener(this);
        btnLast= (Button) findViewById(R.id.btn_last);//上一题
        btnLast.setOnClickListener(this);
        btnAll= (Button) findViewById(R.id.btn_all);//查看全部
        btnAll.setOnClickListener(this);
        btnRestart= (Button) findViewById(R.id.btn_restart);//重新答题
        btnRestart.setOnClickListener(this);
        btnSubmit= (Button) findViewById(R.id.btn_submit);//提交
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_start://开始答题


//                if(questionDetailsList.size()>0) {
//                    juanInfos.addAll(questionDetailsList);
//                }
//                seeAllQuestion();
               startQuestion();
                titleTv.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);



                if(questionTitleList.size()>0) {

                    btnLast.setVisibility(View.VISIBLE);
                    btnNext.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(mContext,"已经是最后一题了", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_last://上一题

                btnAll.setVisibility(View.GONE);

                lastQuestion();
                break;

            case R.id.btn_next://下一题

                nextQuestion();
                break;

            case R.id.btn_all://查看全部

                btnAll.setVisibility(View.GONE);
                btnLast.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                titleTv.setVisibility(View.VISIBLE);
                btnRestart.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);

                seeAllQuestion();
                break;

            case R.id.btn_restart://重新答题

                restartAnswerInfo();


                break;

            case R.id.btn_submit://提交

                submitAnswerInfo();
                break;
        }

    }

    //开始答题
    private void startQuestion(){

        radioButtons.clear();
        this_CheckBoxs.clear();
        editTexts.clear();
        questionEditTexts.clear();
        birthEditTexts.clear();
        questionTextViews.clear();
        index=0;



        if(questionDetailsList.size()>0) {
            juanInfos.addAll(questionDetailsList);
        }
        //添加问题
        questionTitleList = getQuestionByParent();

        if(questionTitleList.size()>0){
            fretchTree(bigll, questionTitleList.get(index), "");
        }

    }

    private void lastQuestion(){
        //上一题

        if (index == 0) {
            Toast.makeText(this, "已经是第一题了", Toast.LENGTH_SHORT).show();
            return;
        }

        // 去掉答题的文本
        for (EditText editText : questionEditTexts) {
            if (currentInfo.getID() == editText.getId()) {
                editText.setText("");
            }
        }



        // 去掉选择日期的文本
        for (TextView textView : questionTextViews) {
            if (currentInfo.getID() == textView.getId()) {
                textView.setText("请点击选择");
            }
        }



        List<NaireInfo> list = getAnswerByParentId(currentInfo);

        // 去掉小题的文本
        for (NaireInfo wenJuanInfo : list) {
            for (EditText editText : editTexts) {
                if (editText.getId() == wenJuanInfo.getID()) {
                    editText.setText("");
                }
            }

            // 去掉出生年月的文本(可能有问题)
            for (EditText editText : birthEditTexts) {
                if (wenJuanInfo.getID() == editText.getId()) {
                    editText.setText("");
                }
            }

        }


        for (NaireInfo dl : list) {
            //去掉单选按钮
            for (RadioButton rb : radioButtons) {

                if (rb.getId() == dl.getID()) {
                    rb.setChecked(false);
                }

            }
            // 去掉多选按钮
            for (CheckBox checkBox : this_CheckBoxs) {
                if (checkBox.getId() == dl.getID()) {
                    checkBox.setChecked(false);
                }
            }
        }

        index--;
        currentInfo = questionTitleList.get(index);

        if(questionTitleList.size()>0){
            fretchTree(bigll, currentInfo, "");
        }
    }
    //下一题
    private void nextQuestion(){

        NaireInfo info=questionTitleList.get(index);

        List<NaireInfo> tempSmallWenJuan = getAnswerByParentId(info);

        //判断是否已经作答了
        int questionNo = info.getID();
        if (questionEditTexts.size() > 0) {
            for (EditText editText : questionEditTexts) {
                if (editText.getId() == questionNo
                        && "".equals(editText.getText().toString()
                        .trim())) {
                    Toast.makeText(mContext, "答案不能为空!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        if (questionTextViews.size() > 0) {
            for (TextView textView : questionTextViews) {
                if (textView.getId() == questionNo
                        && "请点击选择".equals(textView.getText().toString()
                        .trim())) {
                    Toast.makeText(mContext, "答案不能为空!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }



        if(info.getINPUT_TYPE().contains("单选")||info.getINPUT_TYPE().contains("多选")||info.getTITLE_L().contains("多选")) {
            if (tempSmallWenJuan.size() > 0 && !checkIsRadioed(answerInfo, questionNo)) {

                Toast.makeText(this, "请选择合适的答案", Toast.LENGTH_SHORT).show();

                return;
            }
        }
        if(info.getINPUT_TYPE().contains("无")){
            if (makeEditBrith(answerInfo)){

              return;

            }
        }

        if (makeEdit(answerInfo))
            return;
        if (makeEdit_checkBox(answerInfo))
            return;

        if (index >= questionTitleList.size() - 1) {
            Toast.makeText(this, "已经是最后一题了", Toast.LENGTH_SHORT).show();
            btnAll.setVisibility(View.VISIBLE);
            return;
        }

        List<NaireInfo> tempAnswer = new ArrayList<>();

        List<NaireInfo> tempTitleAnswer = new ArrayList<NaireInfo>();

        for (NaireInfo answer : answerInfo) {
            if (answer.getPARENT_ID() == questionNo) {
                tempAnswer.add(answer);
            }
        }
        for (NaireInfo answer : juanInfos) {//这里可能会出错
            if (answer.getPARENT_ID() ==0) {
                tempTitleAnswer.add(answer);
            }
        }
        tempQuestionIndex = index;


        index++;
        currentInfo = questionTitleList.get(index);

        if(questionTitleList.size()>0){
            fretchTree(bigll, currentInfo, "");
        }
    }


    //重新答题
    private void restartAnswerInfo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("温馨提示").setMessage("确定要重新答题吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        titleTv.setVisibility(View.GONE);
                        btnLast.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        btnRestart.setVisibility(View.GONE);
                        restartQuestion();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).create().show();


    }

    //提交
    private void submitAnswerInfo(){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("温馨提示").setMessage("确定要提交吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        getAnswerInfo();

                        String answerString;

                        answerString = parseAnswerInfo();

                        if (answerInfos2.size() > 0) {
                            answerInfos2.clear();
                        }

                        if (questionTitleList.size() > 0) {
                            // bigll.removeAllViews();
                            if (tempList.size() > 0) {
                                tempList.clear();
                            }
                        }


                        Log.e("2018-01-31","您的答案是:"+answerString);

                        try {
                            shujuliu= answerString.getBytes("UTF-8");
                            submit();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).create().show();


    }

    //提交企业版
    private void submit(){

//        提交Set_Qa_Receiv_Special.aspx这个页面
//        post参数:GPS,提交需传经纬度
        final HttpClient client = new DefaultHttpClient();



        final String strhttp = MyOkHttpUtils.BaseUrl+"/Json/Set_Qa_Receiv_Special.aspx?GPS=" + InvestTypeActivity.jDuStr + "," + InvestTypeActivity.wDuStr;
        Log.e("2018-01-31","企业提交url"+strhttp);
        new Thread(

                new Runnable() {
                    @Override
                    public void run() {
                        String cookies = SharedPreferencesUtils.getString("cookies");
                        Log.e("2018-01-31","cookies=="+cookies);
                        HttpPost post = new HttpPost(strhttp);
                        try {
                            post.setHeader("cookie", cookies);
                            if (shujuliu!=null) {
                                Log.e("2018-01-31","提交shujuliu"+new String(shujuliu));
//                                String str = Base64.encodeToString(shujuliu, Base64.DEFAULT);
//                                StringEntity stringEntity = new StringEntity(str);
//                                stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
//                                        "application/json"));
//                                stringEntity.setContentEncoding(new BasicHeader(
//                                        HTTP.CONTENT_ENCODING, HTTP.UTF_8));
//                                post.setEntity(stringEntity);
                                ByteArrayEntity arrayEntity = new ByteArrayEntity(shujuliu);
                                arrayEntity.setContentType("application/octet-stream");
                                post.setEntity(arrayEntity);
                            }

                            HttpResponse response = client.execute(post);
                            Log.e("2018-01-31","提交响应码"+response.getStatusLine().getStatusCode());



                        //    if (response.getStatusLine().getStatusCode() == 200) {

                                HttpEntity entity=response.getEntity();
                                //EntityUtils中的toString()方法转换服务器的响应数据
                                final String str= EntityUtils.toString(entity, "utf-8");

                                Log.e("2018-01-31","提交str"+str);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(TextUtils.equals(str,"True")){
                                            Toast.makeText(mContext,"提交成功!",Toast.LENGTH_SHORT).show();
                                            //EventBus.getDefault().post(new ComListInfo());//通知企业列表界面刷新
                                            finish();
                                        }else{
                                            Intent i=new Intent(mContext,OvertimeDialogActivity.class);//提交失败
                                            startActivity(i);
                                        }
                                    }
                                });



                          //  }
                        } catch (Exception e) {

                            e.printStackTrace();
                        } finally {
                            post.abort();
                        }

                    }
                }

        ).start();

    }

    private void seeAllQuestion(){
        //查看全部
        questionTitleList = getQuestionByParent();

        bigll.removeAllViews();

        for(NaireInfo info:questionTitleList){
            fretchTree(bigll,info,"all");
        }

    }

    //重新答题
    private void restartQuestion(){

        index=0;
        radioButtons.clear();
        this_CheckBoxs.clear();
        editTexts.clear();
        questionEditTexts.clear();
        birthEditTexts.clear();
        questionTextViews.clear();
        if(questionTitleList.size()>0){
            if (tempList.size() > 0) {
                tempList.clear();
            }
            fretchTree(bigll, questionTitleList.get(index), "");
        }
    }

    //问题的布局
    private void fretchTree(LinearLayout layout, NaireInfo info, String isAll){

        if("".equals(isAll)){
            bigll.removeAllViews();
        }

        LinearLayout alllinearLayout=new LinearLayout(this);//整体布局（包括问题和选项）
        alllinearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams allparam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        alllinearLayout.setLayoutParams(allparam);
        LinearLayout qll=new LinearLayout(this);//问题的布局

        //if(info.getTITLE_L().length()>=20) {//左边的文字长度大于等于20就换行

            qll.setOrientation(LinearLayout.VERTICAL);
      //  }else{

       //     qll.setOrientation(LinearLayout.HORIZONTAL);
       // }

        if(!TextUtils.equals("",info.getTITLE_TOP())) {
            TextView tvTop = new TextView(this);//问题上边的部分
            tvTop.setText(info.getTITLE_TOP());
            tvTop.setPadding(10, 0, 0, 0);//向右移动10xp
            tvTop.setTextColor(Color.parseColor("#000000"));
            tvTop.setTextSize(20);
            qll.addView(tvTop, allparam);
        }
        TextView tvLeft = new TextView(this);//问题左边的部分

        tvLeft.setText(info.getCODE()+info.getTITLE_L());
        tvLeft.setPadding(10,0,0,0);//向右移动10xp
        tvLeft.setTextColor(Color.parseColor("#000000"));
        tvLeft.setTextSize(18);
        qll.addView(tvLeft,allparam);

        LinearLayout qRightll=new LinearLayout(this);//问题的右边布局(包括一个EditText和一个TextView)
        qRightll.setOrientation(LinearLayout.HORIZONTAL);
        if(info.isINPUT()){
            qRightll.setVisibility(View.VISIBLE);
            EditText et=new EditText(this);//问题的输入框
            // et.setTextSize(15);
            et.setGravity(Gravity.CENTER);
            if(isAll.equals("all")){
                et.setEnabled(false);
            }
            et.setPadding(0,0,0,30);
            et.setId(info.getID());
            if(!info.getTITLE_L().contains("出生")&&!info.getTITLE_L().contains("就业时间")&&!info.getTITLE_L().contains("就业的时间")&&!info.getTITLE_L().contains("什么时候")){
                if(TextUtils.equals(info.getINPUT_TYPE(),"数字")) {
                    et.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                if(info.getTITLE_L().contains("姓名")){
                   // et.setText("张三丰");
                }

                if(questionEditTexts.size()>0){
                    List<EditText> tempEditTexts=new ArrayList<>();
                    for(EditText editText2:questionEditTexts){
                        if(editText2.getId()==info.getID()){
                            et.setText(editText2.getText());
                            //从答案中得到家庭成员的名字
                            if(info.getTITLE_L().contains("姓名")){

                            }
                            tempEditTexts.add(editText2);
                        }
                    }
                    questionEditTexts.removeAll(tempEditTexts);
                    tempEditTexts.clear();
                }

                LinearLayout.LayoutParams etParam = new LinearLayout.LayoutParams(
                        info.getWIDTH(),
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                et.setLayoutParams(etParam);

                questionEditTexts.add(et);

                if(aInfo!=null&&aInfo.size()>0){

                    for(NaireAnswerInfo caInfo:aInfo){

                        for(EditText myEt:questionEditTexts){
                            if(caInfo.getDETIL_ID()==myEt.getId()){

                                myEt.setText(caInfo.getINPUT_VALUE());

                            }
                        }

                    }

                }

                qRightll.addView(et);
            }else{

                final TextView tv=new TextView(this);//问题里面选择日期的
                if(isAll.equals("all")){
                    tv.setEnabled(false);
                }
                tv.setTextSize(15);

                tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
                tv.setTextColor(0xff538ee5);
                tv.setPadding(10,0,0,0);
                Drawable drawable= getResources().getDrawable(R.drawable.rili);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tv.setCompoundDrawables(null,null,drawable,null);

                tv.setText("请点击选择");
                tv.setId(info.getID());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Calendar c=Calendar.getInstance();

                        new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                                tv.setText(year+"年"+(month+1)+"月"+dayOfMonth+"日");

                            }
                        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();

                    }
                });

                if(questionTextViews.size()>0){
                    List<TextView> tempTextViews=new ArrayList<>();
                    for(TextView textView2:questionTextViews){
                        if(textView2.getId()==info.getID()){
                            tv.setText(textView2.getText());
                            tempTextViews.add(textView2);
                        }
                    }
                    questionTextViews.removeAll(tempTextViews);
                    tempTextViews.clear();
                }
                questionTextViews.add(tv);
                qRightll.addView(tv,allparam);
            }


        }else{
            qRightll.setVisibility(View.GONE);
        }

        TextView tvRight = new TextView(this);//问题右边的部分
        tvRight.setText(info.getTITLE_R());
        tvRight.setTextSize(18);
        tvRight.setTextColor(Color.parseColor("#000000"));
        qRightll.addView(tvRight,allparam);

        qll.addView(qRightll,allparam);

        answerInfo = getAnswerByParentId(info);

         if(answerInfo!=null&&answerInfo.size()>0) {

             if (!TextUtils.equals("", answerInfo.get(0).getTITLE_TOP())) {
                 TextView tvBottom = new TextView(this);//问题上边的部分
                 tvBottom.setText(answerInfo.get(0).getTITLE_TOP());
                 tvBottom.setPadding(10, 0, 0, 0);//向右移动10xp
                 tvBottom.setTextColor(Color.parseColor("#000000"));
                 tvBottom.setTextSize(15);
                 tvBottom.setTypeface(null, Typeface.ITALIC);//斜体
                 qll.addView(tvBottom, allparam);
             }
         }




        alllinearLayout.addView(qll,allparam);



        LinearLayout optionlinearLayout=new LinearLayout(this);//选项的布局
        optionlinearLayout.setOrientation(LinearLayout.HORIZONTAL);




        LinearLayout xuanxiangll=new LinearLayout(this);
        xuanxiangll.setOrientation(LinearLayout.VERTICAL);

        RadioGroup rg=new RadioGroup(this);
        rg.setLayoutParams(allparam);



        if(info.getINPUT_TYPE().contains("多选")||info.getTITLE_L().contains("多选")){
            //多选题的布局

            List<CheckBox> checkBoxGroup=new ArrayList<CheckBox>();

            for(NaireInfo list:answerInfo){

                fretchTreeByQuestionMultiSelect(checkBoxGroup, rg,
                        list, xuanxiangll, isAll,
                        getMaxxuangxiang(info.getTITLE_L()));

            }

        }else if(info.getINPUT_TYPE().contains("单选")){
            //单选题的布局

            for(NaireInfo list:answerInfo){

                fretchTreeByQuestion(rg,xuanxiangll,list,isAll);

            }

        }else if(info.getINPUT_TYPE().contains("无")){
           //出生年月
            for(NaireInfo list:answerInfo){

                fretchTreeByBirth(xuanxiangll,list,isAll);

            }
        }


        xuanxiangll.setLayoutParams(allparam);
        optionlinearLayout.addView(rg);//这里

        optionlinearLayout.addView(xuanxiangll,allparam);
        //  optionlinearLayout.setBackgroundResource(R.drawable.gridebg);
        alllinearLayout.addView(optionlinearLayout,allparam);

        layout.addView(alllinearLayout,allparam);
    }

    private int getMaxxuangxiang(String a){

        if(a.contains("最多选")&&a.contains("项")){

            return Integer.valueOf(a.substring(a.indexOf("最多选")+3,a.indexOf("项")));

        }

        if(a.contains("不要超过")&&a.contains("个)")){

            return Integer.valueOf(a.substring(a.indexOf("不要超过")+4,a.indexOf("个)")));


        }

        if(a.contains("不要超过")&&a.contains("个）")){

            return Integer.valueOf(a.substring(a.indexOf("不要超过")+4,a.indexOf("个）")));


        }

        return 30;
    }

    //出生年月的布局
    private void fretchTreeByBirth(LinearLayout xuanxiangll, final NaireInfo info, String isAll) {

        LinearLayout linearLayout=new LinearLayout(this);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 100);
        linearLayout.setLayoutParams(llParam);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 100);

        TextView tv=new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setText(info.getTITLE_L());
        //  tv.setTextColor(Color.parseColor("#000000"));
        linearLayout.addView(tv,textparams);

        if(info.isINPUT()) {
            EditText et = new EditText(this);
            if(isAll.equals("all")){
                et.setEnabled(false);
            }
            //2018-2-2注释
//            if(isTablet) {
//                et.setPadding(0, 0, 0, 10);
//            }else{
//                et.setPadding(0, -25, 0, 0);
//            }
            et.setPadding(0,0,0,20);//往上移动20xp
            et.setText("");
            et.setId(info.getID());
            if (TextUtils.equals(info.getINPUT_TYPE(), "数字")) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if(birthEditTexts.size()>0){
                List<EditText> tempEditTexts=new ArrayList<>();
                for(EditText editText2:birthEditTexts){
                    if(editText2.getId()==info.getID()){
                        et.setText(editText2.getText());
                        tempEditTexts.add(editText2);
                    }
                }
                birthEditTexts.removeAll(tempEditTexts);
                tempEditTexts.clear();
            }
            LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
                    info.getWIDTH(), 120);
            birthEditTexts.add(et);

            if(aInfo!=null&&aInfo.size()>0){

                for(NaireAnswerInfo caInfo:aInfo){

                    for(EditText myRb:birthEditTexts){
                        if(caInfo.getDETIL_ID()==myRb.getId()){

                            myRb.setText(caInfo.getINPUT_VALUE());

                        }
                    }

                }

            }

            et.setGravity(Gravity.CENTER);
            et.setLayoutParams(etParams);
            linearLayout.addView(et);
        }

        TextView tvRight = new TextView(this);
        //     tvRight.setTextColor(Color.parseColor("#000000"));
        tvRight.setText(info.getTITLE_R());
        LinearLayout.LayoutParams tvRightParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
        linearLayout.addView(tvRight, tvRightParams);

        xuanxiangll.addView(linearLayout,llParam);
    }

    //单选题的布局
    private void fretchTreeByQuestion(RadioGroup rg, LinearLayout xuanxiangll, final NaireInfo info, String isAll) {

        LinearLayout linearLayout=new LinearLayout(this);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 90);
        linearLayout.setLayoutParams(llParam);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        RadioButton rb=new RadioButton(this);
        LinearLayout.LayoutParams rbParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 90);
        rb.setLayoutParams(rbParam);
        rb.setId(info.getID());
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.equals("结束", info.getJUMP_CODE())){
                    btnAll.setVisibility(View.GONE);
                }

            }
        });
        if(isAll.equals("all")){
            rb.setEnabled(false);
        }

        rg.addView(rb);


        if(radioButtons.size()>0){

            List<RadioButton> tempRadioButtons=new ArrayList<>();
            for(RadioButton rButton :radioButtons){

                if(rButton.getId()==info.getID()&&rButton.isChecked()){
                    rb.setChecked(true);
                    tempRadioButtons.add(rButton);
                }

            }
            radioButtons.removeAll(tempRadioButtons);
            tempRadioButtons.clear();
        }

        radioButtons.add(rb);

        if(aInfo!=null&&aInfo.size()>0){

            for(NaireAnswerInfo caInfo:aInfo){

                for(RadioButton myRb:radioButtons){
                    if(caInfo.getDETIL_ID()==myRb.getId()){

                        myRb.setChecked(true);

                    }
                }

            }

        }

        LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, 90);

        TextView tv=new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setText(info.getTITLE_L());
        //  tv.setTextColor(Color.parseColor("#000000"));
        linearLayout.addView(tv,textparams);

        if(info.isINPUT()) {
            EditText et = new EditText(this);
            if(isAll.equals("all")){
                et.setEnabled(false);
            }
            //2018-2-2注释
//            if(isTablet) {
//                et.setPadding(0, 0, 0, 10);
//            }else{
//                et.setPadding(0, -20, 0, 0);
//            }
            et.setGravity(Gravity.CENTER);
            et.setText("");
            et.setId(info.getID());

            if (TextUtils.equals(info.getINPUT_TYPE(), "数字")) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
            if(editTexts.size()>0){
                List<EditText> tempEditTexts=new ArrayList<>();
                for(EditText editText2:editTexts){
                    if(editText2.getId()==info.getID()){
                        et.setText(editText2.getText());
                        tempEditTexts.add(editText2);
                    }
                }
                editTexts.removeAll(tempEditTexts);
                tempEditTexts.clear();
            }
            editTexts.add(et);
            linearLayout.addView(et, textparams);
        }

        TextView tvRight = new TextView(this);
        //     tvRight.setTextColor(Color.parseColor("#000000"));
        tvRight.setText(info.getTITLE_R());
        LinearLayout.LayoutParams tvRightParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 90);
        linearLayout.addView(tvRight, tvRightParams);


        xuanxiangll.addView(linearLayout,llParam);
    }


    //多选题选项的布局
    private void fretchTreeByQuestionMultiSelect(List<CheckBox> CheckBoxGroup,
                                                 RadioGroup group, NaireInfo info,
                                                 LinearLayout xuanxiangll, String isAll, int MultiSelect){

        LinearLayout ll=new LinearLayout(this);

        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams llParam;
        if(info.isINPUT()){
            if(info.getTITLE_L().length()<25) {

                llParam = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, 280);


            }else if(info.getTITLE_L().length()<50){
                llParam = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, 350);


            }else {
                llParam = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, 400);


            }
        }else{
         llParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 90);
        }


        ll.setLayoutParams(llParam);

        CheckBox cb=new CheckBox(this);
        cb.setOnCheckedChangeListener(new MyOnCheckedChangeListener(
                CheckBoxGroup,MultiSelect));
        cb.setId(info.getID());
        if(isAll.equals("all")){
            cb.setEnabled(false);
        }
//        LinearLayout.LayoutParams cbParam = new LinearLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT, 200);
//        group.addView(cb,cbParam);

        if(this_CheckBoxs.size()>0){
            List<CheckBox> tempRadioButtons=new ArrayList<>();
            for(CheckBox _CheckBox:this_CheckBoxs){
                if(_CheckBox.getId()==info.getID()&&_CheckBox.isChecked()){
                    cb.setChecked(true);
                    tempRadioButtons.add(_CheckBox);
                }
            }
            this_CheckBoxs.removeAll(tempRadioButtons);
            tempRadioButtons.clear();
        }

        for(int i=0;i<this_CheckBoxs.size();i++){
            if(this_CheckBoxs.get(i).getId()==cb.getId()){
                this_CheckBoxs.remove(i);
                break;
            }
        }
        this_CheckBoxs.add(cb);

        if(aInfo!=null&&aInfo.size()>0){

            for(NaireAnswerInfo caInfo:aInfo){

                for(CheckBox myRb:this_CheckBoxs){
                    if(caInfo.getDETIL_ID()==myRb.getId()){

                        myRb.setChecked(true);

                    }
                }

            }

        }

        //多选题选项的文字和输入框
        TextView tvLeft=new TextView(this);//左边的文字
        tvLeft.setText(info.getTITLE_L());


        LinearLayout.LayoutParams   tvParam;

        if(info.isINPUT()) {
            tvParam   = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            tvLeft.setGravity(Gravity.CENTER);
            tvParam   = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,90);
        }

       tvLeft.setLayoutParams(tvParam);
        ll.addView(tvLeft);

        if(info.isINPUT()) {

            if(info.getTITLE_L().length()<25) {

                LinearLayout.LayoutParams cbParam = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, 280);

                cb.setLayoutParams(cbParam);

                group.addView(cb);
            }else if(info.getTITLE_L().length()<50){
                LinearLayout.LayoutParams cbParam = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, 350);

                cb.setLayoutParams(cbParam);

                group.addView(cb);
            }else {
                LinearLayout.LayoutParams cbParam = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, 400);

                cb.setLayoutParams(cbParam);

                group.addView(cb);
            }
            EditText et = new EditText(this);
            if(isAll.equals("all")){
                et.setEnabled(false);
            }
            //2018-2-2注释
//            if(isTablet) {
//                et.setPadding(0, 0, 0, 10);
//            }else{
//                et.setPadding(0, -20, 0, 0);
//            }
            et.setGravity(Gravity.CENTER);
            et.setText("");
            et.setId(info.getID());

            if (TextUtils.equals(info.getINPUT_TYPE(), "数字")) {
                et.setInputType(InputType.TYPE_CLASS_NUMBER);
            }

            if (editTexts.size() > 0) {
                List<EditText> tempEditTexts = new ArrayList<EditText>();
                for (EditText editText2 : editTexts) {
                    if (editText2.getId() == info.getID()) {
                        et.setText(editText2.getText());
                        tempEditTexts.add(editText2);
                    }

                }
                editTexts.removeAll(tempEditTexts);
                tempEditTexts.clear();
            }
            LinearLayout.LayoutParams etParam = new LinearLayout.LayoutParams(
                    info.getWIDTH(), ViewGroup.LayoutParams.WRAP_CONTENT);
            editTexts.add(et);

            if(aInfo!=null&&aInfo.size()>0){

                for(NaireAnswerInfo caInfo:aInfo){

                    for(EditText myRb:editTexts){
                        if(caInfo.getDETIL_ID()==myRb.getId()){

                            myRb.setText(caInfo.getINPUT_VALUE());

                        }
                    }

                }

            }

            et.setLayoutParams(etParam);
            ll.addView(et);
            TextView tvRight = new TextView(this);
            tvRight.setGravity(Gravity.CENTER);

            tvRight.setText(info.getTITLE_R());
            ll.addView(tvRight,llParam);

        }else{
            LinearLayout.LayoutParams cbParam = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, 90);
            group.addView(cb,cbParam);

        }


        xuanxiangll.addView(ll);
    }

    private List<NaireInfo> getQuestionByParent(){
        //从问卷信息中找出问题的信息
        List<NaireInfo> questionInfos = new ArrayList<>();

        for(NaireInfo detailsList : juanInfos){
            if(detailsList.getPARENT_ID()==0){
                questionInfos.add(detailsList);
            }
        }
        return questionInfos;
    }

    private List<NaireInfo> getAnswerByParentId(NaireInfo info){
        //用问题的信息得到选项的信息
        List<NaireInfo> aInfos = new ArrayList<>();

        for(NaireInfo list:juanInfos){

            if(list.getPARENT_ID()==info.getID()){
                aInfos.add(list);
            }

        }

        return aInfos;
    }

    private void getAnswerInfo() {
        if (radioButtons.size() > 0) {
            for (RadioButton radioButton : radioButtons) {
                if (radioButton.isChecked()) {
                    AnswerInfo answerInfo = new AnswerInfo();

                    for (NaireInfo wenJuanInfo : juanInfos) {
                        if (wenJuanInfo.getID() == radioButton.getId()) {
                            answerInfo.setAnswerId(wenJuanInfo.getID());
                            answerInfo.setAnswerNo(wenJuanInfo.getID());
                            answerInfo.setAnswerText("");
                        }
                    }
                    if (editTexts.size() > 0) {
                        for (EditText editText : editTexts) {
                            if (radioButton.getId() == editText.getId()) {
                                answerInfo.setAnswerText(editText.getText()
                                        .toString().trim());
                            }
                        }
                    }
                    answerInfos2.add(answerInfo);
                }
            }
        }
        if (this_CheckBoxs.size() > 0) {
            for (CheckBox radioButton : this_CheckBoxs) {
                if (radioButton.isChecked()) {
                    AnswerInfo answerInfo = new AnswerInfo();

                    for (NaireInfo wenJuanInfo : juanInfos) {
                        if (wenJuanInfo.getID() == radioButton.getId()) {
                            answerInfo.setAnswerId(wenJuanInfo.getID());
                            answerInfo.setAnswerNo(wenJuanInfo.getID());
                            answerInfo.setAnswerText("");
                        }
                    }
                    if (editTexts.size() > 0) {
                        for (EditText editText : editTexts) {
                            if (radioButton.getId() == editText.getId()) {
                                answerInfo.setAnswerText(editText.getText()
                                        .toString().trim());
                            }
                        }
                    }
                    answerInfos2.add(answerInfo);
                }
            }
        }

        if (questionEditTexts.size() > 0) {
            List<NaireInfo> questionInfos = getQuestionByParent();
            for (EditText editText : questionEditTexts) {
                for (NaireInfo wenJuanInfo : questionInfos) {
                    if (editText.getId() == wenJuanInfo.getID()
                            && !"".equals(editText.getText().toString().trim())
                            && null != editText.getText().toString().trim()) {
                        AnswerInfo answerInfo = new AnswerInfo();
                        answerInfo.setAnswerId(wenJuanInfo.getID());
                        answerInfo.setAnswerNo(wenJuanInfo.getID());
                        answerInfo.setAnswerText(editText.getText().toString()
                                .trim());
                        answerInfos2.add(answerInfo);
                    }
                }
            }

            if(birthEditTexts.size()>0){

                for(EditText editText:birthEditTexts){
                    AnswerInfo answerInfo = new AnswerInfo();
                    answerInfo.setAnswerId(editText.getId());
                    answerInfo.setAnswerNo(editText.getId());
                    answerInfo.setAnswerText(editText.getText().toString()
                            .trim());
                    answerInfos2.add(answerInfo);
                }

            }

        }

        if (questionTextViews.size() > 0) {
            List<NaireInfo> questionInfos = getQuestionByParent();
            for (TextView textView : questionTextViews) {
                for (NaireInfo wenJuanInfo : questionInfos) {
                    if (textView.getId() == wenJuanInfo.getID()
                            && !"请点击选择".equals(textView.getText().toString().trim())
                            ) {
                        AnswerInfo answerInfo = new AnswerInfo();
                        answerInfo.setAnswerId(wenJuanInfo.getID());
                        answerInfo.setAnswerNo(wenJuanInfo.getID());
                        answerInfo.setAnswerText(textView.getText().toString()
                                .trim());
                        answerInfos2.add(answerInfo);
                    }
                }
            }
        }

    }

    //组装答案

    private String parseAnswerInfo() {
        JSONArray array = new JSONArray();
        if (answerInfos2.size() > 0) {
            for (AnswerInfo answerInfo : answerInfos2) {
                JSONObject object = new JSONObject();
                try {
                    object.put("DETIL_ID", answerInfo.getAnswerId());
                    object.put("INPUT_VALUE", answerInfo.getAnswerText());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                array.put(object);
            }
        }

        return array.toString();
    }

    //判断单选题多选题是否作答
    private boolean checkIsRadioed(List<NaireInfo> infos, int questionId) {

        for (RadioButton rb : radioButtons) {

            for (NaireInfo dl : infos) {

                if (rb.getId() == dl.getID() && dl.getPARENT_ID() == questionId && rb.isChecked()) {

                    return true;

                }

            }

        }

        for (CheckBox _CheckBox : this_CheckBoxs) {
            for (NaireInfo dl : infos) {
                if (_CheckBox.getId() == dl.getID()
                        && dl.getPARENT_ID() == questionId
                        && _CheckBox.isChecked()) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean makeEditBrith(List<NaireInfo> infos) {
        if (birthEditTexts.size() > 0) {
                    for (EditText editText : birthEditTexts) {
                        for (NaireInfo wenJuanInfo : infos) {
                            if (editText.getId() == wenJuanInfo.getID()
                                    && "".equals(editText.getText().toString()
                                    .trim())) {
                                Toast.makeText(mContext, "答案不能为空!",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    }
        }
        return false;
    }

    private boolean makeEdit(List<NaireInfo> infos) {
        if (editTexts.size() > 0) {
            for (RadioButton radioButton : radioButtons) {
                if (radioButton.isChecked()) {
                    for (EditText editText : editTexts) {
                        for (NaireInfo wenJuanInfo : infos) {
                            if (radioButton.getId() == wenJuanInfo.getID()
                                    && editText.getId() == wenJuanInfo.getID()
                                    && "".equals(editText.getText().toString()
                                    .trim())) {
                                Toast.makeText(mContext, "答案不能为空!",
                                        Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    private boolean makeEdit_checkBox(List<NaireInfo> infos) {
        if (editTexts.size() > 0) {
            for (CheckBox radioButton : this_CheckBoxs) {
                if (radioButton.isChecked()) {
                    for (EditText editText : editTexts) {
                        for (NaireInfo wenJuanInfo : infos) {
                            if (radioButton.getId() == wenJuanInfo.getID()
                                    && editText.getId() == wenJuanInfo.getID()
                                    && "".equals(editText.getText().toString()
                                    .trim())) {
                                Toast.makeText(mContext, "答案不能为空!",
                                        Toast.LENGTH_SHORT).show();

                                return true;
                            }

                        }
                    }
                }

            }
        }
        return false;
    }

    //复选框的监听
    public class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        int _MultiSelect;
        List<CheckBox> _group;

        public MyOnCheckedChangeListener(List<CheckBox> _group, int _MultiSelect) {
            this._group = _group;
            this._MultiSelect = _MultiSelect;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked){

                if(_group.size()>_MultiSelect-1){
                    buttonView.setChecked(false);
                    Toast.makeText(mContext, "最多选" + _MultiSelect + "项",
                            Toast.LENGTH_SHORT).show();
                }else{
                    _group.add((CheckBox)buttonView);
                }

            }else{

                CheckBox _check_box=(CheckBox) buttonView;

                if(_group.contains(_check_box)){
                    _group.remove(_check_box);
                };


            }

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
             if (TextUtils.equals("1",typeStr)) {
            showMessage("温馨提示", "确定要放弃答题吗?");
             } else  if (TextUtils.equals("2",typeStr)){
                showMessage("温馨提示", "确定要退出吗?");
             }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title).setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).create().show();
    }

//    /**
//     * 判断是否平板设备
//     * @param context
//     * @return true:平板,false:手机
//     */
//    private boolean isTabletDevice(Context context) {
//        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=
//                Configuration.SCREENLAYOUT_SIZE_LARGE;
//    }
}
