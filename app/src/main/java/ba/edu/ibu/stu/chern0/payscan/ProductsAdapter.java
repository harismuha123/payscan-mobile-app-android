package ba.edu.ibu.stu.chern0.payscan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductViewHolder>{
    private Context mContext;
    private List<Product> productList;
    private OnBottomReachedListener onBottomReachedListener;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ProductsAdapter(Context mContext, List<Product> productList) {
        this.mContext = mContext;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.product_card, parent, false);
        return new ProductViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        if (position == productList.size() - 1) {
            onBottomReachedListener.onBottomReached(position);
        }

        Product product = productList.get(position);
        holder.getName().setText(product.getName());
        holder.getPrice().setText(product.getPrice());

        /* Load images with Glide */
        Glide.with(mContext).load(product.getThumbnail())
                            .apply(new RequestOptions()
                                .centerCrop())
                            .into(holder.getThumbnail());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }
}
