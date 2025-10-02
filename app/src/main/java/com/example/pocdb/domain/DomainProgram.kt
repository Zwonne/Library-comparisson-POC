package com.example.pocdb.domain

data class DomainProgram(
    val id: Long,
    val name: String,
    val description: String?,
    val startTime: Long,
    val endTime: Long
)