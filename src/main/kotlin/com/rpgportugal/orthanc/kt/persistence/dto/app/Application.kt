package com.rpgportugal.orthanc.kt.persistence.dto.app

import jakarta.persistence.*

@Entity
@Table(name = "application")
open class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long = 0

    @Column(name = "name", nullable = false)
    open var name: String = ""

    @Column(name = "description", nullable = false)
    open var token: String = ""
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Application) return false

        if (id != other.id) return false
        if (name != other.name) return false
        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + token.hashCode()
        return result
    }


}