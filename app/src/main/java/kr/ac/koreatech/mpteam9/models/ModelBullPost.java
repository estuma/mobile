package kr.ac.koreatech.mpteam9.models;

public class ModelBullPost {
    String pContent, pId, pImage, pTime, pTitle, uDp, uEmail, uName, uid;

    public ModelBullPost(String pContent, String pId, String pImage, String pTime, String pTitle, String uDp, String uEmail, String uName, String uid) {
        this.pContent = pContent;
        this.pId = pId;
        this.pImage = pImage;
        this.pTime = pTime;
        this.pTitle = pTitle;
        this.uDp = uDp;
        this.uEmail = uEmail;
        this.uName = uName;
        this.uid = uid;
    }

    public String getpContent() {
        return pContent;
    }

    public void setpContent(String pContent) {
        this.pContent = pContent;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
