package com.example.lumonote.ui.home

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.lumonote.R
import com.example.lumonote.databinding.ActivityHomeViewBinding
import com.example.lumonote.ui.home.calendar.CalendarViewFragment
import com.example.lumonote.ui.home.notepreview.NotePreviewViewFragment
import com.example.lumonote.ui.home.settings.SettingsViewFragment
import com.example.lumonote.utils.GeneralUIHelper

class HomeViewActivity : AppCompatActivity() {

    private lateinit var homeViewBinding: ActivityHomeViewBinding
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_view)

        homeViewBinding = ActivityHomeViewBinding.inflate(layoutInflater)
        setContentView(homeViewBinding.root)

        val notePreviewViewFragment = NotePreviewViewFragment()
        val calendarViewFragment = CalendarViewFragment()
        val settingsViewFragment = SettingsViewFragment()
        val navigationFragments: MutableList<Fragment> =
            mutableListOf(notePreviewViewFragment, calendarViewFragment,
                settingsViewFragment)

        val navigationButtonIVs: MutableList<ImageView> =
            mutableListOf(homeViewBinding.notesViewIV, homeViewBinding.calendarViewIV,
                homeViewBinding.settingsViewIV)


        supportFragmentManager.beginTransaction().apply {
            add(homeViewBinding.currentHomeFragmentFL.id, notePreviewViewFragment)
            add(homeViewBinding.currentHomeFragmentFL.id, calendarViewFragment)
            add(homeViewBinding.currentHomeFragmentFL.id, settingsViewFragment)

            hide(calendarViewFragment)
            hide(settingsViewFragment)

            commit()
        }

        switchToFragment(navigationButtonIVs,homeViewBinding.notesViewIV, navigationFragments,
            notePreviewViewFragment, R.color.gold, R.color.light_grey_2)


        homeViewBinding.notesViewIV.setOnClickListener {
            switchToFragment(navigationButtonIVs, homeViewBinding.notesViewIV, navigationFragments,
                notePreviewViewFragment, R.color.gold, R.color.light_grey_2)
        }

        homeViewBinding.calendarViewIV.setOnClickListener {
            switchToFragment(navigationButtonIVs, homeViewBinding.calendarViewIV, navigationFragments,
                calendarViewFragment, R.color.gold, R.color.light_grey_2)
        }

        homeViewBinding.settingsViewIV.setOnClickListener {
            switchToFragment(navigationButtonIVs, homeViewBinding.settingsViewIV, navigationFragments,
                settingsViewFragment, R.color.gold, R.color.light_grey_2)
        }
    }


    private fun switchToFragment(buttonIVList: MutableList<ImageView>, targetIV: ImageView,
                                 fragmentList: MutableList<Fragment>, targetFragment: Fragment,
                                 activeColor: Int, inactiveColor: Int) {

        supportFragmentManager.beginTransaction().apply {
            for (fragment in fragmentList) {

                if (fragment == targetFragment) {
                    //show it
                    show(fragment)
                } else {
                    // hide the rest
                    hide(fragment)
                }
            }

            commit()
        }




        for (buttonImageView in buttonIVList) {
            if (buttonImageView == targetIV) {
                generalUIHelper.changeButtonIVColor(this, buttonImageView, activeColor)
            } else {
                generalUIHelper.changeButtonIVColor(this, buttonImageView, inactiveColor)
            }
        }

    }

}