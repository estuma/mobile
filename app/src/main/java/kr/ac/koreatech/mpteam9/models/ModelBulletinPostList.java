package kr.ac.koreatech.mpteam9.models;

public class ModelBulletinPostList {
    String pId, pTitle, pTime;
    String uId, uDp, uName;

    public ModelBulletinPostList(){

    }

    public ModelBulletinPostList(String pId, String pTitle, String pTime, String uId, String uDp, String uName) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pTime = pTime;
        this.uId = uId;
        this.uDp = uDp;
        this.uName = uName;
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
}