package com.example.pizzastore.presentation.menu

import android.net.Uri
import android.util.DisplayMetrics
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.pizzastore.R
import com.example.pizzastore.di.getApplicationComponent
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.ProductType
import com.example.pizzastore.presentation.utils.CircularLoading
import com.example.pizzastore.presentation.utils.ClickableIconByResourceId
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(
    paddingValues: PaddingValues,
    onCityClick: () -> Unit,
    onAddressClick: (isTakeout: Boolean) -> Unit,
    onCityIsEmpty: () -> Unit
) {

    val component = getApplicationComponent()
    val viewModel: MenuScreenViewModel = viewModel(factory = component.getViewModelFactory())
    val screenState = viewModel.screenState.collectAsState()

    when (screenState.value) {
        is MenuScreenState.Content -> {
            val currentState = screenState.value as MenuScreenState.Content
            MenuScreenContent(
                paddingValues = paddingValues,
                onCityClick = onCityClick,
                onAddressClick = onAddressClick,
                viewModel = viewModel,
                cityState = currentState.city,
                products = currentState.products,
                listStoriesUri = currentState.stories,
                indexMapForScroll = currentState.indexingByTypeMap,
                bucket = currentState.bucket
            )
        }

        MenuScreenState.Loading -> {
            CircularLoading()
        }

        MenuScreenState.Initial -> {}
        MenuScreenState.EmptyCity -> {
            onCityIsEmpty()
        }
    }
}

@Composable
fun MenuScreenContent(
    paddingValues: PaddingValues,
    viewModel: MenuScreenViewModel,
    cityState: City,
    products: List<Product>,
    listStoriesUri: List<Uri>,
    indexMapForScroll: Map<ProductType, List<Int>>,
    bucket: Bucket,
    onCityClick: () -> Unit,
    onAddressClick: (isTakeout: Boolean) -> Unit
) {

    val listProductTypes = viewModel.listProductTypes

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var currentVisibleType by remember {
        mutableStateOf(viewModel.getInitialProductType())
    }
    val firstVisibleIndexProductState by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }
    currentVisibleType = viewModel.getCurrentVisibleType(firstVisibleIndexProductState)

    Column(
        modifier = Modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        ChoseCity(
            cityState.name,
            16.dp,
            16.dp,
            onCityClick
        )
        ChoseDeliveryType(
            city = cityState,
            onDeliveryClick = { newDeliveryType ->
                viewModel.changeDeliveryType(
                    newDeliveryType
                )
            },
            onAddressClick = { isTakeout ->
                onAddressClick(isTakeout)
            }
        )
        StoriesLazyRow(listStoriesUri = listStoriesUri)
        ProductTypesLazyRow(
            listProductTypes = listProductTypes,
            currentVisibleType = currentVisibleType
        ) { clickedType ->
            coroutineScope.launch {
                val indexType = indexMapForScroll[clickedType]?.first() ?: 0
                listState.animateScrollToItem(index = indexType)
            }
        }
        LazyProductColumn(
            listState = listState,
            products = products,
            bucket = bucket,
            viewModel = viewModel
        )
    }
}

