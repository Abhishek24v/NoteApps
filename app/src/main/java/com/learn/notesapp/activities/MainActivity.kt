package com.learn.notesapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.learn.notesapp.R
import com.learn.notesapp.databinding.ActivityMainBinding
import com.learn.notesapp.db.DataBase
import com.learn.notesapp.repository.NoteRepository
import com.learn.notesapp.viewModel.NoteViewModel
import com.learn.notesapp.viewModel.NoteViewModelFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var noteViewModel: NoteViewModel
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)

        try{
            setContentView(binding.root)
            val noteRepository = NoteRepository(DataBase(this))
            val noteViewModelFactory = NoteViewModelFactory(noteRepository)
            noteViewModel = ViewModelProvider(this,
            noteViewModelFactory)[NoteViewModel::class.java]
        } catch (e :Exception) {
            Log.e("TAG","error")
        }
    }
}