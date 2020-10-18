package com.abhirajsharma.theuniversitydatabase.ui.welcomeFrags

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abhirajsharma.theuniversitydatabase.MainActivity
import com.abhirajsharma.theuniversitydatabase.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private val TAG = "checkMe"
    private var mAuth: FirebaseAuth? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.loginRootLayout.viewTreeObserver.addOnGlobalLayoutListener(this)

        mAuth = FirebaseAuth.getInstance()

        view.loginCancelBtn.setOnClickListener {
            hideSoftKeyboard(context as Activity, view)
            requireActivity().supportFragmentManager.popBackStack()
        }

        view.loginNextBtn.setOnClickListener {
            hideSoftKeyboard(context as Activity, view)
            validateUser(view)
        }

        view.loginUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.loginUsernameLayout.error = null
            }
        })

        view.loginPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.loginPassword.error = null
            }
        })

        return view
    }

    private fun validateUser(view: View) {
        when {
            view.loginUsername.text?.isEmpty()!! -> {
                view.loginUsernameLayout.error = "Field cannot be Empty"
            }
            view.loginPassword.text?.isEmpty()!! -> {
                view.loginPasswordLayout.error = "Field cannot be Empty"
            }
            view.loginPassword.text?.length!! < 8 -> {
                view.loginPasswordLayout.error = "Password must contain 8 characters"
            }
            else -> {
                view.loginPasswordLayout.error = null
                view.loginUsernameLayout.error = null
                loginUser(view)
            }
        }
    }

    private fun loginUser(view: View) {
        val email = view.loginUsername.text.toString()
        val password = view.loginPassword.text.toString()
        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(
                activity as AppCompatActivity
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user: FirebaseUser? = mAuth?.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    view.loginRootLayout.showSnackBar("Authentication Failed")
//                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            startActivity(Intent(activity, MainActivity::class.java))
        }
    }

    private fun hideSoftKeyboard(activity: Activity, view: View) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    private fun View.showSnackBar(msg: String) {
        val snack = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT)
        snack.setBackgroundTint(Color.WHITE)
        snack.setTextColor(Color.BLACK)
        snack.animationMode = Snackbar.ANIMATION_MODE_SLIDE
        snack.show()
    }

    override fun onGlobalLayout() {
        val handler = Handler()
        if (view != null) {
            handler.postDelayed({
                val heightDiff: Int =
                    requireView().loginRootLayout.rootView.height - requireView().loginRootLayout.height
                if (heightDiff > 100) {
                    requireView().loginScrollLayout.scrollBy(
                        0,
                        requireView().loginScrollLayout.bottom
                    )
                }
            }, 10)
        }
    }
}