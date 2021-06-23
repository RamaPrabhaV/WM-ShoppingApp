package com.example.shopping.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.shopping.Repository.ProductRepository;
import com.example.shopping.model.Product;

public class ProductsViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    public final MutableLiveData<Boolean> productsResponseLiveData;

    public ProductsViewModel(@NonNull Application application) {
        super(application);
        productsResponseLiveData = new MutableLiveData<>();
        productRepository = new ProductRepository(productsResponseLiveData);
    }

    @Nullable
    Product getItem(@NonNull final Integer index) {
        return productRepository.getItem(index);
    }

    int getItemCount() {
        return productRepository.getItemCount();
    }
}
