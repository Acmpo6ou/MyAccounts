/*
 * Copyright (c) 2020. Kolvakh Bohdan
 * This file is part of MyAccounts.
 *
 * MyAccounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyAccounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.acmpo6ou.myaccounts.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.openDatabaseUtil

open class OpenDatabaseViewModel : ViewModel() {
    private var databaseIndex: Int = 0
    lateinit var app: MyApp
    lateinit var SRC_DIR: String

    private val title = MutableLiveData<String>()
    private val incorrectPassword = MutableLiveData<Boolean>()

    fun getTitle() = title
    fun getIncorrectPassword() = incorrectPassword

    /**
     * This method is called by fragment to initialize ViewModel.
     *
     * Saves [app], [SRC_DIR] and [databaseIndex]. Sets title for app bar.
     * @param[app] application instance used to access databases list.
     * @param[databaseIndex] index of database that we want to open.
     * @param[SRC_DIR] path to src directory that contains databases.
     */
    fun setDatabase(app: MyApp, databaseIndex: Int, SRC_DIR: String) {
        this.app = app
        this.SRC_DIR = SRC_DIR
        this.databaseIndex = databaseIndex

        val name = app.databases[databaseIndex].name
        title.value = "Open $name"
    }

    fun verifyPassword(password: String){

    }

    /**
     * Used to open databases by given Database instance.
     *
     * In particular opening database means reading content of corresponding .db file,
     * decrypting and deserializing it, then assigning deserialized database map to `data`
     * property of given Database.
     *
     * @param[database] Database instance with password, name and salt to open database.
     * @return same Database instance but with `data` property filled with deserialized
     * database map.
     */
    open fun openDatabase(database: Database): Database {
        return openDatabaseUtil(database, SRC_DIR)
    }
}