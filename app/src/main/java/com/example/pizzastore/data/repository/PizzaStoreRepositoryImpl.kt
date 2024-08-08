package com.example.pizzastore.data.repository

import android.util.Log
import com.example.pizzastore.data.network.ApiFactory
import com.example.pizzastore.data.localdatabase.PizzaDao
import com.example.pizzastore.data.localdatabase.entity.AccountDbModel
import com.example.pizzastore.data.localdatabase.entity.SessionSettingsDbModel
import com.example.pizzastore.data.mapper.LocalMapper
import com.example.pizzastore.data.mapper.RemoteMapper
import com.example.pizzastore.data.network.model.PathResponseDto
import com.example.pizzastore.data.remotedatabase.DatabaseService
import com.example.pizzastore.data.remotedatabase.model.DBResponseOrder
import com.example.pizzastore.domain.entity.Account
import com.example.pizzastore.domain.entity.AddressWithPath
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryDetails
import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.entity.OrderStatus
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.SessionSettings
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject


class PizzaStoreRepositoryImpl @Inject constructor(
    private val pizzaDao: PizzaDao,
    private val databaseService: DatabaseService,
    private val localMapper: LocalMapper,
    private val remoteMapper: RemoteMapper
) : PizzaStoreRepository {

    private val userBucket = MutableStateFlow(Bucket())
    private val listProductsStateFlow = MutableStateFlow<List<Product>>(listOf())
    private val dbResponseOrderFlow = MutableStateFlow<DBResponseOrder>(DBResponseOrder.Initial)

    init {
        subscribeListProducts()
    }

    //<editor-fold desc="getStoriesUseCase">
    override fun getStoriesUseCase() = databaseService.getListStoriesUri()
    //</editor-fold>

    //<editor-fold desc="getPathUseCase">
    override suspend fun getPathUseCase(point1: String, point2: String): Path {
        val call: Response<PathResponseDto>?
        var result = Path.EMPTY_PATH
        try {
            call = ApiFactory.apiService.getPath(point1, point2)
            val pathResponseDto = call.body() as PathResponseDto?
            val pathDto = pathResponseDto?.paths
            if (pathDto != null) {
                result = localMapper.mapPathDtoToEntity(pathDto[0])
            }

        } catch (e: HttpException) {
            Log.d("HTTP_ERROR", e.code().toString())
        }
        return result
    }
    //</editor-fold>

    //<editor-fold desc="getAddressUseCase">
    override suspend fun getAddressUseCase(pointLatLng: String): AddressWithPath {
        val addressDto = ApiFactory.apiService.getAddress(pointLatLng)
        var result = AddressWithPath.EMPTY_ADDRESS
        if (addressDto.addressList.isNotEmpty()) {
            result = localMapper.mapAddressDtoToEntity(
                addressDto.addressList[0]
            )
            addressDto.addressList.forEach {
                if (it.houseNumber != null) {
                    result = localMapper.mapAddressDtoToEntity(it)
                    return@forEach
                }
            }
        }
        return result
    }
    //</editor-fold>

    //<editor-fold desc="setCityUseCase">
    override suspend fun setCityUseCase(city: City) {
        val cityDbModel = localMapper.mapCityToCityDbModel(city)
        pizzaDao.get().map {
            if (it != null) {
                localMapper.mapSessionSettingsDbModelToEntity(
                    settingsDbModel = it.copy(city = cityDbModel),
                    products = listProductsStateFlow.value
                )
            } else {
                SessionSettings(city = city)
            }
        }.collect {
            if (it == null) return@collect
            val dbModel = localMapper.mapSessionSettToDbModel(it)
            CoroutineScope(Dispatchers.IO).launch {
                pizzaDao.addSessionSettings(dbModel)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="getCurrentSettingsUseCase">
    override fun getCurrentSettingsUseCase(): Flow<SessionSettings?> {
        return pizzaDao
            .get()
            .map { sessionSettingsDbModel ->
                if (sessionSettingsDbModel != null) {
                    val products = listProductsStateFlow.value
                    val settings = localMapper.mapSessionSettingsDbModelToEntity(
                        sessionSettingsDbModel,
                        products
                    )
                    val account = settings?.account
                    if (account != null) {
                        val orders = account.orders
                        val lastOrder = if (orders.isNotEmpty()) {
                            orders.last()
                        } else {
                            null
                        }
                        if (lastOrder != null && lastOrder.status != OrderStatus.ACCEPT) {
                            databaseService.sendLastOpenedOrderId(lastOrder.id)
                        }
                    }

                    settings

                } else {
                    SessionSettings()
                }
            }
    }
    //</editor-fold>

    //<editor-fold desc="getCitiesUseCase">
    override fun getCitiesUseCase(): Flow<List<City>> = databaseService.getListCitiesFlow()
    //</editor-fold>

    //<editor-fold desc="setPointUseCase">
    override suspend fun setPointUseCase(point: Point) {
        pizzaDao.get().map { sessionSettingsDbModel ->
            val currentAccount = sessionSettingsDbModel?.account ?: AccountDbModel()
            val currentDeliveryDetails = currentAccount.deliveryDetails
            val products = listProductsStateFlow.value
            localMapper.mapSessionSettingsDbModelToEntity(
                settingsDbModel = sessionSettingsDbModel?.copy(
                    account = currentAccount.copy(
                        deliveryDetails = currentDeliveryDetails.copy(
                            pizzaStore = localMapper.mapPointToDbModel(point)
                        )
                    )
                ),
                products = products
            )
        }.collect {
            if (it == null) return@collect
            val dbModel = localMapper.mapSessionSettToDbModel(it)
            CoroutineScope(Dispatchers.IO).launch {
                pizzaDao.addSessionSettings(dbModel)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="getProductsUseCase">
    override fun getProductsUseCase(): Flow<List<Product>> =
        databaseService.getListProductsFlow()
    //</editor-fold>

    //<editor-fold desc="increaseProductInBucketUseCase">
    override fun increaseProductInBucketUseCase(product: Product) {
        val order = userBucket.value.order.toMutableMap()
        val currentProductCount = order[product] ?: 0
        order[product] = currentProductCount + 1
        userBucket.value = Bucket(order = order)
    }
    //</editor-fold>

    //<editor-fold desc="decreaseProductInBucketUseCase">
    override fun decreaseProductInBucketUseCase(product: Product) {
        val order = userBucket.value.order.toMutableMap()
        val currentProductCount = order[product] ?: 0
        val newCountProduct = currentProductCount - 1
        if (currentProductCount <= 0) {
            order.remove(product)
        } else {
            order[product] = newCountProduct
        }
        userBucket.value = Bucket(order = order)
    }
    //</editor-fold>

    //<editor-fold desc="getBucketUseCase">
    override fun getBucketUseCase() = userBucket.asStateFlow()
    //</editor-fold>

    //<editor-fold desc="sendDeliveryDetailsUseCase">
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun sendDeliveryDetailsUseCase(details: DeliveryDetails) {
        val products = listProductsStateFlow.value
        val sessionSettingsFlow = pizzaDao.get()
            .map { sessionSettingsDbModel ->
                if (sessionSettingsDbModel != null) {
                    val settings = localMapper.mapSessionSettingsDbModelToEntity(
                        sessionSettingsDbModel,
                        products
                    )
                    val account = settings?.account ?: Account()
                    settings?.copy(account = account.copy(deliveryDetails = details))
                } else {
                    SessionSettings(account = Account(deliveryDetails = details))
                }
            }
        val deferred = CompletableDeferred<SessionSettingsDbModel>(null)
        CoroutineScope(Dispatchers.IO).launch {
            sessionSettingsFlow.collect {
                if (it != null) {
                    val sessionSettingsDto = localMapper.mapSessionSettToDbModel(it)
                    deferred.complete(sessionSettingsDto)
                }
            }
        }
        deferred.await()
        CoroutineScope(Dispatchers.IO).launch {
            pizzaDao.addSessionSettings(deferred.getCompleted())
        }
    }
    //</editor-fold>

    //<editor-fold desc="finishOrderingUseCase">
    override suspend fun finishOrderingUseCase() {
        val bucket = userBucket.value
        val products = listProductsStateFlow.value
        val bucketDto = remoteMapper.mapBucketToBucketDto(bucket)

        dbResponseOrderFlow.value = DBResponseOrder.Processing
        val orderSendingResult = databaseService.sendCurrentOrder(bucketDto)
        dbResponseOrderFlow.value = orderSendingResult
        val orderId = when (orderSendingResult) {
            is DBResponseOrder.Complete -> orderSendingResult.orderId
            else -> Order.ERROR_ID
        }
        if (orderId != Order.ERROR_ID) {
            val sessionSettingsFlow = pizzaDao.get()
                .map { sessionSettingsDbModel ->

                    val settings =
                        localMapper.mapSessionSettingsDbModelToEntity(
                            sessionSettingsDbModel,
                            products
                        ) ?: SessionSettings()

                    val account = settings.account ?: Account()
                    val orders = account.orders.toMutableList()

                    orders.add(
                        Order(
                            id = orderId,
                            status = OrderStatus.NEW,
                            bucket = bucket
                        )
                    )

                    settings.copy(
                        account = account.copy(
                            orders = orders
                        )
                    )
                }
            val deferred = CompletableDeferred<SessionSettingsDbModel>(null)
            CoroutineScope(Dispatchers.IO).launch {
                sessionSettingsFlow.collect {
                    val sessionSettingsDto = localMapper.mapSessionSettToDbModel(it)
                    if (deferred.complete(sessionSettingsDto)) {
                        this.cancel()
                    }
                }
            }
            val sessionSettings = runBlocking { deferred.await() }
            userBucket.value = Bucket()
            CoroutineScope(Dispatchers.IO).launch {
                pizzaDao.addSessionSettings(sessionSettings)
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="getCurrentOrderUseCase">
    override fun getCurrentOrderUseCase() = databaseService
        .getCurrentOrder()
        .map { orderDto ->
            val products = listProductsStateFlow.value
            var orderDtoMapped = remoteMapper.mapOrderDtoToEntity(orderDto, products)
            val currentSettingsDbModel = getSessionSettingsCompletableDeferred()
            val currentSettings = localMapper.mapSessionSettingsDbModelToEntity(
                currentSettingsDbModel,
                products
            ) ?: SessionSettings()
            val account = currentSettings.account
            val orders = account?.orders?.toMutableList()
            if (!orders.isNullOrEmpty() && orderDtoMapped != null) {
                orders[orders.size - 1] = orderDtoMapped
                val newAccount = account.copy(orders = orders)
                val newSettings = currentSettings.copy(account = newAccount)
                val settingsDbModel = localMapper.mapSessionSettToDbModel(newSettings)
                CoroutineScope(Dispatchers.IO).launch {
                    pizzaDao.addSessionSettings(settingsDbModel)
                }
            }
            if (orderDtoMapped != null && orderDtoMapped.status == OrderStatus.ACCEPT) {
                databaseService.onOrderFinished()
                orderDtoMapped = null
            }
            orderDtoMapped
        }
    //</editor-fold>

    //<editor-fold desc="getSessionSettingsCompletableDeferred">
    private fun getSessionSettingsCompletableDeferred(): SessionSettingsDbModel {
        val deferred = CompletableDeferred<SessionSettingsDbModel>(null)
        CoroutineScope(Dispatchers.IO).launch {
            pizzaDao.get().collect { sessionSettings ->
                val sessionSettingsDto = sessionSettings ?: SessionSettingsDbModel()
                if (deferred.complete(sessionSettingsDto)) {
                    this.cancel()
                }
            }
        }
        return runBlocking { deferred.await() }
    }
    //</editor-fold>

    //<editor-fold desc="subscribeListProducts">
    private fun subscribeListProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            databaseService
                .getListProductsFlow()
                .collect {
                    listProductsStateFlow.value = it
                }
        }
    }
    //</editor-fold>

    //<editor-fold desc="acceptOrderUseCase">
    override fun acceptOrderUseCase() {
        databaseService.acceptOrder()
    }
    //</editor-fold>

    //<editor-fold desc="disposeDBResponseUseCase">
    override fun disposeDBResponseUseCase() {
        dbResponseOrderFlow.value = DBResponseOrder.Initial
    }

    //</editor-fold>

    //<editor-fold desc="getDbResponseFlow">
    override fun getDbResponseFlow() = dbResponseOrderFlow.asStateFlow()
    //</editor-fold>
}