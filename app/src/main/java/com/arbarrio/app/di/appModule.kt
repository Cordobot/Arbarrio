package com.arbarrio.app.di

import androidx.room.Room
import com.arbarrio.app.data.local.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.arbarrio.app.data.repository.AuthRepositoryImpl
import com.arbarrio.app.domain.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.arbarrio.app.data.repository.PropertyRepositoryImpl
import com.arbarrio.app.domain.repository.PropertyRepository
import com.arbarrio.app.ui.screens.*
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import com.arbarrio.app.data.local.PreferencesManager

val appModule = module {
    // Preferences (DataStore)
    single { PreferencesManager(androidContext()) }

    // Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Database
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "arbarrio_db"
        ).build()
    }
    
    // DAOs
    single { get<AppDatabase>().propertyDao() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }
    
    // ViewModels
    factory { AuthViewModel(get(), get()) }
    factory { HomeViewModel(get(), get()) }
    factory { AddPropertyViewModel(get(), get()) }
    factory { EditProfileViewModel(get()) }
    factory { MyPropertiesViewModel(get(), get()) }
    factory { FavoritesViewModel(get()) }
}
