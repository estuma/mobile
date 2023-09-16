package kr.ac.koreatech.mpteam9.models;

import android.widget.ImageView;

public class ModelTradingPostList {
    //use same name as we givne while uploading post
    String pId, pTitle, pTime;
    String uId, uDp, uName;
    String pDorm, pKind, pState;

    public ModelTradingPostList(){

    }

    public ModelTradingPostList(String pId, String pTitle, String pTime, String uId, String uDp, String uName, String pDorm, String pKind, String pState) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pTime = pTime;
        this.uId = uId;
        this.uDp = uDp;
        this.uName = uName;
        this.pDorm = pDorm;
        this.pKind = pKind;
        this.pState = pState;
    }

    public ModelTradingPostList(String pId, String pTitle, String pDorm, String pKind, String pState) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDorm = pDorm;
        this.pKind = pKind;
        this.pState = pState;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getpDorm() {
        return pDorm;
    }

    public void setpDorm(String pDorm) {
        this.pDorm = pDorm;
    }

    public String getpKind() {
        return pKind;
    }

    public void setpKind(String pKind) {
        this.pKind = pKind;
    }

    public String getpState() {
        return pState;
    }

    public void setpState(String pState) {
        this.pState = pState;
    }
}