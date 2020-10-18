package com.abhirajsharma.theuniversitydatabase.ui.welcomeFrags

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abhirajsharma.theuniversitydatabase.MainActivity
import com.abhirajsharma.theuniversitydatabase.R
import com.abhirajsharma.theuniversitydatabase.others.CurrentUserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_student_details.view.*
import org.angmarch.views.NiceSpinner
import java.io.File

class StudentDetailsFragment : Fragment() {

    val TAG = "checkMe"
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    private var mStorageRef: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_details, container, false)

        mStorageRef = FirebaseStorage.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val niceSpinner = view.findViewById(R.id.batch) as NiceSpinner
        val dataSet: List<String> = ArrayList(listOf("2020", "2019", "2018", "2017", "2016"))
        niceSpinner.attachDataSource(dataSet)

        val niceSpinner2 = view.findViewById(R.id.branch) as NiceSpinner
        val dataSet2: List<String> = ArrayList(listOf("CSE", "ECE", "ME", "CE (Civil)", "IT"))
        niceSpinner2.attachDataSource(dataSet2)

        val niceSpinner3 = view.findViewById(R.id.section) as NiceSpinner
        val dataSet3: List<String> = ArrayList(listOf("A", "B", "C"))
        niceSpinner3.attachDataSource(dataSet3)

        view.makeAccount.setOnClickListener {
            CurrentUserInfo.batch = niceSpinner.text.toString()
            CurrentUserInfo.branch = niceSpinner2.text.toString()
            CurrentUserInfo.section = niceSpinner3.text.toString()
            createUser()
        }

        return view
    }

    private fun createUser() {
        Log.d(TAG, "Reached Authentication")
        val email = CurrentUserInfo.email
        val password = CurrentUserInfo.password
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity as AppCompatActivity) { task ->
                if (task.isSuccessful) {
                    mStorageRef = FirebaseStorage.getInstance().reference
                    Log.d(TAG, "Authentication Done")
                    saveToFirebase(CurrentUserInfo.image!!)
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserDb() {
        val userList: MutableMap<String, Any> = HashMap()
        Log.d(TAG, "Reached Database")
        userList["name"] = CurrentUserInfo.username
        userList["batch"] = CurrentUserInfo.batch
        userList["branch"] = CurrentUserInfo.branch
        userList["email"] = CurrentUserInfo.email
        userList["rollNo"] = CurrentUserInfo.rollNum
        userList["password"] = CurrentUserInfo.password
        userList["img"] = CurrentUserInfo.imageUrl
        userList["section"] = CurrentUserInfo.section
        val branchSection = CurrentUserInfo.branch + "-" + CurrentUserInfo.section
        db.collection(CurrentUserInfo.accountType).document(CurrentUserInfo.batch)
            .collection(branchSection)
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

    private fun saveToFirebase(f: File) {
        Log.d(TAG, "Reached Storage")
        val file = Uri.fromFile(f)!!
        val studentName = CurrentUserInfo.username.replace(" ", "_") + ".jpg"
        val riversRef = mStorageRef!!.child("StudentsProfilePicture/$studentName")
        riversRef.putFile(file)
            .addOnSuccessListener {
                Log.d(TAG, "File Upload Done")

                riversRef.downloadUrl.addOnCompleteListener { task ->
                    val profileImageUrl = task.result.toString()
                    CurrentUserInfo.imageUrl = profileImageUrl
                    addUserDb()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "File Upload Failure")

            }
    }

    private fun updateUI() {
        startActivity(Intent(activity, MainActivity::class.java))
    }

}