//<editor-fold desc="LazyProductColumn">
@Composable
fun LazyProductColumn(
    listState: LazyListState,
    products: List<Product>,
    bucket: Bucket,
    viewModel: MenuScreenViewModel
) {
    LazyColumn(
        modifier = Modifier
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp
            ),
        state = listState
    ) {
        items(products) { product ->

            val request = ImageRequest
                .Builder(LocalContext.current)
                .data(product.photo)
                .size(coil.size.Size.ORIGINAL)
                .build()

            val painter = rememberAsyncImagePainter(
                model = request
            )
            val productCount = bucket.order[product] ?: 0

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                        .height(130.dp)
                ) {
                    Text(text = product.name)
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp),
                        text = product.description,
                        fontWeight = FontWeight.Light
                    )
                    PriceRow(
                        productCount = productCount,
                        product = product,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="PriceRow">
@Composable
fun PriceRow(
    productCount: Int,
    product: Product,
    viewModel: MenuScreenViewModel
) {
    Row(
        modifier = Modifier.padding(top = 4.dp, end = 16.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (productCount > 0) {
            Row (
                modifier = Modifier
                    .padding(
                        start = 4.dp,
                        end = 4.dp
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.LightGray.copy(alpha = 0.15f)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                ClickableIconByResourceId(resourceId = R.drawable.ic_minus) {
                    viewModel.decreaseProductInBucket(product)
                }
                Text(text = productCount.toString())
                ClickableIconByResourceId(resourceId = R.drawable.ic_plus) {
                    viewModel.increaseProductInBucket(product)
                }
            }
        } else {
            ClickableIconByResourceId (resourceId = R.drawable.ic_shopping_bag) {
                viewModel.increaseProductInBucket(product)
            }
            Text(text = product.price.toString() +
                    stringResource(R.string.roubles_postfix))
        }
    }
}
//</editor-fold>

//<editor-fold desc="ProductTypesLazyRow">
@Composable
fun ProductTypesLazyRow(
    listProductTypes: List<ProductType>,
    currentVisibleType: ProductType,
    onTypeClicked: (ProductType) -> Unit
) {

    var clickedType: ProductType by remember(currentVisibleType) {
        mutableStateOf(currentVisibleType)
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        item {
            Spacer(
                modifier = Modifier
                    .width(16.dp)
            )
        }
        items(items = listProductTypes) { productType ->
            Row(
                modifier = Modifier
                    .width(100.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (clickedType == productType)
                            Color.LightGray
                        else Color.LightGray.copy(alpha = 0.3f)
                    )
                    .clickable {
                        clickedType = productType
                        onTypeClicked(clickedType)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = productType.frontName)
            }
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}
//</editor-fold>

//<editor-fold desc="StoriesLazyRow">
@Composable
fun StoriesLazyRow(listStoriesUri: List<Uri>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        items(items = listStoriesUri) { imageUri ->
            val request = ImageRequest
                .Builder(LocalContext.current)
                .data(imageUri)
                .size(coil.size.Size.ORIGINAL)
                .build()

            val painter = rememberAsyncImagePainter(
                model = request
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = 3.dp,
                        color = colorResource(R.color.orange),
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = painter,
                    contentDescription = "content_by_uri"
                )
            }
        }
    }
}
//</editor-fold>

//<editor-fold desc="ChoseDeliveryType">
@Composable
fun ChoseDeliveryType(
    city: City,
    onDeliveryClick: (DeliveryType) -> Unit,
    onAddressClick: (isTakeout: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(10))
            .background(Color.LightGray.copy(alpha = 0.2f))
    ) {

        var isTakeout by remember {
            mutableStateOf(true)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(20))
                .border(
                    BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
                    RoundedCornerShape(20)
                )
                .background(Color.LightGray.copy(alpha = 0.3f)),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {

            isTakeout = when (city.deliveryType) {
                DeliveryType.TAKE_OUT -> true
                DeliveryType.DELIVERY_TO -> false
            }

            val lightGray30 = Color.LightGray.copy(alpha = 0.3f)

            val deliveryColor by animateColorAsState(
                if (isTakeout) lightGray30 else Color.White, label = "deliveryColor"
            )
            val takeoutColor by animateColorAsState(
                if (isTakeout) Color.White else lightGray30, label = "takeoutColor"
            )

            when (city.deliveryType) {
                DeliveryType.TAKE_OUT -> {
                    TextButton(
                        text = stringResource(R.string.delivery_text),
                        color = deliveryColor
                    ) { onDeliveryClick(DeliveryType.DELIVERY_TO) }
                    TextButton(
                        text = stringResource(R.string.takeout_text),
                        color = takeoutColor
                    ) { onDeliveryClick(DeliveryType.TAKE_OUT) }
                }

                DeliveryType.DELIVERY_TO -> {
                    TextButton(
                        text = stringResource(R.string.delivery_text),
                        color = deliveryColor
                    ) { onDeliveryClick(DeliveryType.DELIVERY_TO) }
                    TextButton(
                        text = stringResource(R.string.takeout_text),
                        color = takeoutColor
                    ) { onDeliveryClick(DeliveryType.TAKE_OUT) }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
                .clickable {
                    val currentDeliveryIsTakeOut = isTakeout
                    onAddressClick(currentDeliveryIsTakeOut)
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.chose_address_string),
                color = colorResource(R.color.orange),
                fontSize = 14.sp
            )
            Icon(
                modifier = Modifier
                    .size(20.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_angle_right),
                contentDescription = null,
                tint = colorResource(R.color.orange)
            )
        }
    }

}
//</editor-fold>

//<editor-fold desc="TextButton">
@Composable
fun TextButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    val displayMetrics: DisplayMetrics = LocalContext.current.resources.displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(20))
            .background(color)
            .width((dpWidth / 2 - 32).dp)
            .height(32.dp)
            .wrapContentHeight(align = Alignment.CenterVertically)
            .clickable {
                onClick()
            },
        textAlign = TextAlign.Center,
        fontSize = 12.sp,
        text = text
    )
}
//</editor-fold>

//<editor-fold desc="ChoseCity">
@Composable
fun ChoseCity(
    cityName: String,
    paddingStart: Dp,
    paddingTop: Dp,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = paddingStart, top = paddingTop)
            .clip(RoundedCornerShape(30))
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = cityName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
        Icon(
            modifier = Modifier
                .size(28.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_angle_down),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        )
    }
}
//</editor-fold>