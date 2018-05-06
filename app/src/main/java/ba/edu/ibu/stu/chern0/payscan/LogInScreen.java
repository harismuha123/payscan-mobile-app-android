package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LogInScreen extends AppCompatActivity implements View.OnFocusChangeListener{

    private EditText email;
    private EditText password;

    /* input layouts (Material Design) */
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /* Animations on EditText fields getting/losing focus */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        /* hide soft keyboard when both fields lose focus */
        if (!email.hasFocus() && !password.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        /* validating email and password field using CustomValidator */
        if (!password.hasFocus()) {
            CustomValidator.validatePassword(passwordLayout);
        }

        if (!email.hasFocus()) {
            CustomValidator.validateEmail(emailLayout);
        }

    }

    /* workaround for bug which doesn't allow changing TypeFace of EditText */
    private void setPasswordTypeface() {
        emailLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
        passwordLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
    }

    /* Go to Registration Screen */
    public void register(View view) {
        Intent intent = new Intent(LogInScreen.this, RegisterAccount.class);
        startActivity(intent);
    }

    /* binding views to their components */
    private void bindViews() {
        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPassword);

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        setPasswordTypeface();

        /* set listeners on EditText fields */
        email.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
    }
}
