package com.example.shopping.network;


import java.util.List;
import javax.annotation.Generated;
import android.os.Parcelable;

import com.example.shopping.model.Product;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Generated by gson library from the Server API response.
 * Contains information about a single page of a prodcut and the number of total products.
 */
@Generated("jsonschema2pojo")
public class ProductsResponse implements Parcelable
{
    @SerializedName("products")
    @Expose
    private List<Product> products = null;
    @SerializedName("totalProducts")
    @Expose
    private Integer totalProducts;
    @SerializedName("pageNumber")
    @Expose
    private Integer pageNumber;
    @SerializedName("pageSize")
    @Expose
    private Integer pageSize;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    public final static Creator<ProductsResponse> CREATOR = new Creator<ProductsResponse>() {

        @SuppressWarnings({
                "unchecked"
        })
        public ProductsResponse createFromParcel(android.os.Parcel in) {
            return new ProductsResponse(in);
        }

        public ProductsResponse[] newArray(int size) {
            return (new ProductsResponse[size]);
        }

    };

    protected ProductsResponse(android.os.Parcel in) {
        in.readList(this.products, (com.example.shopping.model.Product.class.getClassLoader()));
        this.totalProducts = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.pageNumber = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.pageSize = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.statusCode = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public ProductsResponse() {
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Integer getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeList(products);
        dest.writeValue(totalProducts);
        dest.writeValue(pageNumber);
        dest.writeValue(pageSize);
        dest.writeValue(statusCode);
    }

    public int describeContents() {
        return 0;
    }
}