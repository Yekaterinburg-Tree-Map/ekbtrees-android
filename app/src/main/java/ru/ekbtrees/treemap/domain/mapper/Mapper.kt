package ru.ekbtrees.treemap.domain.mapper

interface Mapper<SRC, DST> {
    fun map(from: SRC): DST
}