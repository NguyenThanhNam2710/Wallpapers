
package com.example.wallpapers.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Galleries {

    @SerializedName("galleries")
    @Expose
    private Galleries_ galleries;
    @SerializedName("stat")
    @Expose
    private String stat;

    public Galleries_ getGalleries() {
        return galleries;
    }

    public void setGalleries(Galleries_ galleries) {
        this.galleries = galleries;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

}
