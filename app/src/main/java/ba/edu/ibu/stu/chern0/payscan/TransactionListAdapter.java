package ba.edu.ibu.stu.chern0.payscan;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListViewHolder>{

    private JSONArray data;


    public TransactionListAdapter(JSONArray data) {
        this.data = data;
    }

    @NonNull
    @Override
    public TransactionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View transactionView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card, parent, false);
        return new TransactionListViewHolder(transactionView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListViewHolder holder, int position) {
        try {
            holder.getArticleNameText().setText(data.getJSONObject(position).getString("article_name"));
            holder.getCustomerText().setText(data.getJSONObject(position).getString("customer"));
            holder.getSellerTransText().setText(data.getJSONObject(position).getString("seller"));
            holder.getPriceTransText().setText(data.getJSONObject(position).getString("amount"));
            holder.getTimestampText().setText(data.getJSONObject(position).getString("timestamp"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }
}
