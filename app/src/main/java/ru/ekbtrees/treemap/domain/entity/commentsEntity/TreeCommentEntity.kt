package ru.ekbtrees.treemap.domain.entity.commentsEntity

data class TreeCommentEntity(
    val id: String,
    val treeId: Long,
    val authorId: String,
    val text: String,
    val createTime: String
)
