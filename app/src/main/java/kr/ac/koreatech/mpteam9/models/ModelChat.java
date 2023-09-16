package kr.ac.koreatech.mpteam9.models;

public class ModelChat {

    private String message, receiver, sender, timestamp;
    private boolean isSeen;

    public ModelChat() {}
    public ModelChat(String message, String receiver, String sender,
                     String timestamp, boolean isSeen)
    {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setMessage(String message) {
        this.message=message;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
