package com.example.pizzastore.presentation.contacts

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.presentation.utils.ClickableIconByResourceId

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

//<editor-fold desc="ContactsScreenContent">
@Composable
fun ContactsScreenContent(paddingValues: PaddingValues) {

    val iconSize = 50.dp
    val context = LocalContext.current
    val telegramIntent = getActionIntent(stringResource(R.string.telegram_contact_developer))
    val githubIntent = getActionIntent(stringResource(R.string.github_project))
    val mailIntent = getActionIntent(stringResource(R.string.mail_contact_developer))

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
                text = stringResource(R.string.its_would_be_yours_contact),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = stringResource(R.string.designed_and_developed),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp),
                text = stringResource(R.string.developer_RP),
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
                context.startActivity(mailIntent)
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
                context.startActivity(githubIntent)
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="getActionIntent">
fun getActionIntent(uriString: String) = Intent(
    Intent.ACTION_VIEW,
    Uri.parse(uriString)
)
//</editor-fold>

