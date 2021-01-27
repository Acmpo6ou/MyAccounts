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

package com.acmpo6ou.myaccounts.database_utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.R
import com.acmpo6ou.myaccounts.core.MainActivityInter
import com.acmpo6ou.myaccounts.core.SuperViewModel
import com.acmpo6ou.myaccounts.core.ViewModelFragment
import com.acmpo6ou.myaccounts.databinding.CreateEditDatabaseFragmentBinding
import com.acmpo6ou.myaccounts.str
import com.github.javafaker.Faker
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.LooperMode

// ViewModelFragment is abstract, so here we create TestFragment so that we can instantiate it
// in tests
class TestFragment: ViewModelFragment(){
    override val viewModel = SuperViewModel()
    override val mainActivity: MainActivityInter = mock()

    // dummy onCreateView method
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val b = CreateEditDatabaseFragmentBinding
                .inflate(layoutInflater, container, false)
        return b.root
    }
}

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class ViewModelFragmentInst {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val faker = Faker()
    lateinit var scenario: FragmentScenario<TestFragment>
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_MyAccounts_NoActionBar)
        scenario.onFragment {
            whenever(it.mainActivity.myContext).thenReturn(context)
        }
    }

    @Test
    fun `should display call showError when errorMsg changes`(){
        scenario.onFragment {
            val errorTitle = context.resources.getString(R.string.error_title)
            val msg = faker.str()

            it.initModel()
            it.viewModel.errorMsg = msg

            verify(it.mainActivity).showError(errorTitle, msg)
        }
    }
}