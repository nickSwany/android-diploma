package ru.practicum.android.diploma.data.dto.responses.fields

import com.google.gson.annotations.SerializedName

data class Employer(
    @SerializedName("accredited_it_employer")
    val accreditedItEmployer: Boolean,
    @SerializedName("alternate_url")
    val alternateUrl: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("logo_urls")
    val logoUrls: LogoUrls,
    @SerializedName("name")
    val name: String?,
    @SerializedName("trusted")
    val trusted: Boolean,
    @SerializedName("url")
    val url: String?
)
