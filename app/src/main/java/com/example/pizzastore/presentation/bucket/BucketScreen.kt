package com.example.pizzastore.presentation.bucket

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.Product

@Composable
fun BucketScreen(
    paddingValues: PaddingValues,
) {

    val component = getApplicationComponent()
    val viewModel: BucketScreenViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState by viewModel.screenState.collectAsState()

    when (screenState) {
        is BucketScreenState.Initial -> {}
        is BucketScreenState.Content -> {
            val currentScreenState = screenState as BucketScreenState.Content
            BucketScreenContent(
                viewModel = viewModel,
                listProductsFromBucket = currentScreenState.productsList
            )
        }
    }
}

@Composable
fun BucketScreenContent(
    viewModel: BucketScreenViewModel,
    listProductsFromBucket: List<Product>
) {
    Column(
        modifier = Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        OrderSize(viewModel = viewModel)
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            items(listProductsFromBucket) { orderedProduct ->
                val request = ImageRequest
                    .Builder(LocalContext.current)
                    .data(orderedProduct.photo)
                    .size(coil.size.Size.ORIGINAL)
                    .build()

                val painter = rememberAsyncImagePainter(
                    model = request
                )
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxSize(),
                            painter = painter,
                            contentDescription = "image_product"
                        )
                    }
                    Column (
                        modifier = Modifier
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = orderedProduct.name,
                            fontSize = 20.sp
                        )
                        Row (
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = orderedProduct.price.toString(),
                                fontWeight = FontWeight.Light
                            )
                            Row {
                                ClickableIconByResourceId(R.drawable.ic_minus) {
                                    viewModel.decreaseProductInBucket(orderedProduct)
                                }
                                Text(text = viewModel.getProductCount(orderedProduct).toString())
                                ClickableIconByResourceId(R.drawable.ic_plus) {
                                    viewModel.increaseProductInBucket(orderedProduct)
                                }
                            }
                        }
                    }

                }

            }
        }
    }

}

//<editor-fold desc="ClickableIcon">
@Composable
fun ClickableIconByResourceId(
    resourceId: Int,
    onClick: () -> Unit
) {
    Icon(
        modifier = Modifier
            .size(25.dp)
            .padding(
                start = 4.dp,
                end = 4.dp
            )
            .clickable {
                onClick()
            },
        imageVector = ImageVector.vectorResource(resourceId),
        contentDescription = "clickable_icon"
    )
}
//</editor-fold>

//<editor-fold desc="OrderSize">
@Composable
fun OrderSize(viewModel: BucketScreenViewModel) {
    val orderedProductCount = viewModel.getOrderCountProducts()
    val orderSum = viewModel.getOrderSum()
    val orderDetailTemplate = if (
        (orderedProductCount % 10) in 2..4
        && orderedProductCount !in 10..14
    ) {
        orderedProductCount.toString() +
                stringResource(R.string.order_sum_template_2_4) + orderSum.toString() +
                stringResource(R.string.roubles_postfix)
    } else if (orderedProductCount % 10 == 1) {
        orderedProductCount.toString() + stringResource(R.string.order_sum_template_one) +
                orderSum.toString() + stringResource(R.string.roubles_postfix)
    } else if (orderedProductCount > 1) {
        orderedProductCount.toString() +
                stringResource(R.string.order_sum_template_many) + orderSum.toString() +
                stringResource(R.string.roubles_postfix)
    } else {
        stringResource(R.string.empty_bucket)
    }
    Row {
        Text(
            text = orderDetailTemplate,
            fontSize = 24.sp
        )
    }
}
//</editor-fold>