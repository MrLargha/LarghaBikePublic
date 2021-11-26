package ru.mrlargha.larghabike.model.converters

interface Converter<F, T> {
    fun convert(from: F): T
}

interface TwoWayConverter<F, T>: Converter<F, T> {
    fun convertReverse(from: T): F
}