package ba.edu.ibu.stu.chern0.payscan;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private TextView name, price;
    private ImageView thumbnail;

    public ProductViewHolder(View view, final ProductsAdapter.OnItemClickListener listener, final ProductsAdapter.OnItemLongClickListener lListener) {
        super(view);
        name = view.findViewById(R.id.name);
        price = view.findViewById(R.id.price);
        thumbnail = view.findViewById(R.id.thumbnail);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
        /* handle long click on items */
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (lListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        lListener.onItemLongClick(position);
                    }
                }
                return true;
            }
        });
    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getPrice() {
        return price;
    }

    public void setPrice(TextView price) {
        this.price = price;
    }

    public ImageView getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageView thumbnail) {
        this.thumbnail = thumbnail;
    }
}
