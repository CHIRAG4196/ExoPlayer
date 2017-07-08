package com.example.chirag.exoplayer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MainResponse {

    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("data")
    @Expose
    private ArrayList<Song> data = null;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public ArrayList<Song> getData() {
        return data;
    }

    public void setData(ArrayList<Song> data) {
        this.data = data;
    }
}