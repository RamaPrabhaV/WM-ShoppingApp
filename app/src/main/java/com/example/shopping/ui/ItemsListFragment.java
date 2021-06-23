package com.example.shopping.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shopping.R;
import com.example.shopping.databinding.FragmentItemListBinding;
import com.example.shopping.model.Product;

import static com.example.shopping.Constants.LOADING;
import static com.example.shopping.Constants.READY;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends Fragment {

    private FragmentItemListBinding binding;
    private ProductsViewModel viewModel;

    public ItemsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentItemListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.itemList;

        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        View itemDetailFragmentContainer = view.findViewById(R.id.item_detail_nav_container);

        /* Click Listener to trigger navigation based on if you have
         * a single pane layout or two pane layout
         */
        View.OnClickListener onClickListener = itemView -> {
            Product item =
                    (Product) itemView.getTag();
            Bundle arguments = new Bundle();
            arguments.putParcelable(ItemDetailFragment.ARG_CLICKED_PRODUCT, item);
            if (itemDetailFragmentContainer != null) {
                Navigation.findNavController(itemDetailFragmentContainer)
                        .navigate(R.id.fragment_item_detail, arguments);
            } else {
                Navigation.findNavController(itemView).navigate(R.id.show_item_detail, arguments);
            }
        };

        /*
         * Context click listener to handle Right click events
         * from mice and trackpad input to provide a more native
         * experience on larger screen devices
         */
        View.OnContextClickListener onContextClickListener = itemView -> {
            Product item =
                    (Product) itemView.getTag();
            Toast.makeText(
                    itemView.getContext(),
                    "Context click of item " + item.getProductId(),
                    Toast.LENGTH_LONG
            ).show();
            return true;
        };

        setupRecyclerView(recyclerView, onClickListener, onContextClickListener);
    }

    private void setupRecyclerView(
            RecyclerView recyclerView,
            View.OnClickListener onClickListener,
            View.OnContextClickListener onContextClickListener
    ) {
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        viewModel = new ViewModelProvider(this).get(ProductsViewModel.class);
        ItemsListAdapter adapter = new ItemsListAdapter(viewModel, onClickListener,
                onContextClickListener);

        viewModel.productsResponseLiveData.observe(getViewLifecycleOwner(), repo_state -> {
            ImageView loading = binding.spinner;
            loading.setVisibility(repo_state == LOADING ? View.VISIBLE : View.GONE);
            if (repo_state == READY) {
                adapter.datasetChanged();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        // load initial data and show loading spinner
        viewModel.getItem(0);
        Glide.with(ItemsListFragment.this)
                .load(R.drawable.loading7)
                .into(binding.spinner);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    viewModel.getItem(layoutManager.findFirstVisibleItemPosition() + 1);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
