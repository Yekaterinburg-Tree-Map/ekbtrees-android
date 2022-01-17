package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.entity.commentsEntity.*

interface CommentsRepository {

    suspend fun getTreeCommentBy(id: Int): List<TreeCommentEntity>

    suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult

    suspend fun updateTreeComment(id: Int): UploadResult

    suspend fun deleteTreeComment(id: Int): UploadResult
}

