package com.reggya.stroryapp.data

import com.google.gson.annotations.SerializedName

data class LoginResultResponse(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("token")
	val token: String? = null
)
