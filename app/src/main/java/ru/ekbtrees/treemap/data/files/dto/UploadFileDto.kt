package ru.ekbtrees.treemap.data.files.dto

sealed class UploadFileDto {
    object Success: UploadFileDto()
    object Progress: UploadFileDto()
    data class Error(
        val throwable: Throwable
    ): UploadFileDto()
}
