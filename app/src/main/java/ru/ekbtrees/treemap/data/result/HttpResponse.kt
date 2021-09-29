package ru.ekbtrees.treemap.data.result

interface HttpResponse {

    val statusCode: Int

    val statusMessage: String?

    val url: String?
}