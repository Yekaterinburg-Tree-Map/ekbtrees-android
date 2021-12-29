package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.*

interface CommentsRepository {

    suspend fun getTreeCommentBy(id: String): TreeCommentEntity

    suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult

    suspend fun updateTreeComment(treeCommentEntity: TreeCommentEntity): UploadResult

    suspend fun deleteTreeComment(): UploadResult
}

/** Класс для результата загрузки */
sealed class UploadResult {
    object Success: UploadResult()
    object Failure: UploadResult()
}