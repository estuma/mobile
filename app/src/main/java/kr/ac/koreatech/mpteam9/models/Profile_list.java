package kr.ac.koreatech.mpteam9.models;

public class Profile_list {
    private String uid;
    private String nickname;
    private String email;
    private String name;
    private String Building;
    private String joinDate;
    private String BuildingIndex;
    private String pw;
    private String image;
    private String userVertification;

    public Profile_list(){};

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuilding() {
        return Building;
    }

    public void setBuilding(String Building) {
        this.Building = Building;
    }

    public void setBuildingIndex(String building_index) { this.BuildingIndex = building_index; }

    public String getBuildingIndex() { return BuildingIndex; }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public String getPw() { return pw; }

    public void setPw(String pw) { this.pw = pw; }

    public String getImage() { return image; }

    public void setImage(String image){ this.image = image; }

    public void setUserVertification(String userVertification) { this.userVertification = userVertification; }

    public String getUserVertification(){ return userVertification; }

    public Profile_list(String nickname, String email, String name, String Building, String BuildingIndex,
                        String pw, String joinDate, String image, String userVertification){
        this.nickname = nickname;
        this.email = email;
        this.name = name;
        this.Building = Building;
        this.BuildingIndex = BuildingIndex;
        this.pw = pw;
        this.joinDate = joinDate;
        this.image = image;
        this.userVertification = userVertification;
    }
}
