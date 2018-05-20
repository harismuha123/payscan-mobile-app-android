package ba.edu.ibu.stu.chern0.payscan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LogInScreen extends AppCompatActivity implements View.OnFocusChangeListener{
    private SharedPreferences shared;
    private EditText email;
    private EditText password;

    /* input layouts (Material Design) */
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private boolean hasLogIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* check for existence of logged in user */
        shared = this.getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);
        String username = shared.getString("username", "");
        if (!username.equals("")) {
            Intent productIntent = new Intent(LogInScreen.this, ProductDrawer.class);
            startActivity(productIntent);
            finish();
        }

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

        /* check if log in button has been clicked first; if not, do not validate */
        if (hasLogIn) {
            /* validating email and password field using CustomValidator */
            if (!password.hasFocus()) {
                CustomValidator.validatePassword(passwordLayout);
            }

            if (!email.hasFocus()) {
                CustomValidator.validateEmail(emailLayout);
            }
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

    public void logIn(View view) {
        hasLogIn = true;
        /* check if fields are empty */
        if (CustomValidator.isValid(emailLayout, passwordLayout)) {
            final ProgressDialog progressDialog = new ProgressDialog(LogInScreen.this, R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Logging in...");
            progressDialog.show();

            HashMap<String, String> data = new HashMap<String, String>();
            data.put("email", email.getText().toString());
            data.put("password", password.getText().toString());

            JsonObjectRequest jor = new JsonObjectRequest(
                    Request.Method.POST, Constants.API_URL + "db/get", new JSONObject(data),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                progressDialog.cancel();
                                if (status.equals("success")) {
                                    /* get current user's username and save to SharedPreferences*/
                                    String username = response.getString("user_name");
                                    shared.edit().putString("username", username).apply();
                                    Toast.makeText(LogInScreen.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                    Intent productView = new Intent(LogInScreen.this, ProductDrawer.class);
                                    startActivity(productView);
                                    finish();
                                } else if (status.equals("pass_incorrect")) {
                                    Toast.makeText(LogInScreen.this, "The password is incorrect.", Toast.LENGTH_SHORT).show();
                                } else if (status.equals("email_incorrect")) {
                                    Toast.makeText(LogInScreen.this, "Entered email does not exist in the database.", Toast.LENGTH_SHORT).show();
                                } else if (status.equals("not_activated")) {
                                    Toast.makeText(LogInScreen.this, "Your account has not been activated yet.", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", "Error");
                        }
                    }
            );
            /* Add request to Volley asynchronous queue */
            NetworkQueue.getInstance(this).addToRequestQueue(jor);
        } else {
            Toast.makeText(this, "Please enter proper credentials.", Toast.LENGTH_LONG).show();
        }
    }
}
