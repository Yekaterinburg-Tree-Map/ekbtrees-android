package ru.ekbtrees.treemap.data.files.dto

sealed class UploadFileDto {
    class Success(val fileId: Long): UploadFileDto()
    data class Error(
        val exception: Exception
    ): UploadFileDto()
}
