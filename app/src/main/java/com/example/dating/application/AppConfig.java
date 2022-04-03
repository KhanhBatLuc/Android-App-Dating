package com.example.dating.application;

public class AppConfig {
    public static final int RESPONSE_OK = 200;
    public static final int REQUEST_TIME_OUT = 200;
    public static final String BASE_URL = "http://192.168.100.7:3000";
    public static final String IMAGE_URL = BASE_URL + "/api/user/image/";
    public static final String URL_AUTH_LOGIN = "api/user/login";
    public static final String URL_USER_USERNAME = "api/user/{name}";
    public static final String URL_USER_FCM_KEY = "api/user/{fcm_key}";
    public static final String URL_LIST_ROOM = "api/user/list/room";
    public static final String URL_CREATE_ROOM = "api/user/create/room";
    public static final String URL_ROOM_ONE = "api/user/room/{room}";
    public static final String URL_AUTH_REGISTER = "api/user/register";
    public static final String URL_USER_PROFILE = "api/user/profile";
    public static final String URL_USER_LOCATION = "api/user/location";
    public static final String URL_USER_LOGOUT = "api/user/logout";
    public static final String URL_USER_DELETE = "api/user/delete";
    public static final String URL_USER_ACCOUNT = "api/user/account";
    public static final String URL_USER_AVATAR = "api/user/avatar";
    public static final String URL_USER_PHOTO_PROFILE = "api/user/photo";
    public static final String URL_USER_LIST = "api/user/list";
    public static final String URL_USER_DETAIL = "api/user/detail";
    public static final String URL_USER_MATCH = "api/user/match";
    public static final String URL_USER_UPDATE_MATCH = "api/user/update_match";
    public static final String URL_USER_NOTIFY = "api/user/notify";
    public static final String URL_USER_MATCHED = "api/user/matched";


}
