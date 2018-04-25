package ba.edu.ibu.stu.chern0.payscan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        if (!email.hasFocus()) {
            CustomValidator.validateEmail(emailLayout);
        }

        if (!rePassword.hasFocus()) {
            CustomValidator.validateRetypedPassword(rePasswordLayout, passwordLayout);
        }

        if (!nameLayout.hasFocus()) {
            CustomValidator.validateName(nameLayout);
        }
    }

    /* Workaround for Calligraphy not working with textPassword attribute */
    private void setPasswordTypeface() {
        nameLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
        emailLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
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

    /* onclick function which starts the sign up process */
    public void createAccount(View view) {

        /* declare progress dialog and define the message */
        final ProgressDialog progressDialog = new ProgressDialog(RegisterAccount.this, R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        /* create Runnable which runs the validation method and cancels the progress dialog */
        Runnable progressRunnable = new Runnable() {
            @Override
            public void run() {
                progressDialog.cancel();
                if (CustomValidator.isValid(nameLayout, emailLayout, passwordLayout, rePasswordLayout)) {
                    Toast.makeText(RegisterAccount.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterAccount.this, LogInScreen.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterAccount.this, "Sign up failed! Please enter valid data!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        /* Handler which cancels the progress dialog after delaying the Runnable for 3 seconds */
        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3000);
    }
}
