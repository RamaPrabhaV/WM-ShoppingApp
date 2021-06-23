package com.example.shopping.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit Interface to fetch the list of products from the walmart server through rest APIs.
 * Page Number of the list of products starts from 1.
 * Page Size is the number of products returned in a page. Maximum is 30.
 */
public interface GetProductsService {
    @GET("/walmartproducts/{pageNumber}/{pageSize}")
    Call<ProductsResponse> getProducts(
            @Path("pageNumber") Integer pageNumber,
            @Path("pageSize") Integer pageSize);
}

