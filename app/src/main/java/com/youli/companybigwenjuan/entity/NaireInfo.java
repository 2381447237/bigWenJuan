package com.youli.companybigwenjuan.entity;

import java.io.Serializable;

/**
 * Created by liutao on 2018/1/31.
 *
 * http://183.194.4.58:82/Json/Get_Qa_Detil_Special.aspx
 *
 * 问卷信息
 */

public class NaireInfo implements Serializable{


    /**
     * ID : 1
     * TITLE_L : 上级主管部门：
     * TITLE_R :
     * CODE :
     * ORDER_V : 1
     * INPUT : false
     * INPUT_TYPE : 单选
     * JUMP_CODE :
     * PARENT_ID : 0
     * MASTER_ID : null
     * REMOVE_CODE :
     * TYPE_ID : 1
     * BINDINFO :
     * WIDTH : 0
     * TITLE_TOP :
     * OUT_VALUE :
     * BINDINFO_INPUT :
     * STAFF_ID : 1
     * RecordCount : 0
     * TreeLevel : 0
     */

    private int ID;
    private String TITLE_L;
    private String TITLE_R;
    private String CODE;
    private int ORDER_V;
    private boolean INPUT;
    private String INPUT_TYPE;
    private String JUMP_CODE;
    private int PARENT_ID;
    private Object MASTER_ID;
    private String REMOVE_CODE;
    private int TYPE_ID;
    private String BINDINFO;
    private int WIDTH;
    private String TITLE_TOP;
    private String OUT_VALUE;
    private String BINDINFO_INPUT;
    private int STAFF_ID;
    private int RecordCount;
    private int TreeLevel;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTITLE_L() {
        return TITLE_L;
    }

    public void setTITLE_L(String TITLE_L) {
        this.TITLE_L = TITLE_L;
    }

    public String getTITLE_R() {
        return TITLE_R;
    }

    public void setTITLE_R(String TITLE_R) {
        this.TITLE_R = TITLE_R;
    }

    public String getCODE() {
        return CODE;
    }

    public void setCODE(String CODE) {
        this.CODE = CODE;
    }

    public int getORDER_V() {
        return ORDER_V;
    }

    public void setORDER_V(int ORDER_V) {
        this.ORDER_V = ORDER_V;
    }

    public boolean isINPUT() {
        return INPUT;
    }

    public void setINPUT(boolean INPUT) {
        this.INPUT = INPUT;
    }

    public String getINPUT_TYPE() {
        return INPUT_TYPE;
    }

    public void setINPUT_TYPE(String INPUT_TYPE) {
        this.INPUT_TYPE = INPUT_TYPE;
    }

    public String getJUMP_CODE() {
        return JUMP_CODE;
    }

    public void setJUMP_CODE(String JUMP_CODE) {
        this.JUMP_CODE = JUMP_CODE;
    }

    public int getPARENT_ID() {
        return PARENT_ID;
    }

    public void setPARENT_ID(int PARENT_ID) {
        this.PARENT_ID = PARENT_ID;
    }

    public Object getMASTER_ID() {
        return MASTER_ID;
    }

    public void setMASTER_ID(Object MASTER_ID) {
        this.MASTER_ID = MASTER_ID;
    }

    public String getREMOVE_CODE() {
        return REMOVE_CODE;
    }

    public void setREMOVE_CODE(String REMOVE_CODE) {
        this.REMOVE_CODE = REMOVE_CODE;
    }

    public int getTYPE_ID() {
        return TYPE_ID;
    }

    public void setTYPE_ID(int TYPE_ID) {
        this.TYPE_ID = TYPE_ID;
    }

    public String getBINDINFO() {
        return BINDINFO;
    }

    public void setBINDINFO(String BINDINFO) {
        this.BINDINFO = BINDINFO;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public void setWIDTH(int WIDTH) {
        this.WIDTH = WIDTH;
    }

    public String getTITLE_TOP() {
        return TITLE_TOP;
    }

    public void setTITLE_TOP(String TITLE_TOP) {
        this.TITLE_TOP = TITLE_TOP;
    }

    public String getOUT_VALUE() {
        return OUT_VALUE;
    }

    public void setOUT_VALUE(String OUT_VALUE) {
        this.OUT_VALUE = OUT_VALUE;
    }

    public String getBINDINFO_INPUT() {
        return BINDINFO_INPUT;
    }

    public void setBINDINFO_INPUT(String BINDINFO_INPUT) {
        this.BINDINFO_INPUT = BINDINFO_INPUT;
    }

    public int getSTAFF_ID() {
        return STAFF_ID;
    }

    public void setSTAFF_ID(int STAFF_ID) {
        this.STAFF_ID = STAFF_ID;
    }

    public int getRecordCount() {
        return RecordCount;
    }

    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }

    public int getTreeLevel() {
        return TreeLevel;
    }

    public void setTreeLevel(int TreeLevel) {
        this.TreeLevel = TreeLevel;
    }
}
