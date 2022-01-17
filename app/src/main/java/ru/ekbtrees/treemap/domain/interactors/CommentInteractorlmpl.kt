package ru.ekbtrees.treemap.domain.interactors

import ru.ekbtrees.treemap.domain.entity.commentsEntity.*
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.UploadResult

class CommentInteractorlmpl(private val commentRepository: CommentsRepository) : CommentInteractor {
    override suspend fun getTreeCommentBy(id: Int): List<TreeCommentEntity> {
        return commentRepository.getTreeCommentBy(id)
    }

    override suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult {
        return commentRepository.saveTreeComment(newTreeCommentEntity)
    }

    override suspend fun updateTreeComment(id: Int): UploadResult {
        return commentRepository.updateTreeComment(id)
    }

    override suspend fun deleteTreeComment(id: Int): UploadResult {
        return commentRepository.deleteTreeComment(id)
    }
}