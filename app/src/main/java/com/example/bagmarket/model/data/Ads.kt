package com.example.bagmarket.model.data


import com.google.gson.annotations.SerializedName
data class AdsResponse(
    val success:Boolean,
    val ads:List<Ads>
)

data class Ads(
    @SerializedName("adId")
    val adId: String,
    @SerializedName("imageURL")
    val imageURL: String,
    @SerializedName("productId")
    val productId: String
)