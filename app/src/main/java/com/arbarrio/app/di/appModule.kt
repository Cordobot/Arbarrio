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
import org.koin.androidx.viewmodel.dsl.viewModel
import com.arbarrio.app.data.local.PreferencesManager
import com.arbarrio.app.data.location.LocationService

val appModule = module {
    // Location
    single { LocationService(androidContext()) }

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
    single<PropertyRepository> { PropertyRepositoryImpl(get(), get()) }
    
    // ViewModels
    viewModel { AuthViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { AddPropertyViewModel(get(), get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { MyPropertiesViewModel(get(), get()) }
    viewModel { (propertyId: String) -> PropertyDetailViewModel(get(), propertyId) }
    viewModel { FavoritesViewModel(get()) }
}
