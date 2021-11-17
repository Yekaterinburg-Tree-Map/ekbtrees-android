package ru.ekbtrees.treemap.ui.common

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest

fun uploadFile(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    filePath: String,
    requestObserverDelegate: RequestObserverDelegate
) {
    MultipartUploadRequest(context, "https://ekb-trees-help.ru/api/file/upload").apply {
        setMethod("POST")
        addFileToUpload(filePath, parameterName = "file")
        subscribe(context, lifecycleOwner, requestObserverDelegate)
        startUpload()
    }
}