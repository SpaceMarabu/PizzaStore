package com.example.pizzastore.data.mapper

import com.example.pizzastore.data.localdatabase.entity.AccountDbModel
import com.example.pizzastore.data.localdatabase.entity.AddressDetailsDbModel
import com.example.pizzastore.data.localdatabase.entity.BucketDbModel
import com.example.pizzastore.data.localdatabase.entity.CityDbModel
import com.example.pizzastore.data.localdatabase.entity.DeliveryDetailsDbModel
import com.example.pizzastore.data.localdatabase.entity.OrderDbModel
import com.example.pizzastore.data.localdatabase.entity.PointDbModel
import com.example.pizzastore.data.localdatabase.entity.ProductCountDbModel
import com.example.pizzastore.data.localdatabase.entity.SessionSettingsDbModel
import com.example.pizzastore.data.network.model.AddressDto
import com.example.pizzastore.data.network.model.PathDto
import com.example.pizzastore.domain.entity.Account
import com.example.pizzastore.domain.entity.AddressDetails
import com.example.pizzastore.domain.entity.AddressWithPath
import com.example.pizzastore.domain.entity.Bucket
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.DeliveryDetails
import com.example.pizzastore.domain.entity.DeliveryType
import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.entity.OrderStatus
import com.example.pizzastore.domain.entity.Path
import com.example.pizzastore.domain.entity.Point
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.SessionSettings
import javax.inject.Inject

class LocalMapper @Inject constructor() {

    //<editor-fold desc="mapSessionSettToDbModel">
    fun mapSessionSettToDbModel(sessionSettings: SessionSettings): SessionSettingsDbModel {
        val city = sessionSettings.city
        val account = sessionSettings.account ?: Account()
        val ordersIn = account.orders
        val ordersOut = mutableListOf<OrderDbModel>()
        ordersIn.forEach { order ->
            ordersOut.add(
                OrderDbModel(
                    id = order.id,
                    status = order.status.ordinal.toString(),
                    bucket = mapBucketToDbModel(order.bucket)
                )
            )
        }

        val accountDbModel = AccountDbModel(
            account.id,
            orders = ordersOut,
            number = account.number,
            name = account.name,
            lastName = account.lastName,
            deliveryDetails = mapDeliveryDetailsToDbModel(account.deliveryDetails)
        )

        return SessionSettingsDbModel(
            id = sessionSettings.id,
            city = mapCityToCityDbModel(city),
            account = accountDbModel
        )
    }
    //</editor-fold>

    //<editor-fold desc="mapSessionSettingsDbModelToEntity">
    fun mapSessionSettingsDbModelToEntity(
        settingsDbModel: SessionSettingsDbModel?,
        products: List<Product>
    ): SessionSettings? {
        return if (settingsDbModel == null) {
            null
        } else {
            val account = settingsDbModel.account ?: AccountDbModel()
            val ordersIn = account.orders
            val ordersOut = mutableListOf<Order>()
            ordersIn.forEach { orderDbModel ->
                val bucket = mapDbModelToBucket(orderDbModel.bucket, products)
                ordersOut.add(
                    Order(
                        id = orderDbModel.id,
                        status = getOrderStatusByDbModel(orderDbModel),
                        bucket = bucket
                    )
                )
            }
            SessionSettings(
                city = mapDbModelToCity(settingsDbModel.city),
                account = Account(
                    id = account.id,
                    number = account.number,
                    name = account.name,
                    lastName = account.lastName,
                    deliveryDetails = mapDeliveryDetailsDbModelToEntity(account.deliveryDetails),
                    orders = ordersOut
                ),
            )
        }
    }
    //</editor-fold>

    //<editor-fold desc="mapDeliveryDetailsToDbModel">
    private fun mapDeliveryDetailsToDbModel(
        deliveryDetails: DeliveryDetails
    ): DeliveryDetailsDbModel {

        val point = deliveryDetails.pizzaStore ?: Point()
        val pointDbModel = PointDbModel(
            id = point.id,
            address = point.address,
            coords = point.coords
        )

        val deliveryAddress = deliveryDetails.deliveryAddress ?: AddressDetails()
        val addressDetailsDbModel = AddressDetailsDbModel(
            address = deliveryAddress.address,
            entrance = deliveryAddress.entrance,
            doorCode = deliveryAddress.doorCode,
            floor = deliveryAddress.doorCode,
            apartment = deliveryAddress.apartment,
            comment = deliveryAddress.comment
        )

        return DeliveryDetailsDbModel(
            type = deliveryDetails.type.ordinal.toString(),
            pizzaStore = pointDbModel,
            deliveryAddress = addressDetailsDbModel,
            deliveryGeoPoint = deliveryDetails.deliveryGeoPoint
        )
    }
    //</editor-fold>

