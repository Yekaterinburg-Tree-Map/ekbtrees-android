package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.commentsEntity.*

interface CommentsRepository {

    suspend fun getTreeCommentBy(id: String): List<TreeCommentEntity>

    suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult

    suspend fun updateTreeComment(id: String): UploadResult

    suspend fun deleteTreeComment(id: String): UploadResult
}

