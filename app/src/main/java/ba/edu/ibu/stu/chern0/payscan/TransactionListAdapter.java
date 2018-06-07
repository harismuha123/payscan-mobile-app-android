package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListViewHolder>{
    private Context mContext;
    private List<Transaction> transactionList;

    public TransactionListAdapter(Context mContext, List<Transaction> transactionList) {
        this.mContext = mContext;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View transactionView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_item, parent, false);
        return new TransactionListViewHolder(transactionView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        holder.getArticleNameText().setText(transaction.getArticle());
        holder.getCustomerText().setText(transaction.getCustomer());
        holder.getSellerTransText().setText(transaction.getSeller());
        holder.getPriceTransText().setText(transaction.getAmount());
        holder.getTimestampText().setText(transaction.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
