/*
 * Copyright (c) 2020-2021. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.database_utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.ui.OpenDatabaseViewModel
import com.github.javafaker.Faker
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class SuperViewModelTests {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val model = OpenDatabaseViewModel()
    private val faker = Faker()

    @Test
    fun `initialize should set title`(){
        val titleStart = faker.lorem().sentence()
        val app = MyApp()
        app.databases = mutableListOf(Database("main"))

        model.initialize(app, 0, "", titleStart)
        Assert.assertEquals("$titleStart main", model.getTitle())
    }
}