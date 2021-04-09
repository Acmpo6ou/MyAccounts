/*
 * Copyright (c) 2020-2021. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.databases_list

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation.setViewNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragment
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.actionEditDatabase
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesFragmentDirections.actionOpenDatabase
import com.acmpo6ou.myaccounts.database.databases_list.DatabasesPresenter
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class DatabasesFragmentInst {
    lateinit var scenario: FragmentScenario<DatabasesFragment>
    private lateinit var navController: NavController

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val warningTitle = context.resources.getString(R.string.warning)
    private val confirmDeleteMsg = context.resources.getString(R.string.confirm_delete)
    private val confirmCloseMsg = context.resources.getString(R.string.confirm_close)

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)

        val app = MyApp()
        app.databases = mutableListOf(Database("main"))
        scenario.onFragment { it.app = app }
    }

    private fun mockNavController(fragment: DatabasesFragment) {
        navController = mock()
        setViewNavController(fragment.requireView(), navController)
    }

    @Test
    fun `exportDialog should start appropriate intent`() {
        val expectedAction = Intent.ACTION_CREATE_DOCUMENT
        val expectedCategory = Intent.CATEGORY_OPENABLE
        val expectedType = "application/x-tar"
        val expectedTitle = "main.tar"

        scenario.onFragment {
            it.exportDialog(0)
        }

        // check all intent properties
        val intent: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals(
            "exportDatabase: incorrect intent action!",
            expectedAction, intent.action
        )
        assertEquals(
            "exportDatabase: incorrect intent category!",
            expectedCategory, intent.categories.first()
        )
        assertEquals(
            "exportDatabase: incorrect intent type!",
            expectedType, intent.type
        )
        assertEquals(
            "exportDatabase: incorrect intent title!",
            expectedTitle, intent.getStringExtra(Intent.EXTRA_TITLE)
        )
    }

    @Test
    fun `navigateToEdit should pass appropriate database index`() {
        scenario.onFragment {
            mockNavController(it)
            it.navigateToEdit(0)

            val expectedAction = actionEditDatabase(0)
            verify(navController).navigate(expectedAction)
        }
    }

    @Test
    fun `navigateToOpen should pass appropriate database index`() {
        scenario.onFragment {
            mockNavController(it)
            it.navigateToOpen(0)

            val expectedAction = actionOpenDatabase(0)
            verify(navController).navigate(expectedAction)
        }
    }

    @Test
    fun `confirmDelete should create dialog with appropriate message and title`() {
        scenario.onFragment {
            it.confirmDelete(0)
        }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(
            "confirmDelete created dialog with incorrect title!",
            warningTitle, title?.text
        )
        assertEquals(
            "confirmDelete created dialog with incorrect message!",
            String.format(confirmDeleteMsg, "main"), message?.text
        )
    }

    @Test
    fun `confirmClose should create dialog with appropriate message and title`() {
        scenario.onFragment {
            it.confirmClose(0)
        }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(
            "confirmClose created dialog with incorrect title!",
            warningTitle, title?.text
        )
        assertEquals(
            "confirmClose created dialog with incorrect message!",
            String.format(confirmCloseMsg, "main"), message?.text
        )
    }

    /**
     * Used in tests to setup real (non mocked) DatabasesPresenter to test
     * how does DatabasesPresenter and DatabasesFragment are integrated.
     * @param[fragment] DatabasesFragment for which we will setup DatabasesPresenter.
     */
    private fun setupRealPresenter(fragment: DatabasesFragment) {
        // list of databases for test
        val databases = mutableListOf(
            Database("main"), // locked
            Database("test", "123") /* opened*/
        )

        val presenter = DatabasesPresenter(fragment)
        presenter.databases = databases
        fragment.presenter = presenter
    }

    /**
     * Used in tests to get measured and laid out recyclerview.
     * @param[fragment] DatabasesFragment from which we will get the recyclerview.
     */
    private fun getRecycler(fragment: DatabasesFragment): RecyclerView {
        // find recycler, measure and lay it out, so that later we can obtain its items
        val recycler: RecyclerView = fragment.view!!.findViewById(R.id.itemsList)
        recycler.measure(0, 0)
        recycler.layout(0, 0, 100, 10000)
        return recycler
    }

    @Test
    fun `when closing database lock icon should change`() {
        scenario.onFragment {
            setupRealPresenter(it)
            it.presenter.closeDatabase(1)
            val recycler = getRecycler(it)

            // check that lock icon changed to locked
            val itemLayout = recycler.getChildAt(1)
            val lockImg = itemLayout?.findViewById<ImageView>(R.id.itemIcon)
            assertEquals(R.drawable.ic_locked, lockImg?.tag)
        }
    }

    @Test
    fun `when deleting database it should disappear from recycler`() {
        scenario.onFragment {
            setupRealPresenter(it)
            it.presenter.deleteDatabase(0)
            val recycler = getRecycler(it)

            // check that the first database is `test` as `main` was deleted
            val itemLayout = recycler.getChildAt(0)
            val databaseName = itemLayout?.findViewById<TextView>(R.id.itemName)
            assertEquals("test", databaseName?.text)
        }
    }
}