package com.example.dating.activity;

import com.example.dating.R;
import com.example.dating.Utils.JavaScriptAction;
import com.example.dating.activity.Matched.Matched_Activity;
import com.example.dating.adapter.AdapterMessage;
import com.example.dating.application.AppConfig;
import com.example.dating.application.Functions;
import com.example.dating.databinding.ActivityChatBinding;
import com.example.dating.dataholder.SessionManager;
import com.example.dating.model.Message;
import com.example.dating.model.Room;
import com.example.dating.model.User;
import com.example.dating.model.response.ResponseMain;
import com.example.dating.network.RetrofitClient;
import com.example.dating.service.SocketIOService;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

public class ChatBoxActivity extends AppCompatActivity {
    private static final String TAG = ChatBoxActivity.class.getSimpleName();
    public static final String ON_MESSAGE_RECEIVED = "com.example.dating.activity.ON_MESSAGE_RECEIVED";
    public RecyclerView myRecylerView;
    public List<Message> MessageList;
    public AdapterMessage chatBoxAdapter;
    private Context curent_context = this;
    private Socket socket;
    ActivityChatBinding binding;
    public String roomId;
    private Room room;
    private String imageString;

    public static final int TEXT = 0, PICTURE = 1, VIDEO = 3;

    public static final int SENT = 1, READ = 2, WAIT = 3, ACCEPT = 4, DENY = 5, STOP = 6;

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    Bitmap myBitmap;

