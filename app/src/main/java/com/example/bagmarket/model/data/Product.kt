package com.example.bagmarket.model.data


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
data class ProductResponse(
    val success:Boolean,
    val products:List<Product>
)

@Entity("product_table")
data class Product(
    @SerializedName("category")
    val category: String,
    @SerializedName("detailText")
    val detailText: String,
    @SerializedName("imgUrl")
    val imgUrl: String,
    @SerializedName("material")
    val material: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: String,

    @PrimaryKey
    @SerializedName("productId")
    val productId: String,

    @SerializedName("soldItem")
    val soldItem: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("quantity")
    val quantity: String?
)