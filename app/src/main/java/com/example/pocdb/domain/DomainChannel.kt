package com.example.pocdb.domain

data class DomainChannel(
    val id: Long,
    val name: String?,
    val programs: List<DomainProgram>
)