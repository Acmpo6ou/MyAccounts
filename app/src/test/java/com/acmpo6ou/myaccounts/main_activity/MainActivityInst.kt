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

import android.content.Context
import android.content.Intent
import android.os.Looper
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.MainActivity
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.findSnackbarTextView
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MainActivityInst {
    lateinit var mainScenario: ActivityScenario<MainActivity>

    // get string resources
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val noUpdatesMsg = context.resources.getString(R.string.no_updates)

    @Before
    fun setup(){
        mainScenario = launch(MainActivity::class.java)
    }

    @Test
    fun `noUpdates should display snackbar`(){
        mainScenario.onActivity {
            // call noUpdates and get the snackbar
            it.noUpdates()
            // this is because of some Robolectric main looper problems
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            val v: View = it.findViewById(android.R.id.content)
            val snackbar = v.rootView.findSnackbarTextView()

            // check that snackbar was displayed
            Assert.assertTrue(
                    "No snackbar is displayed when call to noUpdates is made!",
                    snackbar != null)

            // check the snackbar's message
            Assert.assertEquals(
                    "noUpdates snackbar has incorrect message!",
                    noUpdatesMsg,
                    snackbar?.text
            )
        }
    }

    @Test
    fun `importDialog should start appropriate intent`(){
        // create expected intent
        val expectedIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        expectedIntent.addCategory(Intent.CATEGORY_OPENABLE)
        expectedIntent.type = "application/x-tar"

        // call importDialog
        mainScenario.onActivity { it.importDialog() }

        // check all intent properties
        val actual: Intent = Shadows.shadowOf(RuntimeEnvironment.application).nextStartedActivity
        Assert.assertEquals(
                "importDialog: incorrect intent action!",
                expectedIntent.action,
                actual.action
        )
        Assert.assertEquals(
                "importDialog: incorrect intent category!",
                expectedIntent.categories,
                actual.categories
        )
        Assert.assertEquals(
                "importDialog: incorrect intent type!",
                expectedIntent.type,
                actual.type
        )
    }
}