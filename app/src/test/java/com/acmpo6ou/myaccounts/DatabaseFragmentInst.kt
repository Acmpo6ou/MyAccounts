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

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Looper.getMainLooper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class DatabaseFragmentInstrumentation {
    lateinit var databaseScenario: FragmentScenario<DatabaseFragment>
    private lateinit var navController: NavController
    private val faker = Faker()

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val successMessage = context.resources.getString(R.string.success_message)
    private val warningTitle = context.resources.getString(R.string.warning)
    private val confirmDeleteMsg = context.resources.getString(R.string.confirm_delete)
    private val confirmCloseMsg = context.resources.getString(R.string.confirm_close)

    @Before
    fun setUp(){
        // Create a graphical FragmentScenario for the DatabaseFragment
        databaseScenario = launchFragmentInContainer<DatabaseFragment>(
            themeResId = R.style.Theme_MyAccounts_NoActionBar)

        // mock presenter with fake databases
        val databases = listOf(Database("main"))
        val presenter = mock<DatabasesPresenterInter>()
        whenever(presenter.databases).thenReturn(databases)
        databaseScenario.onFragment {
            it.presenter = presenter
        }
    }

    private fun setUpNavController() {
        // Create a TestNavHostController
        navController = TestNavHostController(
                ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.mobile_navigation)

        databaseScenario.onFragment { fragment ->
            // Set the NavController property on the fragment
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun `+ FAB must navigate to CreateDatabaseFragment`() {
        setUpNavController()
        // Verify that performing a click changes the NavController’s state
        databaseScenario.onFragment {
            val addButton = it.view?.findViewById<View>(R.id.addDatabase)
            addButton?.performClick()
        }

        assertEquals(
            "(+) FAB on DatabaseFragment doesn't navigate to CreateDatabaseFragment!",
            navController.currentDestination?.id,
            R.id.createDatabaseFragment
        )
    }

    @Test
    fun `navigateToEdit should navigate to EditDatabaseFragment`(){
        setUpNavController()
        databaseScenario.onFragment {
            // call navigateToEdit
            it.navigateToEdit("")
        }

        // verify that we navigated to edit database
        assertEquals(
                "navigateToEdit doesn't navigate to EditDatabaseFragment!",
                navController.currentDestination?.id,
                R.id.editDatabaseFragment
        )
    }

    @Test
    fun `navigateToEdit should pass database json string to EditDatabaseFragment`(){
        setUpNavController()

    }

    @Test
    fun `exportDialog should start appropriate intent`(){
        // create expected intent with default file name `main.tar` and file type `.tar`
        val expectedIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        expectedIntent.addCategory(Intent.CATEGORY_OPENABLE)
        expectedIntent.type = "application/x-tar"
        expectedIntent.putExtra(Intent.EXTRA_TITLE, "main.tar")

        // call exportDialog
        databaseScenario.onFragment {
            it.exportDialog(0)
        }

        // check all intent properties
        val actual: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity
        assertEquals(
                "exportDatabase: incorrect intent action!",
                expectedIntent.action,
                actual.action
        )
        assertEquals(
                "exportDatabase: incorrect intent category!",
                expectedIntent.categories,
                actual.categories
        )
        assertEquals(
                "exportDatabase: incorrect intent type!",
                expectedIntent.type,
                actual.type
        )
        assertEquals(
                "exportDatabase: incorrect intent title!",
                expectedIntent.getStringExtra(Intent.EXTRA_TITLE),
                actual.getStringExtra(Intent.EXTRA_TITLE)
        )
    }

    @Test
    fun `startDatabase should start appropriate intent`(){
        // serialized database string that wil be passed with intent
        val databaseString = faker.lorem().sentence()

        // create expected intent
        var expectedIntent = Intent()

        // call startDatabase
        databaseScenario.onFragment {
            expectedIntent = Intent(it.myContext, AccountsActivity::class.java)
            expectedIntent.putExtra("database", databaseString)

            it.startDatabase(databaseString)
        }

        // check that appropriate intent was started
        val actual: Intent = shadowOf(RuntimeEnvironment.application).nextStartedActivity

        assertEquals(
                "startDatabase has started intent with incorrect serialized string!",
                expectedIntent.getStringExtra("database"),
                actual.getStringExtra("database"),
        )
        assertEquals(
                "startDatabase should start AccountsActivity!",
                expectedIntent.component?.className,
                actual.component?.className,
        )
    }

    @Test
    fun `showSuccess should display snackbar`(){
        databaseScenario.onFragment {
            // call showSuccess and get the snackbar
            it.showSuccess()
            // this is because of some Robolectric main looper problems
            shadowOf(getMainLooper()).idle()
            val snackbar: TextView? = it.view?.findSnackbarTextView()

            // check that snackbar was displayed
            assertTrue(
        "No snackbar is displayed when call to DatabaseFragment.showSuccess is made!",
        snackbar != null)

            // check the snackbar's message
            assertEquals(
                    "showSuccess snackbar has incorrect message!",
                    successMessage,
                    snackbar?.text
            )
        }
    }

    @Test
    fun `showError should display error dialog`(){
        databaseScenario.onFragment {
            it.showError("Error occurred!", "Error details.")
        }
        val dialog: Dialog? = ShadowAlertDialog.getLatestDialog()
        assertTrue(
                "showError doesn't display dialog!",
                dialog != null
        )
    }

    @Test
    fun `showError should create dialog with appropriate title and message`(){
        val expectedTitle = "Error occurred!"
        val expectedMsg = "Error details."
        databaseScenario.onFragment {
            it.showError(expectedTitle, expectedMsg)
        }

        val dialog: Dialog? = ShadowAlertDialog.getLatestDialog()
        val title = dialog?.findViewById<TextView>(R.id.alertTitle)
        val message = dialog?.findViewById<TextView>(android.R.id.message)

        assertEquals(
                "showError created dialog with incorrect title!",
                expectedTitle,
                title?.text,
        )
        assertEquals(
                "showError created dialog with incorrect message!",
                expectedMsg,
                message?.text,
        )
    }

    @Test
    fun `confirmDelete should create dialog with appropriate message and title`(){
        // create dialog
        databaseScenario.onFragment {
            it.confirmDelete(0)
        }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(
                "confirmDelete created dialog with incorrect title!",
                warningTitle,
                title?.text
        )
        assertEquals(
                "confirmDelete created dialog with incorrect message!",
                String.format(confirmDeleteMsg, "main"),
                message?.text
        )
    }

    @Test
    fun `confirmClose should create dialog with appropriate message and title`(){
        // create dialog
        databaseScenario.onFragment {
            it.confirmClose(0)
        }

        val dialog = ShadowAlertDialog.getLatestDialog() as AlertDialog
        val title = dialog.findViewById<TextView>(R.id.alertTitle)
        val message = dialog.findViewById<TextView>(android.R.id.message)

        assertEquals(
                "confirmClose created dialog with incorrect title!",
                warningTitle,
                title?.text
        )
        assertEquals(
                "confirmClose created dialog with incorrect message!",
                String.format(confirmCloseMsg, "main"),
                message?.text
        )
    }
}