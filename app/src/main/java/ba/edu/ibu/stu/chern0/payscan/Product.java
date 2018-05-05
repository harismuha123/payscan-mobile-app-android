package ba.edu.ibu.stu.chern0.payscan;

import android.net.Uri;

public class Product {
    private String name;
    private float price;
    private Uri thumbnail;

    public Product() {}

    public Product(String name, float price, Uri thumbnail) {
        this.name = name;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Uri thumbnail) {
        this.thumbnail = thumbnail;
    }
}
