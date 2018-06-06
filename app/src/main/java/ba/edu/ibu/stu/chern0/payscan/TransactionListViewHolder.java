package ba.edu.ibu.stu.chern0.payscan;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class TransactionListViewHolder extends RecyclerView.ViewHolder {

    public TextView customerText, sellerTransText, priceTransText, timestampText, articleNameText;

    public TransactionListViewHolder(View v) {
        super(v);

        customerText = v.findViewById(R.id.customerText);
        sellerTransText = v.findViewById(R.id.sellerTransText);
        priceTransText = v.findViewById(R.id.priceTransText);
        timestampText = v.findViewById(R.id.timestampText);
        articleNameText = v.findViewById(R.id.articleNameText);
    }

    public TextView getCustomerText() {
        return customerText;
    }

    public void setCustomerText(TextView customerText) {
        this.customerText = customerText;
    }

    public TextView getSellerTransText() {
        return sellerTransText;
    }

    public void setSellerTransText(TextView sellerTransText) {
        this.sellerTransText = sellerTransText;
    }

    public TextView getPriceTransText() {
        return priceTransText;
    }

    public void setPriceTransText(TextView priceTransText) {
        this.priceTransText = priceTransText;
    }

    public TextView getTimestampText() {
        return timestampText;
    }

    public void setTimestampText(TextView timestampText) {
        this.timestampText = timestampText;
    }

    public TextView getArticleNameText() {
        return articleNameText;
    }

    public void setArticleNameText(TextView articleNameText) {
        this.articleNameText = articleNameText;
    }
}
