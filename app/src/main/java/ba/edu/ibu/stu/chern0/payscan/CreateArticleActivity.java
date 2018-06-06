package ba.edu.ibu.stu.chern0.payscan;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateArticleActivity extends AppCompatActivity {
    private SharedPreferences shared;
    private static int RESULT_LOAD_IMAGE = 16;
    private static final String[] CATEGORIES = new String[] {
            "Vozila",
            "Nekretnine",
            "Mobilni uređaji",
            "Kompjuteri",
            "Tehnika",
            "Nakit i satovi",
            "Moj dom",
            "Literatura",
            "Muzička oprema",
            "Umjetnost",
            "Sportska oprema",
            "Karte",
            "Životinje",
            "Biznis i industrija",
            "Ljepota i zdravlje",
            "Video igre",
            "Kolekcionarstvo",
            "Antikviteti",
            "Odjeća i obuća",
            "Servisi i usluge",
            "Poslovi",
            "Igre i igračke",
            "Bebe",
            "Sve ostalo"
    };

    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView categoryText;
    private TextInputEditText articleNameText, priceText, locationText;
    private TextView descriptionText;
    private ImageView uploadImage;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);
        shared = getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);

        articleNameText = findViewById(R.id.nameText);
        priceText = findViewById(R.id.priceTransText);
        locationText = findViewById(R.id.locationText);
        descriptionText = findViewById(R.id.descriptionText);
        uploadImage = findViewById(R.id.upload_image);


        categoryText = (AutoCompleteTextView) findViewById(R.id.categoryList);
        categoryText.setThreshold(1);
        categoryText.setAdapter(adapter);

        categoryText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (CATEGORIES.length > 0) {
                    // show all suggestions
                    if (!categoryText.getText().toString().equals(""))
                        adapter.getFilter().filter(null);
                    categoryText.showDropDown();
                }
                return false;
            }
        });
    }

    public void submitArticle(View v) {
        /* get product details */
        HashMap<String, String> product = new HashMap<>();
        product.put("name", articleNameText.getText().toString());
        product.put("price", priceText.getText().toString());
        product.put("category", categoryText.getText().toString());
        product.put("seller", shared.getString("id", ""));
        product.put("location", locationText.getText().toString());
        product.put("description", descriptionText.getText().toString());
        product.put("picture", shared.getString("image", ""));

        /* Start the process of uploading product data */
        if(validateFields(categoryText.getText().toString(), articleNameText.getText().toString(), priceText.getText().toString(), locationText.getText().toString())) {
            if(validateCategories(categoryText.getText().toString())) {
                final ProgressDialog progressDialog = new ProgressDialog(CreateArticleActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Dodavanje novog proizvoda...");
                progressDialog.show();
                /* Make a request if everything is valid */
                JsonObjectRequest jor = new JsonObjectRequest(
                        Request.Method.POST, Constants.API_URL + "db/products/add", new JSONObject(product),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(CreateArticleActivity.this, "Novi proizvod uspješno dodan.", Toast.LENGTH_LONG).show();
                                progressDialog.cancel();
                                Intent intent = new Intent(CreateArticleActivity.this, ProductDrawer.class);
                                startActivity(intent);
                                finish();
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

    private boolean validateFields(String category, String name, String price, String location) {

        if(category.equals("") || name.equals("") || price.equals("") || location.equals("")) {
            Toast.makeText(this, "Molimo vas popunite sva polja!", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }

        return false;
    }
    
    private boolean validateCategories(String category) {
        for (String CATEGORY : CATEGORIES) {
            if (category.equals(CATEGORY)) {
                return true;
            }
        }

        Toast.makeText(this, "Neispravna kategorija!", Toast.LENGTH_SHORT).show();
        return false;
    }

    /* Upload article photo */
    public void uploadProductPhoto(View view) {
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        photoPicker.setType("image/*");
        startActivityForResult(photoPicker, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                /* Get an image and convert it to Base64 */
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                uploadImage.setImageBitmap(selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                /* Show an 'Uploading...' progress screen */
                final ProgressDialog progressDialog = new ProgressDialog(CreateArticleActivity.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Dodavanje slike...");
                progressDialog.show();

                HashMap<String, String> image = new HashMap<>();
                image.put("image", encodedImage);

                /* upload new image to Imgur server */
                JsonObjectRequest jor = new JsonObjectRequest(
                        Request.Method.POST, Constants.API_URL + "product/upload", new JSONObject(image),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String imageLink = response.getString("link");
                                    shared.edit().putString("image", imageLink).apply();
                                    progressDialog.cancel();
                                    Glide.with(CreateArticleActivity.this).load(imageLink)
                                            .apply(new RequestOptions()
                                                    .centerCrop())
                                            .into(uploadImage);
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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked an image",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CreateArticleActivity.this, ProductDrawer.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
