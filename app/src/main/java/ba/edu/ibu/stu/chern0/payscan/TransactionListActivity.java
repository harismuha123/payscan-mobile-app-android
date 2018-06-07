package ba.edu.ibu.stu.chern0.payscan;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Transaction> transactionList;

    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        transactionList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);
        mRecyclerView = findViewById(R.id.transaction_recycler);
        shared = getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new TransactionListAdapter(this, transactionList);
        mRecyclerView.setAdapter(mAdapter);


        getTransactionData();
    }

    /* Get data from our API */
    public void getTransactionData() {
        /* Make a new request for a JSON object */
        JsonObjectRequest jor = new JsonObjectRequest(
                Request.Method.GET,Constants.API_URL + "db/transactions/" + shared.getString("id", ""), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("transactions");
                            if (data.length() >= 1) {
                                for (int i = 0; i < data.length(); i++) {
                                    /* Get individual JSON object and its attributes */
                                    JSONObject jsonObject = data.getJSONObject(i);
                                    String articleName = jsonObject.getString("article_name");
                                    String sellerName = jsonObject.getString("seller");
                                    String customerName = jsonObject.getString("customer");
                                    String amount = jsonObject.getString("amount");
                                    String purchaseTimestamp = jsonObject.getString("timestamp");

                                    transactionList.add(new Transaction(articleName, sellerName, customerName, amount, purchaseTimestamp));
                                    mAdapter.notifyDataSetChanged();
                                }
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
}
