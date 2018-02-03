package com.youli.companybigwenjuan.entity;

/**
 * Created by liutao on 2018/2/1.
 *
 *
 *  Get_Receiv_Master.aspx 已完成列表,显示提交时间 参数 page,rows
 //   http://183.194.4.58:89/Json/Get_Receiv_Master.aspx?page=0&rows=20
 */
//[{"ID":1,"RECEIV_STAFF":1,"RECEIV_TIME":"2018-01-31T19:42:08.61","GPS":"121.43322893,31.29547694","RecordCount":1}]
public class YdcListInfo {


    /**
     * ID : 1
     * RECEIV_STAFF : 1
     * RECEIV_TIME : 2018-01-31T19:42:08.61
     * GPS : 121.43322893,31.29547694
     * RecordCount : 1
     */

    private int ID;
    private int RECEIV_STAFF;
    private String RECEIV_TIME;//完成时间
    private String GPS;
    private int RecordCount;

    public YdcListInfo(String RECEIV_TIME) {
        this.RECEIV_TIME = RECEIV_TIME;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getRECEIV_STAFF() {
        return RECEIV_STAFF;
    }

    public void setRECEIV_STAFF(int RECEIV_STAFF) {
        this.RECEIV_STAFF = RECEIV_STAFF;
    }

    public String getRECEIV_TIME() {
        return RECEIV_TIME;
    }

    public void setRECEIV_TIME(String RECEIV_TIME) {
        this.RECEIV_TIME = RECEIV_TIME;
    }

    public String getGPS() {
        return GPS;
    }

    public void setGPS(String GPS) {
        this.GPS = GPS;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }
}
