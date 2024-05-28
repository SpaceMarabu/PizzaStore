package com.example.pizzastore.data.remotedatabase

import android.net.Uri
import android.util.Log
import com.example.pizzastore.domain.entity.City
import com.example.pizzastore.domain.entity.Order
import com.example.pizzastore.domain.entity.Product
import com.example.pizzastore.domain.entity.ProductType
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseImpl : DatabaseService {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage = Firebase.storage("gs://pizzastore-b379f.appspot.com")
    private val storageRef = firebaseStorage.reference.child("product")

    private val dRefProduct = firebaseDatabase.getReference("product")
    private val dRefCities = firebaseDatabase.getReference("cities")
    private val dRefAccount = firebaseDatabase.getReference("account")
    private val dRefOrder = firebaseDatabase.getReference("order")

    private val listPicturesUriFlow: MutableSharedFlow<List<Uri>> = MutableSharedFlow(replay = 1)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val listOrdersStateFlow = MutableStateFlow<List<Order>>(listOf())

    //<editor-fold desc="listOrdersColdFlow">
    private val listOrdersColdFlow = callbackFlow {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listOrders = mutableListOf<Order>()
                for (data in dataSnapshot.children) {
                    val key: Int = dataFromChildren.key?.toInt() ?: continue
                    if (maxProductIdFlow.value < key) {
                        maxProductIdFlow.value = key
                    }

                    val id = dataFromChildren.child("id").value.toString().toInt()
                    val name = dataFromChildren.child("name").value.toString()
                    val typeFromSnapshot = dataFromChildren.child("type").child("type").value
                    val price = dataFromChildren.child("price").value.toString().toInt()
                    val photo = dataFromChildren.child("photo").value.toString()
                    val description = dataFromChildren.child("description").value.toString()
                    val typeObject = when (typeFromSnapshot) {
                        ProductType.PIZZA.type -> ProductType.PIZZA
                        ProductType.DESSERT.type -> ProductType.DESSERT
                        ProductType.STARTER.type -> ProductType.STARTER
                        ProductType.DRINK.type -> ProductType.DRINK
                        else -> ProductType.ROLL
                    }
                    listOrders.add(
                        Order(
                            id = key.toInt(),
                            status = value.status,
                            bucket = value.bucket
                        )
                    )
                }
                val returnList = listOrders.toList()
                trySend(returnList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "PizzaStoreFirebaseImpl",
                    "loadCities:onCancelled",
                    databaseError.toException()
                )
            }
        }
        dRefOrder.addValueEventListener(postListener)

        awaitClose {
            dRefOrder.removeEventListener(postListener)
        }
    }
    //</editor-fold>

    //<editor-fold desc="listCitiesFlow">
    private val listCitiesFlow = callbackFlow {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listCities = mutableListOf<City>()
                for (data in dataSnapshot.children) {
                    val key: String = data.key ?: continue
                    val value = data.getValue(City::class.java) ?: continue
                    listCities.add(
                        City(
                            id = key.toInt(),
                            name = value.name,
                            points = value.points.filterNotNull()
                        )
                    )
                }
                val returnList = listCities.toList()
                trySend(returnList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "PizzaStoreRepositoryImpl",
                    "loadCities:onCancelled",
                    databaseError.toException()
                )
            }
        }
        dRefCities.addValueEventListener(postListener)

        awaitClose {
            dRefCities.removeEventListener(postListener)
        }
    }
    //</editor-fold>

    //<editor-fold desc="listProductsFlow">
    private val listProductsFlow = callbackFlow {

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listProducts = mutableListOf<Product>()
                for (dataFromChildren in dataSnapshot.children) {

                    val id = dataFromChildren.child("id").value.toString().toInt()
                    val name = dataFromChildren.child("name").value.toString()
                    val typeFromSnapshot = dataFromChildren.child("type").child("type").value
                    val price = dataFromChildren.child("price").value.toString().toInt()
                    val photo = dataFromChildren.child("photo").value.toString()
                    val description = dataFromChildren.child("description").value.toString()
                    val typeObject = when (typeFromSnapshot) {
                        ProductType.PIZZA.type -> ProductType.PIZZA
                        ProductType.DESSERT.type -> ProductType.DESSERT
                        ProductType.STARTER.type -> ProductType.STARTER
                        ProductType.DRINK.type -> ProductType.DRINK
                        else -> ProductType.ROLL
                    }
                    listProducts.add(
                        Product(
                            id = id,
                            name = name,
                            type = typeObject,
                            price = price,
                            photo = photo,
                            description = description
                        )
                    )
                }
                val returnList = listProducts.toList()
                trySend(returnList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(
                    "PizzaStoreRepositoryImpl",
                    "loadCities:onCancelled",
                    databaseError.toException()
                )
            }
        }
        dRefProduct.addValueEventListener(postListener)

        awaitClose {
            dRefProduct.removeEventListener(postListener)
        }
    }

    //</editor-fold>
    init {
        coroutineScope.launch {
            loadStoriesUri()
            subscribeOrders()
        }
    }

    private suspend fun subscribeOrders() {
        listOrdersColdFlow
            .stateIn(CoroutineScope(Dispatchers.IO))
            .collect {
                listOrdersStateFlow.emit(it)
            }
    }

    suspend fun addOrEditOrder(order: Order) {
        var currentOrder = order
        val currentIdToInsert = 1
        val orderId = if (currentOrder.id == -1) {
            currentOrder = currentOrder.copy(id = currentIdToInsert)
            currentIdToInsert.toString()
        } else {
            currentOrder.id.toString()
        }
        withContext(Dispatchers.IO) {
            dRefOrder.child(orderId)
                .setValue(currentOrder)
                .addOnSuccessListener {
                    //
                }
                .addOnFailureListener { e ->
                    //
                }
        }
    }

    //<editor-fold desc="getListProductsFlow">
    override fun getListProductsFlow(): Flow<List<Product>> = listProductsFlow
    //</editor-fold>

    //<editor-fold desc="getListCitiesFlow">
    override fun getListCitiesFlow() = listCitiesFlow
    //</editor-fold>

    //<editor-fold desc="getListStoriesUri">
    override fun getListStoriesUri() = listPicturesUriFlow.asSharedFlow()
    //</editor-fold>

    //<editor-fold desc="loadStoriesUri">
    private suspend fun loadStoriesUri() {
        withContext(Dispatchers.IO) {
            val scope = CoroutineScope(Dispatchers.IO)
            storageRef
                .child("story")
                .listAll()
                .addOnSuccessListener { result ->
                    val tempUriList = mutableListOf<Uri>()
                    scope.launch {
                        result
                            .items
                            .forEach { storageReference ->
                                val uri = getUriByStorageReference(storageReference)
                                tempUriList.add(uri)
                            }
                        listPicturesUriFlow.emit(tempUriList)
                    }
                }
        }
    }
    //</editor-fold>

    //<editor-fold desc="getUriByStorageReference">
    private suspend fun getUriByStorageReference(storageReference: StorageReference) =
        withContext(Dispatchers.IO) {
            val deferred = CompletableDeferred<Uri>()
            storageReference
                .downloadUrl
                .addOnSuccessListener {
                    if (it != null) {
                        deferred.complete(it)
                    }
                }
            deferred.await()
        }
//</editor-fold>

}