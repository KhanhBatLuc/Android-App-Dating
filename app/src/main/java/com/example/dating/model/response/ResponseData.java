package com.example.dating.model.response;

import com.example.dating.model.Match;
import com.example.dating.model.Message;
import com.example.dating.model.Photo;
import com.example.dating.model.Room;
import com.example.dating.model.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseData {

    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("photos")
    @Expose
    private List<Photo> photoList;

    @SerializedName("match")
    @Expose
    private Match match;

    @SerializedName("matches")
    @Expose
    private List<Match> matches;

    @SerializedName("distance")
    @Expose
    private String distance;

    @SerializedName("room")
    @Expose
    private Room room;

    @SerializedName("rooms")
    @Expose
    private List<Room> roomList;

    @SerializedName("messages")
    @Expose
    private List<Message> messageList;

    @SerializedName("users")
    @Expose
    private List<User> usersList;

    public List<User> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
