package ru.ekbtrees.treemap.domain.entity.commentsEntity

data class NewTreeCommentEntity(
    val treeId: String,
    val authorId: Long,
    val text: String,
    val createTime: String
)