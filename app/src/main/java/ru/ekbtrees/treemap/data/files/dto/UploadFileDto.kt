package ru.ekbtrees.treemap.data.files.dto

sealed class UploadFileDto {
    class Success(val fileId: Long): UploadFileDto()
    object Progress: UploadFileDto()
    data class Error(
        val throwable: Throwable
    ): UploadFileDto()
}