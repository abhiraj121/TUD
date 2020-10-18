package com.abhirajsharma.theuniversitydatabase.ui.welcomeFrags

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.abhirajsharma.theuniversitydatabase.R
import com.abhirajsharma.theuniversitydatabase.others.CurrentTeacherInfo
import com.abhirajsharma.theuniversitydatabase.others.CurrentUserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_registration.view.*


class RegistrationFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener {

    val TAG = "checkMe"
    private var mAuth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registration, container, false)

        view.rootLayout.viewTreeObserver.addOnGlobalLayoutListener(this)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        view.registerCancelBtn.setOnClickListener {
            hideSoftKeyboard(context as Activity, view)
            requireActivity().supportFragmentManager.popBackStack()
        }

        view.registerMail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.registerMailLayout.error = null
            }
        })

        view.registerUsername.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.registerMailLayout.error = null
            }
        })

        view.registerPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                view.registerMailLayout.error = null
            }
        })

        view.registerNextBtn.setOnClickListener {
            hideSoftKeyboard(context as Activity, view)
            validateUser(view)
        }

        return view
    }

    private fun validateUser(view: View) {
        when {
            view.registerMail.text?.isEmpty()!! -> {
                view.registerMailLayout.error = "Field cannot be Empty"
            }
            view.registerUsername.text?.isEmpty()!! -> {
                view.registerUsernameLayout.error = "Field cannot be Empty"
            }
            view.registerPassword.text?.isEmpty()!! -> {
                view.registerPasswordLayout.error = "Field cannot be Empty"
            }
            view.registerPassword.text?.length!! < 8 -> {
                view.registerPasswordLayout.error = "Password must contain 8 characters"
            }
            else -> {
                view.registerPasswordLayout.error = null
                view.registerUsernameLayout.error = null
                view.registerMailLayout.error = null
                CurrentUserInfo.email = view.registerMail.text.toString()
                CurrentUserInfo.username = view.registerUsername.text.toString()
                CurrentUserInfo.password = view.registerPassword.text.toString()
                CurrentTeacherInfo.email = view.registerMail.text.toString()
                CurrentTeacherInfo.username = view.registerUsername.text.toString()
                CurrentTeacherInfo.password = view.registerPassword.text.toString()
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    )
                    .replace(R.id.signInContainer, UserTypeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun hideSoftKeyboard(activity: Activity, view: View) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    override fun onGlobalLayout() {
        try {
            val handler = Handler()
            if (view != null) {
                handler.postDelayed({
                    val heightDiff =
                        requireView().rootLayout.rootView.height - requireView().rootLayout.height
                    if (heightDiff > 100) {
                        requireView().scrollLayout.scrollBy(0, requireView().scrollLayout.bottom)
                    }
                }, 10)
            }
        } catch (e: Exception) {

        }
    }
}