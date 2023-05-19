package com.example.bagmarket.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bagmarket.di.myModules
import com.example.bagmarket.model.repository.TokenInMemory
import com.example.bagmarket.model.repository.user.UserRepository
import com.example.bagmarket.ui.features.IntroScreen
import com.example.bagmarket.ui.features.cart.CartScreen
import com.example.bagmarket.ui.features.category.CategoryScreen
import com.example.bagmarket.ui.features.main.MainScreen
import com.example.bagmarket.ui.features.product.ProductScreen
import com.example.bagmarket.ui.features.profile.ProfileScreen
import com.example.bagmarket.ui.features.signIn.SignInScreen
import com.example.bagmarket.ui.features.signUp.SignUpScreen
import com.example.bagmarket.ui.theme.BackgroundMain
import com.example.bagmarket.ui.theme.MainAppTheme
import com.example.bagmarket.util.KEY_CATEGORY_ARG
import com.example.bagmarket.util.KEY_PRODUCT_ARG
import com.example.bagmarket.util.MyScreens
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.get
import dev.burnoo.cokoin.navigation.KoinNavHost
import org.koin.android.ext.koin.androidContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Koin(appDeclaration = {
                androidContext(this@MainActivity)
                modules(myModules)
            }){
                MainAppTheme {
                    Surface(
                        color = BackgroundMain,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val userRepository:UserRepository=get()
                        userRepository.loadToken()
                        BazaarUi()
                    }
                }
            }
        }
    }
}


@Composable
fun BazaarUi() {
    val navController = rememberNavController()
    KoinNavHost(navController = navController, startDestination = MyScreens.MainScreen.route)
    {

        composable(MyScreens.MainScreen.route) {
            if(TokenInMemory.token !=null ){
                MainScreen()
            }else{
                IntroScreen()}

        }
        composable(
            route = MyScreens.ProductScreen.route + "/" + "{$KEY_PRODUCT_ARG}",
            arguments = listOf(navArgument(KEY_PRODUCT_ARG) {
                type = NavType.StringType
            })
        ) {
            ProductScreen(it.arguments!!.getString(KEY_PRODUCT_ARG, "null")) //id product
        }
        composable(
            route = MyScreens.CategoryScreen.route + "/" + "{$KEY_CATEGORY_ARG}",
            arguments = listOf(navArgument(KEY_CATEGORY_ARG) {
                type = NavType.StringType
            })
        ) {
            CategoryScreen(it.arguments!!.getString(KEY_CATEGORY_ARG, "null"))
        }
        composable(MyScreens.ProfileScreen.route) {
            ProfileScreen()
        }
        composable(MyScreens.CartScreen.route) {
            CartScreen()
        }
        composable(MyScreens.SignUpScreen.route) {
            SignUpScreen()
        }
        composable(MyScreens.SignInScreen.route) {
            SignInScreen()
        }


    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Koin(appDeclaration = {modules(myModules)}){
        MainAppTheme {
            Surface(
                color = BackgroundMain,
                modifier = Modifier.fillMaxSize()
            ) {
                BazaarUi()
            }
        }
    }
}