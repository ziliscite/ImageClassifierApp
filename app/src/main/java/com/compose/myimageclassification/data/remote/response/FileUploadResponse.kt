package com.compose.myimageclassification.data.remote.response

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("data")
    var data: Data = Data()
)