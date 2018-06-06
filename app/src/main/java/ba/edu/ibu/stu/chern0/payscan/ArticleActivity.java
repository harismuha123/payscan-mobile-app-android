package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ArticleActivity extends AppCompatActivity {

    private Intent intent;
    private String[] linkArray;
    private String articleID;
    private ImageView articleImage;
    private TextView articleName, priceText, locationText,
                     categoryText, sellerText, detailText;
    private JSONObject product;
    private SharedPreferences shared;
    private String sellerPhone, payScanUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        intent = getIntent();
        linkArray = intent.getStringExtra("link").split("/");
        articleID = linkArray[4];

        bindViews();

        getProductData(articleID);

        /* Make an image zoomable */
        articleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ArticleActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.zoomable_image, null);
                PhotoView photoView = mView.findViewById(R.id.imageView);
                photoView.setImageDrawable(articleImage.getDrawable());
                mBuilder.setView(mView);
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
    }

    private void getProductData(String articleID) {
        /* Make a new request for a JSON object */
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,Constants.API_URL + "product/"+ articleID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            product = response;
                            /* Get individual JSON object and its attributes */
                            String productName = response.getString("name");
                            String productPrice = response.getString("price");
                            String productLocation  = response.getString("location");
                            String productCategory = response.getString("category");
                            String productSeller = response.getString("seller");
                            String productImage = response.getString("picture");
                            String productDescription = response.getString("description");
                            payScanUser = response.getString("payscan_user");

                            /* Either open the user's profile or show his/her phone number */
                            sellerText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (payScanUser.equals("1")) {
                                        try {
                                            sellerPhone = product.getString("phone_number");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        callUser(sellerPhone);
                                    } else {
                                        openProfile();
                                    }
                                }
                            });

                            Glide.with(ArticleActivity.this).load(Uri.parse(productImage)).into(articleImage);
                            articleName.setText(productName);
                            priceText.setText(productPrice);
                            locationText.setText(productLocation);
                            categoryText.setText(productCategory);
                            sellerText.setText(productSeller);
                            detailText.setText(productDescription);

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.DEBUG = true;
                        Log.e("VOLLEY", "Error");
                    }
                }
        );
        /* Add request to Volley asynchronous queue */
        NetworkQueue.getInstance(this).addToRequestQueue(jor);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void bindViews() {
        articleImage = findViewById(R.id.articleImage);
        articleName = findViewById(R.id.articleName);

        priceText = findViewById(R.id.priceText);
        locationText = findViewById(R.id.locationText);
        categoryText = findViewById(R.id.categoryText);
        sellerText = findViewById(R.id.sellerText);

        detailText = findViewById(R.id.detailText);
        detailText.setMovementMethod(new ScrollingMovementMethod());
    }

    public void openLocation(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+locationText.getText().toString()));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    public void displayQr(View view) throws JSONException {
        Intent intent = new Intent(ArticleActivity.this, ProductQrCode.class);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String format = simpleDateFormat.format(new Date());
        product.put("timestamp", format);
        intent.putExtra("product", product.toString());
        startActivity(intent);
    }

    public void openProfile() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.olx.ba/profil/"+sellerText.getText().toString()));
        startActivity(intent);
    }

    public void callUser(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
