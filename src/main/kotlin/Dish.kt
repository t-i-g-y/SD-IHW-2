package org.example

// Расширенный класс Dish с отзывами
data class Dish(val name: String, var quantity: Int, var price: Double, var preparationTime: Int) {
    val reviews: MutableList<Review> = mutableListOf()

    fun addReview(review: Review) {
        reviews.add(review)
    }

    fun getAverageRating(): Double = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else 0.0
}
