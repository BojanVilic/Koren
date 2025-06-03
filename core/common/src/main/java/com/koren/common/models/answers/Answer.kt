package com.koren.common.models.answers

data class Answer(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val tags: List<TagType> = emptyList(),
    val attachmentUrl: String? = null,
    val createdByUserId: String = "",
    val createdAtTimestamp: Long = System.currentTimeMillis(),

)

enum class TagType {
    CONTACT,
    MEDICAL,
    HOME,
    LINK,
    SCHOOL,
    PET,
    GENERIC
}