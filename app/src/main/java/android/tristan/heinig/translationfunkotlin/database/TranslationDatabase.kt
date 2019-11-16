package android.tristan.heinig.translationfunkotlin.database

import android.content.Context
import android.tristan.heinig.translationfunkotlin.AppExecutors
import android.tristan.heinig.translationfunkotlin.database.dao.TranslationDao
import android.tristan.heinig.translationfunkotlin.database.entity.TranslationItem
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.*

@Database(entities = arrayOf(TranslationItem::class), version = 1, exportSchema = false)
abstract class TranslationDatabase : RoomDatabase() {
    abstract fun mTranslationDao(): TranslationDao

    private class DataInitialisation : Runnable {
        override fun run() {
            val databaseActions = Runnable {
                val translationDao = INSTANCE!!.mTranslationDao()
                val now = System.currentTimeMillis()
                translationDao.insert(TranslationItem("How are you?", "Wie geht es dir?", "en", "de", Date(now - 1000 * 60 * 60), 10))
                translationDao.insert(TranslationItem("I am well.", "Mit geht es gut.", "en", "de", Date(now), 1))
            }
            INSTANCE!!.runInTransaction(databaseActions)
        }
    }

    companion object {
        private const val DATABASE_NAME = "translation_database"
        @Volatile
        private var INSTANCE: TranslationDatabase? = null

        fun getInstance(context: Context): TranslationDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: createDatabase(context).also { INSTANCE = it }
                }

        private fun createDatabase(context: Context): TranslationDatabase {
            val callback = object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    AppExecutors.instance.diskIO().execute(DataInitialisation())
                }
            }
            return Room.databaseBuilder(context.applicationContext, TranslationDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration().addCallback(callback).build()
        }
    }
}