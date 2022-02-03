package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.utils.Resource

interface FileRepository {

    suspend fun upload(filePath: String): Resource<Long>

    suspend fun deleteFile(fileId: Long): Resource<Unit>
}