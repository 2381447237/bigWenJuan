package com.youli.companybigwenjuan.entity;

import java.io.Serializable;

/**
 * Created by liutao on 2018/2/1.
 *
 * Get_Qa_Receiv_Special.aspx 获取已完成明细 参数Receiv_Master_id
 * http://183.194.4.58:89/Json/Get_Qa_Receiv_Special.aspx?Receiv_Master_id=1
 *
 * 问卷答案
 */

public class NaireAnswerInfo implements Serializable{


    /**
     * ID : 1
     * RECEIV_MASTER_ID : 1
     * DETIL_ID : 7
     * INPUT_VALUE :
     * MASTER_ID : 1
     * RecordCount : 0
     */

    private int ID;
    private int RECEIV_MASTER_ID;
    private int DETIL_ID;//要这个
    private String INPUT_VALUE;//要这个
    private int MASTER_ID;
    private int RecordCount;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getRECEIV_MASTER_ID() {
        return RECEIV_MASTER_ID;
    }

    public void setRECEIV_MASTER_ID(int RECEIV_MASTER_ID) {
        this.RECEIV_MASTER_ID = RECEIV_MASTER_ID;
    }

    public int getDETIL_ID() {
        return DETIL_ID;
    }

    public void setDETIL_ID(int DETIL_ID) {
        this.DETIL_ID = DETIL_ID;
    }

    public String getINPUT_VALUE() {
        return INPUT_VALUE;
    }

    public void setINPUT_VALUE(String INPUT_VALUE) {
        this.INPUT_VALUE = INPUT_VALUE;
    }

    public int getMASTER_ID() {
        return MASTER_ID;
    }

    public void setMASTER_ID(int MASTER_ID) {
        this.MASTER_ID = MASTER_ID;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }

    @Override
    public String toString() {
        return "NaireAnswerInfo{" +
                "DETIL_ID=" + DETIL_ID +
                '}';
    }
}