    //<editor-fold desc="mapDeliveryDetailsDbModelToEntity">
    private fun mapDeliveryDetailsDbModelToEntity(
        deliveryDetailsDbModel: DeliveryDetailsDbModel
    ): DeliveryDetails {

        val point = deliveryDetailsDbModel.pizzaStore ?: PointDbModel()
        val pointDbModel = Point(
            id = point.id,
            address = point.address,
            coords = point.coords
        )

        val deliveryAddress = deliveryDetailsDbModel.deliveryAddress ?: AddressDetailsDbModel()
        val addressDetailsDbModel = AddressDetails(
            address = deliveryAddress.address,
            entrance = deliveryAddress.entrance,
            doorCode = deliveryAddress.doorCode,
            floor = deliveryAddress.doorCode,
            apartment = deliveryAddress.apartment,
            comment = deliveryAddress.comment
        )

        val deliveryType = when (deliveryDetailsDbModel.type.toInt()) {
            DeliveryType.DELIVERY_TO.ordinal -> DeliveryType.DELIVERY_TO
            else -> DeliveryType.TAKE_OUT
        }

        return DeliveryDetails(
            type = deliveryType,
            pizzaStore = pointDbModel,
            deliveryAddress = addressDetailsDbModel,
            deliveryGeoPoint = deliveryDetailsDbModel.deliveryGeoPoint
        )
    }
    //</editor-fold>

    //<editor-fold desc="mapPointToDbModel">
    fun mapPointToDbModel(point: Point) = PointDbModel(
        id = point.id,
        address = point.address,
        coords = point.coords
    )
    //</editor-fold>

    //<editor-fold desc="getOrderStatusByDbModel">
    private fun getOrderStatusByDbModel(dbModel: OrderDbModel): OrderStatus {
        return when (dbModel.status.toInt()) {
            OrderStatus.NEW.ordinal -> OrderStatus.NEW
            OrderStatus.PROCESSING.ordinal -> OrderStatus.PROCESSING
            OrderStatus.FINISH.ordinal -> OrderStatus.FINISH
            else -> OrderStatus.ACCEPT
        }
    }
    //</editor-fold>

    //<editor-fold desc="mapAddressDtoToEntity">
    fun mapAddressDtoToEntity(dto: AddressDto): AddressWithPath {
        return AddressWithPath(
            city = dto.city,
            street = dto.street,
            houseNumber = dto.houseNumber
        )
    }
    //</editor-fold>

    //<editor-fold desc="mapPathDtoToEntity">
    fun mapPathDtoToEntity(dto: PathDto): Path {
        return Path(
            distance = dto.distance,
            time = dto.time
        )
    }
    //</editor-fold>

    //<editor-fold desc="mapBucketToDbModel">
    private fun mapBucketToDbModel(bucket: Bucket): BucketDbModel {
        val orderMap = bucket.order
        val orderList = mutableListOf<ProductCountDbModel>()
        orderMap.forEach {
            orderList.add(
                ProductCountDbModel(
                    idProduct = it.key.id,
                    productCount = it.value
                )
            )
        }
        return BucketDbModel(
            order = orderList
        )
    }
    //</editor-fold>

    //<editor-fold desc="mapDbModelToBucket">
    private fun mapDbModelToBucket(bucketDbModel: BucketDbModel, products: List<Product>): Bucket {
        val orderList = bucketDbModel.order
        val orderMap = mutableMapOf<Product, Int>()
        orderList.forEach { currentProductCount ->
            products.forEach { product ->
                if (product.id == currentProductCount.productCount) {
                    orderMap[product] = currentProductCount.productCount
                }
            }
        }
        return Bucket(orderMap)
    }
    //</editor-fold>

    //<editor-fold desc="mapCityToCityDbModel">
    fun mapCityToCityDbModel(city: City?): CityDbModel? {
        if (city == null) return null
        val points = mutableListOf<PointDbModel>()
        city.points.forEach {
            val pointDbModel = PointDbModel(
                id = it.id,
                address = it.address,
                coords = it.coords
            )
            points.add(pointDbModel)
        }
        return CityDbModel(
            id = city.id,
            name = city.name,
            deliveryType = city.deliveryType.ordinal.toString(),
            points = points
        )
    }
    //</editor-fold>

    //<editor-fold desc="mapDbModelToCity">
    private fun mapDbModelToCity(cityDbModel: CityDbModel?): City? {
        if (cityDbModel == null) return null
        val pointsDbModel = mutableListOf<Point>()
        cityDbModel.points.forEach {
            val point = Point(
                id = it.id,
                address = it.address,
                coords = it.coords
            )
            pointsDbModel.add(point)
        }
        return City(
            id = cityDbModel.id,
            name = cityDbModel.name,
            deliveryType = getDeliveryTypeByDbModel(cityDbModel),
            points = pointsDbModel
        )
    }
    //</editor-fold>

    //<editor-fold desc="getDeliveryTypeByDbModel">
    private fun getDeliveryTypeByDbModel(dbModel: CityDbModel): DeliveryType {
        return when (dbModel.deliveryType) {
            DeliveryType.DELIVERY_TO.ordinal.toString() -> DeliveryType.DELIVERY_TO
            else -> DeliveryType.TAKE_OUT
        }
    }
    //</editor-fold>
}
