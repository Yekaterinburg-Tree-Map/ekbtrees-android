package ru.ekbtrees.treemap.ui.model

sealed class PhotoUiModel {
    object Error : PhotoUiModel()
    class Uploading(val filePath: String) : PhotoUiModel()
    class Photo(val photoUrl: String, val photoId: Long) : PhotoUiModel()
}