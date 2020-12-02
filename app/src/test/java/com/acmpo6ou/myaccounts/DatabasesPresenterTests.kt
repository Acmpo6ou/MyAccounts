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

package com.acmpo6ou.myaccounts

import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabaseFragmentInter
import com.acmpo6ou.myaccounts.core.DatabasesPresenter
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Spy

class DatabasesPresenterTests {
    private lateinit var view: DatabaseFragmentInter
    private lateinit var presenter: DatabasesPresenter
    private lateinit var presenterSpy: DatabasesPresenter

    @Before
    fun setUp(){
        view = mock()
        presenter = DatabasesPresenter(view)
    }

    private fun setupPresenterSpy(){
        presenterSpy = spy(presenter)
        presenterSpy.databases = listOf(Database("main"))
    }

    @Test
    fun `exportSelected should call exportDialog`(){
        presenter.exportSelected(0)
        verify(view).exportDialog(0)
    }

    @Test
    fun `exportSelected should save database index to exportIndex`(){
        presenter.exportSelected(1)
        assertEquals(1, presenter.exportIndex)
    }

    @Test
    fun `deleteSelected should call confirmDelete`(){
        presenter.deleteSelected(0)
        verify(view).confirmDelete(0)
    }

    @Test
    fun `closeSelected should call closeDatabase when database is saved`(){
        setupPresenterSpy()
        whenever(presenterSpy.isDatabaseSaved(0)).thenReturn(true)

        presenterSpy.closeSelected(0)
        verify(presenterSpy).closeDatabase(0)
    }

    @Test
    fun `closeSelected should call confirmClose when database isn't saved`(){
        setupPresenterSpy()
        whenever(presenterSpy.isDatabaseSaved(0)).thenReturn(false)

        presenterSpy.closeSelected(0)
        verify(view).confirmClose(0)
    }
}