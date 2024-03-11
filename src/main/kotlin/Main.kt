package org.example
import java.time.LocalDateTime
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    val menu = Menu() // Создание объекта меню
    val admin = UserFactory.createUser("admin", "admin", "000") // Создание администратора
    val visitor1 = UserFactory.createUser("visitor", "user1", "pass1") // Создание посетителей
    val visitor2 = UserFactory.createUser("visitor", "user2", "pass2")
    val visitor3 = UserFactory.createUser("visitor", "user3", "pass3")

    // Инициализация хранилища данных
    val dataStorage = DataStorage
    dataStorage.saveData("\n" + LocalDateTime.now().toString())
    // Создание списка пользователей с администратором и посетителями
    val users = mutableListOf(admin, visitor1, visitor2, visitor3)

    // Добавление блюд в меню
    val dish1 = Dish("Complex", 100, 180.00, 5)
    val dish2 = Dish("Pizza", 28, 500.00, 7)
    val dish3 = Dish("Salad", 80, 350.00, 4)

    menu.addDish(dish1)
    menu.addDish(dish2)
    menu.addDish(dish3)

    var currentUser: User?

    while (true) {
        println("\n=== Restaurant Management System (RMS) ===")
        println("1. Login")
        println("2. Register")
        println("3. Exit")
        println("Choose option: ")

        try {
            when (scanner.nextInt()) {
                1 -> {
                    println("Username: ")
                    val username = scanner.next()
                    println("Password: ")
                    val password = scanner.next()

                    currentUser = users.find { it.username == username && it.password == password }

                    if (currentUser == null) {
                        println("Authorization error. Try again: ")
                        continue
                    }

                    when (currentUser) {
                        is Admin -> adminMenu(currentUser, menu, scanner, dataStorage)
                        is Visitor -> visitorMenu(currentUser, menu, scanner, dataStorage)
                    }
                }
                2 -> {
                    println("Username: ")
                    val username = scanner.next()

                    currentUser = users.find { it.username == username }
                    if (currentUser != null) {
                        println("Username is taken. Try again")
                        continue
                    }
                    println("Password: ")
                    val password = scanner.next()
                    users.add(UserFactory.createUser("visitor", username, password))
                    dataStorage.saveData("User $username added")
                    println("User $username added")
                    continue
                }
                3 -> return
                else -> println("Invalid input. Please, try again.")
            }
        } catch (e: InputMismatchException) {
            println("Invalid input. Please, try again.")
            scanner.next()
        }
    }
}


fun adminMenu(admin: Admin, menu: Menu, scanner: Scanner, dataStorage: DataStorage) {
    while (true) {
        println("\n=== Admin Menu ===")
        println("1. Add dish")
        println("2. Remove dish")
        println("3. Update dish info")
        println("4. Open menu")
        println("5. View reviews and statistics")
        println("6. Back")

        when (scanner.nextInt()) {
            1 -> {
                println("Enter dish name: ")
                val name = scanner.next()
                println("Enter quantity: ")
                val quantity = scanner.nextInt()
                println("Enter price: ")
                val price = scanner.nextDouble()
                println("Enter preparation time: ")
                val prepTime = scanner.nextInt()

                val dish = Dish(name, quantity, price, prepTime)
                admin.addDish(menu, dish)
                dataStorage.saveData("Added dish: $name")
                println("Added dish.")
            }

            2 -> {
                println("Input name of dish to remove: ")
                val name = scanner.next()
                admin.removeDish(menu, name)
                dataStorage.saveData("Dish removed: $name")
                println("Dish removed.")
            }

            3 -> {
                println("Enter name of dish to update: ")
                val name = scanner.next()
                println("Enter new quantity: ")
                val quantity = scanner.nextInt()
                println("Enter new quantity: ")
                val price = scanner.nextDouble()
                println("Enter new preparation time: ")
                val prepTime = scanner.nextInt()

                admin.setDishDetails(menu, name, quantity, price, prepTime)
                dataStorage.saveData("Updated dish: $name")
                println("Updated dish.")
            }

            4 -> {
                menu.listDishes()
            }

            5 -> {
                menu.dishes.forEach { dish ->
                    println("Dish: ${dish.name}")
                    println("Average rating: ${dish.getAverageRating()}")
                    println("Reviews:")
                    dish.reviews.forEach { review ->
                        println("Rating: ${review.rating}, Comment: ${review.comment}")
                    }
                }
            }
            6 -> return
        }
    }
}

