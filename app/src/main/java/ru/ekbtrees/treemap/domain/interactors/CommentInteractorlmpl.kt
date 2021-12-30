package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.commentsEntity.*
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.UploadResult

class CommentInteractorlmpl(private val commentRepository: CommentsRepository) : CommentInteractor {
    override suspend fun getTreeCommentBy(id: String): List<TreeCommentEntity> {
        return commentRepository.getTreeCommentBy(id)
    }

    override suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult {
        return commentRepository.saveTreeComment(newTreeCommentEntity)
    }

    override suspend fun updateTreeComment(id: String): UploadResult {
        return commentRepository.updateTreeComment(id)
    }

    override suspend fun deleteTreeComment(id: String): UploadResult {
        return commentRepository.deleteTreeComment(id)
    }
}