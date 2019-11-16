package android.tristan.heinig.translationfunkotlin.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {

  /**
   * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds. Once we got a notification via onChanged, we
   * stop observing.
   */
  @Throws(InterruptedException::class)
  fun <T> getValue(pLiveData: LiveData<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
      override fun onChanged(pObject: T?) {
        data[0] = pObject
        latch.countDown()
        pLiveData.removeObserver(this)
      }
    }
    pLiveData.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)

    return data[0] as T
  }
}