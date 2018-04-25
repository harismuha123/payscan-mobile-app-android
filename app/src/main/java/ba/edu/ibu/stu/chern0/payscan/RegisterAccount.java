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

public class RegisterAccount extends AppCompatActivity implements View.OnFocusChangeListener {
    private EditText email;
    private EditText password;
    private EditText name;
    private EditText rePassword;

    /* input layouts (Material Design */
    private TextInputLayout passwordLayout;
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout rePasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        /* Find necessary views and bind them to view references */
        bindViews();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /* Go back to Log In Screen */
    public void backToLogin(View view) {
        Intent intent = new Intent(RegisterAccount.this, LogInScreen.class);
        startActivity(intent);
    }
    /* Animations on EditText fields getting/losing focus */
    @Override
    public void onFocusChange(View v, boolean b) {
        /* hide soft keyboard when all fields lose focus */
        if (!email.hasFocus() && !password.hasFocus() && !name.hasFocus() && !rePassword.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        if (!password.hasFocus()) {
            CustomValidator.validatePassword(passwordLayout);
        }
    }

    /* Workaround for Calligraphy not working with textPassword attribute */
    private void setPasswordTypeface() {
        passwordLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
        rePasswordLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
    }

    /* Bind views to their XML components */
    private void bindViews() {
        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPassword);
        rePassword = findViewById(R.id.textRePassword);
        name = findViewById(R.id.textName);

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        rePasswordLayout = findViewById(R.id.rePasswordLayout);
        nameLayout = findViewById(R.id.nameLayout);
        setPasswordTypeface();

        /* set listeners on EditText fields */
        email.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        rePassword.setOnFocusChangeListener(this);
        name.setOnFocusChangeListener(this);
    }
}
