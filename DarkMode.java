package com.devimpact.inote.settings.changeapptheme;


public class DarkMode {
    public void EnableDarkMode() {
        // تغيير لون الخلفية
        setBackground(Color.BLACK);

        // تغيير لون النص
        setForeground(Color.WHITE);

        // تغيير لون الروابط
        setLinkColor(Color.CYAN);

        // تغيير لون حواف النص
        setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
    }

    public void DisableDarkMode() {
        // تغيير لون الخلفية
        setBackground(Color.WHITE);

        // تغيير لون النص
        setForeground(Color.BLACK);

        // تغيير لون الروابط
        setLinkColor(Color.BLUE);

        // تغيير لون حواف النص
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }
}