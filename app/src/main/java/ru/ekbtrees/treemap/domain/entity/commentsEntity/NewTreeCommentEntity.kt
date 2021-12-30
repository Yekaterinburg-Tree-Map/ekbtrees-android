package ru.ekbtrees.treemap.domain.entity.commentsEntity

data class NewTreeCommentEntity(
    val treeId: String,
    val authorId: String,
    val text: String,
    val createTime: String,
    val updateTime: String?
)