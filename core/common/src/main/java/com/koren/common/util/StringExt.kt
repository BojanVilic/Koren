package com.koren.common.util

fun String?.orUnknownError(): String {
    return this ?: "Unknown error"
}