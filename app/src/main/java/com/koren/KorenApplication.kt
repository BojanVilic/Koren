package com.koren

import android.app.Application
import androidx.work.Configuration
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.koren.map.service.LocationWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class KorenApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: LocationWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        SingletonImageLoader.setSafe {
            ImageLoader.Builder(applicationContext)
                .networkCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .memoryCache {
                    MemoryCache.Builder()
                        .maxSizePercent(applicationContext, 0.25)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.resolve("image_cache"))
                        .maxSizePercent(0.02)
                        .build()
                }
                .build()
        }
    }
}