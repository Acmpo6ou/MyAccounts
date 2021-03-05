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

package com.acmpo6ou.myaccounts.core.superclass

import com.acmpo6ou.myaccounts.BuildConfig
import com.acmpo6ou.myaccounts.R

/**
 * Super class for MainPresenter and AccountsPresenter.
 */
abstract class SuperPresenter : SuperPresenterInter {
    abstract val view: SuperActivityInter

    /**
     * This method is called when user clicks `Check for updates` in navigation drawer.
     */
    override fun checkUpdatesSelected(){
    }

    /**
     * This methods determines whether there are updates available by given [latestVersion].
     *
     * If [latestVersion] is different then currently installed one, then there are updates
     * available, otherwise they aren't.
     * Depending on whether there are updates or not we launch UpdatesActivity or display a
     * snackbar that there are no updates.
     * @param[latestVersion] latest app version that is available on github releases.
     */
    fun checkForUpdates(latestVersion: String) {
        if(BuildConfig.VERSION_NAME != latestVersion) {
            view.app.latestVersion = latestVersion // save latestVersion for UpdatesActivity
            view.startUpdatesActivity()
        }
        else{
            view.noUpdates()
        }
    }

    /**
     * Called when user clicks `Changelog` in navigation drawer.
     */
    override fun navigateToChangelog() = view.navigateTo(R.id.actionChangelog)

    /**
     * Called when user clicks `Settings` in navigation drawer.
     */
    override fun navigateToSettings() = view.navigateTo(R.id.actionSettings)

    /**
     * Called when user clicks `About` in navigation drawer.
     */
    override fun navigateToAbout() = view.navigateTo(R.id.actionAbout)
}