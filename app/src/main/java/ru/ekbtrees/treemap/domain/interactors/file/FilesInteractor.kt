package ru.ekbtrees.treemap.domain.interactors.file

import ru.ekbtrees.treemap.domain.utils.Resource

interface FilesInteractor {
    suspend fun sendFiles(filePaths: List<String>, onFileUploaded: (Int, Resource<Long>) -> Unit)

    suspend fun deleteFile(fileId: Long): Resource<Unit>
}
