package com.golub.golubroman.innotechtest.Start.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Model {

    @SerializedName("time")
    @Expose
    private String time;

    public Model(String time){
        this.time = time;
    }

    public String getData() {
        return time;
    }

    public void setData(String time) {
        this.time = time;
    }

}