package android.tristan.heinig.translationfunkotlin

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(private val mDiskIO: Executor, private val mNetworkIO: Executor, private val mMainThread: Executor) {
    fun diskIO(): Executor = mDiskIO

    fun networkIO(): Executor = mNetworkIO

    fun mainThread(): Executor = mMainThread

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        private var INSTANCE: AppExecutors? = null
        val instance: AppExecutors
            get() = INSTANCE ?: AppExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(3), MainThreadExecutor())
                    .also { INSTANCE = it }
    }
}