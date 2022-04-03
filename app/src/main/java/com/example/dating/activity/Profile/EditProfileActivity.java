package com.example.dating.activity.Profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.example.dating.R;
import com.example.dating.application.AppConfig;
import com.example.dating.databinding.ActivityEditProfileBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Photo;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    ActivityEditProfileBinding binding;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    //firebase
    private static final int REQUEST_PERMISSION_SETTING = 101;

    ImageButton back;
    TextView toolbartag;
    ImageView imageView;
    Bitmap myBitmap;
    String[] permissionsRequired = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Context mContext = EditProfileActivity.this;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private User user;
    private List<Photo> photoList;
    private String token = null;
    private String id = null;
    private int sort;
    private RequestBody reqFile;
    private MultipartBody.Part body;
    private RequestBody name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                id = new SessionManager(EditProfileActivity.this).getUniqueIdentifier();
                token = new SessionManager(EditProfileActivity.this).getAccessToken();
                requestGetProfile();
            }
        }, 500);


        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        requestMultiplePermissions();

        toolbartag = findViewById(R.id.toolbartag);
        toolbartag.setText("Thông tin người dùng");

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.womanButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                user.setSex("Nữ");
                woman();
            }
        });

        binding.manButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                user.setSex("Nam");
                man();
            }
        });

        binding.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = binding.imageView1;
                sort = 1;
                proceedAfterPermission();

            }
        });
        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = binding.imageView2;
                sort = 2;
                proceedAfterPermission();

            }
        });

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = binding.imageView3;
                sort = 3;
                proceedAfterPermission();

            }
        });

        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = binding.imageView4;
                sort = 4;
                proceedAfterPermission();

            }
        });

        binding.imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = binding.imageView5;
                sort = 5;
                proceedAfterPermission();

            }
        });

        binding.imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView = binding.imageView6;
                sort = 6;
                proceedAfterPermission();

            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPostProfile();
            }
        });


    }


    private boolean checkSelfPermission() {
        return ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(EditProfileActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED;
    }


    private void requestMultiplePermissions() {
        if (checkSelfPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this, permissionsRequired[2])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Cấp quyền cho ứng dụng");
                builder.setMessage("Ứng dụng cần được cấp quyền Camera và Vị trí.");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(EditProfileActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)
                    || permissionStatus.getBoolean(permissionsRequired[1], false)
                    || permissionStatus.getBoolean(permissionsRequired[2], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                builder.setTitle("Cấp quyền cho ứng dụng");
                builder.setMessage("Ứng dụng cần được cấp quyền Camera và Vị trí.");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Cấp quyền Camera và Vị trí.", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(EditProfileActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            // txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.putBoolean(permissionsRequired[1], true);
            editor.putBoolean(permissionsRequired[2], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {


        final CharSequence[] options = {"Chụp ảnh", "Chọn ảnh từ bộ sưu tập", "Hủy"};


        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);

        builder.setTitle("Tải ảnh!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Chụp ảnh")) {

                    cameraIntent();

                } else if (options[item].equals("Chọn ảnh từ bộ sưu tập")) {

                    galleryIntent();


                } else if (options[item].equals("Hủy")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (checkSelfPermission()) {
                //Got Permission
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

        uploadImage(thumbnail);

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                uploadImage(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        imageView.setImageBitmap(bm);
    }

    private void uploadImage(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
        imageView.setImageBitmap(thumbnail);
        File destination = new File(Environment.getExternalStorageDirectory(), id + System.currentTimeMillis() + ".png");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();

            reqFile = RequestBody.create(MediaType.parse("image/*"), destination);
            body = MultipartBody.Part.createFormData("file", destination.getName(), reqFile);
            name = RequestBody.create(MediaType.parse("text/plain"), "file");
            requestPostPhoto();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (checkSelfPermission()) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }


    private void requestPostPhoto() {
        RetrofitClient.getNetworkConfiguration().requestPostPhoto("Bearer " + token, body, name, sort).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        user = data.getData().getUser();
                        Toast.makeText(EditProfileActivity.this, data.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(EditProfileActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostPhoto", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestPostPhoto", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(EditProfileActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("onFailure requestPostPhoto", t.getMessage());
            }
        });
    }

    private void requestGetProfile() {
        RetrofitClient.getNetworkConfiguration().requestGetProfile("Bearer " + token).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        photoList = data.getData().getPhotoList();
                        if (photoList.size() > 0)
                            getPhotos();


                        user = data.getData().getUser();
                        binding.setUser(user);

                        if(user.getSex().equalsIgnoreCase("Nam"))
                            man();
                        else
                            woman();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(EditProfileActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestGetProfile", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("IOException requestGetProfile", e.toString());
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(EditProfileActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("errorBody requestGetProfile", t.getMessage());
            }
        });
    }


    private void requestPostProfile() {
//        Log.e("requestPostProfile: user" , user.toString());
        RetrofitClient.getNetworkConfiguration().requestPostProfile("Bearer " + token, user).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    ResponseMain data = response.body();
                    if (data.getStatus() == 1) {
                        user = data.getData().getUser();

                        Toast.makeText(EditProfileActivity.this, data.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        String err = response.errorBody().string();
                        Log.e("errorBody", err);
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(err, ResponseMain.class);
//                        Toast.makeText(EditProfileActivity.this, errorData.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostProfile", errorData.getMessage());
                    } catch (IOException e) {
//                        Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("errorBody requestPostProfile", e.toString());
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(EditProfileActivity.this, "Sự cố đã xảy ra: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("errorBody requestPostProfile", t.getMessage());

            }
        });
    }

    private void getPhotos() {
        for (Photo p : photoList)
            switch (Integer.parseInt(p.getSort())) {
                case 1:
                    Glide.with(mContext).load(AppConfig.IMAGE_URL + p.getName()).into(binding.imageView1);
                    break;
                case 2:
                    Glide.with(mContext).load(AppConfig.IMAGE_URL + p.getName()).into(binding.imageView2);
                    break;
                case 3:
                    Glide.with(mContext).load(AppConfig.IMAGE_URL + p.getName()).into(binding.imageView3);
                    break;
                case 4:
                    Glide.with(mContext).load(AppConfig.IMAGE_URL + p.getName()).into(binding.imageView4);
                    break;
                case 5:
                    Glide.with(mContext).load(AppConfig.IMAGE_URL + p.getName()).into(binding.imageView5);
                    break;
                case 6:
                    Glide.with(mContext).load(AppConfig.IMAGE_URL + p.getName()).into(binding.imageView6);
                    break;
            }
    }

    private void man(){
        binding.manText.setTextColor(R.color.colorPrimaryDark);
        binding.manButton.setBackgroundResource(R.drawable.ic_check_select);
        binding.womanText.setTextColor(R.color.black);
        binding.womanButton.setBackgroundResource(R.drawable.ic_check_unselect);
    }

    private void woman(){
        binding.womanText.setTextColor(R.color.colorPrimaryDark);
        binding.womanButton.setBackgroundResource(R.drawable.ic_check_select);
        binding.manText.setTextColor(R.color.black);
        binding.manButton.setBackgroundResource(R.drawable.ic_check_unselect);
    }

}
