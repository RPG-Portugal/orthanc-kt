package com.rpgportugal.orthanc.kt.persistence.repository.jpa

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.SharedCacheMode
import jakarta.persistence.ValidationMode
import jakarta.persistence.spi.ClassTransformer
import jakarta.persistence.spi.PersistenceUnitInfo
import jakarta.persistence.spi.PersistenceUnitTransactionType
import java.lang.ClassLoader.getSystemClassLoader
import java.net.URL
import java.util.*
import javax.sql.DataSource


class OrthancPersistenceUnitInfo(
    private val classes: List<Class<*>>,
    private val jdbcUrl: String,
)  : PersistenceUnitInfo {
    override fun getPersistenceUnitName(): String {
        return "orthanc"
    }

    override fun getPersistenceProviderClassName(): String {
        return "org.hibernate.jpa.HibernatePersistenceProvider"
    }

    override fun getScopeAnnotationName(): String? {
        return null
    }

    override fun getQualifierAnnotationNames(): MutableList<String> {
        return Collections.emptyList()
    }

    override fun getTransactionType(): PersistenceUnitTransactionType {
        return PersistenceUnitTransactionType.RESOURCE_LOCAL
    }

    override fun getJtaDataSource(): DataSource {
        val ds = HikariDataSource()

        ds.jdbcUrl = jdbcUrl
        return ds
    }

    override fun getNonJtaDataSource(): DataSource? {
        return null
    }

    override fun getMappingFileNames(): MutableList<String> {
        return Collections.emptyList()
    }

    override fun getJarFileUrls(): MutableList<URL> {
        return Collections.emptyList()
    }

    override fun getPersistenceUnitRootUrl(): URL? {
        return null
    }

    override fun getManagedClassNames(): MutableList<String> {
        return classes.map { it.javaClass.name }.toMutableList()
    }

    override fun excludeUnlistedClasses(): Boolean {
        return false
    }

    override fun getSharedCacheMode(): SharedCacheMode? {
        return null
    }

    override fun getValidationMode(): ValidationMode? {
        return null
    }

    override fun getProperties(): Properties {
        return Properties()
    }

    override fun getPersistenceXMLSchemaVersion(): String? {
        return null
    }

    override fun getClassLoader(): ClassLoader {
        return getSystemClassLoader()
    }

    override fun addTransformer(p0: ClassTransformer?) {

    }

    override fun getNewTempClassLoader(): ClassLoader {
        return getSystemClassLoader()
    }


}