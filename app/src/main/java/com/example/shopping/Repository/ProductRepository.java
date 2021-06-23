package com.example.shopping.Repository;

import android.util.LruCache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.shopping.Constants;
import com.example.shopping.model.Product;
import com.example.shopping.network.ProductsResponse;
import com.example.shopping.network.GetProductsService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.shopping.Constants.LOADING;
import static com.example.shopping.Constants.READY;


/**
 * Communicates with the server to fetch the list of products.
 * Contains cache of most recently downloaded products.
 * Sends notifications about its internal status changes.
 */
public class ProductRepository {

    private static final int PAGE_SIZE = 30;
    private static final int NONE = -1;

    private final GetProductsService getProductsService;
    private final MutableLiveData<Boolean> productsResponseLiveData;
    private final LruCache<Integer, Product> inMemoryDb = new LruCache<>(PAGE_SIZE * 2);
    private int totalCount = 1;
    private int currentPageRequested = NONE;

    /**
     * Initialize all the member variables.
     *
     * @param productsLiveData live data to communicate internal state changes.
     */
    public ProductRepository(final MutableLiveData<Boolean> productsLiveData) {
        productsResponseLiveData = productsLiveData;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        getProductsService = new retrofit2.Retrofit.Builder()
                .baseUrl(Constants.SERVICE_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetProductsService.class);
    }

    /**
     * Creates retrofit service service to make call to the server.
     * asynchronously parse response inside method onResponse.
     * Notifies observers about the state change.
     *
     * @param page index of the page of the products to be retrieved.
     */
    private void fetchProductsAsync(@NonNull Integer page) {
        getProductsService.getProducts(page, PAGE_SIZE)
                .enqueue(new Callback<ProductsResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductsResponse> call, @NonNull Response<ProductsResponse> response) {
                        if (response.body() != null) {
                            ProductsResponse products = response.body();
                            totalCount = products.getTotalProducts();
                            currentPageRequested = NONE;
                            int index = (products.getPageNumber() - 1) * products.getPageSize();
                            for (Product item : products.getProducts()) {
                                cleanupProductText(item);
                                inMemoryDb.put(index++, item);
                            }
                            productsResponseLiveData.postValue(READY);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductsResponse> call, @NonNull Throwable t) {
                        productsResponseLiveData.postValue(READY);
                    }
                });
    }

    /**
     * Remove non printable characters from the product name and the descriptions.
     *
     * @param product input product object to be cleaned up.
     */
    private void cleanupProductText(@NonNull final Product product) {
        String badChars = "�";
        String text = product.getProductName();
        if (text != null) {
            text = text.replaceAll(badChars, " ");
            product.setProductName(text);
        }
        text = product.getShortDescription();
        if (text != null) {
            text = text.replaceAll(badChars, " ");
            product.setShortDescription(text);
        }
        text = product.getLongDescription();
        if (text != null) {
            text = text.replaceAll(badChars, " ");
            product.setLongDescription(text);
        }
    }

    /**
     * Gets the product object from the cache by index.
     * If the product for the given index is not in the cache, trigger request to the server.
     *
     * @param index of the needed product object.
     * @return product in the given index or null if the index not found.
     */
    @Nullable
    public Product getItem(@NonNull final Integer index) {
        Product prod = inMemoryDb.get(index);
        if (prod == null) {
            // not in cache
            tryToLoadPage(index);
        } else {
            // preload next page if index is the last item available in cache
            if (index < totalCount && inMemoryDb.get(index + 1) == null) {
                tryToLoadPage(index + 1);
            }
        }

        return prod;
    }

    /**
     * Returns the total number of the products.
     *
     * @return the total number of the products.
     */
    public int getItemCount() {
        return totalCount;
    }

    /**
     * Validate the item index and calculate the page number to be downloaded.
     *
     * @param itemIndex index of the product needed.
     */
    private void tryToLoadPage(int itemIndex) {
        if (currentPageRequested == NONE && itemIndex < totalCount) {
            int pageNumber = itemIndex / PAGE_SIZE + 1;
            currentPageRequested = pageNumber;
            productsResponseLiveData.postValue(LOADING);
            fetchProductsAsync(pageNumber);
        }
    }
}
