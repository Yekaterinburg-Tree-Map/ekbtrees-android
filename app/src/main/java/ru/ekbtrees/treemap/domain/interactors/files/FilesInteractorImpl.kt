package ru.ekbtrees.treemap.domain.interactors.files

import ru.ekbtrees.treemap.domain.repositories.FilesRepository
import ru.ekbtrees.treemap.domain.utils.Resource
import javax.inject.Inject

class FilesInteractorImpl @Inject constructor(private val filesRepository: FilesRepository) :
    FilesInteractor {
    override suspend fun sendFile(filePath: String): Resource<Long> =
        filesRepository.upload(filePath = filePath)
}