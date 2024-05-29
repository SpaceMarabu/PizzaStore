package com.example.pizzastore.presentation.order.bucket

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
    onOrderingFinish: () -> Unit
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
                listProductsFromBucket = currentScreenState.productsList,
                paddingValues = paddingValues
            ) {
                onOrderingFinish()
            }
        }
    }
}

//<editor-fold desc="BucketScreenContent">
@Composable
fun BucketScreenContent(
    viewModel: BucketScreenViewModel,
    listProductsFromBucket: List<Product>,
    paddingValues: PaddingValues,
    onOrderingFinish: () -> Unit
) {
    var isOrderListEmpty by remember {
        mutableStateOf(listProductsFromBucket.isEmpty())
    }
    if (listProductsFromBucket.isEmpty()) {
        isOrderListEmpty = true
    }

    Column(
        modifier = Modifier
            .padding(
                top = 16.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = paddingValues.calculateBottomPadding()
            )
    ) {
        OrderSize(viewModel = viewModel)
        LazyProductColumn(
            listProductsFromBucket = listProductsFromBucket,
            viewModel = viewModel
        )
        if (!isOrderListEmpty) {
            OderButton {
                onOrderingFinish()
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="OderButton">
@Composable
fun OderButton(onClick: () -> Unit) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = 16.dp
                )
                .height(50.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(colorResource(R.color.orange))
                .clickable {
                    onClick()
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.order_button_text),
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        }
    }
}
//</editor-fold>

//<editor-fold desc="LazyProductColumn">
@Composable
fun LazyProductColumn(
    listProductsFromBucket: List<Product>,
    viewModel: BucketScreenViewModel
) {
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
            ProductItem(
                painter = painter,
                orderedProduct = orderedProduct,
                viewModel = viewModel
            )
        }
    }
}
//</editor-fold>

//<editor-fold desc="ProductItem">
@Composable
fun ProductItem(
    painter: Painter,
    orderedProduct: Product,
    viewModel: BucketScreenViewModel
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
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
                .height(80.dp)
                .fillMaxWidth()
                .padding(start = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = orderedProduct.name,
                fontSize = 20.sp
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = orderedProduct.price.toString() +
                            stringResource(id = R.string.roubles_postfix),
                    fontWeight = FontWeight.Light
                )
                Row (
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray.copy(alpha = 0.15f)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
//</editor-fold>

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