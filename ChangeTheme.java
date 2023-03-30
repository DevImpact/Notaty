package com.devimpact.inote.settings.changeapptheme;


public class ChangeTheme {
    // Colors
    private int primaryColor;
    private int accentColor;
    private int textColor;
    private int backgroundColor;
    private int dividerColor;

    // Fonts
    private String fontName;
    private int fontSize;

    // Backgrounds
    private Drawable mainBackground;
    private Drawable noteBackground;

    // Others
    private boolean isDarkModeEnabled;
	
    public void ChangeTheme() {
        if (isDarkModeEnabled) {
            // Set dark mode colors
            primaryColor = Color.parseColor("#212121");
            accentColor = Color.parseColor("#FF4081");
            textColor = Color.parseColor("#FFFFFF");
            backgroundColor = Color.parseColor("#303030");
            dividerColor = Color.parseColor("#BDBDBD");

            // Set dark mode font
            fontName = "Roboto";
            fontSize = 18;

            // Set dark mode backgrounds
            mainBackground = new ColorDrawable(Color.parseColor("#212121"));
            noteBackground = new ColorDrawable(Color.parseColor("#303030"));
        } else {
            // Set light mode colors
            primaryColor = Color.parseColor("#1E88E5");
            accentColor = Color.parseColor("#FF4081");
            textColor = Color.parseColor("#212121");
            backgroundColor = Color.parseColor("#FFFFFF");
            dividerColor = Color.parseColor("#BDBDBD");

            // Set light mode font
            fontName = "Roboto";
            fontSize = 18;

            // Set light mode backgrounds
            mainBackground = new ColorDrawable(Color.parseColor("#FFFFFF"));
            noteBackground = new ColorDrawable(Color.parseColor("#F5F5F5"));
        }
    }
}