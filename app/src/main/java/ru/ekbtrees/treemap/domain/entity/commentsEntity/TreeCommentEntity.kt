package ru.ekbtrees.treemap.domain.entity.commentsEntity

data class TreeCommentEntity(
    val id: Int,
    val createTime: String,
    val authorId: Int,
    val text: String,
    val treeId: Int
)
