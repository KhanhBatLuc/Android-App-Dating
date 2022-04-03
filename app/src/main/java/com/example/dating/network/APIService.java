package com.example.dating.network;

import com.example.dating.application.AppConfig;
import com.example.dating.model.Match;
import com.example.dating.model.Room;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {

    @POST(AppConfig.URL_AUTH_LOGIN)
    Call<ResponseMain> requestPostLogin(@Body User user);

    @POST(AppConfig.URL_AUTH_REGISTER)
    Call<ResponseMain> requestPostRegister(@Body User user);

    @POST(AppConfig.URL_USER_PROFILE)
    Call<ResponseMain> requestPostProfile(@Header("Authorization") String token, @Body User user);


    @POST(AppConfig.URL_USER_LOCATION)
    Call<ResponseMain> requestPostLocation(@Header("Authorization") String token, @Body User user);

    @POST(AppConfig.URL_USER_LOGOUT)
    Call<ResponseMain> requestPostLogout(@Header("Authorization") String token);

    @POST(AppConfig.URL_USER_DELETE)
    Call<ResponseMain> requestPostDeleteAccount(@Header("Authorization") String token);

    @GET(AppConfig.URL_USER_ACCOUNT)
    Call<ResponseMain> requestGetAccount(@Header("Authorization") String token);

    @Multipart
    @POST(AppConfig.URL_USER_AVATAR)
    Call<ResponseMain> requestPostAvatar(@Header("Authorization") String token, @Part MultipartBody.Part image, @Part("file") RequestBody name);

    @Multipart
    @POST(AppConfig.URL_USER_PHOTO_PROFILE)
    Call<ResponseMain> requestPostPhoto(@Header("Authorization") String token, @Part MultipartBody.Part image, @Part("file") RequestBody name, @Part("sort") int sort);

    @GET(AppConfig.URL_USER_PROFILE)
    Call<ResponseMain> requestGetProfile(@Header("Authorization") String token);

    @GET(AppConfig.URL_USER_LIST)
    Call<ResponseMain> requestGetUserList(@Header("Authorization") String token);

    @GET(AppConfig.URL_USER_NOTIFY)
    Call<ResponseMain> requestGetUserListNotify(@Header("Authorization") String token);

    @POST(AppConfig.URL_USER_DETAIL)
    Call<ResponseMain> requestGetUserDetail(@Header("Authorization") String token, @Body User user);

    @POST(AppConfig.URL_USER_MATCH)
    Call<ResponseMain> requestPostMatch(@Header("Authorization") String token, @Body Match match);

    @POST(AppConfig.URL_USER_UPDATE_MATCH)
    Call<ResponseMain> requestPostUpdateMatch(@Header("Authorization") String token, @Body Match match);

    @GET(AppConfig.URL_USER_MATCHED)
    Call<ResponseMain> requestGetUserMatched(@Header("Authorization") String token);

    @GET(AppConfig.URL_LIST_ROOM)
    Call<ResponseMain> requestGetRooms(@Header("Authorization") String token);

    @GET(AppConfig.URL_ROOM_ONE)
    Call<ResponseMain> requestGetRoom(@Header("Authorization") String Authorization, @Path("room") String room);

    @GET(AppConfig.URL_USER_USERNAME)
    Call<ResponseMain> requestGetUserName(@Header("Authorization") String Authorization, @Path("name") String name);

    @POST(AppConfig.URL_CREATE_ROOM)
    Call<ResponseMain> requestPostCreateRoom(@Header("Authorization") String Authorization, @Body Room room);

    @POST(AppConfig.URL_USER_FCM_KEY)
    Call<ResponseMain> requestGetUpdateFcmKey(@Header("Authorization") String Authorization, @Body User user);

//    @Multipart
//    @POST(AppConfig.URL_USER_PHOTO_PROFILE)
//    Call<ResponseMain> requestPostPhoto(@Header("Authorization") String token, @Part MultipartBody.Part image, @Part("file") RequestBody name, @Part("sort") int sort);

}
