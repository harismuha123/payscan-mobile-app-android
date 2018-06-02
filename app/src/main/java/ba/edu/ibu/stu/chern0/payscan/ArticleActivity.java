package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import me.grantland.widget.AutofitHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ArticleActivity extends AppCompatActivity {

    private Intent intent;
    private String[] linkArray;
    private String articleID;
    private ImageView articleImage;
    private TextView articleName, priceText, locationText,
                     categoryText, sellerText, detailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        intent = getIntent();
        linkArray = intent.getStringExtra("link").split("/");
        articleID = linkArray[4];
        Log.i("link", Constants.API_URL + articleID);

        bindViews();

        getProductData(articleID);
    }

    private void getProductData(String articleID) {
        /* Make a new request for a JSON object */
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,Constants.API_URL + "product/"+ articleID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            /* Get individual JSON object and its attributes */
                            String productName = response.getString("name");
                            String productPrice = response.getString("price");
                            String productLocation  = response.getString("location");
                            String productCategory = response.getString("category");
                            String productSeller = response.getString("seller");
                            String productImage = response.getString("picture");
                            String productDescription = response.getString("description");

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

        AutofitHelper.create(articleName);

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

    public void testFunc(View v) {
        Intent intent = new Intent(this, CreateArticleActivity.class);
        startActivity(intent);
    }


}
