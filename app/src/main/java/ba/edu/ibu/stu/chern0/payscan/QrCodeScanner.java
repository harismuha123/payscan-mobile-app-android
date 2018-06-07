package ba.edu.ibu.stu.chern0.payscan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static int CAMERA_PERMISSION = 14;
    private SharedPreferences shared;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        shared = getSharedPreferences("ba.edu.ibu.stu.chern0.payscan", Context.MODE_PRIVATE);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        try {
            final JSONObject productData = new JSONObject(rawResult.toString());
            final HashMap<String, String> transaction = new HashMap<>();
            transaction.put("article_id", productData.getString("id"));
            transaction.put("customer_id", shared.getString("id", ""));
            transaction.put("seller_id", productData.getString("seller"));
            transaction.put("amount", productData.getString("price").split(" ")[0]);

            /* create a new alert */
            AlertDialog.Builder builder = new AlertDialog.Builder(QrCodeScanner.this);
            builder.setMessage("Ime artikla: " + productData.getString("name") + "\n"
                             + "Cijena: " + productData.getString("price") + "\n"
                             + "Prodavač: " + productData.getString("seller") + "\n"
                             + "Da li želite kupiti ovaj artikal?").setTitle("Detalji artikla");
            /* add new buttons */
            builder.setPositiveButton("Kupi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final ProgressDialog progressDialog = new ProgressDialog(QrCodeScanner.this, R.style.Theme_AppCompat_DayNight_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Transakcija u toku...");
                    progressDialog.show();
                    /* Make a transaction request */
                    JsonObjectRequest jor = new JsonObjectRequest(
                            Request.Method.POST, Constants.API_URL + "db/buy", new JSONObject(transaction),
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("VOLLEY", response.toString());
                                    progressDialog.cancel();
                                    shared.edit().putBoolean("purchased", true).apply();
                                    Toast.makeText(QrCodeScanner.this, "Artikal uspješno kupljen!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("VOLLEY", "Error");
                                    Log.e("VOLLEY", error.toString());
                                }
                            }
                    );

                    NetworkQueue.getInstance(QrCodeScanner.this).addToRequestQueue(jor);
                }
            });
            builder.setNegativeButton("Otkaži", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}