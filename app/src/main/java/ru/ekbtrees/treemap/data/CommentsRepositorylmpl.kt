package ru.ekbtrees.treemap.data

import ru.ekbtrees.treemap.data.api.CommentApiService
import ru.ekbtrees.treemap.data.mappers.*
import ru.ekbtrees.treemap.data.result.RetrofitResult
import ru.ekbtrees.treemap.domain.entity.NewTreeCommentEntity
import ru.ekbtrees.treemap.domain.entity.TreeCommentEntity
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.UploadResult

class CommentsRepositorylmpl(
    private val commentApiService: CommentApiService
    ) : CommentsRepository {
    override suspend fun getTreeCommentBy(id: String): TreeCommentEntity {
        when(val result = commentApiService.getTreeCommentBy(treeId = id.toInt())){
            is RetrofitResult.Success -> {
                return result.value.toTreeCommentEntity()
            }
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

    override suspend fun updateTreeComment(treeCommentEntity: TreeCommentEntity): UploadResult {
        val treeCommentDto = treeCommentEntity.toTreeCommentDto()
        return when(commentApiService.updateTreeComment(treeCommentDto)){
            is RetrofitResult.Success -> {
                UploadResult.Success
            }
            is RetrofitResult.Failure<*> -> {
                UploadResult.Failure
            }
        }
    }

    override suspend fun deleteTreeComment(): UploadResult {
        TODO("Not yet implemented")
    }


}