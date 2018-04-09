package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    private EditText email;
    private EditText password;
    private TextView emailTitle;
    private TextView passwordTitle;
    private TextView createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Installing Calligraphy font library, adding default font path leading to Raleway-Light.ttf */
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Raleway-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .addCustomStyle(AppCompatButton.class, android.R.attr.buttonStyle)
                .build()
        );
        setContentView(R.layout.activity_main);

        /* Find necessary views and bind them to view references */
        email = findViewById(R.id.textEmail);
        password = findViewById(R.id.textPassword);
        emailTitle = findViewById(R.id.emailTitle);
        passwordTitle = findViewById(R.id.passwordTitle);
        createAccount = findViewById(R.id.account);

//        createAccount.setPaintFlags(createAccount.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        email.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);


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

        if (email.hasFocus()) {
            emailTitle.animate().alpha(1).setDuration(250);

            email.setHint("");
        } else {
            emailTitle.animate().alpha(0).setDuration(250);
            email.setHint("E-mail");
        }

        if (password.hasFocus()) {
            passwordTitle.animate().alpha(1).setDuration(250);
            password.setHint("");
        } else {
            passwordTitle.animate().alpha(0).setDuration(250);
            password.setHint("Password");
        }
    }
}
