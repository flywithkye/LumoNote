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

        val navigationButtonIVs: MutableList<ImageView> =
            mutableListOf(homeViewBinding.notesViewIV, homeViewBinding.calendarViewIV,
                homeViewBinding.settingsViewIV)

        supportFragmentManager.beginTransaction().apply {
            replace(homeViewBinding.currentHomeFragmentFL.id, notePreviewViewFragment)
            commit()
        }


        homeViewBinding.notesViewIV.setOnClickListener {
            switchToFragment(navigationButtonIVs, notePreviewViewFragment, homeViewBinding.notesViewIV,
                R.color.gold, R.color.light_grey_2)
        }

        homeViewBinding.calendarViewIV.setOnClickListener {
            switchToFragment(navigationButtonIVs, calendarViewFragment, homeViewBinding.calendarViewIV,
                R.color.gold, R.color.light_grey_2)
        }

        homeViewBinding.settingsViewIV.setOnClickListener {
            switchToFragment(navigationButtonIVs, settingsViewFragment, homeViewBinding.settingsViewIV,
                R.color.gold, R.color.light_grey_2)
        }
    }


    private fun switchToFragment(buttonIVList: MutableList<ImageView>, targetFragment: Fragment,
                                 targetIV: ImageView, activeColor: Int, inactiveColor: Int) {

        supportFragmentManager.beginTransaction().apply {
            replace(homeViewBinding.currentHomeFragmentFL.id, targetFragment)
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