package com.example.pizzastore.domain.entity

data class Path(
    val distance: Float = EMPTY_PATH_DISTANCE,
    val time: Long = EMPTY_PATH_TIME
) {
    companion object {
        const val EMPTY_PATH_DISTANCE = -1F
        const val EMPTY_PATH_TIME = -1L
        val EMPTY_PATH = Path()
    }
}