    String[] permissionsRequired = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };

    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, count = 0;
    private String userChoosenTask;
    private String token = null;
    private String id = "";
    private String video_id = "";
    private int status = 100;
    private Boolean isPeerConnected = false;
    private Boolean isAudio = true;
    private Boolean isVideo = true;
    private Vibrator vibs = null;


    public static void start(Context context, String room) {
        Intent starter = new Intent(context, ChatBoxActivity.class);
        starter.putExtra("room", room);
        context.startActivity(starter);
    }

    private BroadcastReceiver onMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.e("onMessageReceiver: ", intent.getExtras().getString("message"));
            Gson gson = new Gson();
            Message m = gson.fromJson(intent.getExtras().getString("message"), Message.class);
            Log.e("onMessageReceiver: ", m.toString());
            if (m.getContentType() != VIDEO) {
                if (!id.equalsIgnoreCase(m.getId())) {

                    MessageList.add(m);
                    // add the new updated list to the adapter
                    // notify the adapter to update the recycler view
                    chatBoxAdapter.notifyItemInserted(MessageList.size());
                    //set the adapter for the recycler view
                    myRecylerView.smoothScrollToPosition(MessageList.size());

                    id = m.getId();
                }


            } else {
                switch (m.getReadStatus()) {
                    case WAIT:
                        onCallRequest(m.getFrom().getId(), m.getFrom().getName(), m.getFrom().getAvatar());
                        video_id = m.getId();
//                            status = WAIT;
                        Log.i("onMessageReceiver WAIT  ", video_id);
                        break;
                    case ACCEPT:
                        switchToControls();
                        String startCall = "javascript:startCall('" + m.getFrom().getId() + "')";
                        callJavascriptFunction(startCall);
                        Log.i("onMessageReceiver ACCEPT  ", video_id);
                        break;
                    case DENY:
                        switchToDeny();
                        Log.i("onMessageReceiver DENY  ", video_id);

//                        if (!video_id.equalsIgnoreCase(m.getId())) {
                            MessageList.add(m);
                            // add the new updated list to the adapter
                            // notify the adapter to update the recycler view
                            chatBoxAdapter.notifyItemInserted(MessageList.size());
                            //set the adapter for the recycler view
                            myRecylerView.smoothScrollToPosition(MessageList.size());
//                        }
                        break;
                    case STOP:
                        String toggleStop = "javascript:closePeer()";
                        callJavascriptFunction(toggleStop);
                        binding.webView.loadUrl("about:blank");
                        switchToDeny();
//                        setupWebView();

                        binding.webView.loadUrl("file:///android_asset/call.html");
                        String init = "javascript:init('" + new SessionManager(ChatBoxActivity.this).getUniqueIdentifier() + "');";
                        binding.webView.loadUrl(init);

                        Log.i("onMessageReceiver STOP  ", video_id);
//                        if (!video_id.equalsIgnoreCase(m.getId())) {
                            MessageList.add(m);
                            // add the new updated list to the adapter
                            // notify the adapter to update the recycler view
                            chatBoxAdapter.notifyItemInserted(MessageList.size());
                            //set the adapter for the recycler view
                            myRecylerView.smoothScrollToPosition(MessageList.size());
//                        }
                        break;
                }
            }

            User user = m.getFrom();
            if (!user.getId().equalsIgnoreCase(new SessionManager(ChatBoxActivity.this).getUniqueIdentifier()))
                binding.txtToolbarStatus.setText(user.getGetLastSeen());
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(onMessageReceiver, new IntentFilter(ON_MESSAGE_RECEIVED));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        binding.toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        vibs = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        requestMultiplePermissions();


        Gson gson = new Gson();
        room = gson.fromJson(getIntent().getExtras().getString("room"), Room.class);
        joinRoom();

        User user = room.getAdmin();
        if (user.getId().equalsIgnoreCase(new SessionManager(ChatBoxActivity.this).getUniqueIdentifier()))
            user = room.getUser();

        binding.txtToolbarTitle.setText(user.getName());
        binding.txtToolbarStatus.setText(user.getGetLastSeen());
        Picasso.get().load(AppConfig.IMAGE_URL + user.getAvatar()).into(binding.imgAvatar);

        //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = findViewById(R.id.recyclerMessageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        chatBoxAdapter = new AdapterMessage(MessageList, ChatBoxActivity.this);
        myRecylerView.setAdapter(chatBoxAdapter);
        requestRoom();
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                if (!binding.edMessage.getText().toString().isEmpty()) {
                    sendMessage(binding.edMessage.getText().toString(), TEXT, SENT);
                    binding.edMessage.setText("");
                }
            }
        });

        binding.imgArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toggleStop = "javascript:closePeer()";
                callJavascriptFunction(toggleStop);
                binding.webView.loadUrl("about:blank");
                isPeerConnected = false;

                Intent intent = new Intent(getApplicationContext(), Matched_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("binding.btnImage.setOnClickListener ", "proceedAfterPermission");
                proceedAfterPermission();
            }
        });

        binding.toggleAudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAudio = !isAudio;
                String toggleAudio = "javascript:toggleAudio('" + isAudio + "')";
                callJavascriptFunction(toggleAudio);
                Log.i("toggleAudio ", toggleAudio);
                binding.toggleAudioBtn.setImageResource(isAudio ? R.drawable.mic_on : R.drawable.mic_off);
            }
        });

        binding.toggleVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVideo = !isVideo;
                String toggleVideo = "javascript:toggleVideo('" + isVideo + "')";
                callJavascriptFunction(toggleVideo);
                Log.i("toggleVideo ", toggleVideo);
                binding.toggleVideoBtn.setImageResource(isVideo ? R.drawable.video_camera_on : R.drawable.video_camera_off);
            }
        });

        binding.toggleStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String toggleStop = "javascript:closePeer()";
                callJavascriptFunction(toggleStop);
                binding.webView.loadUrl("about:blank");
                switchToDeny();
//                if (status != WAIT) {
                sendMessage(video_id, VIDEO, STOP);
//                } else {
//                    status = 100;
//                    sendMessage(video_id, VIDEO, DENY);
//                }

