package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.commentsEntity.*
import ru.ekbtrees.treemap.domain.repositories.UploadResult

interface CommentInteractor {
    suspend fun getTreeCommentBy(id: Int): List<TreeCommentEntity>

    suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult

    suspend fun updateTreeComment(id: Int): UploadResult

    suspend fun deleteTreeComment(id: Int): UploadResult
}