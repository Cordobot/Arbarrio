package com.arbarrio.app.di

import androidx.room.Room
import com.arbarrio.app.data.local.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.arbarrio.app.data.repository.AuthRepositoryImpl
import com.arbarrio.app.domain.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.arbarrio.app.data.repository.PropertyRepositoryImpl
import com.arbarrio.app.domain.repository.PropertyRepository
import com.arbarrio.app.ui.screens.AuthViewModel
import com.arbarrio.app.ui.screens.HomeViewModel
import org.koin.dsl.module

val appModule = module {
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
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }
    
    // ViewModels
    factory { AuthViewModel(get()) }
    factory { HomeViewModel(get()) }
    
    // UseCases
}
