package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.UploadResult

interface CommentInteractor {
    suspend fun getTreeCommentBy(id: String): TreeCommentEntity

    suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult

    suspend fun updateTreeComment(treeCommentEntity: TreeCommentEntity): UploadResult

    suspend fun deleteTreeComment(): UploadResult
}