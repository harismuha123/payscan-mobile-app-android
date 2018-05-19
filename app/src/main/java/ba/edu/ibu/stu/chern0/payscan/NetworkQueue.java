package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NetworkQueue {
    private static NetworkQueue NetworkQueue;
    public static Context context;
    private RequestQueue RequestQueue;

    /* Create a new instance of NetworkQueue */
    private NetworkQueue(Context context) {
        this.context = context;
        RequestQueue = getRequestQueue();
    }

    /* Get the existing (or create a new) instance of NetworkQueue */
    public static synchronized NetworkQueue getInstance(Context context) {
        if (NetworkQueue == null)
            NetworkQueue = new NetworkQueue(context);
        return NetworkQueue;
    }

    /* Get a RequestQueue */
    public RequestQueue getRequestQueue() {
        if (RequestQueue == null)
            RequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return RequestQueue;
    }

    /* Add to RequestQueue */
    public <Item> void addToRequestQueue(Request<Item> request) {
        getRequestQueue().add(request);
    }
}
