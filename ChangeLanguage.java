package com.devimpact.inote.settings.changeapptheme;


public class ChangeLanguage {

    public void changeLanguage(String language) {
        // إعدادات تغيير لغة التطبيق
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}