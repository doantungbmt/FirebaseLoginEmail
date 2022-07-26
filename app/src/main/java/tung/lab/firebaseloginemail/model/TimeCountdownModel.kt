package tung.lab.firebaseloginemail.model

import com.google.gson.annotations.SerializedName

class TimeCountdownModel(
    @SerializedName("date")
    val date: String,
    @SerializedName("durationTime")
    val durationTime: String,
    @SerializedName("skipCount")
    val skipCount: String,
    @SerializedName("targetTime")
    val targetTime: String
) {
}