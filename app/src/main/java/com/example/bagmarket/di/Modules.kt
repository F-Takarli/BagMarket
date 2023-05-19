package com.example.bagmarket.di

import android.content.Context
import androidx.room.Room
import com.example.bagmarket.model.db.AppDatabase
import com.example.bagmarket.model.net.createApiService
import com.example.bagmarket.model.repository.cart.CartRepository
import com.example.bagmarket.model.repository.cart.CartRepositoryImpl
import com.example.bagmarket.model.repository.comments.CommentRepository
import com.example.bagmarket.model.repository.comments.CommentRepositoryImpl
import com.example.bagmarket.model.repository.product.ProductRepository
import com.example.bagmarket.model.repository.product.ProductRepositoryImpl
import com.example.bagmarket.model.repository.user.UserRepository
import com.example.bagmarket.model.repository.user.UserRepositoryImpl
import com.example.bagmarket.ui.features.cart.CartViewModel
import com.example.bagmarket.ui.features.category.CategoryViewModel
import com.example.bagmarket.ui.features.main.MainViewModel
import com.example.bagmarket.ui.features.product.ProductViewModel
import com.example.bagmarket.ui.features.profile.ProfileViewModel
import com.example.bagmarket.ui.features.signIn.SignInViewModel
import com.example.bagmarket.ui.features.signUp.SignUpViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModules = module {
    single { androidContext().getSharedPreferences("data", Context.MODE_PRIVATE) }
    single { createApiService() }
    single<UserRepository> { UserRepositoryImpl(get(),get()) }

    viewModel { SignUpViewModel(get()) }

    viewModel { SignInViewModel(get()) }

    //-----------------------------------------------
    single { Room.databaseBuilder(androidContext(),AppDatabase::class.java,"app_database.db").build() }
    single<ProductRepository>{ ProductRepositoryImpl( get(),get<AppDatabase>().productDao() ) }
    viewModel {(isNetConnected:Boolean) -> MainViewModel(get(),get(),isNetConnected) }
    //-------------------------------------------------------------------
    viewModel { CategoryViewModel(get()) }
    //-------------------------------------------------------------------
    single<CommentRepository>{CommentRepositoryImpl(get())}
    single<CartRepository>{ CartRepositoryImpl(get(),get()) }
    viewModel { ProductViewModel(get(),get(),get()) }
    //------------------------------------------------
    viewModel { ProfileViewModel(get()) }
    //------------------------------------------------
    viewModel { CartViewModel(get(),get()) }
}