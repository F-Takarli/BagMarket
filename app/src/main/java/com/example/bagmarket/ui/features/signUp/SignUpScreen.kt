package com.example.bagmarket.ui.features.signUp


import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bagmarket.R
import com.example.bagmarket.ui.theme.BackgroundMain
import com.example.bagmarket.ui.theme.Blue
import com.example.bagmarket.ui.theme.MainAppTheme
import com.example.bagmarket.ui.theme.Shapes
import com.example.bagmarket.util.MyScreens
import com.example.bagmarket.util.VALUE_SUCCESS
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.burnoo.cokoin.navigation.getNavController
import dev.burnoo.cokoin.navigation.getNavViewModel
import ir.dunijet.dunibazaar.util.NetworkChecker

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    MainAppTheme {
        Surface(
            color = BackgroundMain,
            modifier = Modifier.fillMaxSize()    //wrapContentSize()
        ) {
            SignUpScreen()
        }
    }
}

@Composable
fun SignUpScreen() {
    val uiController= rememberSystemUiController()
    SideEffect {  uiController.setStatusBarColor(Blue)   }
    val navigation = getNavController()
    val viewModel = getNavViewModel<SignUpViewModel>()
    val context= LocalContext.current
    clearInputs(viewModel)
    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(Blue)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.95f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconApp()
            MainCardView(navigation, viewModel) {
                viewModel.signUpUser(){
                    if(it== VALUE_SUCCESS){
                        navigation.navigate(MyScreens.MainScreen.route){
                            popUpTo(MyScreens.IntroScreen.route){inclusive=true}
                        }

                    }else{
                        Toast.makeText(context,it,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun IconApp() {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .size(64.dp)
    ) {
        Image(
            modifier = Modifier.padding(14.dp),
            painter = painterResource(id = R.drawable.ic_icon_app),
            contentDescription = null
        )

    }
}

@Composable
fun MainCardView(navigation: NavController, viewModel: SignUpViewModel, signUpEvent: () -> Unit) {
    val name = viewModel.name.observeAsState("")
    val email = viewModel.email.observeAsState("")
    val password = viewModel.password.observeAsState("")
    val confirmPassword = viewModel.confirmPassword.observeAsState("")
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), elevation = 10.dp, shape = Shapes.medium
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp), text = "Sign Up",
                style = TextStyle(color = Blue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            MainTextField(
                hint = "Your Full Name",
                edtValue = name.value,
                icon = R.drawable.ic_person
            ) { viewModel.name.value = it }
            MainTextField(
                hint = "Email",
                edtValue = email.value,
                icon = R.drawable.ic_email
            ) { viewModel.email.value = it }
            PasswordTextField(
                hint = "Password",
                edtValue = password.value,
                icon = R.drawable.ic_password
            ) { viewModel.password.value = it }
            PasswordTextField(
                hint = "Confirm Password",
                edtValue = confirmPassword.value,
                icon = R.drawable.ic_password
            ) { viewModel.confirmPassword.value = it }

            Button(onClick = {
                if (name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty() && confirmPassword.value.isNotEmpty()) {
                    if (password.value == confirmPassword.value) {
                        if (password.value.length >= 8) {
                            if (Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
                                if (NetworkChecker(context).isInternetConnected) {
                                    signUpEvent.invoke()
                                } else {
                                    Toast.makeText(context, "Please connect to internet", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                            Toast.makeText(context, "Email format is not true!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Password characters should be more than 8!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(context, "Passwords are not the same!", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(context, "Please complete all boxes", Toast.LENGTH_SHORT).show()
            }

        }, modifier = Modifier.padding(top = 28.dp, bottom = 8.dp)) {
        Text(modifier = Modifier.padding(8.dp), text = "Register Account")
    }
        Row(
            modifier = Modifier.padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account?")
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                navigation.navigate(MyScreens.SignInScreen.route) {
                    popUpTo(MyScreens.SignUpScreen.route) { inclusive = true }
                }
            }) {
                Text("Log In", color = Blue)
            }
        }
    }

}
}

@Composable
fun MainTextField(
    hint: String,
    edtValue: String,
    icon: Int,
    onValueChanges: (String) -> Unit
) {
    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        label = { Text(hint) },
        singleLine = true,
        value = edtValue,
        onValueChange = onValueChanges,
        placeholder = { Text(hint) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        shape = Shapes.medium,
        leadingIcon = { Icon(painter = painterResource(id = icon), contentDescription = null) }
    )
}

@Composable
fun PasswordTextField(
    hint: String,
    edtValue: String,
    icon: Int,
    onValueChanges: (String) -> Unit
) {
    val passwordVisible = remember { mutableStateOf(false) }
    OutlinedTextField(
        label = { Text(hint) },
        singleLine = true,
        value = edtValue,
        onValueChange = onValueChanges,
        placeholder = { Text(hint) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 12.dp),
        shape = Shapes.medium,
        leadingIcon = { Icon(painter = painterResource(id = icon), contentDescription = null) },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), //شکل کیبورد را تغییر میدهد تا برای پسورد مناسب باشد
        trailingIcon = {
            val image = if (passwordVisible.value) painterResource(id = R.drawable.ic_invisible)
            else painterResource(id = R.drawable.ic_visible)
            Icon(painter = image,
                contentDescription = null,
                modifier = Modifier.clickable {
                    passwordVisible.value = !passwordVisible.value
                })
        }
    )
}

fun clearInputs(viewModel: SignUpViewModel){
    viewModel.email.value=""
    viewModel.name.value=""
    viewModel.password.value=""
    viewModel.confirmPassword.value=""
}