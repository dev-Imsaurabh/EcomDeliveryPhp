package com.mac.ecomdeliveryphp.Adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mac.ecomdeliveryphp.Fragments.ActiveFragment
import com.mac.ecomdeliveryphp.Fragments.DeliveredFragment

abstract class DelPagerAdapter{


    class PagerAdapter(appCompatActivity: AppCompatActivity):FragmentStateAdapter(appCompatActivity){


        override fun getItemCount(): Int {

            return 2

        }

        override fun createFragment(position: Int): Fragment {

            return when(position){
                0-> ActiveFragment()
                1->DeliveredFragment()

                else->ActiveFragment()
            }
        }


    }

}
