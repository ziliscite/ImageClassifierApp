package com.compose.myimageclassification.data.remote.response

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("result")
    var result: String? = null,
    @SerializedName("confidenceScore")
    var confidenceScore: Double? = null,
    @SerializedName("isAboveThreshold")
    var isAboveThreshold: Boolean? = null,
    @SerializedName("createdAt")
    var createdAt: String? = null
)