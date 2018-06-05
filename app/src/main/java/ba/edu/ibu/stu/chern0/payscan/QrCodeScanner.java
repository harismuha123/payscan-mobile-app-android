package ba.edu.ibu.stu.chern0.payscan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.regex.Pattern;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static int CAMERA_PERMISSION = 14;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
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
            JSONObject productData = new JSONObject(rawResult.toString());
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
                    Toast.makeText(QrCodeScanner.this, "Artikal uspješno kupljen!", Toast.LENGTH_SHORT).show();
                    finish();
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