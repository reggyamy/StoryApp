package com.reggya.stroryapp.data

import com.google.gson.annotations.SerializedName

data class PostResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("loginResult")
	val loginResult: LoginResultResponse? = null
)
