package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout, rePasswordLayout;
    private TextInputEditText emailText, passwordText, rePasswordText;
    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        shared = this.getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);

        emailLayout = findViewById(R.id.editEmailField);
        passwordLayout = findViewById(R.id.editPasswordField);
        rePasswordLayout = findViewById(R.id.confirmPasswordField);

        emailText = findViewById(R.id.emailText);
        emailText.setText(shared.getString("email", ""));

        setPasswordTypeface();
    }

    public void updateProfile(View v) {
        if(CustomValidator.validateEmail(emailLayout)) {
            if(CustomValidator.validatePassword(passwordLayout)) {
                if(CustomValidator.validateRetypedPassword(passwordLayout, rePasswordLayout)) {
                    Toast.makeText(this, "Ažuriran profil!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /* workaround for bug which doesn't allow changing TypeFace of EditText */
    private void setPasswordTypeface() {
        emailLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
        passwordLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
    }
}
