package ba.edu.ibu.stu.chern0.payscan;

import android.annotation.SuppressLint;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class CreateArticleActivity extends AppCompatActivity {

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_article);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CATEGORIES);

        articleNameText = findViewById(R.id.nameText);
        priceText = findViewById(R.id.priceText);
        locationText = findViewById(R.id.locationText);
        descriptionText = findViewById(R.id.descriptionText);


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
        if(validateFields(categoryText.getText().toString(), articleNameText.getText().toString(), priceText.getText().toString(), locationText.getText().toString())) {
            if(validateCategories(categoryText.getText().toString())) {
                Toast.makeText(this, "Kreiran artikal!", Toast.LENGTH_SHORT).show();
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


}
