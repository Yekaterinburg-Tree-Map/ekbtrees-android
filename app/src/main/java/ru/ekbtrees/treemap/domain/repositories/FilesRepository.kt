package ru.ekbtrees.treemap.domain.repositories

import ru.ekbtrees.treemap.domain.utils.Resource

interface FilesRepository {

    suspend fun upload(filePath: String): Resource<Long>
}