package ba.edu.ibu.stu.chern0.payscan;

import android.app.Application;
import android.support.v7.widget.AppCompatButton;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class PayScanApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Installing Calligraphy font library, adding default font path leading to Raleway-Light.ttf */
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .addCustomStyle(AppCompatButton.class, android.R.attr.buttonStyle)
                .build()
        );
    }
}
