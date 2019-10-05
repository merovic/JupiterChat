package jupiter.amirahmed.com.jupiterchat.Models;

import java.util.Date;

public class ConversationItem {

    private String name;
    private String sender;
    private String receiver;
    private String receiverImage;
    private String lastMessage;
    private Date timeStamp;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    //@ServerTimestamp
    public Date getTimestamp() { return timeStamp; }

    public void setTimestamp(Date timestamp) { timeStamp = timestamp; }
}
