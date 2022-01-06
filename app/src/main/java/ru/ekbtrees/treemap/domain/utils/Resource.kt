package ru.ekbtrees.treemap.domain.utils

sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String? = null) : Resource<T>()
}
