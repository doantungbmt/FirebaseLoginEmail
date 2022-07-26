package tung.lab.firebaseloginemail.model

import com.google.gson.annotations.SerializedName

data class FreeSkipModel(
    @SerializedName("date")
    val date : String,
    @SerializedName("durationTime")
    val durationTime : String,
    @SerializedName("skipCount")
    val skipCount : String
) {
}