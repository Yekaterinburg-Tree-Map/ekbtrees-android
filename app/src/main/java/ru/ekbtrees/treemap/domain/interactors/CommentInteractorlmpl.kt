package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.*
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.UploadResult

class CommentInteractorlmpl(private val commentRepository: CommentsRepository) : CommentInteractor {
    override suspend fun getTreeCommentBy(id: String): TreeCommentEntity {
        return commentRepository.getTreeCommentBy(id)
    }

    override suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult {
        return commentRepository.saveTreeComment(newTreeCommentEntity)
    }

    override suspend fun updateTreeComment(treeCommentEntity: TreeCommentEntity): UploadResult {
        return commentRepository.updateTreeComment(treeCommentEntity)
    }

    override suspend fun deleteTreeComment(): UploadResult {
        return deleteTreeComment()
    }
}