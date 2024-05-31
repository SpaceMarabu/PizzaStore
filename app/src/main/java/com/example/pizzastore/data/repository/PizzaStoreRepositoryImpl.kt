package com.example.pizzastore.data.repository

import android.util.Log
import com.example.cryptoapp.data.network.ApiFactory
import com.example.pizzastore.data.localdatabase.CityDao
import com.example.pizzastore.data.localdatabase.SessionSettingsDbModel
import com.example.pizzastore.data.mapper.LocalMapper
import com.example.pizzastore.data.mapper.RemoteMapper
import com.example.pizzastore.data.network.model.PathResponseDto
import com.example.pizzastore.data.remotedatabase.DatabaseService
import com.example.pizzastore.data.remotedatabase.entity.DBResultOrder
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject


class PizzaStoreRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val databaseService: DatabaseService,
    private val localMapper: LocalMapper,
    private val remoteMapper: RemoteMapper
) : PizzaStoreRepository {

    private val userBucket = MutableStateFlow(Bucket())
    private val currentOrderId = MutableStateFlow(Order.DEFAULT_ID)

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
        cityDao.get().map {
            if (it != null) {
                localMapper.mapSessionSettingsDbModelToEntity(
                    it.copy(city = city)
                )
            } else {
                SessionSettings(city = city)
            }
        }.collect {
            if (it == null) return@collect
            val dbModel = localMapper.mapSessionSettToDbModel(it)
            cityDao.addSessionSettings(dbModel)
        }
    }
    //</editor-fold>

    //<editor-fold desc="getCurrentSettingsUseCase">
    override fun getCurrentSettingsUseCase(): Flow<SessionSettings?> {
        return cityDao
            .get()
            .map {
                if (it != null) {
                    localMapper.mapSessionSettingsDbModelToEntity(it)
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
        cityDao.get().map {
            val currentAccount = it?.account ?: Account()
            val currentDeliveryDetails = currentAccount.deliveryDetails
            localMapper.mapSessionSettingsDbModelToEntity(
                it?.copy(
                    account = currentAccount.copy(
                        deliveryDetails = currentDeliveryDetails.copy(
                            pizzaStore = point
                        )
                    )
                )
            )
        }.collect {
            if (it == null) return@collect
            val dbModel = localMapper.mapSessionSettToDbModel(it)
            cityDao.addSessionSettings(dbModel)
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
        val sessionSettingsFlow = cityDao.get()
            .map {
                if (it != null) {
                    val settings = localMapper.mapSessionSettingsDbModelToEntity(it)
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
        cityDao.addSessionSettings(deferred.getCompleted())
    }
    //</editor-fold>

    //<editor-fold desc="finishOrderingUseCase">
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun finishOrderingUseCase() {
        val bucket = userBucket.value
        val bucketDto = remoteMapper.mapBucketToBucketDto(bucket)
        val orderId = when (val orderSendingResult = databaseService.sendCurrentOrder(bucketDto)) {
            is DBResultOrder.Complete -> orderSendingResult.orderId
            DBResultOrder.Error -> Order.ERROR_ID
        }
        currentOrderId.value = orderId
        if (orderId != Order.ERROR_ID) {
            val sessionSettingsFlow = cityDao.get()
                .map {
                    val settings =
                        localMapper.mapSessionSettingsDbModelToEntity(it) ?: SessionSettings()
                    val account = settings.account ?: Account()
                    val orders = account.orders.toMutableList()
                    orders.add(
                        Order(
                            id = orderId,
                            status = OrderStatus.NEW,
                            bucket = bucket
                        )
                    )
                    settings.copy(account = account.copy(orders = orders))
                }
            val deferred = CompletableDeferred<SessionSettingsDbModel>(null)
            CoroutineScope(Dispatchers.IO).launch {
                sessionSettingsFlow.collect {
                    val sessionSettingsDto = localMapper.mapSessionSettToDbModel(it)
                    deferred.complete(sessionSettingsDto)
                }
            }
            deferred.await()
            cityDao.addSessionSettings(deferred.getCompleted())
        }
    }
    //</editor-fold>

    //<editor-fold desc="getCurrentOrderIdUseCase">
//    override fun getCurrentOrderIdUseCase() = currentOrderId.asStateFlow()
    //</editor-fold>

    //<editor-fold desc="getOrderUseCase">
    override fun getOrderUseCase() = databaseService.getCurrentOrder()
    //</editor-fold>
}