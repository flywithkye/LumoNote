package com.example.lumonote

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.data.models.Tag
import com.example.lumonote.databinding.ActivityMainBinding
import com.example.lumonote.ui.home.HomeViewActivity
import com.example.lumonote.ui.noteview.NoteViewActivity
import com.example.lumonote.ui.noteview.NotePreviewAdapter
import com.example.lumonote.ui.home.TagDisplayAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start the home screen activity
        var intent = Intent(this, HomeViewActivity::class.java)
        startActivity(intent)
    }

}