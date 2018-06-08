package ba.edu.ibu.stu.chern0.payscan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserProductView extends AppCompatActivity {
    private int PAGE = 1;
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private List<Product> productList;
    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_view);
        /* Create collapsing toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        shared = getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);

        /* Create the RecyclerView & product list */
        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        adapter = new ProductsAdapter(this, productList);
        adapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(UserProductView.this, ArticleActivity.class);
                intent.putExtra("link", productList.get(position).getLink().toString());
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new ProductsAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                final int productPosition = position;
                final String productId = productList.get(position).getLink().toString().split("/")[4];
                AlertDialog.Builder builder = new AlertDialog.Builder(UserProductView.this);
                builder.setMessage("Opcije artikla");
                /* add new buttons */
                builder.setPositiveButton("Obriši", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProductView.this);
                        builder.setMessage("Da li ste sigurni da želite obrisati ovaj artikal?");
                        /* add new buttons */
                        builder.setPositiveButton("Obriši", new DialogInterface.OnClickListener() {
                            @Override
                            /* perform delete operation */
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final ProgressDialog progressDialog = new ProgressDialog(UserProductView.this, R.style.Theme_AppCompat_DayNight_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Brisanje artikla...");
                                progressDialog.show();
                                /* Make a new request for a JSON object */
                                JsonObjectRequest jor = new JsonObjectRequest(
                                        Request.Method.DELETE,Constants.API_URL + "products/" + productId, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                try {
                                                    shared.edit().putBoolean("deleted", true).apply();
                                                    String data = response.getString("status");
                                                    progressDialog.cancel();
                                                    /* remove an article from the list */
                                                    productList.remove(productPosition);
                                                    adapter.notifyItemRemoved(productPosition);
                                                    adapter.notifyItemRangeChanged(productPosition, productList.size());
                                                } catch(JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.e("VOLLEY", "Body: " + error.getMessage());
                                                Log.e("VOLLEY", "Error");
                                            }
                                        }
                                );
                                /* Add request to Volley asynchronous queue */
                                NetworkQueue.getInstance(UserProductView.this).addToRequestQueue(jor);
                            }
                        });
                        builder.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(UserProductView.this, "Operacija otkazana.", Toast.LENGTH_LONG).show();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
                builder.setNegativeButton("Uredi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(UserProductView.this, CreateArticleActivity.class);
                        intent.putExtra("product_id", productId);
                        startActivity(intent);
                    }
                });
                builder.setNeutralButton("Prodaj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* Get product data and generate QR code */
                        JsonObjectRequest jor = new JsonObjectRequest(
                                Request.Method.GET,Constants.API_URL + "product/"+ productId, null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            Intent intent = new Intent(UserProductView.this, ProductQrCode.class);
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                                            String format = simpleDateFormat.format(new Date());
                                            response.put("timestamp", format);
                                            intent.putExtra("product", response.toString());
                                            startActivity(intent);
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
                                        Toast.makeText(UserProductView.this, "Došlo je do greške prilikom slanja zahtjeva.", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                        /* Add request to Volley asynchronous queue */
                        NetworkQueue.getInstance(UserProductView.this).addToRequestQueue(jor);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        /* Set up RecyclerView */
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        getProductData();
        PAGE++;

        /* Call new data page upon reaching the end of previous one */
        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
           /*     if (PAGE > 0) {
                    getProductData();
                    PAGE++;
                }*/
            }
        });
    }

    /* Show and hide toolbar on scroll */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        final ImageView imageView = findViewById(R.id.backdrop);
        imageView.setImageResource(R.drawable.logo_rounded_white);

        /* show and hide toolbar title */
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1)
                    scrollRange = appBarLayout.getTotalScrollRange();
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Moji proizvodi");
                    imageView.setImageResource(android.R.color.transparent);
                    isShown = true;
                } else if (isShown) {
                    collapsingToolbarLayout.setTitle(" ");
                    imageView.setImageResource(R.drawable.logo_rounded_white);
                    isShown = false;
                }
            }
        });
    }

    /* Convert dp to pixels */
    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /* Get data from our API */
    public void getProductData() {
        /* Make a new request for a JSON object */
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,Constants.API_URL + "user/products/" + shared.getString("id", ""), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("products");
                            if (data.length() >= 1) {
                                for (int i = 0; i < data.length(); i++) {
                                    /* Get individual JSON object and its attributes */
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    String productName = jsonObject.getString("name");
                                    String productPrice = jsonObject.getString("price") + " KM";
                                    String productPicture = jsonObject.getString("picture");
                                    String productLink = "https://www.olx.ba/artikal/" + jsonObject.getString("id");

                                    /* Check if product is valid */
                                    if (!(productName.equals("") && productPrice.equals("") && productPicture.equals("")) &&
                                            (!(productName.equals("") && productPrice.equals("PO DOGOVORU") && productPicture.equals("")))) {
                                        productList.add(new Product(productName, productPrice, Uri.parse(productPicture), Uri.parse(productLink)));
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                /* say that there are no more pages */
                                PAGE = 0;
                            }
                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "Body: " + error.getMessage());
                        Log.e("VOLLEY", "Error");
                        Log.e("VOLLEY", shared.getString("id", ""));
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
}
