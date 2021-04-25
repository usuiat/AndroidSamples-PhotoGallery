package net.engawapg.app.photogallery

import android.app.Application
import net.engawapg.app.photogallery.gallery.PhotoGalleryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
}

@JvmField
val appModule = module {
    viewModel {
        PhotoGalleryViewModel(androidApplication())
    }
}
