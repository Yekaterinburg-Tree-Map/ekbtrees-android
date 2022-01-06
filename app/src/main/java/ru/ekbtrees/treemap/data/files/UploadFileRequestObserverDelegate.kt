package ru.ekbtrees.treemap.data.files

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto

class UploadFileRequestObserverDelegate(
    private val coroutineScope: CoroutineScope
) {
    private val _flow: MutableSharedFlow<UploadFileDto> = MutableSharedFlow()
    val flow: Flow<UploadFileDto> = _flow
}