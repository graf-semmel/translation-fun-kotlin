/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.tristan.heinig.translationfunkotlin.database

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.tristan.heinig.translationfunkotlin.database.dao.TranslationDao
import android.tristan.heinig.translationfunkotlin.database.entity.TranslationItem
import android.tristan.heinig.translationfunkotlin.util.LiveDataTestUtil
import junit.framework.Assert.*
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Test the implementation of [TranslationDao]
 */
@RunWith(AndroidJUnit4::class)
class TranslationDaoTest {

  // executes each task synchronously
  @get:Rule var instantTaskExecutorRule = InstantTaskExecutorRule()
  private lateinit var mDatabase: TranslationDatabase
  private lateinit var mTranslationDao: TranslationDao

  private lateinit var translationItemNewerLessViews: TranslationItem
  private lateinit var translationItemOlderMoreViews: TranslationItem

  @Before
  @Throws(Exception::class)
  fun initDb() {
    mDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
      TranslationDatabase::class.java).allowMainThreadQueries().build()
    mTranslationDao = mDatabase.mTranslationDao()
    val mTimeMillis = System.currentTimeMillis()
    translationItemNewerLessViews = buildTranslationItem("1", "", "", "", mTimeMillis, 1)
    translationItemOlderMoreViews = buildTranslationItem("2", "", "", "", mTimeMillis - 1000, 10)
  }

  @After
  @Throws(Exception::class)
  fun closeDb() {
    mDatabase.close()
  }

  @Test
  @Throws(InterruptedException::class)
  fun getEmptyTranslationList() {
    val translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAll())
    assertTrue(translationItems.isEmpty())
  }

  @Test
  @Throws(InterruptedException::class)
  fun insertTranslationsAndDeleteAllTranslations() {
    mTranslationDao.insert(translationItemNewerLessViews)
    mTranslationDao.insert(translationItemOlderMoreViews)
    var translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate())
    assertEquals(2, translationItems.size)
    mTranslationDao.deleteAll()
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate())
    assertEquals(0, translationItems.size)
  }

  @Test
  @Throws(InterruptedException::class)
  fun getTranslationOrderedByDate() {
    mTranslationDao.insert(translationItemNewerLessViews)
    mTranslationDao.insert(translationItemOlderMoreViews)
    var translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate())
    assertEquals(2, translationItems.size)
    assertTrue(translationItems[0].date.after(translationItems[1].date))
    // change order of inserted items and test again
    mTranslationDao.deleteAll()
    mTranslationDao.insert(translationItemOlderMoreViews)
    mTranslationDao.insert(translationItemNewerLessViews)
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByDate())
    assertEquals(2, translationItems.size)
    assertTrue(translationItems[0].date.after(translationItems[1].date))
  }

  @Test
  @Throws(InterruptedException::class)
  fun getTranslationOrderedByViews() {
    mTranslationDao.insert(translationItemNewerLessViews)
    mTranslationDao.insert(translationItemOlderMoreViews)
    var translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByViews())
    assertEquals(2, translationItems.size)
    assertTrue(translationItems[0].views > translationItems[1].views)
    // change order of inserted items and test again
    mTranslationDao.deleteAll()
    mTranslationDao.insert(translationItemOlderMoreViews)
    mTranslationDao.insert(translationItemNewerLessViews)
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getAllOrderedByViews())
    assertEquals(2, translationItems.size)
    assertTrue(translationItems[0].views > translationItems[1].views)
  }

  @Test
  @Throws(InterruptedException::class)
  fun getTranslationOrderedByDateWithLimit() {
    val mTimeMillis = translationItemNewerLessViews.date.time
    mTranslationDao.insert(translationItemNewerLessViews)
    mTranslationDao.insert(translationItemOlderMoreViews)
    var translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostRecent(1))
    assertEquals(1, translationItems.size)
    assertEquals(mTimeMillis, translationItems[0].date.time)
    // change order of inserted items and test again
    mTranslationDao.deleteAll()
    mTranslationDao.insert(translationItemOlderMoreViews)
    mTranslationDao.insert(translationItemNewerLessViews)
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostRecent(1))
    assertEquals(1, translationItems.size)
    assertEquals(mTimeMillis, translationItems[0].date.time)
  }

  @Test
  @Throws(InterruptedException::class)
  fun getTranslationOrderedByViewsWithLimit() {
    mTranslationDao.insert(translationItemNewerLessViews)
    mTranslationDao.insert(translationItemOlderMoreViews)
    var translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostViewed(1))
    assertEquals(1, translationItems.size)
    assertEquals(10, translationItems[0].views)
    // change order of inserted items and test again
    mTranslationDao.deleteAll()
    mTranslationDao.insert(translationItemOlderMoreViews)
    mTranslationDao.insert(translationItemNewerLessViews)
    translationItems = LiveDataTestUtil.getValue(mTranslationDao.getMostViewed(1))
    assertEquals(1, translationItems.size)
    assertEquals(10, translationItems[0].views)
  }

  @Test
  fun insertTranslationAndGetTranslationByTextAndDeleteByText() {
    val text = "text"
    translationItemNewerLessViews.text = text
    mTranslationDao.insert(translationItemNewerLessViews)
    var translationItemDB = mTranslationDao.getByText(text)
    assertNotNull(translationItemDB)
    assertEquals(text, translationItemDB!!.text)
    mTranslationDao.delete(text)
    translationItemDB = mTranslationDao.getByText(text)
    assertNull(translationItemDB)
  }

  @Test
  fun updateTranslation() {
    val text = "text"
    val translation = "translation"
    translationItemNewerLessViews.text = text
    translationItemNewerLessViews.translation = translation
    mTranslationDao.insert(translationItemNewerLessViews)
    var translationItemDB = mTranslationDao.getByText(text)
    assertNotNull(translationItemDB)
    assertEquals(translation, translationItemDB!!.translation)
    val updatedTranslation = "updated translation"
    translationItemDB.translation = updatedTranslation
    mTranslationDao.update(translationItemDB)
    translationItemDB = mTranslationDao.getByText(text)
    assertEquals(updatedTranslation, translationItemDB!!.translation)
  }

  private fun buildTranslationItem(pText: String, pTranslation: String, pSourceLngCode: String, pTargetLngCode: String, pDate: Long,
    pViews: Int): TranslationItem = TranslationItem(pText, pTranslation, pSourceLngCode, pTargetLngCode, Date(pDate), pViews)
}
