package ru.ekbtrees.treemap.data

import ru.ekbtrees.treemap.data.api.CommentApiService
import ru.ekbtrees.treemap.data.mappers.*
import ru.ekbtrees.treemap.data.result.RetrofitResult
import ru.ekbtrees.treemap.domain.entity.commentsEntity.*
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.UploadResult

class CommentsRepositoryImpl(
    private val commentApiService: CommentApiService
    ) : CommentsRepository {
    override suspend fun getTreeCommentBy(id: Int): List<TreeCommentEntity> {
        when(val result = commentApiService.getTreeCommentBy(id)){
            is RetrofitResult.Success -> return result.value.map { comment -> comment.toTreeCommentEntity() }
            is RetrofitResult.Failure<*> -> {
                error(result)
            }
            else -> error("Unexpected case")
        }
    }

    override suspend fun saveTreeComment(newTreeCommentEntity: NewTreeCommentEntity): UploadResult {
        val treeCommentDto = newTreeCommentEntity.toNewTreeCommentDto()
        return when(commentApiService.saveTreeComment(treeCommentDto)){
            is RetrofitResult.Success -> {
                UploadResult.Success
            }
            is RetrofitResult.Failure<*> -> {
                UploadResult.Failure
            }
        }
    }

    override suspend fun updateTreeComment(id: Int): UploadResult {
        return when(commentApiService.updateTreeComment(id)){
            is RetrofitResult.Success -> {
                UploadResult.Success
            }
            is RetrofitResult.Failure<*> -> {
                UploadResult.Failure
            }
        }
    }

    override suspend fun deleteTreeComment(id: Int): UploadResult {
        return when(commentApiService.deleteTreeComment(id)) {
            is RetrofitResult.Success -> {
                UploadResult.Success
            }
            is RetrofitResult.Failure<*> -> {
                UploadResult.Failure
            }
        }
    }


}