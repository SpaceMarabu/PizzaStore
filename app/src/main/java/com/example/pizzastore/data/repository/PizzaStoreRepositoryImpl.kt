package com.example.pizzastore.data.repository

import android.util.Log
import com.example.cryptoapp.data.network.ApiFactory
import com.example.pizzastore.data.localdatabase.CityDao
import com.example.pizzastore.data.mapper.Mapper
import com.example.pizzastore.data.network.model.PathResponseDto
import com.example.pizzastore.data.remotedatabase.DatabaseService
import com.example.pizzastore.domain.entity.Account
import com.example.pizzastore.domain.entity.Address
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.SessionSettings
import com.example.pizzastore.domain.repository.PizzaStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject


class PizzaStoreRepositoryImpl @Inject constructor(
    private val cityDao: CityDao,
    private val databaseService: DatabaseService,
    private val mapper: Mapper
) : PizzaStoreRepository {

    //<editor-fold desc="getStoriesUseCase">
    override fun getStoriesUseCase() = databaseService.getListStoriesUri()
    //</editor-fold>

    //<editor-fold desc="getPathUseCase">
    override suspend fun getPathUseCase(point1: String, point2: String): Path {
        var call: Response<PathResponseDto>? = null
        var result = Path.EMPTY_PATH
        try {
            call = ApiFactory.apiService.getPath(point1, point2)
            val pathResponseDto = call.body() as PathResponseDto?
            val pathDto = pathResponseDto?.paths
            if (pathDto != null) {
                result = mapper.mapPathDtoToEntity(pathDto[0])
            }

        } catch (e: HttpException) {
            Log.d("HTTP_ERROR", e.code().toString())
        }
        return result
    }
    //</editor-fold>

    //<editor-fold desc="getAddressUseCase">
    override suspend fun getAddressUseCase(pointLatLng: String): Address {
        val addressDto = ApiFactory.apiService.getAddress(pointLatLng)
        var result = Address.EMPTY_ADDRESS
        if (addressDto.addressList.isNotEmpty()) {
            result = mapper.mapAddressDtoToEntity(
                addressDto.addressList[0]
            )
            addressDto.addressList.forEach {
                if (it.houseNumber != null) {
                    result = mapper.mapAddressDtoToEntity(it)
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
                mapper.mapSessionSettingsDbModelToEntity(
                    it.copy(city = city)
                )
            } else {
                SessionSettings(city = city)
            }
        }.collect {
            if (it == null) return@collect
            val dbModel = mapper.mapSessionSettToDbModel(it)
            cityDao.addSessionSettings(dbModel)
        }
    }
    //</editor-fold>

    //<editor-fold desc="getCurrentSettingsUseCase">
    override fun getCurrentSettingsUseCase(): Flow<SessionSettings?> {
        return cityDao.get().map {
            if (it != null) {
                mapper.mapSessionSettingsDbModelToEntity(it)
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
                mapper.mapSessionSettingsDbModelToEntity(
                    it?.copy(account = currentAccount.copy(pizzaStore = point))
                )
        }.collect {
            if (it == null) return@collect
            val dbModel = mapper.mapSessionSettToDbModel(it)
            cityDao.addSessionSettings(dbModel)
        }
    }
    //</editor-fold>

    //<editor-fold desc="getProductsUseCase">
    override fun getProductsUseCase(): Flow<List<Product>> =
        databaseService.getListProductsFlow()
    //</editor-fold>
}