//                setupWebView();
                binding.webView.loadUrl("file:///android_asset/call.html");
                String init = "javascript:init('" + new SessionManager(ChatBoxActivity.this).getUniqueIdentifier() + "');";
                binding.webView.loadUrl(init);
                Log.i("send stop  ", video_id);
            }
        });

        binding.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCallRequest();
            }
        });


        binding.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibs.cancel();
                binding.callLayout.setVisibility(View.GONE);
                switchToControls();
                sendMessage(video_id, VIDEO, ACCEPT);
                Log.i("send accept  ", video_id);
            }
        });

        binding.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibs.cancel();
                switchToDeny();
                sendMessage(video_id, VIDEO, DENY);
                Log.i("send reject  ", video_id);
            }
        });

        setupWebView();
    }

    @Override
    public void onBackPressed() {
        String toggleStop = "javascript:closePeer()";
        callJavascriptFunction(toggleStop);
        binding.webView.loadUrl("about:blank");
        isPeerConnected = false;

        Intent intent = new Intent(getApplicationContext(), Matched_Activity.class);
        startActivity(intent);
        finish();
    }

    private void joinRoom() {
        Log.e("joinRoom", "joinRoom");
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_ROOM_ID, room.getId());
        //gửi tiêu đề + nd sang bên khác  "abc", 'sgsdfsdfsdfdsds'
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        startService(service);
    }

    private void sendMessage(String content, int content_type, int read_status) {
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("from", new SessionManager(ChatBoxActivity.this).getUniqueIdentifier());
            mainObj.put("room", room.getId());
            mainObj.put("content", content);
            mainObj.put("content_type", content_type);
            mainObj.put("read_status", read_status);
            mainObj.put("event_type", 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        Toast.makeText(ChatBoxActivity.this, "mainObj.toString(): " + mainObj.toString(), Toast.LENGTH_SHORT).show();
        Log.e("mainObj.toString(): ", mainObj.toString());
        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_MESSAGE);
        service.putExtra(SocketIOService.EXTRA_DATA, mainObj.toString());
        startService(service);
    }

    private void requestRoom() {
        RetrofitClient.getNetworkConfiguration().requestGetRoom("Bearer " + new SessionManager(this).getAccessToken(), room.getId()).enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.isSuccessful()) {
                    Room room = response.body().getData().getRoom();
                    List<Message> messages = response.body().getData().getMessageList();
//                    Log.e("requestGetRoom messages : ", messages.toString());
                    MessageList.addAll(messages);
                    // add the new updated list to the adapter
                    // notify the adapter to update the recycler view
                    chatBoxAdapter.notifyItemInserted(MessageList.size());
                    //set the adapter for the recycler view
                    myRecylerView.smoothScrollToPosition(MessageList.size());

                } else {
                    try {
                        Gson gson = new Gson();
                        ResponseMain errorData = gson.fromJson(response.errorBody().string(), ResponseMain.class);
//                        Toast.makeText(ChatBoxActivity.this, errorData.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("errorBody requestGetRoom", errorData.getMessage());
                    } catch (IOException e) {
                        Log.e("IOException requestGetRoom", e.toString());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
//                Toast.makeText(ChatBoxActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onFailure requestGetRoom", t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String toggleStop = "javascript:closePeer()";
        callJavascriptFunction(toggleStop);
        binding.webView.loadUrl("about:blank");
        this.isPeerConnected = false;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onMessageReceiver);
    }

    private boolean checkSelfPermission() {
        return ActivityCompat.checkSelfPermission(ChatBoxActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ChatBoxActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ChatBoxActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(ChatBoxActivity.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED;
    }

    private void requestMultiplePermissions() {
        if (checkSelfPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatBoxActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ChatBoxActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ChatBoxActivity.this, permissionsRequired[2])
                    || ActivityCompat.shouldShowRequestPermissionRationale(ChatBoxActivity.this, permissionsRequired[3])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatBoxActivity.this);
                builder.setTitle("Cấp quyền cho ứng dụng");
                builder.setMessage("Ứng dụng cần được cấp quyền Camera và Bộ nhớ.");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ChatBoxActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
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
                    || permissionStatus.getBoolean(permissionsRequired[2], false)
                    || permissionStatus.getBoolean(permissionsRequired[3], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ChatBoxActivity.this);
                builder.setTitle("Cấp quyền cho ứng dụng");
                builder.setMessage("Ứng dụng cần được cấp quyền Camera và Bộ nhớ.");
                builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
//                        Toast.makeText(getBaseContext(), "Cấp quyền Camera và Vị trí.", Toast.LENGTH_LONG).show();
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
                ActivityCompat.requestPermissions(ChatBoxActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            // txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.putBoolean(permissionsRequired[1], true);
            editor.putBoolean(permissionsRequired[2], true);
            editor.putBoolean(permissionsRequired[3], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {


        final CharSequence[] options = {"Chụp ảnh", "Chọn ảnh từ bộ sưu tập", "Hủy"};


        AlertDialog.Builder builder = new AlertDialog.Builder(ChatBoxActivity.this);

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
                Log.i("onActivityResult ", "proceedAfterPermission");
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Log.e("SELECT_FILE ", " SELECT_FILE ");
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                Log.e("REQUEST_CAMERA ", " REQUEST_CAMERA ");
                onCaptureImageResult(data);
            }
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
        ByteArrayOutputStream bytes = null;

        try {
            bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);

            byte[] imageBytes = bytes.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        } catch (Exception e) {

            Log.e("Exception uploadImage", e.toString());

        } catch (OutOfMemoryError e) {
            bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 50, bytes);

            byte[] imageBytes = bytes.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        }

        sendMessage(imageString, PICTURE, SENT);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (checkSelfPermission()) {
                //Got Permission
                Log.i("onPostResume ", "proceedAfterPermission");
                proceedAfterPermission();
            }
        }
    }


    //////////////////Video calll


    private void sendCallRequest() {
        if (!this.isPeerConnected) {
            Toast.makeText((Context) this, "Chưa có kết nối mạng", Toast.LENGTH_LONG).show();
            Log.e("sendCallRequest isPeerConnected ", this.isPeerConnected + " ");
            return;
        } else {
            switchToControls();
//            status = WAIT;
            sendMessage("", VIDEO, WAIT);
        }

    }


    private void setupWebView() {

        Log.e("setupWebView ", " setupWebView");
        binding.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webView.addJavascriptInterface(new JavaScriptAction(this), "Android");

        binding.webView.loadUrl("file:///android_asset/call.html");


        binding.webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                String init = "javascript:init('" + new SessionManager(ChatBoxActivity.this).getUniqueIdentifier() + "');";
                binding.webView.loadUrl(init);
                Log.e("init ", init);
            }
        });

