package com.example.dating.Utils;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.example.dating.activity.ChatBoxActivity;

public class JavaScriptAction {
    private ChatBoxActivity activity;

    public JavaScriptAction(ChatBoxActivity activity) {
        this.activity = activity;
        Log.i("constructor  JavascriptInterface", "JavascriptInterface ");
    }

    @JavascriptInterface
    public void onPeerConnected() {
        Log.i("@android.webkit.JavascriptInterface ", "@android.webkit.JavascriptInterface ");
        this.activity.onPeerConnected();
    }
}
