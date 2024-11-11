package com.rpgportugal.orthanc.kt.discord.domain.archive

enum class ArchiveState {
    Unarchived,
    Archived,
    Impossible;

    override fun toString() = name.uppercase()
}