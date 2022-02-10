package ru.ekbtrees.treemap.domain.interactors.file

import ru.ekbtrees.treemap.domain.repositories.FileRepository
import ru.ekbtrees.treemap.domain.utils.Resource
import javax.inject.Inject

class FilesInteractorImpl @Inject constructor(private val fileRepository: FileRepository) :
    FilesInteractor {
    override suspend fun sendFiles(
        filePaths: List<String>,
        onFileUploaded: (Int, Resource<Long>) -> Unit
    ) {
        filePaths.forEachIndexed { index, path ->
            val resource = fileRepository.upload(path)
            onFileUploaded(index, resource)
        }
    }

    override suspend fun deleteFile(fileId: Long): Resource<Unit> =
        fileRepository.deleteFile(fileId = fileId)
}