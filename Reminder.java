package com.devimpact.inote.settings.changeapptheme;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder {

    public void SetReminder(int seconds) {
        Timer timer = new Timer();
        timer.schedule(new RemindTask(), seconds*1000);
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Reminder: Time's up!");
            timer.cancel(); //Terminate the timer thread
        }
    }

    public static void main(String args[]) {
        Reminder reminder = new Reminder();
        System.out.println("Setting reminder...");
        reminder.SetReminder(10);
        System.out.println("Reminder set!");
    }
}