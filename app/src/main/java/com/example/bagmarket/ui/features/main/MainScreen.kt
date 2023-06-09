package com.example.bagmarket.ui.features.main

import android.widget.Toast
import com.example.bagmarket.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.bagmarket.di.myModules
import com.example.bagmarket.model.data.Ads
import com.example.bagmarket.model.data.CheckOut
import com.example.bagmarket.model.data.Product
import com.example.bagmarket.ui.theme.*
import com.example.bagmarket.util.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.Koin
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import ir.dunijet.dunibazaar.util.NetworkChecker
import org.koin.core.parameter.parametersOf
import androidx.compose.material.IconButton as IconButton1


@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    Koin(appDeclaration = { modules(myModules) }) {
        MainAppTheme {
            Surface(
                color = BackgroundMain,
                modifier = Modifier.fillMaxSize()
            ) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val uiController = rememberSystemUiController()
    SideEffect { uiController.setStatusBarColor(Color.White) }
    val viewModel =
        getNavViewModel<MainViewModel>(parameters = { parametersOf(NetworkChecker(context).isInternetConnected) })
    val navigation = getNavController()
    if (NetworkChecker(context).isInternetConnected) {
        viewModel.loadBadgeNumber()
    }
    if (viewModel.getPaymentStatus() == PAYMENT_PENDING) {
        if (NetworkChecker(context).isInternetConnected) {
            viewModel.getCheckoutData()
        }
    }
    Box{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            if (viewModel.showProgressBar.value) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Blue
                )
            }
            TopToolbar(badgeNumber = viewModel.badgeNumber.value,
                onCartClicked = {
                    if(NetworkChecker(context).isInternetConnected)
                        navigation.navigate(MyScreens.CartScreen.route)
                    else
                        Toast.makeText(context,"Please Connect To Internet",Toast.LENGTH_SHORT).show()
                }, onProfileClicked = {
                    navigation.navigate(MyScreens.ProfileScreen.route)
                })
            CategoryBar(CATEGORY) {
                navigation.navigate(MyScreens.CategoryScreen.route+"/"+it) }

            val productDataState = viewModel.dataProducts
            val adsDataState = viewModel.dataAds
            ProductSubjectList(TAGS, productDataState.value, adsDataState.value){
                navigation.navigate(MyScreens.ProductScreen.route+"/"+it)
            }

        }
        if (viewModel.showPaymentResultDialog.value) {

            PaymentResultDialog(
                checkoutResult = viewModel.checkoutData.value,
                onDismiss = {
                    viewModel.showPaymentResultDialog.value = false
                    viewModel.setPaymentStatus(NO_PAYMENT)
                }
            )

        }

    }
}
//------------------------------------------------
@Composable
private fun PaymentResultDialog(
    checkoutResult: CheckOut,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {

        Card(
            elevation = 8.dp,
            shape = Shapes.medium
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Payment Result",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Main Data
                if (checkoutResult.order?.status?.toInt() == PAYMENT_SUCCESS) {

                    AsyncImage(
                        model = R.drawable.success_anim,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(110.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Payment was successful!", style = TextStyle(fontSize = 16.sp))
                    Text(
                        text = "Purchase Amount: " + stylePrice(
                            (checkoutResult.order!!.amount).substring(
                                0,
                                (checkoutResult.order!!.amount).length - 1
                            )
                        )
                    )

                } else {

                    AsyncImage(
                        model = R.drawable.fail_anim,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(110.dp)
                            .padding(top = 6.dp, bottom = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = "Payment was not successful!", style = TextStyle(fontSize = 16.sp))
                    Text(
                        text = "Purchase Amount: " + stylePrice(
                            (checkoutResult.order!!.amount).substring(
                                0,
                                (checkoutResult.order.amount).length - 1
                            )
                        )
                    )

                }

                // Ok Button
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onDismiss) {
                        Text(text = "ok")
                    }
                    Spacer(modifier = Modifier.height(4.dp))

                }
            }
        }
    }
}


//-----------------------------------------------------
@Composable
fun TopToolbar(badgeNumber:Int, onCartClicked: () -> Unit, onProfileClicked: () -> Unit) {
    TopAppBar(elevation = 0.dp,
        backgroundColor = Color.White,
        title = { Text(text = "Bag Market") },
        actions = {
            IconButton1(onClick = { onCartClicked.invoke() }) {
                if (badgeNumber == 0) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                } else {
                    BadgedBox(badge = { Badge { Text(badgeNumber.toString()) } }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    }
                }
            }
            IconButton1(onClick = { onProfileClicked.invoke() }) {
                Icon(Icons.Default.Person, null)
            }
        }

    )

}

//-----------------------------------------------------
@Composable
fun CategoryBar(categoryList: List<Pair<String, Int>>, onCategoryClicked: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(top = 16.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(categoryList.size) {
            CategoryItem(categoryList[it], onCategoryClicked)
        }
    }

}

@Composable
fun CategoryItem(subject: Pair<String, Int>, onCategoryClicked: (String) -> Unit) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable { onCategoryClicked.invoke(subject.first) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = Shapes.medium,
            color = CardViewBackground
        ) {
            Image(
                painter = painterResource(id = subject.second),
                contentDescription = null,
                modifier = Modifier.padding(16.dp)
            )

        }
        Text(
            text = subject.first,
            modifier = Modifier.padding(top = 4.dp),
            style = TextStyle(color = Color.Gray)
        )

    }
}

//-----------------------------------------------------
@Composable
fun ProductSubjectList(tags: List<String>, products: List<Product>, ads: List<Ads> , onProductClicked: (String) -> Unit) {
    val context = LocalContext.current
    if (products.isNotEmpty()) {
            Column {
            tags.forEachIndexed { it, _ ->
                val withTagData = products.filter { products -> products.tags == tags[it] }
                ProductSubject(tags[it], withTagData.shuffled(),onProductClicked)
                if (ads.size >= 2) {
                    if (it == 1 || it == 2)
                        BigPictureAdvertisement(ads[it - 1],onProductClicked)
                }
            }

        }
    }
}

@Composable
fun ProductSubject(subject: String, data: List<Product>, onProductClicked: (String) -> Unit) {
    Column(modifier = Modifier.padding(top = 32.dp)) {
        Text(
            text = subject,
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.h6
        )
        ProductBar(data,onProductClicked)
    }

}

@Composable
fun ProductBar(data: List<Product>, onProductClicked: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(top = 16.dp),
        contentPadding = PaddingValues(end = 16.dp)
    ) {
        items(data.size) {
            ProductItem(data[it],onProductClicked)
        }
    }
}

@Composable
fun ProductItem(product: Product, onProductClicked: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 16.dp)
            .clickable {onProductClicked.invoke(product.productId)},
        elevation = 4.dp,
        shape = Shapes.medium
    ) {
        Column {

            AsyncImage(
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Crop,
                model = product.imgUrl,
                contentDescription = null
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stylePrice(product.price) ,
                    style = TextStyle(fontSize = 14.sp)
                )

                Text(
                    text = product.soldItem + " Sold",
                    style = TextStyle(fontSize = 13.sp, color = Color.Gray)
                )

            }

        }

    }
}

//-----------------------------------------------------
@Composable
fun BigPictureAdvertisement(ads: Ads,onProductClicked: (String) -> Unit) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(top = 32.dp, start = 16.dp, end = 16.dp)
            .clip(Shapes.medium)
            .clickable {onProductClicked.invoke(ads.productId) },
        model = ads.imageURL,
        contentDescription = null,
        contentScale = ContentScale.Crop
    )

}

