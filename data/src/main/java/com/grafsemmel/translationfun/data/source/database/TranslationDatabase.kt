package com.grafsemmel.translationfun.data.source.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.data.source.database.dao.TranslationDao
import com.grafsemmel.translationfun.data.source.database.entity.TranslationEntity
import java.util.*

@Database(entities = arrayOf(TranslationEntity::class), version = 1, exportSchema = false)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        private const val DATABASE_NAME = "translation_database"
        @Volatile
        private var INSTANCE: TranslationDatabase? = null
        private val databaseInitialisation = {
            val databaseActions = {
                val translationDao = INSTANCE!!.translationDao()
                val now = System.currentTimeMillis()
                translationDao.insert(TranslationEntity("How are you?", "Wie geht es dir?", "en", "de", Date(now - 1000 * 60 * 60), 10))
                translationDao.insert(TranslationEntity("I am well.", "Mit geht es gut.", "en", "de", Date(now), 1))
            }
            INSTANCE!!.runInTransaction(databaseActions)
        }

        fun getInstance(context: Context): TranslationDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: createDatabase(context).also { INSTANCE = it }
        }

        private fun createDatabase(context: Context): TranslationDatabase {
            val callback = object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    AppExecutors.instance.diskIO().execute { databaseInitialisation.invoke() }
                }
            }
            return Room.databaseBuilder(context.applicationContext, TranslationDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration().addCallback(callback).build()
        }
    }
}