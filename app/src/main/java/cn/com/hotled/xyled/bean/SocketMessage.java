package cn.com.hotled.xyled.bean;

/**
 * Created by Lam on 2016/12/20.
 */

public class SocketMessage {
    String messageText;
    long msgTime;
    boolean success;
    boolean isServer;

    public SocketMessage( ) {

    }

    public SocketMessage(String messageText, long msgTime, boolean success, boolean isServer) {
        this.messageText = messageText;
        this.msgTime = msgTime;
        this.success = success;
        this.isServer = isServer;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isServer() {
        return isServer;
    }

    public void setServer(boolean server) {
        isServer = server;
    }

    @Override
    public String toString() {
        return "SocketMessage{" +
                "messageText='" + messageText + '\'' +
                ", msgTime=" + msgTime +
                ", success=" + success +
                ", isServer=" + isServer +
                '}';
    }
}
