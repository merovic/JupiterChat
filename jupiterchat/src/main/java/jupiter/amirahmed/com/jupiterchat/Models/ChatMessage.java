package jupiter.amirahmed.com.jupiterchat.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage {

    private String message;
    private String user_image;
    private String sender;
    private Date timeStamp;

    public ChatMessage() {
        //empty constructor needed
    }

    public ChatMessage(String message, String user_image, String sender, Date timeStamp) {
        this.message = message;
        this.user_image = user_image;
        this.sender = sender;
        this.timeStamp = timeStamp;
    }

    public ChatMessage(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @ServerTimestamp
    public Date getTimestamp() { return timeStamp; }

    public void setTimestamp(Date timestamp) { timeStamp = timestamp; }
}