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

package com.acmpo6ou.myaccounts.create_edit_database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.acmpo6ou.myaccounts.ModelTest
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.CreateDatabaseViewModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateDatabaseModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = CreateDatabaseViewModel()
    lateinit var spyModel: CreateDatabaseViewModel

    private val name = faker.str()
    override val password = faker.str()
    private val db = Database(name, password, salt)

    @Before
    fun setup(){
        val app = MyApp()
        app.databases = mutableListOf(Database("main"))

        model.initialize(app, SRC_DIR)
        spyModel = spy(model){ on{generateSalt()} doReturn salt }
        spyModel.uiDispatcher = Dispatchers.Unconfined
        spyModel.defaultDispatcher = Dispatchers.Unconfined
    }

    @Test
    fun `validateName should change emptyNameErr`(){
        // if name isn't empty emptyNameErr should be false
        model.validateName(faker.str())
        assertFalse(model.emptyNameErr)

        // if name is empty emptyNameErr should be true
        model.validateName("")
        assertTrue(model.emptyNameErr)
    }

    @Test
    fun `validateName should use fixName`(){
        val name = " \\/%$" // this name will be empty when cleaned by fixName
        model.validateName(name)
        assertTrue(model.emptyNameErr)
    }

    @Test
    fun `validateName should set existsNameErr to true when Database with such name exists`(){
        model.validateName("main")
        assertTrue(model.existsNameErr)

        // same should happen even if name contains unsupported characters
        model.validateName("m/a/i/n/") // will become `main` when cleaned by fixName
        assertTrue(model.existsNameErr)
    }

    @Test
    fun `apply should call createDatabase`(){
        runBlocking {
            spyModel.apply(name, password)
        }
        verify(spyModel).createDatabase(db)
    }

    @Test
    fun `apply should handle any error`(){
        val msg = faker.str()
        val exception = Exception(msg)
        whenever(spyModel.createDatabase(db))
                .doAnswer{
                    throw exception
                }

        runBlocking {
            spyModel.apply(name, password)
        }
        assertEquals(exception.toString(), spyModel.errorMsg)
        assertFalse(spyModel.loading)
    }

    @Test
    fun `apply should add created Database to the list`(){
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(db in spyModel.databases)
    }

    @Test
    fun `apply should set created`(){
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(spyModel.created)
    }

    @Test
    fun `apply should set loading to true`(){
        runBlocking {
            spyModel.apply(name, password)
        }
        assertTrue(spyModel.loading)
    }
}