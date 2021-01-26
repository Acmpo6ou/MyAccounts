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
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.ui.EditDatabaseFragment
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class EditDatabaseFragmentInst {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    lateinit var scenario: FragmentScenario<EditDatabaseFragment>
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val app = context.applicationContext as MyApp
    lateinit var db: Database

    @Before
    fun setup() {
        db = Database("main", "123")
        app.databases = mutableListOf(db)

        scenario = launchFragmentInContainer(themeResId= R.style.Theme_MyAccounts_NoActionBar)
        scenario.onFragment {
            it.app = app
            it.initModel()
            it.args = mock{on{databaseIndex} doReturn 0}
        }
    }

    @Test
    fun `initForm should fill name and password fields`(){
        scenario.onFragment {
            it.initForm()
            assertEquals(db.name, it.b.databaseName.text.toString())
            assertEquals(db.password, it.b.databasePassword.text.toString())
            assertEquals(db.password, it.b.databaseRepeatPassword.text.toString())
        }
    }
}