fun visitorMenu(visitor: Visitor, menu: Menu, scanner: Scanner, dataStorage: DataStorage) {
    var currentOrder: Order? = null

    while (true) {
        println("\n=== Visitor Menu ===")
        println("1. Open menu")
        println("2. Make order")
        println("3. Add dish to order")
        println("4. Cancel order")
        println("5. Pay for order and leave review")
        println("6. Back")

        when (scanner.nextInt()) {
            1 -> menu.listDishes()
            2 -> {
                val orderDetails = mutableMapOf<String, Int>()
                println("Please, enter each dish as [Dish] [Quantity]. Enter 'done', when finished.\nExample: Pizza 2")
                while (true) {
                    print("Enter dish or 'done' to stop: ")
                    val input = scanner.nextLine()

                    if (input.lowercase() == "done") break

                    val parts = input.split(" ")
                    if (parts.size < 2) {
                        println("Incorrect input. Please, enter dish info as [Dish] [Quantity].")
                        continue
                    }

                    try {
                        val dishName = parts.dropLast(1).joinToString(" ")
                        val quantity = parts.last().toInt()

                        if (quantity <= 0) {
                            println("Quantity must be over 0. Please, try again.")
                            continue
                        }

                        if (menu.getDish(dishName) == null) {
                            println("Dish '$dishName' is not found in the menu. Please, try again.")
                            continue
                        }

                        if (menu.getDish(dishName)!!.quantity < quantity) {
                            println("Not enough in supply for this dish. Please, ry again")
                            continue
                        }
                        orderDetails[dishName] = quantity
                        println("Added $quantity portions of $dishName to your order.")
                    } catch (e: NumberFormatException) {
                        println("Incorrect quantity, please input numbers in the correct format.")
                    }
                }

                if (orderDetails.isNotEmpty()) {
                    currentOrder = visitor.placeOrder(menu, orderDetails)
                    dataStorage.saveData("Order is placed: ${currentOrder.id}")
                    println("Order with ID ${currentOrder.id} is placed.")
                } else {
                    println("Order is not placed.")
                }
            }

            3 -> {
                if (currentOrder == null || currentOrder.isCompleted) {
                    println("No active orders to add dish to.")
                    continue
                }

                println("Enter name of dish to add: ")
                val dishName = scanner.next()
                println("Enter quantity: ")
                val quantity = scanner.nextInt()

                val dish = menu.getDish(dishName)
                if (dish != null) {
                    visitor.addToOrder(currentOrder, dish, quantity)
                    dataStorage.saveData("Added to order: ${currentOrder.id}, dish: $dishName")
                    println("Dish added to order.")
                } else {
                    println("Dish not found.")
                }
            }

            4 -> {
                if (currentOrder != null && !currentOrder.isCompleted) {
                    visitor.cancelOrder(currentOrder)
                    dataStorage.saveData("Order cancelled: ${currentOrder.id}")
                    println("Order cancelled.")
                } else {
                    println("No active order to cancel.")
                }
            }

            5 -> {
                if (currentOrder != null && currentOrder.isCompleted) {
                    println("Order payment: ${currentOrder.id}")
                    println("Leave review? (Y/N)")
                    val decision = scanner.next()
                    if (decision.equals("Y", ignoreCase = true)) {
                        currentOrder.dishes.keys.forEach { dish ->
                            println("Enter rating for ${dish.name} (1-5): ")
                            val rating = scanner.nextInt()
                            println("Enter comment for ${dish.name}: ")
                            val comment = scanner.next()
                            visitor.leaveReview(dish, rating, comment)
                        }
                    }
                    currentOrder.pay()
                } else {
                    println("No order to pay for.")
                }
            }

            6 -> return
            else -> println("Incorrect option, please try again.")
        }
    }
}