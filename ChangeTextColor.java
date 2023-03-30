package com.devimpact.inote.settings.changeapptheme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.TextView;

public class ChangeTextColor {
    private Context context;
    private TextView textView;

    public ChangeTextColor(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    public void setTextColor(String color) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("textColor", color);
        editor.apply();
        textView.setTextColor(Color.parseColor(color));
    }

    public String getTextColor() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        return sharedPreferences.getString("textColor", "#000000");
    }
}
