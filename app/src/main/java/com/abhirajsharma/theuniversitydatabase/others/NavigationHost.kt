package com.abhirajsharma.theuniversitydatabase.others

import androidx.fragment.app.Fragment

interface NavigationHost {

    fun navigateTo(fragment: Fragment, addToBackStack: Boolean)

}
