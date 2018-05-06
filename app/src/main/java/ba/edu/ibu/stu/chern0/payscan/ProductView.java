package ba.edu.ibu.stu.chern0.payscan;

import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;

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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ProductView extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_product_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        adapter = new ProductsAdapter(this, productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareProducts();
    }

    /* Show and hide toolbar on scroll */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        /* show and hide toolbar title */
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1)
                    scrollRange = appBarLayout.getTotalScrollRange();
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.app_name));
                    isShown = true;
                } else if (isShown) {
                    collapsingToolbarLayout.setTitle("");
                    isShown = false;
                }
            }
        });
    }

    /* Add products to list */
    private void prepareProducts() {
        getProductData();
        productList.add(new Product("Alat za brizganje epruveta", 5000, Uri.parse("https://s7.pik.ba/galerija/2017-05/03/10/slika-615156-590a380f7f961-thumb.jpg")));
        productList.add(new Product("Integralni biskviz za pse", 11, Uri.parse("https://s8.pik.ba/galerija/2017-11/21/02/slika-166790-5a14284d62ee0-thumb.jpg")));
        productList.add(new Product("Pasat 3 karavan dijelovi", 1000, Uri.parse("https://s6.pik.ba/galerija/2016-08/14/12/slika-139597-57b04c0c289c1-thumb.jpg")));
        productList.add(new Product("Pumpa za vodu potopna 600 wati", 42, Uri.parse("https://s8.pik.ba/galerija/2017-11/13/07/slika-59873-5a09e7588206a-thumb.jpg")));

        adapter.notifyDataSetChanged();
    }

    /* Convert dp to pixels */
    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void getProductData() {
        /* instantiate the queue */
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue requestQueue = new RequestQueue(cache, network);
        /* start the queue */
        requestQueue.start();

        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET, "https://payscan-api.herokuapp.com/rest/products", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("products");
                            for (int i = 0; i < data.length(); i++) {
                                String product = "";
                                JSONObject jsonObject = data.getJSONObject(i);
                                String productName = jsonObject.getString("name");
                                String productPrice = jsonObject.getString("price");
                                String prodctPicture = jsonObject.getString("picture");

                                product += productName + ", " + productPrice + ", " + prodctPicture + "\n";
                                Log.i("VOLLEY", product);
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
        requestQueue.add(jor);
    }
}
