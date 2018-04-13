package ba.edu.ibu.stu.chern0.payscan;

import android.support.v7.widget.AppCompatButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class CustomFont {
    public static void install(String fontPath) {
        /* Installing Calligraphy font library, adding default font path leading to Raleway-Light.ttf */
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(fontPath)
                .setFontAttrId(R.attr.fontPath)
                .addCustomStyle(AppCompatButton.class, android.R.attr.buttonStyle)
                .build()
        );
    }
}
