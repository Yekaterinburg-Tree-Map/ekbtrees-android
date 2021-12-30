package ru.ekbtrees.treemap.data

import ru.ekbtrees.treemap.data.api.CommentApiService
import ru.ekbtrees.treemap.data.mappers.*
import ru.ekbtrees.treemap.data.result.RetrofitResult
import ru.ekbtrees.treemap.domain.entity.commentsEntity.*
import ru.ekbtrees.treemap.domain.repositories.CommentsRepository
import ru.ekbtrees.treemap.domain.repositories.UploadResult

class CommentsRepositorylmpl(
    private val commentApiService: CommentApiService
    ) : CommentsRepository {
    override suspend fun getTreeCommentBy(id: String): List<TreeCommentEntity> {
        val result = commentApiService.getTreeCommentBy(id)
        when(result){
            is RetrofitResult.Success -> {
                val treeCommentEntityList = mutableListOf<TreeCommentEntity>()
                for (comm in result.value) {
                    treeCommentEntityList.add(comm.toTreeCommentEntity())
                }
                return treeCommentEntityList
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

    override suspend fun updateTreeComment(id: String): UploadResult {
        return when(commentApiService.updateTreeComment(id)){
            is RetrofitResult.Success -> {
                UploadResult.Success
            }
            is RetrofitResult.Failure<*> -> {
                UploadResult.Failure
            }
        }
    }

    override suspend fun deleteTreeComment(id: String): UploadResult {
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