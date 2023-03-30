package com.devimpact.inote.settings;

import android.widget.TextView;
import java.util.Locale;

public class ChangeAppTheme {

    public void enableDarkMode() {
        DarkMode dm = new DarkMode();
        dm.EnableDarkMode();
    }

    public void disableDarkMode() {
        DarkMode dm = new DarkMode();
        dm.DisableDarkMode();
    }

    public void backupNotes(String notes) {
        BackupNotes.backupNotes(notes);
    }

    public void setBackgroundColor(String color) {
        ChangeBackgroundColor bg = new ChangeBackgroundColor();
        bg.setBackgroundColor(color);
    }

    public static void changeTypeface(TextView textView, Typeface typeface) {
        ChangeFont.changeTypeface(textView, typeface);
    }

    public static void changeTextSize(TextView textView, float size) {
        ChangeFont.changeTextSize(textView, size);
    }

    public void changeLanguage(String language) {
        ChangeLanguage cl = new ChangeLanguage();
        cl.changeLanguage(language);
    }

    public void changeNotificationSettings(boolean enableNotifications, boolean enableReminders, int reminderTime) {
        ChangeNotificationSettings cns = new ChangeNotificationSettings(enableNotifications, enableReminders, reminderTime);
        cns.changeNotificationSettings(enableNotifications, enableReminders, reminderTime);
    }

    public void setTextColor(String color) {
        ChangeTextColor ctc = new ChangeTextColor(context, textView);
        ctc.setTextColor(color);
    }

    public String getTextColor() {
        ChangeTextColor ctc = new ChangeTextColor(context, textView);
        return ctc.getTextColor();
    }

    public void setReminder(int seconds) {
        Reminder r = new Reminder();
        r.SetReminder(seconds);
    }

    public void changeTheme() {
        ChangeTheme ct = new ChangeTheme();
        ct.ChangeTheme();
    }
}