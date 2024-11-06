package com.rpgportugal.orthanc.kt.discord

enum class ArchiveState {
    Unarchived,
    Archived,
    Impossible;

    override fun toString() = name.uppercase()
}