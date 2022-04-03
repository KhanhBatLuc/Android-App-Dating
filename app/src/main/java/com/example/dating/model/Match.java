package com.example.dating.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Match  implements Serializable {
    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("owner")
    @Expose
    private String owner;

    @SerializedName("other")
    @Expose
    private String other;

    @SerializedName("match")
    @Expose
    private int match;

    public Match() {
    }

    public Match(String other, int match) {
        this.other = other;
        this.match = match;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public int getMatch() {
        return match;
    }

    public void setMatch(int match) {
        this.match = match;
    }
}
