package com.example.pizzastore.presentation.contacts

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.presentation.funs.ClickableIconByResourceId


@Composable
fun ContactsScreen(paddingValues: PaddingValues) {

    val component = getApplicationComponent()
    val viewModel: ContactsScreenViewModel = viewModel(factory = component.getViewModelFactory())

    val screenState = viewModel.state.collectAsState()

    when (screenState.value) {

        is ContactsScreenState.Initial -> {}

        is ContactsScreenState.Content -> {
            ContactsScreenContent(paddingValues = paddingValues)
        }
    }
}


@Composable
fun ContactsScreenContent(paddingValues: PaddingValues) {

    val iconSize = 50.dp
    val context = LocalContext.current
    val telegramIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://t.me/nyangoodbye")
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column (
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Тут могли бы быть ваши контакты",
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = "Задизайнено и разработано:",
                fontSize = 24.sp
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp),
                text = "Климовым Д.И.",
                fontSize = 20.sp
            )
        }
        Row {
            ClickableIconByResourceId(
                resourceId = R.drawable.ic_telegram,
                size = iconSize,
                color = colorResource(id = R.color.orange)
            ) {
                context.startActivity(telegramIntent)
            }
            ClickableIconByResourceId(
                resourceId = R.drawable.ic_mail,
                size = iconSize,
                color = colorResource(id = R.color.orange)
            ) {
                
            }
        }
        Row (
            modifier = Modifier
            .padding(bottom = 32.dp)
        ){
            ClickableIconByResourceId(
                resourceId = R.drawable.ic_github,
                size = iconSize
            ) {
                
            }
        }
    }
}

