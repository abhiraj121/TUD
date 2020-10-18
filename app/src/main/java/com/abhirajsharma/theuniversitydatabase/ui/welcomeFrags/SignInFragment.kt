package com.abhirajsharma.theuniversitydatabase.ui.welcomeFrags

import android.content.Intent
import android.graphics.Color
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sign_in.view.*


class SignInFragment : Fragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    val TAG = "checkMe"
    lateinit var db: FirebaseFirestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        db = FirebaseFirestore.getInstance()

        view.loginBtn.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.signInContainer, LoginFragment())
                .addToBackStack(null)
                .commit()
        }

        view.registerBtn.setOnClickListener {
            activity!!.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.signInContainer, RegistrationFragment())
                .addToBackStack(null)
                .commit()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity as AppCompatActivity, gso)

        view.googleBtn.setOnClickListener {
            signIn()
        }

        view.fbBtn.setOnClickListener {
            view.showSnackBar("Coming Soon :)")
        }
        return view
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleResult(task)
            } catch (e: Exception) {
            }
        } else {
            Toast.makeText(activity, "Problem in execution order :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        val account = completedTask.getResult(ApiException::class.java)!!
        firebaseAuthWithGoogle(account.idToken!!)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity as AppCompatActivity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser!!
                    addToDb(user)
                    updateUI(user)
                } else {
                    view?.rootLayout?.showSnackBar("Authentication Failed.")
                }
            }
    }

    private fun addToDb(user: FirebaseUser) {
        val userList: MutableMap<String, Any> = HashMap()
        userList["img"] = user.photoUrl.toString()
        userList["name"] = user.displayName.toString()
        userList["password"] = ""
        db.collection("teachers").document(user.email.toString())
            .set(userList)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            startActivity(Intent(activity, MainActivity::class.java))
        }
    }


    private fun View.showSnackBar(msg: String) {
        val snack = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
        snack.setBackgroundTint(Color.WHITE)
        snack.setTextColor(Color.BLACK)
        snack.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snack.show()
    }
}