//        isPeerConnected = true;
    }

    private void onCallRequest(String id, String name, String image) {

        binding.callLayout.setVisibility(View.VISIBLE);
        Picasso.get().load(AppConfig.IMAGE_URL + image).into(binding.imageIncoming);
        binding.incomingCallTxt.setText(name + " đang gọi bạn...");
        binding.pulsator.start();

        if (!id.equalsIgnoreCase(new SessionManager(this).getUniqueIdentifier())) {
            vibs.vibrate(3 * 60 * 1000); //3p
        }
    }

    private void switchToControls() {
        binding.chatLayout.setVisibility(View.GONE);
        binding.webView.setVisibility(View.VISIBLE);
        binding.callControlLayout.setVisibility(View.VISIBLE);
    }

    private void switchToDeny() {
        binding.chatLayout.setVisibility(View.VISIBLE);
        binding.webView.setVisibility(View.GONE);
        binding.callControlLayout.setVisibility(View.GONE);
        binding.callLayout.setVisibility(View.GONE);
    }


    private void callJavascriptFunction(String functionString) {
        binding.webView.post(new Runnable() {
            public final void run() {
                binding.webView.evaluateJavascript(functionString, null);
            }
        });
    }

    public void onPeerConnected() {
        this.isPeerConnected = true;
        Log.d("onPeerConnected ", "isPeerConnected " + this.isPeerConnected);
    }


}