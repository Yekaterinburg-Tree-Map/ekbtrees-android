package ru.ekbtrees.treemap.data.files

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import ru.ekbtrees.treemap.data.files.dto.UploadFileDto

class UploadFileRequestObserverDelegate(
    private val coroutineScope: CoroutineScope
) : RequestObserverDelegate {
    private val _flow: MutableSharedFlow<UploadFileDto> = MutableSharedFlow()
    val flow: Flow<UploadFileDto> = _flow

    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {

    }

    override fun onCompletedWhileNotObserving() {
    }

    override fun onError(context: Context, uploadInfo: UploadInfo, exception: Throwable) {
        coroutineScope.launch { _flow.emit(UploadFileDto.Error(exception)) }
    }

    override fun onProgress(context: Context, uploadInfo: UploadInfo) {
        coroutineScope.launch { _flow.emit(UploadFileDto.Progress) }
    }

    override fun onSuccess(
        context: Context,
        uploadInfo: UploadInfo,
        serverResponse: ServerResponse
    ) {
        val fileId = serverResponse.bodyString.toLong()
        coroutineScope.launch { _flow.emit(UploadFileDto.Success(fileId)) }
    }
}