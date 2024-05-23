package com.example.pizzastore.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.ProductType
import com.example.pizzastore.domain.usecases.GetCurrentSettingsUseCase
import com.example.pizzastore.domain.usecases.GetProductsUseCase
import com.example.pizzastore.domain.usecases.GetStoriesUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class MenuScreenViewModel @Inject constructor(
    private val getCurrentSettingsUseCase: GetCurrentSettingsUseCase,
    private val getStoriesUseCase: GetStoriesUseCase,
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    val screenState = MutableStateFlow<MenuScreenState>(MenuScreenState.Initial)

    private val changesFlow = MutableSharedFlow<ScreenStateChanges>(
        replay = 4,
        extraBufferCapacity = 4,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val listProductTypes = ProductType.allTypes
    private val _typesMap = MutableStateFlow(getInitialProductIndexMap())

    private val scope = viewModelScope


    init {
        screenState.value = MenuScreenState.Loading
        scope.launch {
            subscribeScreenStateChanges()
            loadCity()
        }
        scope.launch {
            loadStories()
            loadProducts()
        }
    }

    fun getCurrentVisibleType(index: Int): ProductType {
        var resultType = listProductTypes[0]
        _typesMap.value.forEach { element ->
            if (element.value.contains(index)) {
                resultType = element.key
            }
        }
        return resultType
    }

    //<editor-fold desc="subscribeScreenStateChanges">
    private fun subscribeScreenStateChanges() {
        viewModelScope.launch {
            changesFlow.collect { changesKind ->
                when (changesKind) {
                    is ScreenStateChanges.ChangeCity -> {
                        if (screenState.value is MenuScreenState.Loading) {
                            screenState.value = MenuScreenState.Content(city = changesKind.city)
                        } else {
                            val currentScreenState = screenState.value as MenuScreenState.Content
                            screenState.value = currentScreenState.copy(city = changesKind.city)
                        }
                    }

                    is ScreenStateChanges.ChangeProducts -> {
                        val currentScreenState = screenState.value as MenuScreenState.Content
                        screenState.value =
                            currentScreenState.copy(products = changesKind.products)
                    }

                    is ScreenStateChanges.ChangeStories -> {
                        val currentScreenState = screenState.value as MenuScreenState.Content
                        screenState.value =
                            currentScreenState.copy(stories = changesKind.stories)
                    }

                    is ScreenStateChanges.ChangeIndexingMap -> {
                        if (screenState.value is MenuScreenState.Loading) {
                            delay(300)
                            changesFlow.emit(changesKind)
                        } else {
                            val currentScreenState = screenState.value as MenuScreenState.Content
                            screenState.value =
                                currentScreenState.copy(indexingByTypeMap = changesKind.map)
                        }
                    }
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="getInitialProductIndexMap">
    private fun getInitialProductIndexMap(): MutableMap<ProductType, List<Int>> {
        val resultMap = mutableMapOf<ProductType, List<Int>>()
        listProductTypes.forEach {
            resultMap[it] = listOf()
        }
        return resultMap
    }
    //</editor-fold>

    //<editor-fold desc="sortListProducts">
    private suspend fun sortListProducts(products: List<Product>): List<Product> {
        val types = listProductTypes
        val resultList = mutableListOf<Product>()
        types.forEach { currentType ->
            val currentTypeProducts =
                products.filter { currentProduct ->
                    currentProduct.type == currentType
                }
            resultList.addAll(currentTypeProducts)
        }
        resultList.forEachIndexed indexedLoop@{ index, product ->
            val currentType = product.type
            val foundValueFromMapByCurrentType = _typesMap.value[currentType] ?: listOf()
            val currentIndexMutableList = foundValueFromMapByCurrentType.toMutableList()
            currentIndexMutableList.add(index)
            _typesMap.value[currentType] = currentIndexMutableList
        }
        changesFlow.emit(
            ScreenStateChanges.ChangeIndexingMap(map = _typesMap.value)
        )

        return resultList
    }
    //</editor-fold>

    //<editor-fold desc="loadProducts">
    private suspend fun loadProducts() {
        getProductsUseCase
            .getProductsFlow()
            .collect {
                val listProductSorted = sortListProducts(it)
                changesFlow.emit(ScreenStateChanges.ChangeProducts(products = listProductSorted))
            }
    }
    //</editor-fold>

    //<editor-fold desc="loadStories">
    private fun loadStories() {
        viewModelScope.launch {
            getStoriesUseCase
                .getStoriesFlow()
                .collect {
                    changesFlow.emit(ScreenStateChanges.ChangeStories(stories = it))
                }
        }
    }
    //</editor-fold>

    //<editor-fold desc="loadCity">
    private suspend fun loadCity() {
        getCurrentSettingsUseCase
            .getCurrentSettingsFlow()
            .stateIn(scope)
            .collect {
                if (it?.city == null) {
                    screenState.emit(MenuScreenState.EmptyCity)
                    delay(300)
                    screenState.emit(MenuScreenState.Loading)
                } else {
                    val city = it.city
                    changesFlow.emit(ScreenStateChanges.ChangeCity(city = city))
                }
            }
    }
    //</editor-fold>

    //<editor-fold desc="changeDeliveryType">
    fun changeDeliveryType(type: DeliveryType) {
        scope.launch {
            if (screenState.value is MenuScreenState.Content) {
                val currentState = screenState.value as MenuScreenState.Content
                val currentCity = currentState.city
                val newCity = currentCity.copy(deliveryType = type)
                changesFlow.emit(
                    ScreenStateChanges.ChangeCity(city = newCity)
                )
            }
        }
    }
    //</editor-fold>
}