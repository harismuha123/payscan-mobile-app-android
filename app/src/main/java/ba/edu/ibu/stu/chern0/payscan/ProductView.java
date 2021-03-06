package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductView extends AppCompatActivity {
    private int PAGE = 1;
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private List<Product> productList;
    private int category;
    private Intent intent;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_view);
        /* Create collapsing toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initCollapsingToolbar();

        /* get category from previous activity */
        intent = getIntent();
        category = intent.getIntExtra("category", 1);
        categoryName = intent.getStringExtra("category_name");

        /* Create the RecyclerView & product list */
        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        adapter = new ProductsAdapter(this, productList);
        adapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ProductView.this, ArticleActivity.class);
                intent.putExtra("link", productList.get(position).getLink().toString());
                startActivity(intent);
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
                if (PAGE > 0) {
                    getProductData();
                    PAGE++;
                }
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
                    collapsingToolbarLayout.setTitle(categoryName);
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
                Request.Method.GET,Constants.API_URL + "category/"+ category + "/" + PAGE, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("products");
                            if (data.length() > 1) {
                                for (int i = 0; i < data.length(); i++) {
                                    /* Get individual JSON object and its attributes */
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    String productName = jsonObject.getString("name");
                                    String productPrice = jsonObject.getString("price");
                                    String productPicture = jsonObject.getString("picture");
                                    String productLink = jsonObject.getString("link");

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
}