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

package com.acmpo6ou.myaccounts.core

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import com.acmpo6ou.myaccounts.R
import com.google.android.material.textfield.TextInputEditText

class GeneratePassword(activity: AppCompatActivity,
                       pass1: TextInputEditText, pass2: TextInputEditText) {
    val digits = ('0'..'9').joinToString("")
    val lower = ('a'..'z').joinToString("")
    val upper = lower.toUpperCase()
    val punctuation = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
    val allChars = listOf(digits, lower, upper, punctuation)

    init{
        val dialog = Dialog(activity)
        dialog.setTitle("Generate password")
        dialog.setContentView(R.layout.generate_password)
    }

    /**
     * Generates random password.
     *
     * @param[len] [Int] number that defines length of generated password.
     * @param[chars] [List] of [String] of characters from which password will be generated.
     * @return generated random password.
     */
    fun genPass(len: Int, chars: List<String>): String {
        // here we generate the password using [len] parameter and strings
        // that are passed to genPass and packed to [chars]
        val password =  (1..len)
                .map{ chars.joinToString("").random() }
                .joinToString("")

        // because password generates randomly it not necessary will contain all characters that are
        // specified in [chars], so here we check that generated password contains at least one character
        // from each string specified in [chars] and if not we generate password again
        for(seq in chars){
            if (!(password hasoneof seq)){
                return genPass(len, chars)
            }
        }
        return password
    }
}

/**
 * Checks whether [String] on the left has at least one character from [String] on the right.
 *
 * @param[other] [String] on the right, i.e. the one from which we check characters.
 * @return [Boolean] value representing whether [String] on the left contains
 * at least one character from [String] on the right.
 */
infix fun String.hasoneof(other: String): Boolean {
    for(c in other){
        if ( c in this ){
            return true
        }
    }
    return false
}
