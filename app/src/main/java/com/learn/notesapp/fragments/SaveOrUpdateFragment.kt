package com.learn.notesapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.transition.MaterialContainerTransform
import com.learn.notesapp.R
import com.learn.notesapp.activities.MainActivity
import com.learn.notesapp.databinding.BottomSheetLayoutBinding
import com.learn.notesapp.databinding.FragmentSaveOrUpdateBinding
import com.learn.notesapp.model.Note
import com.learn.notesapp.utils.hideKeyboard
import com.learn.notesapp.viewModel.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SaveOrUpdateFragment : Fragment(R.layout.fragment_save_or_update) {

    private lateinit var navController: NavController
    private lateinit var binding : FragmentSaveOrUpdateBinding
    private var note : Note? = null
    private var color = -1
    private lateinit var result : String
    private val noteViewModel : NoteViewModel by activityViewModels()
    private val currentDate = SimpleDateFormat.getInstance().format(Date())
    private val job = CoroutineScope(Dispatchers.Main)
    private val args : SaveOrUpdateFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = MaterialContainerTransform().apply {
            drawingViewId= R.id.fragment
            scrimColor = Color.TRANSPARENT
            duration = 300L
        }
        sharedElementEnterTransition= animation
        sharedElementReturnTransition = animation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSaveOrUpdateBinding.bind(view)

        navController = Navigation.findNavController(view)
        val activity = activity as MainActivity

        ViewCompat.setTransitionName(
            binding.noteContentFragmentParent,
            "recyclerView_${args.note?.id}"
        )

        binding.backBtn.setOnClickListener {
            requireView().hideKeyboard()
            navController.popBackStack()
        }


        binding.saveNote.setOnClickListener {
            saveNote()
        }

        try {
            binding.eNoteContent.setOnFocusChangeListener {_, hasFocus ->
                if (hasFocus) {
                    binding.bottomBar.visibility = View.VISIBLE
                    binding.eNoteContent.setStylesBar(binding.styleBar)
                } else binding.bottomBar.visibility = View.GONE
            }
        } catch (e: Throwable) {
            Log.e("TAG",e.stackTrace.toString())
        }

        binding.fabColorPicker.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )

            val bottomSheetView : View = layoutInflater.inflate(
                R.layout.bottom_sheet_layout,
                null,
            )
            with(bottomSheetDialog){
                setContentView(bottomSheetView)
                show()
            }
            val bottomSheetBinding = BottomSheetLayoutBinding.bind(bottomSheetView)
            bottomSheetBinding.apply {
                colorPicker.apply {
                    setSelectedColor(color)
                    setOnColorSelectedListener {
                        value ->
                        color = value
                        binding.apply {
                            noteContentFragmentParent.setBackgroundColor(color)
                            toolBarFragmentNoteContent.setBackgroundColor(color)
                            bottomBar.setBackgroundColor(color)
                            activity.window.statusBarColor = color
                        }
                        bottomSheetBinding.bottomSheetParent.setCardBackgroundColor(color)
                    }
                }
                bottomSheetParent.setCardBackgroundColor(color)
            }
            bottomSheetView.post{
                bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        setUpNote()
    }

    private fun setUpNote() {
        val note = args.note
        val title = binding.eTitle
        val content = binding.eNoteContent
        val lstEdited = binding.lastEdited

        if (note==null) {
            binding.lastEdited.text = getString(R.string.edited_on,SimpleDateFormat.getDateInstance().format(Date()))
        }
        if (note!=null) {
            title.setText(note.title)
            content.renderMD(note.content)
            lstEdited.text = getString(R.string.edited_on,note.date)
            color=note.color
            binding.apply {
                job.launch {
                    delay(10)
                    noteContentFragmentParent.setBackgroundColor(color)

                }
                toolBarFragmentNoteContent.setBackgroundColor(color)
                bottomBar.setBackgroundColor(color)
            }
            activity?.window?.statusBarColor = note.color
        }
    }

    private fun saveNote() {
        if(binding.eNoteContent.text.toString().isEmpty() || binding.eTitle.text.toString().isEmpty()){
            Toast.makeText(activity,"Something is empty",Toast.LENGTH_SHORT).show()
        }else{
            note = args.note
            when(note) {
                null ->  {
                    noteViewModel.saveNote(
                        Note(
                            0,
                            binding.eTitle.text.toString(),
                            binding.eNoteContent.getMD(),
                            currentDate,
                            color
                        )
                    )
                    result = "Note Saved"
                    setFragmentResult(
                        "key",
                        bundleOf("bundleKey" to result)
                    )
                    navController.navigate(SaveOrUpdateFragmentDirections.actionSaveOrUpdateFragmentToNoteFragment())
                }
                else ->{
                    updateNote()
                    navController.popBackStack()
                }
            }
        }
    }

    private fun updateNote() {
        if (note!=null){
            noteViewModel.updateNote(
                Note(
                    note!!.id,
                    binding.eTitle.text.toString(),
                    binding.eNoteContent.getMD(),
                    currentDate,
                    color
                )
            )
        }
    }
}



