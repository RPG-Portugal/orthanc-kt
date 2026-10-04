package com.rpgportugal.orthanc.kt.persistence.sql

import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.repository.CrudRepository
import arrow.core.Either
import jakarta.persistence.EntityManager
import kotlin.reflect.KClass

abstract class SqlCrudRepository<T : Any, ID>(
    protected val entityManager: EntityManager,
    private val entityClass: KClass<T>
) : CrudRepository<T, ID> {

    override fun findById(id: ID): Either<DbError, T?> = try {
        Either.Right(entityManager.find(entityClass.java, id))
    } catch (e: Exception) {
        Either.Left(DbError.Unknown(e.message ?: "Unknown error"))
    }

    override fun findAll(): Either<DbError, List<T>> = try {
        val query = entityManager.createQuery("FROM ${entityClass.simpleName}", entityClass.java)
        Either.Right(query.resultList)
    } catch (e: Exception) {
        Either.Left(DbError.Unknown(e.message ?: "Unknown error"))
    }

    override fun save(entity: T): Either<DbError, T> = try {
        val transaction = entityManager.transaction
        if (!transaction.isActive) {
            transaction.begin()
        }
        try {
            entityManager.persist(entity)
            entityManager.flush()
            if (transaction.isActive) {
                transaction.commit()
            }
            Either.Right(entity)
        } catch (e: Exception) {
            if (transaction.isActive) {
                transaction.rollback()
            }
            throw e
        }
    } catch (e: Exception) {
        Either.Left(DbError.Unknown(e.message ?: "Unknown error"))
    }

    override fun delete(id: ID): Either<DbError, Unit> = try {
        val transaction = entityManager.transaction
        if (!transaction.isActive) {
            transaction.begin()
        }
        try {
            val entity = entityManager.find(entityClass.java, id)
            if (entity != null) {
                entityManager.remove(entity)
            }
            entityManager.flush()
            if (transaction.isActive) {
                transaction.commit()
            }
            Either.Right(Unit)
        } catch (e: Exception) {
            if (transaction.isActive) {
                transaction.rollback()
            }
            throw e
        }
    } catch (e: Exception) {
        Either.Left(DbError.Unknown(e.message ?: "Unknown error"))
    }

}
