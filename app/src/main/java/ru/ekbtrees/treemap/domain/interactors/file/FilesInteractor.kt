package ru.ekbtrees.treemap.domain.interactors.file

import ru.ekbtrees.treemap.domain.utils.Resource

interface FilesInteractor {
    suspend fun sendFile(filePath: String): Resource<Long>

    suspend fun deleteFile(fileId: Long): Resource<Unit>
}
