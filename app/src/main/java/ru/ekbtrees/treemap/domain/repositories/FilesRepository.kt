package ru.ekbtrees.treemap.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto

interface FilesRepository {

    suspend fun upload(filePath: String): Flow<UploadFileDto>
}