package ru.ekbtrees.treemap.domain.utils

/** Класс для результата загрузки */
sealed class UploadResult {
    object Success: UploadResult()
    object Failure: UploadResult()
}