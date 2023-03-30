package com.devimpact.inote.settings.changeapptheme;


public class ChangeNotificationSettings {
    private boolean enableNotifications;
    private boolean enableReminders;
    private int reminderTime;

    public ChangeNotificationSettings(boolean enableNotifications, boolean enableReminders, int reminderTime) {
        this.enableNotifications = enableNotifications;
        this.enableReminders = enableReminders;
        this.reminderTime = reminderTime;
    }

    public boolean isEnableNotifications() {
        return enableNotifications;
    }

    public void setEnableNotifications(boolean enableNotifications) {
        this.enableNotifications = enableNotifications;
    }

    public boolean isEnableReminders() {
        return enableReminders;
    }

    public void setEnableReminders(boolean enableReminders) {
        this.enableReminders = enableReminders;
    }

    public int getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(int reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void changeNotificationSettings(boolean enableNotifications, boolean enableReminders, int reminderTime) {
        setEnableNotifications(enableNotifications);
        setEnableReminders(enableReminders);
        setReminderTime(reminderTime);
    }
}