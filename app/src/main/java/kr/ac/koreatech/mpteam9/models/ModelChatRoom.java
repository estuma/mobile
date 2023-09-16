package kr.ac.koreatech.mpteam9.models;

public class ModelChatRoom {
    String id;

    public ModelChatRoom(String id) {
        this.id = id;
    }

    public ModelChatRoom() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
