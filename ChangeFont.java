package com.devimpact.inote.settings.changeapptheme;

import android.graphics.Typeface;
import android.widget.TextView;

public class ChangeFont {
    
    // تغيير نوع الخط
    public static void changeTypeface(TextView textView, Typeface typeface) {
        textView.setTypeface(typeface);
    }

    // تغيير حجم الخط
    public static void changeTextSize(TextView textView, float size) {
        textView.setTextSize(size);
    }
}
