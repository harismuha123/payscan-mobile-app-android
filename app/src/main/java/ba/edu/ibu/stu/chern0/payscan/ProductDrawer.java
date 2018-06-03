package ba.edu.ibu.stu.chern0.payscan;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductDrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView navigationView;
    private int PAGE = 1;
    private int SEARCH_PAGE = 1;
    private int MODE = 0;
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private List<Product> productList;
    private HashMap<String, Integer> categories;
    private SharedPreferences shared;
    private TextView emailText, usernameText;
    private ImageView profilePicture;
    private static int RESULT_LOAD_IMAGE = 16;
    private String searchTerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        shared = this.getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);
        String username = shared.getString("username", "");
        String email = shared.getString("email", "");
        String picture = shared.getString("image", "");

        //    initCollapsingToolbar();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDrawer.this, CreateArticleActivity.class);
                startActivity(intent);
                finish();
            /*    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        /* define all category strings */
        categories = new HashMap<String, Integer>();
        categories.put("Vozila", 1);
        categories.put("Nekretnine", 2);
        categories.put("Mobilni uređaji", 3);
        categories.put("Kompjuteri", 5);
        categories.put("Tehnika", 14);
        categories.put("Nakit i satovi", 68);
        categories.put("Moj dom", 701);
        categories.put("Literatura", 16);
        categories.put("Muzička oprema", 67);
        categories.put("Umjetnost", 69);
        categories.put("Sportska oprema", 171);
        categories.put("Karte", 179);
        categories.put("Životinje", 208);
        categories.put("Biznis i industrija", 224);
        categories.put("Ljepota i zdravlje", 276);
        categories.put("Video igre", 289);
        categories.put("Kolekcionarstvo", 435);
        categories.put("Antikviteti", 455);
        categories.put("Odjeća i obuća", 465);
        categories.put("Servisi i usluge", 2159);
        categories.put("Poslovi", 2286);
        categories.put("Igre i igračke", 283);
        categories.put("Bebe", 371);
        categories.put("Sve ostalo", 328);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        /* Set logged in user's credentials inside navbar */
        emailText = headerView.findViewById(R.id.nav_email);
        usernameText = headerView.findViewById(R.id.nav_username);
        profilePicture = headerView.findViewById(R.id.profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeProfilePicture(view);
            }
        });
        emailText.setText(email);
        usernameText.setText(username);

        /* Create the RecyclerView & product list */
        recyclerView = findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        adapter = new ProductsAdapter(this, productList);

        /* Set up RecyclerView */
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(ProductDrawer.this, ArticleActivity.class);
                intent.putExtra("link", productList.get(position).getLink().toString());
                startActivity(intent);
            }
        });

        getProductData();
        PAGE++;

        /* Call new data page upon reaching the end of previous one */
        adapter.setOnBottomReachedListener(new OnBottomReachedListener() {
            @Override
            public void onBottomReached(int position) {
                if (MODE == 0) {
                    if (PAGE > 0) {
                        getProductData();
                        PAGE++;
                    }
                } else {
                    if (SEARCH_PAGE > 0) {
                        SEARCH_PAGE++;
                        getSearchProductData(searchTerm);
                    }
                }
            }
        });

        /* Load picture if nothing exists */
        if (!picture.equals("")) {
            Glide.with(ProductDrawer.this).load(picture)
                    .apply(new RequestOptions()
                            .centerCrop())
                    .into(profilePicture);
        }

    }

    /* Convert dp to pixels */
    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /* Get data from our API */
    public void getProductData() {
        MODE = 0;
        /* Make a new request for a JSON object */
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,Constants.API_URL + "products/"+ PAGE, null,
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

    public void getSearchProductData(String term) {
        MODE = 1;
        /* Make a new request for a JSON object */
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,Constants.API_URL + "search/"+ SEARCH_PAGE + "/" + term, null,
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
                                SEARCH_PAGE = 0;
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

    public void logOut(View view) {
        SharedPreferences shared = this.getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);
        shared.edit().clear().apply();
        /* go back to log in screen */
        Intent loginIntent = new Intent(ProductDrawer.this, LogInScreen.class);
        Toast.makeText(ProductDrawer.this, "You are now logged out.", Toast.LENGTH_LONG).show();
        startActivity(loginIntent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_drawer, menu);
        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTerm = query;
                productList.clear();
                adapter.notifyDataSetChanged();
                getSearchProductData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    SEARCH_PAGE = 1;
                    productList.clear();
                    adapter.notifyDataSetChanged();
                    getProductData();
                }
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_logout:
                logOut(null);
                break;
            default:
                int id = categories.get(item.getTitle());
                Intent categoryIntent = new Intent(ProductDrawer.this, ProductView.class);
                categoryIntent.putExtra("category", id);
                startActivity(categoryIntent);
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    /* Change profile picture */
    public void changeProfilePicture(View view) {
        /* create a new alert */
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDrawer.this);
        builder.setMessage("Would you like to change your profile picture?").setTitle("Upload a photo");
        /* add new buttons */
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, RESULT_LOAD_IMAGE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                /* Show an 'Uploading...' progress screen */
                final ProgressDialog progressDialog = new ProgressDialog(ProductDrawer.this, R.style.Theme_AppCompat_DayNight_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Uploading profile picture...");
                progressDialog.show();

                HashMap<String, String> user = new HashMap<>();
                String userId = shared.getString("id", "");
                user.put("image", encodedImage);
                user.put("id", userId);

                /* upload new image to Imgur server */
                JsonObjectRequest jor = new JsonObjectRequest(
                        Request.Method.POST, Constants.API_URL + "user/upload", new JSONObject(user),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String imageLink = response.getString("link");
                                    shared.edit().putString("image", imageLink).apply();
                                    progressDialog.cancel();
                                    Glide.with(ProductDrawer.this).load(imageLink)
                                            .apply(new RequestOptions()
                                                    .centerCrop())
                                            .into(profilePicture);
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
}
