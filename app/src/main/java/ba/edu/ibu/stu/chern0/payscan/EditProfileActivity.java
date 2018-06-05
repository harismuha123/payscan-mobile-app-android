package ba.edu.ibu.stu.chern0.payscan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProfileActivity extends AppCompatActivity implements View.OnFocusChangeListener{

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
        passwordText = findViewById(R.id.passwordText);
        rePasswordText = findViewById(R.id.confirmPasswordText);

        emailText.setOnFocusChangeListener(this);
        passwordText.setOnFocusChangeListener(this);
        rePasswordText.setOnFocusChangeListener(this);

        emailText.setText(shared.getString("email", ""));

        setPasswordTypeface();
    }

    public void updateEmail(View v) {
        if(CustomValidator.validateEmail(emailLayout)) {
            HashMap<String, String> user = new HashMap<>();
            String userId = shared.getString("id", "");
            user.put("email", emailText.getText().toString());
            user.put("id", userId);

            final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Promjena korisničkog email-a...");
            progressDialog.show();

            /* upload new image to Imgur server */
            JsonObjectRequest jor = new JsonObjectRequest(
                    Request.Method.POST, Constants.API_URL + "db/update", new JSONObject(user),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.getString("status");
                                progressDialog.cancel();
                                shared.edit().putString("email", emailText.getText().toString()).apply();
                                Toast.makeText(EditProfileActivity.this, "Uspješno ažuriran profil!", Toast.LENGTH_SHORT).show();
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
        }
    }

    public void updatePassword(View v) {
        if(CustomValidator.validatePassword(passwordLayout)) {
            if(CustomValidator.validateRetypedPassword(passwordLayout, rePasswordLayout)) {
                HashMap<String, String> user = new HashMap<>();
                String userId = shared.getString("id", "");
                user.put("password", passwordText.getText().toString());
                user.put("id", userId);

                final ProgressDialog progressDialog = new ProgressDialog(EditProfileActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Promjena korisničke šifre...");
                progressDialog.show();

                /* upload new image to Imgur server */
                JsonObjectRequest jor = new JsonObjectRequest(
                        Request.Method.POST, Constants.API_URL + "db/update", new JSONObject(user),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String status = response.getString("status");
                                    progressDialog.cancel();
                                    passwordText.setText("");
                                    rePasswordText.setText("");
                                    Toast.makeText(EditProfileActivity.this, "Uspješno ažuriran profil!", Toast.LENGTH_SHORT).show();
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
        rePasswordLayout.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf"));
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        /* hide soft keyboard when both fields lose focus */
        if (!emailText.hasFocus() && !passwordText.hasFocus() && !rePasswordText.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
}
