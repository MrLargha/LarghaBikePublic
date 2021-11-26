package ru.mrlargha.larghabike.presentation.ui.screens.ridehost

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.mrlargha.larghabike.presentation.ui.screens.ridehost.fragments.RoadMapFragment
import ru.mrlargha.larghabike.presentation.ui.screens.ridehost.fragments.SpeedometerFragment

class RideHostPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment =
        when(position){
            0 -> SpeedometerFragment()
            else -> RoadMapFragment()
        }

}