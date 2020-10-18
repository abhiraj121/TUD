package com.abhirajsharma.theuniversitydatabase.ui.welcomeFrags

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abhirajsharma.theuniversitydatabase.MainActivity
import com.abhirajsharma.theuniversitydatabase.R
import com.abhirajsharma.theuniversitydatabase.others.CurrentTeacherInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_teacher_details.*
import kotlinx.android.synthetic.main.fragment_teacher_details.view.*
import java.util.*

class TeacherDetailsFragment : Fragment() {

    private val classes = hashSetOf<String>()
    private val TAG = "checkMe"
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_details, container, false)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        view.addChip.setOnClickListener {
            if (!view.teacherClass.text.isNullOrEmpty()) {
                val checkClass = classes.add(
                    view.teacherClass.text.toString().toUpperCase(Locale.ROOT)
                )
                Log.d(TAG, classes.toString())
                if (checkClass) {
                    addNewClass()
                } else {
                    Toast.makeText(activity, "Class already added", Toast.LENGTH_SHORT).show()
                }
            }
        }

        view.nextTeacher.setOnClickListener {
            if (classes.isEmpty()) {
                Log.d(TAG, classes.toString())
                teacherRootLayout.showSnackBar("Please add your classes")
                hideSoftKeyboard(context as Activity, view)
            } else {
                val convertToArrayList: ArrayList<String> = ArrayList<String>(classes)
                CurrentTeacherInfo.classes = convertToArrayList
                hideSoftKeyboard(context as Activity, view)
                createUser()
            }
        }
        return view
    }

    private fun createUser() {
        Log.d(TAG, "Reached Authentication")
        val email = CurrentTeacherInfo.email
        val password = CurrentTeacherInfo.password
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as AppCompatActivity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Authentication Done")
                    addToFirebase()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addToFirebase() {
        val userList: MutableMap<String, Any> = HashMap()
        Log.d(TAG, "Reached Database")
        userList["name"] = CurrentTeacherInfo.username
        userList["email"] = CurrentTeacherInfo.email
        userList["classes"] = CurrentTeacherInfo.classes
        userList["password"] = CurrentTeacherInfo.password
        db.collection(CurrentTeacherInfo.accountType)
            .add(userList)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Database Upload Done")
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
//                Toast.makeText(activity, "Data Added", Toast.LENGTH_SHORT).show()
                updateUI()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Database Upload Failure")
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun updateUI() {
        startActivity(Intent(activity, MainActivity::class.java))
    }

    private fun addNewClass() {
        val chip = Chip(activity)
        val drawable = ChipDrawable.createFromAttributes(
            requireContext(),
            null,
            0,
            R.style.Widget_MaterialComponents_Chip_Entry
        )
        chip.setChipDrawable(drawable)
        chip.isCheckable = false
        chip.isClickable = false
        chip.setChipBackgroundColorResource(R.color.pink)
        chip.setTextColor(Color.WHITE)
        chip.setCloseIconTintResource(R.color.white)
        chip.text = requireView().teacherClass.text.toString().toUpperCase(Locale.ROOT)
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
            classes.remove(chip.text)
            Log.d(TAG, classes.toString())
        }
        chipGroup.addView(chip)
        requireView().teacherClass.setText("")
    }

    private fun View.showSnackBar(msg: String) {
        val snack = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
        snack.setBackgroundTint(Color.WHITE)
        snack.setTextColor(Color.BLACK)
        snack.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snack.show()
    }

    private fun hideSoftKeyboard(activity: Activity, view: View) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }
}
