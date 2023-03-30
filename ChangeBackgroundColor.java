package com.devimpact.inote.settings.changeapptheme;

public class ChangeBackgroundColor {
    // دالة لتغيير لون خلفية التطبيق
    public void setBackgroundColor(String color) {
        // يمكن إضافة أكواد الألوان هنا
        switch (color) {
            case "red":
                // تغيير لون الخلفية إلى اللون الأحمر
                break;
            case "green":
                // تغيير لون الخلفية إلى اللون الأخضر
                break;
            case "blue":
                // تغيير لون الخلفية إلى اللون الأزرق
                break;
            default:
                // إرجاع رسالة خطأ في حالة عدم توفر اللون المحدد
                System.out.println("Invalid color specified!");
                break;
        }
    }
}
