package com.example.newdemo;

public class ModalClass {
    String id, token,ids;

    public ModalClass(String id, String token) {
        this.id = id;
        this.token = token;
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
