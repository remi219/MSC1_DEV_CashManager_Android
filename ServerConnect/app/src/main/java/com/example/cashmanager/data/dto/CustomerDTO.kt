package com.example.cashmanager.data.dto

/**
 * Dto for customer entity
 */
class CustomerDTO() {
    val id: Int? = null

    var username: String? = null

    var password: String? = null

    var email: String? = null

    var firstName: String? = null

    var lastName: String? = null

    constructor(username: String, password: String, email: String, firstName: String, lastName: String) : this() {
        this.username = username
        this.password = password
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
    }
}