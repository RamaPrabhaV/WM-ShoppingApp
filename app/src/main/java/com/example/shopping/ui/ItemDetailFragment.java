package com.example.shopping.ui;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.example.shopping.R;
import com.example.shopping.model.Product;
import com.example.shopping.databinding.FragmentItemDetailBinding;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemsListFragment}
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_CLICKED_PRODUCT = "clicked_product";

    /**
     * The placeholder content this fragment is presenting.
     */
    private Product currentItem;
    private FragmentItemDetailBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            currentItem = bundle.getParcelable(ARG_CLICKED_PRODUCT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (currentItem == null) {
            binding.getRoot().setVisibility(View.INVISIBLE);
            return;
        }

        TextView textView = binding.longDescription;
        TextView textViewProductName = binding.productName;
        TextView textViewShortDesc = binding.shortDescription;
        TextView textViewNumberOfReviews = binding.numberOfReviews;
        RatingBar ratingBar = binding.ratingBar;
        TextView textViewPrice = binding.price;
        TextView textViewInStock = binding.inStock;
        Button buttonAddToCart = binding.addToCartButton;
        ImageView productImageView = binding.itemPhoto;

        ratingBar.setRating(currentItem.getReviewRating());
        if (currentItem.getInStock())
            textViewInStock.setText(R.string.in_stock);
        else
            textViewInStock.setText(R.string.not_available);
        textViewNumberOfReviews.setText(String.format("%s%s%s", "(", currentItem.getReviewCount(), ")"));

        if (!TextUtils.isEmpty(currentItem.getProductImage())) {
            String imageUrl = Constants.SERVICE_BASE_URL + currentItem.getProductImage();
            Glide.with(view).load(imageUrl).into(productImageView);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textViewProductName.setText(Html.fromHtml(currentItem.getProductName(), Html.FROM_HTML_MODE_LEGACY));
            textViewPrice.setText(Html.fromHtml(currentItem.getPrice(), Html.FROM_HTML_MODE_LEGACY));
            textViewShortDesc.setText(Html.fromHtml(currentItem.getShortDescription(), Html.FROM_HTML_MODE_LEGACY));
            textView.setText(Html.fromHtml(currentItem.getLongDescription(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            textViewProductName.setText(Html.fromHtml(currentItem.getProductName()));
            textViewPrice.setText(Html.fromHtml(currentItem.getPrice()));
            textViewShortDesc.setText(Html.fromHtml(currentItem.getShortDescription()));
            textView.setText(Html.fromHtml(currentItem.getLongDescription()));
        }

        buttonAddToCart.setEnabled(currentItem.getInStock());
        buttonAddToCart.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "1 item added to cart", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
