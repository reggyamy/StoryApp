package com.reggya.stroryapp.data


data class ApiResponse<out T>(val type: ApiResponseType, val data: T?, val message: String?){
    companion object{
        fun <T> empty(value: T) = ApiResponse(ApiResponseType.EMPTY, value, null)
        fun error(message: String?) = ApiResponse(ApiResponseType.ERROR, null, message)
        fun <T> success(value: T) = ApiResponse(ApiResponseType.SUCCESS, value, null)
    }
}

enum class ApiResponseType {
    ERROR, SUCCESS, EMPTY,
}