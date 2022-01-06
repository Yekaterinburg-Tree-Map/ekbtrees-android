package ru.ekbtrees.treemap.domain.interactors.files

import ru.ekbtrees.treemap.domain.utils.Resource

interface FilesInteractor {
    suspend fun sendFile(filePath: String): Resource<Long>
}
