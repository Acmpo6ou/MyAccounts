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
import com.acmpo6ou.myaccounts.core.createDatabaseUtil
import com.acmpo6ou.myaccounts.core.deriveKeyUtil
import com.acmpo6ou.myaccounts.getDatabaseMap
import com.acmpo6ou.myaccounts.str
import com.acmpo6ou.myaccounts.ui.EditDatabaseViewModel
import com.macasaet.fernet.StringValidator
import com.macasaet.fernet.Token
import com.macasaet.fernet.Validator
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.spy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount

class EditDatabaseModelTests : ModelTest() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    val model = EditDatabaseViewModel()
    lateinit var spyModel: EditDatabaseViewModel

    private val name = faker.str()
    override val password = faker.str()
    private val db = Database(name, password, salt)

    @Before
    fun setup(){
        val app = MyApp()
        app.databases = mutableListOf(Database("main"), Database("test"))

        model.initialize(app, SRC_DIR, faker.str(), 1)
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
    fun `existsNameErr should be false when Database with such name exists but it's being edited`(){
        // database `test` already exists but it's being edited, so it doesn't count
        model.validateName("test")
        assertFalse(model.existsNameErr)
    }

    @Test
    fun `apply should call createDatabase`(){
        runBlocking {
            spyModel.apply(name, password)
        }
//        verify(spyModel).saveDatabase(db)
    }

    @Test
    fun `apply should handle any error`(){
        val msg = faker.str()
        val exception = Exception(msg)
//        whenever(spyModel.saveDatabase(db))
//                .doAnswer{
//                    throw exception
//                }

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

    /**
     * Helper method used by saveDatabase test to create old database and to call
     * saveDatabase passing through new database.
     */
    private fun setUpSaveDatabase(){
        // this database will be deleted by saveDatabase
        val db = Database("test", "123", salt)
        createDatabaseUtil(db, SRC_DIR, MyApp())

        // this database will be created by saveDatabase
        val newDb = Database("test2", "321",
                salt.reversedArray(), getDatabaseMap())

        // save newDb deleting db
        runBlocking {
            model.saveDatabase("test", newDb, MyApp()).await()
        }
    }

    /**
     * This method decrypts given string using password `123` and [salt].
     *
     * @param[string] string to decrypt.
     * @return decrypted string.
     */
    private fun decryptStr(string: String): String{
        val key = deriveKeyUtil("321", salt.reversedArray())
        val validator: Validator<String> = object : StringValidator {
            // this checks whether our encrypted json string is expired or not
            // in our app we don't care about expiration so we return Instant.MAX.epochSecond
            override fun getTimeToLive(): TemporalAmount {
                return Duration.ofSeconds(Instant.MAX.epochSecond)
            }
        }
        val token = Token.fromString(string)
        return token.validateAndDecrypt(key, validator)
    }

    @Test
    fun `saveDatabase should delete files of old database`(){
        setUpSaveDatabase()

        // check that there is no longer test.db and test.bin files
        val oldDb = File("$SRC_DIR/test.db")
        val oldBin = File("$SRC_DIR/test.bin")

        assertFalse(".db file of old database is not deleted by saveDatabase method!",
                oldDb.exists())
        assertFalse(".bin file of old database is not deleted by saveDatabase method!",
                oldBin.exists())
    }

    @Test
    fun `saveDatabase should create new, non empty database file`(){
        setUpSaveDatabase()

        // this is a .db file that saveDatabase should create for us
        val actualDb = File("$SRC_DIR/test2.db").readBytes()

        // here we decrypt data saved to .db file to check that it was encrypted correctly
        val data = decryptStr(String(actualDb))
        assertEquals("saveDatabase creates incorrect database!",
                jsonDatabase, data)
    }

    @Test
    fun `saveDatabase should create new, non empty salt file`(){
        setUpSaveDatabase()

        // this is a .bin file that saveDatabase should create for us
        val actualBin = File("$SRC_DIR/test2.bin").readBytes()

        // .bin file must have appropriate content (i.e. salt)
        assertEquals("saveDatabase created .bin file with incorrect salt!",
                String(salt.reversedArray()), String(actualBin))
    }
}