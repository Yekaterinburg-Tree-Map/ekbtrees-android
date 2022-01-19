package ru.ekbtrees.treemap.domain.interactors.file

import ru.ekbtrees.treemap.domain.repositories.FileRepository
import ru.ekbtrees.treemap.domain.utils.Resource
import javax.inject.Inject

class FilesInteractorImpl @Inject constructor(private val fileRepository: FileRepository) :
    FilesInteractor {
    override suspend fun sendFile(filePath: String): Resource<Long> =
        fileRepository.upload(filePath = filePath)
}