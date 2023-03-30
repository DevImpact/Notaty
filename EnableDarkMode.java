package com.devimpact.inote.settings.changeapptheme;


public class EnableDarkMode {
   public void enableDarkMode() {
      // تحديد لون الخلفية والنص والأيقونات والأشكال في الوضع الليلي
      int backgroundColor = Color.BLACK;
      int textColor = Color.WHITE;
      int iconColor = Color.WHITE;
      int shapeColor = Color.DKGRAY;

      // تمكين الوضع الليلي في التطبيق
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

      // تطبيق تخصيص الألوان في الوضع الليلي
      Resources res = getContext().getResources();
      res.getColor(R.color.backgroundColor, null);
      res.getColor(R.color.textColor, null);
      res.getColor(R.color.iconColor, null);
      res.getColor(R.color.shapeColor, null);
   }
}