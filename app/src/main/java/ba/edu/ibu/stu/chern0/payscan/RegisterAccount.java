package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.content.Intent;
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
    private TextView emailTitle;
    private TextView passwordTitle;
    private TextView rePasswordTitle;
    private TextView nameTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomFont.install("fonts/Raleway-Light.ttf");
        setContentView(R.layout.activity_register_account);

        /* Find necessary views and bind them to view references */
        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPassword);
        rePassword = findViewById(R.id.textRePassword);
        name = findViewById(R.id.textName);

        emailTitle = findViewById(R.id.emailTitle);
        passwordTitle = findViewById(R.id.passwordTitle);
        rePasswordTitle = findViewById(R.id.passwordReTitle);
        nameTitle = findViewById(R.id.nameTitle);

        email.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        rePassword.setOnFocusChangeListener(this);
        name.setOnFocusChangeListener(this);

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

        /* e-mail field */
        if (email.hasFocus()) {
            emailTitle.animate().alpha(1).setDuration(250);
            email.setHint("");
        } else {
            emailTitle.animate().alpha(0).setDuration(250);
            email.setHint("E-mail");
        }
        /* password field */
        if (password.hasFocus()) {
            passwordTitle.animate().alpha(1).setDuration(250);
            password.setHint("");
        } else {
            passwordTitle.animate().alpha(0).setDuration(250);
            password.setHint("Password");
            CustomValidator.validatePassword(password);
        }
        /* name field */
        if (name.hasFocus()) {
            nameTitle.animate().alpha(1).setDuration(250);
            name.setHint("");
        } else {
            nameTitle.animate().alpha(0).setDuration(250);
            name.setHint("Name");
        }
        /* retype password field */
        if (rePassword.hasFocus()) {
            rePasswordTitle.animate().alpha(1).setDuration(250);
            rePassword.setHint("");
        } else {
            rePasswordTitle.animate().alpha(0).setDuration(250);
            rePassword.setHint("Retype password");
        }
    }
}
