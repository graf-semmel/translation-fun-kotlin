package com.grafsemmel.translationfun.data

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object AppExecutors {
    fun diskIO(): Executor = Executors.newSingleThreadExecutor()

    fun networkIO(): Executor = Executors.newFixedThreadPool(3)

    fun mainThread(): Executor = MainThreadExecutor()

    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }
}