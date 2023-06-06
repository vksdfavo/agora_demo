package com.example.newdemo;

public class ModalClass {
    String id, token,ids, channel,
            callingFrom,
            callingTo,
            status
                    ;

    public ModalClass(String id, String token, String ids, String channel, String callingFrom, String callingTo, String status) {
        this.id = id;
        this.token = token;
        this.ids = ids;
        this.channel = channel;
        this.callingFrom = callingFrom;
        this.callingTo = callingTo;
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCallingFrom() {
        return callingFrom;
    }

    public void setCallingFrom(String callingFrom) {
        this.callingFrom = callingFrom;
    }

    public String getCallingTo() {
        return callingTo;
    }

    public void setCallingTo(String callingTo) {
        this.callingTo = callingTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ModalClass() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
