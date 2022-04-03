package com.example.dating.model;

import androidx.databinding.BaseObservable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("bio")
    @Expose
    private String bio;

    @SerializedName("age")
    @Expose
    private int age;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("sex")
    @Expose
    private String sex;

    @SerializedName("preferSex")
    @Expose
    private String preferSex;

    @SerializedName("birth")
    @Expose
    private String birth;

    @SerializedName("job")
    @Expose
    private String job;

    @SerializedName("company")
    @Expose
    private String company;

    @SerializedName("school")
    @Expose
    private String school;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("height")
    @Expose
    private String height;

    @SerializedName("weight")
    @Expose
    private String weight;

    @SerializedName("literacy")
    @Expose
    private String literacy;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;






    @SerializedName("interests")
    @Expose
    private List<String> interests;

    @SerializedName("interestAgeMin")
    @Expose
    private int interestAgeMin;

    @SerializedName("interestAgeMax")
    @Expose
    private int interestAgeMax;

    @SerializedName("interestHeight")
    @Expose
    private int interestHeight;

    @SerializedName("interestWeight")
    @Expose
    private int interestWeight;

    @SerializedName("interestDistance")
    @Expose
    private int interestDistance;




    @SerializedName("alertMatch")
    @Expose
    private boolean alertMatch;

    @SerializedName("alertMessage")
    @Expose
    private boolean alertMessage;

    @SerializedName("alertLiked")
    @Expose
    private boolean alertLiked;

    @SerializedName("displayAge")
    @Expose
    private boolean displayAge;

    @SerializedName("displayDistance")
    @Expose
    private boolean displayDistance;


    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("fcm_key")
    @Expose
    private String fcmKey;

    @SerializedName("blocked")
    @Expose
    private boolean blocked;

    @SerializedName("device_model")
    @Expose
    private String deviceModel;

    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("passwordConfirm")
    @Expose
    private String passwordConfirm;

    @SerializedName("last_seen")
    @Expose
    private long lastSeen;

    @SerializedName("get_last_seen")
    @Expose
    private String getLastSeen;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("updated_at")
    @Expose
    private long updatedAt;

    @SerializedName("created_at")
    @Expose
    private long createdAt;

    public User() {
    }

    public User(String id, String name, int age, String avatar, String bio, List<String> interests, int distance) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.avatar = avatar;
        this.bio = bio;
        this.interests = interests;
        this.interestDistance = distance;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getInterests() {
        return interests.toString() //remove the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPreferSex() {
        return preferSex;
    }

    public void setPreferSex(String preferSex) {
        this.preferSex = preferSex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFcmKey() {
        return fcmKey;
    }

    public void setFcmKey(String fcmKey) {
        this.fcmKey = fcmKey;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }


    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getGetLastSeen() {
        return getLastSeen;
    }

    public void setGetLastSeen(String getLastSeen) {
        this.getLastSeen = getLastSeen;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLiteracy() {
        return literacy;
    }

    public void setLiteracy(String literacy) {
        this.literacy = literacy;
    }

    public boolean isAlertMatch() {
        return alertMatch;
    }

    public void setAlertMatch(boolean alertMatch) {
        this.alertMatch = alertMatch;
    }

    public boolean isAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(boolean alertMessage) {
        this.alertMessage = alertMessage;
    }

    public boolean isAlertLiked() {
        return alertLiked;
    }

    public void setAlertLiked(boolean alertLiked) {
        this.alertLiked = alertLiked;
    }

    public boolean isDisplayAge() {
        return displayAge;
    }

    public void setDisplayAge(boolean displayAge) {
        this.displayAge = displayAge;
    }

    public boolean isDisplayDistance() {
        return displayDistance;
    }

    public void setDisplayDistance(boolean displayDistance) {
        this.displayDistance = displayDistance;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getInterestAgeMin() {
        return interestAgeMin;
    }

    public void setInterestAgeMin(int interestAgeMin) {
        this.interestAgeMin = interestAgeMin;
    }

    public int getInterestAgeMax() {
        return interestAgeMax;
    }

    public void setInterestAgeMax(int interestAgeMax) {
        this.interestAgeMax = interestAgeMax;
    }

    public int getInterestHeight() {
        return interestHeight;
    }

    public void setInterestHeight(int interestHeight) {
        this.interestHeight = interestHeight;
    }

    public int getInterestWeight() {
        return interestWeight;
    }

    public void setInterestWeight(int interestWeight) {
        this.interestWeight = interestWeight;
    }

    public int getInterestDistance() {
        return interestDistance;
    }

    public void setInterestDistance(int interestDistance) {
        this.interestDistance = interestDistance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", bio='" + bio + '\'' +
                ", age=" + age +
                ", description='" + description + '\'' +
                ", sex='" + sex + '\'' +
                ", preferSex='" + preferSex + '\'' +
                ", birth='" + birth + '\'' +
                ", job='" + job + '\'' +
                ", company='" + company + '\'' +
                ", school='" + school + '\'' +
                ", address='" + address + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", literacy='" + literacy + '\'' +
                ", country='" + country + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", interests=" + interests +
                ", interestAgeMin=" + interestAgeMin +
                ", interestAgeMax=" + interestAgeMax +
                ", interestHeight=" + interestHeight +
                ", interestWeight=" + interestWeight +
                ", interestDistance=" + interestDistance +
                ", alertMatch=" + alertMatch +
                ", alertMessage=" + alertMessage +
                ", alertLiked=" + alertLiked +
                ", displayAge=" + displayAge +
                ", displayDistance=" + displayDistance +
                ", token='" + token + '\'' +
                ", fcmKey='" + fcmKey + '\'' +
                ", blocked=" + blocked +
                ", deviceModel='" + deviceModel + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirm='" + passwordConfirm + '\'' +
                ", lastSeen=" + lastSeen +
                ", avatar='" + avatar + '\'' +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                '}';
    }
}
