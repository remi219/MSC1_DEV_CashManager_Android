package com.example.cashmanager.data

import com.example.cashmanager.data.model.ServerData

/**
 * Class that requests authentication and server information from the remote data source and
 * maintains an in-memory cache of login status and server credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var server: ServerData? = null
        private set

    val isLoggedIn: Boolean
        get() = server != null

    init {
        // If server credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        server = null
    }

    fun logout() {
        server = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Result<ServerData> {
        // handle login
        val result = dataSource.login(username, password)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(serverData: ServerData) {
        this.server = serverData
        // If server credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}
