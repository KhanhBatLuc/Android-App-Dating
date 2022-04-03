package com.example.dating.customfonts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

public class ButtonRobotoBold extends AppCompatButton {

    public ButtonRobotoBold(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font/RobotoBold.ttf");
        this.setTypeface(face);
    }

    public ButtonRobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font/RobotoBold.ttf");
        this.setTypeface(face);
    }

    public ButtonRobotoBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font/RobotoBold.ttf");
        this.setTypeface(face);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
