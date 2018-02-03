package com.youli.companybigwenjuan.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.youli.companybigwenjuan.R;
import com.youli.companybigwenjuan.entity.AnswerInfo;
import com.youli.companybigwenjuan.entity.CommonViewHolder;
import com.youli.companybigwenjuan.entity.NaireAnswerInfo;
import com.youli.companybigwenjuan.entity.NaireInfo;
import com.youli.companybigwenjuan.entity.YdcListInfo;
import com.youli.companybigwenjuan.util.MyDateUtils;
import com.youli.companybigwenjuan.util.MyOkHttpUtils;
import com.youli.companybigwenjuan.view.CommonAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

/**
 * Created by liutao on 2018/2/1.
 *
 * 已调查列表
 */

public class YdcListActivity extends  BaseActivity{

    private Context mContext=this;

    private final int SUCCEED=10001;
    private final int SUCCEED_NODATA=10002;
    private final int  PROBLEM=10003;
    private final int OVERTIME=10004;//登录超时

    private final int SUCCEED_ANSWER=10005;
    private final int SUCCEED_ANSWER_NODATA=10006;

    private PullToRefreshListView lv;
    private List<YdcListInfo> data=new ArrayList<>();
    private CommonAdapter adapter;

    private TextView tvNum;

    private ProgressDialog progressDialog;

    private int PageIndex=0;

    private Handler mHandler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            dismissMyProgressDialog(mContext);

            switch (msg.what) {

                case SUCCEED:

                    if (PageIndex == 0) {
                        data.clear();
                    }

                    data.addAll((List<YdcListInfo>) msg.obj);

                    if(data.size()>0) {
                        tvNum.setText("共有" + data.get(0).getRecordCount() + "人");
                    }
                    if (data != null) {

                        lv.setVisibility(View.VISIBLE);
                    }
                    LvSetAdapter(data);


                    break;
                case PROBLEM:
                    Toast.makeText(mContext, "网络不给力", Toast.LENGTH_SHORT).show();
                    if (lv.isRefreshing()) {
                        lv.onRefreshComplete();//停止刷新或加载更多
                    }
                    break;
                case SUCCEED_NODATA:
                    if (data.size()>0) {

                    tvNum.setText("共有" + data.get(0).getRecordCount() + "人");
            }else{
                        tvNum.setText("共有0人");
                    }
                    Toast.makeText(mContext,"没有更多数据",Toast.LENGTH_SHORT).show();

                    if(lv.isRefreshing()) {
                        lv.onRefreshComplete();//停止刷新或加载更多
                    }
                    break;
                case OVERTIME:

                    Intent i=new Intent(mContext,OvertimeDialogActivity.class);
                    startActivity(i);


                    break;

                case SUCCEED_ANSWER:

                                    Intent intent=new Intent(mContext,NaireDetailActivity.class);
                intent.putExtra("type","2");//2代表已调查
                intent.putExtra("wenjuan",(Serializable)((List<NaireInfo>)getIntent().getSerializableExtra("wenjuan")));
                intent.putExtra("answerInfo",(Serializable)(List<NaireAnswerInfo>)(msg.obj));
                startActivity(intent);
                    break;
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ydc_list);

        initViews();

    }

    private void initViews(){

        tvNum=findViewById(R.id.tv_ydc_num);

        lv=findViewById(R.id.lv_ydc_list);
        lv.setMode(PullToRefreshBase.Mode.BOTH);
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                //刷新
                PageIndex=0;
                initDatas(PageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                //加载更多
                PageIndex++;
                initDatas(PageIndex);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

               // Toast.makeText(mContext,"ID=="+data.get(i-1).getID(),Toast.LENGTH_SHORT).show();

//                Intent intent=new Intent(mContext,NaireDetailActivity.class);
//                intent.putExtra("type","2");//2代表已调查
//                intent.putExtra("wenjuan",(Serializable)((List<NaireInfo>)getIntent().getSerializableExtra("wenjuan")));
//                startActivity(intent);

                getAnswerInfo(data.get(i-1).getID());

            }
        });

        initDatas(PageIndex);

    }

    private void initDatas(final int pIndex){


        new Thread(


                new Runnable() {
                    @Override
                    public void run() {



                            //  http://183.194.4.58:89/Json/Get_Receiv_Master.aspx?page=0&rows=20
                      String     url= MyOkHttpUtils.BaseUrl+"/Json/Get_Receiv_Master.aspx?page="+PageIndex+"&rows=20";

                        Response response=MyOkHttpUtils.okHttpGet(url);

                        Message msg=Message.obtain();

                        if(response!=null){

                            try {
                                String meetStr=response.body().string();

                                if(!TextUtils.equals(meetStr,"[]")&&!TextUtils.equals(meetStr,"[null]")){

                                    Gson gson=new Gson();
                                    msg.obj=gson.fromJson(meetStr,new TypeToken<List<YdcListInfo>>(){}.getType());
                                    msg.what=SUCCEED;
                                }else{
                                    msg.what=SUCCEED_NODATA;
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

    private void LvSetAdapter(List<YdcListInfo> list){

        if(adapter==null){

            adapter=new CommonAdapter<YdcListInfo>(mContext,list,R.layout.ydc_list_item) {
                @Override
                public void convert(CommonViewHolder holder, YdcListInfo item, int position) {

                    TextView tvNo=holder.getView(R.id.tv_no_ydc_list_item);
                    tvNo.setText((position+1)+"");
                    TextView tvDate=holder.getView(R.id.tv_date_ydc_list_item);
                    tvDate.setText(MyDateUtils.stringToYMDHMS(item.getRECEIV_TIME()));

                    LinearLayout ll = holder.getView(R.id.item_ydc_list_ll);

                    if (position % 2 == 0){
                        ll.setBackgroundResource(R.drawable.selector_questionnaire_click_blue);
                    }else {
                        ll.setBackgroundResource(R.drawable.selector_questionnaire_click_white);
                    }
                }
            };

            lv.setAdapter(adapter);

        }else{
            adapter.notifyDataSetChanged();
        }
        if(lv.isRefreshing()) {
            lv.onRefreshComplete();//停止刷新或加载更多
        }
    }

    private void getAnswerInfo(final int i){//获取答案信息

        showMyProgressDialog(mContext);

        new Thread(


                new Runnable() {
                    @Override
                    public void run() {

                        //  http://183.194.4.58:89/Json/Get_Qa_Receiv_Special.aspx?Receiv_Master_id=1
                        String     url= MyOkHttpUtils.BaseUrl+"/Json/Get_Qa_Receiv_Special.aspx?Receiv_Master_id="+i;

                        Response response=MyOkHttpUtils.okHttpGet(url);

                        Message msg=Message.obtain();

                        if(response!=null){

                            try {
                                String meetStr=response.body().string();

                                if(!TextUtils.equals(meetStr,"[]")&&!TextUtils.equals(meetStr,"[null]")){

                                    Gson gson=new Gson();
                                    msg.obj=gson.fromJson(meetStr,new TypeToken<List<NaireAnswerInfo>>(){}.getType());
                                    msg.what=SUCCEED_ANSWER;
                                }else{
                                    msg.what=SUCCEED_ANSWER_NODATA;
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

}

