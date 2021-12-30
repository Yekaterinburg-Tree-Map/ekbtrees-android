package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.commentsEntity.*
import ru.ekbtrees.treemap.domain.repositories.UploadResult

interface CommentInteractor {
    suspend fun getTreeCommentBy(id: String): List<TreeCommentEntity>

    suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult

    suspend fun updateTreeComment(id: String): UploadResult

    suspend fun deleteTreeComment(id: String): UploadResult
}