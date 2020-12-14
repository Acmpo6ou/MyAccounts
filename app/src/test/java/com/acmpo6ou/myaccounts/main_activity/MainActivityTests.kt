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

package com.acmpo6ou.myaccounts.main_activity

import android.app.Activity
import com.acmpo6ou.myaccounts.DatabaseViewTest
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MainPresenterInter
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Test
import org.robolectric.fakes.RoboMenuItem

class MainActivityTests: DatabaseViewTest() {
    private lateinit var activity: MainActivity
    private lateinit var presenter: MainPresenterInter

    @Before
    fun setup(){
        activity = MainActivity()

        // mock presenter and intent
        presenter = mock()
        mockIntent()
        activity.presenter = presenter
    }

    @Test
    fun `'Import database' should call presenter importSelected`(){
        // simulate selecting `Import database` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.import_database))

        verify(presenter).importSelected()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `'Check for updates' should call presenter checkUpdatesSelected`(){
        // simulate selecting `Check for updates` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.check_for_updates))

        verify(presenter).checkUpdatesSelected()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `'Changelog' should call presenter navigateToChangelog`(){
        // simulate selecting `Changelog` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.changelog))

        verify(presenter).navigateToChangelog()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `'Settings' should call presenter navigateToSettings`(){
        // simulate selecting `Settings` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.settings))

        verify(presenter).navigateToSettings()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `'About' should call presenter navigateToAbout`(){
        // simulate selecting `About` in navigation drawer layout
        activity.onNavigationItemSelected(RoboMenuItem(R.id.about))

        verify(presenter).navigateToAbout()

        // all other methods should not be called
        verifyNoMoreInteractions(presenter)
    }

    @Test
    fun `onActivityResult should call checkTarFile when code is IMPORT_RC`(){
        // call onActivityResult passing import request code, result ok and intent with
        // location where to import database
        activity.onActivityResult(activity.IMPORT_RC, Activity.RESULT_OK, intent)

        verify(presenter).checkTarFile(location)
    }

    @Test
    fun `onActivityResult should not call checkTarFile when code is other than EXPORT_RC`(){
        // call onActivityResult passing other request code, result ok and intent
        activity.onActivityResult(OTHER_RC, Activity.RESULT_OK, intent)

        verify(presenter, never()).checkTarFile(location)
    }

    @Test
    fun `onActivityResult should not call checkTarFile when result code is canceled`(){
        // call onActivityResult passing other request code, result canceled and intent
        activity.onActivityResult(activity.IMPORT_RC, Activity.RESULT_CANCELED, intent)

        verify(presenter, never()).checkTarFile(location)
    }
}