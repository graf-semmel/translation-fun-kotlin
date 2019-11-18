package com.grafsemmel.translationfun.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.grafsemmel.translationfun.data.AppExecutors
import com.grafsemmel.translationfun.data.local.database.dao.TranslationDao
import com.grafsemmel.translationfun.data.local.database.entity.TranslationEntity
import java.util.*

@Database(entities = arrayOf(TranslationEntity::class), version = 1, exportSchema = false)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun translationDao(): TranslationDao

    companion object {
        private const val DATABASE_NAME = "translation_database"
        private var INSTANCE: TranslationDatabase? = null
        private val onCreateCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                AppExecutors.diskIO().execute {
                    INSTANCE?.apply {
                        runInTransaction {
                            val translationDao = translationDao()
                            val now = System.currentTimeMillis()
                            translationDao.insert(TranslationEntity("How are you?", "Wie geht es dir?", "en", "de", Date(now - 1000 * 60 * 60), 10))
                            translationDao.insert(TranslationEntity("I am well.", "Mit geht es gut.", "en", "de", Date(now), 1))
                        }
                    }
                }
            }
        }

        fun getInstance(context: Context): TranslationDatabase = INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(context, TranslationDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .addCallback(onCreateCallback)
                    .build()
                    .also { INSTANCE = it }
        }
    }
}