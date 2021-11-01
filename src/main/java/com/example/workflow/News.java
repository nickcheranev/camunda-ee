package com.example.workflow;

import java.io.Serializable;

public class News implements Serializable {
    static final long serialVersionUID = 1L;

    private int newsid;
    private String value;

    public int getNewsid() {
        return newsid;
    }

    public void setNewsid(int newsid) {
        this.newsid = newsid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
