package tung.lab.firebaseloginemail.model

import com.google.gson.annotations.SerializedName

class SkipCountdownModel(
    @SerializedName("date")
    val date: String,
    @SerializedName("durationTime")
    val durationTime: String,
    @SerializedName("skipCount")
    val skipCount: String,
    @SerializedName("targetSkip")
    val targetSkip: String
) {
}