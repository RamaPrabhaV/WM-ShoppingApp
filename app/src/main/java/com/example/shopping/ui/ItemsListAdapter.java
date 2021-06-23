package com.example.shopping.ui;

import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shopping.Constants;
import com.example.shopping.databinding.ItemListContentBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.model.Product;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ProductViewHolder> {
    @NonNull
    private final ProductsViewModel productsViewModel;
    private final View.OnClickListener onClickListener;
    private final View.OnContextClickListener onContextClickListener;

    ItemsListAdapter(@NonNull final ProductsViewModel viewModel, View.OnClickListener onClickListener,
                     View.OnContextClickListener onContextClickListener) {
        productsViewModel = viewModel;
        this.onClickListener = onClickListener;
        this.onContextClickListener = onContextClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListContentBinding binding =
                ItemListContentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productsViewModel.getItem(position);
        if (product == null) {
            holder.reset();
            return;
        }

        holder.textViewProductName.setText(product.getProductName());
        holder.textViewShortDesc.setText(product.getShortDescription());
        holder.textViewPrice.setText(product.getPrice());

        holder.itemView.setTag(product);
        holder.itemView.setOnClickListener(onClickListener);

        if (!TextUtils.isEmpty(product.getProductImage())) {
            String imageUrl = Constants.SERVICE_BASE_URL + product.getProductImage();
            Glide.with(holder.itemView)
                    .load(imageUrl)
                    .into(holder.productImage);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.itemView.setOnContextClickListener(onContextClickListener);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.textViewProductName.setText(Html.fromHtml(product.getProductName(), Html.FROM_HTML_MODE_LEGACY));
            holder.textViewShortDesc.setText(Html.fromHtml(product.getShortDescription(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.textViewProductName.setText(Html.fromHtml(product.getProductName()));
            holder.textViewShortDesc.setText(Html.fromHtml(product.getShortDescription()));
        }

        holder.ratingBar.setRating(product.getReviewRating());
        holder.textViewNumberOfReviews.setText(String.format("%s%s%s", "(", product.getReviewCount(), ")"));
        holder.buttonAddToCart.setEnabled(product.getInStock());
        holder.buttonAddToCart.setOnClickListener(v -> Toast.makeText(v.getContext(), "1 item added to cart", Toast.LENGTH_LONG).show());
    }

    @Override
    public int getItemCount() {
        return productsViewModel.getItemCount();
    }

    public void datasetChanged() {
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewProductName;
        final ImageView productImage;
        final RatingBar ratingBar;
        final TextView textViewNumberOfReviews;
        final TextView textViewPrice;
        final TextView textViewShortDesc;
        final Button buttonAddToCart;

        ProductViewHolder(ItemListContentBinding binding) {
            super(binding.getRoot());
            textViewProductName = binding.productName;
            productImage = binding.itemPhoto;
            ratingBar = binding.ratingBar;
            textViewNumberOfReviews = binding.reviewCount;
            textViewPrice = binding.itemPrice;
            textViewShortDesc = binding.shortDescription;
            buttonAddToCart = binding.addToCartButton;
            reset();
        }

        void reset() {
            textViewProductName.setText("");
            ratingBar.setRating(0);
            textViewNumberOfReviews.setText("0");
            textViewPrice.setText("$");
            textViewShortDesc.setText("");
            buttonAddToCart.setEnabled(false);
        }
    }
}
