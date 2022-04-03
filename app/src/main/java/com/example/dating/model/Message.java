package com.example.dating.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Message  implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("index")
    @Expose
    private String index;

    @SerializedName("from")
    @Expose
    private User from;

    @SerializedName("room")
    @Expose
    private String room;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("content_type")
    @Expose
    private int contentType;

    @SerializedName("event_type")
    @Expose
    private int eventType;

    @SerializedName("read_status")
    @Expose
    private int readStatus;

    @SerializedName("updated_at")
    @Expose
    private long updatedAt;

    @SerializedName("created_at")
    @Expose
    private long createdAt;

    @SerializedName("get_created_at")
    @Expose
    private String createdAtTime;

    @SerializedName("get_updated_at")
    @Expose
    private String updatedAtTime;

    @SerializedName("duration")
    @Expose
    private String duration;

    public Message() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtTime() {
        return createdAtTime;
    }

    public void setCreatedAtTime(String createdAtTime) {
        this.createdAtTime = createdAtTime;
    }

    public String getUpdatedAtTime() {
        return updatedAtTime;
    }

    public void setUpdatedAtTime(String updatedAtTime) {
        this.updatedAtTime = updatedAtTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", index='" + index + '\'' +
                ", from=" + from +
                ", room='" + room + '\'' +
                ", content='" + content + '\'' +
                ", contentType=" + contentType +
                ", eventType=" + eventType +
                ", readStatus=" + readStatus +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", createdAtTime='" + createdAtTime + '\'' +
                ", updatedAtTime='" + updatedAtTime + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }
}