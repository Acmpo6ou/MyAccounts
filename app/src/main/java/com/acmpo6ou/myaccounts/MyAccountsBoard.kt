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

package com.acmpo6ou.myaccounts

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyAccountsBoard : InputMethodService() {

    @Inject
    lateinit var app: MyApp

    @Inject
    lateinit var inputManager: InputMethodManager

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.myaccounts_board, null)
        val pasteButton = view.findViewById<Button>(R.id.pastePassword)

        val noPassword = view.findViewById<TextView>(R.id.noPassword)
        noPassword.visibility = if (app.password.isEmpty()) View.VISIBLE else View.GONE

        pasteButton.setOnClickListener {
            currentInputConnection.commitText(app.password, 1)
            inputManager.showInputMethodPicker()
        }
        return view
    }
}
