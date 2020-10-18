package com.abhirajsharma.theuniversitydatabase.ui.welcomeFrags

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abhirajsharma.theuniversitydatabase.R
import com.abhirajsharma.theuniversitydatabase.others.CurrentUserInfo
import com.abhirajsharma.theuniversitydatabase.others.setLocalImage
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_profile_pic.*
import kotlinx.android.synthetic.main.fragment_profile_pic.view.*
import java.io.File

class ProfilePicFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener {

    private var mProfileFile: File? = null
    private val TAG = "checkMe"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_pic, container, false)

        view.profileRootLayout.viewTreeObserver.addOnGlobalLayoutListener(this)

        view.profilePicNextBtn.setOnClickListener {
            if (rollNum.text.isEmpty()) {
                view.profileRootLayout.showSnackBar("Enter your Roll Number")
            } else if (mProfileFile == null) {
                val msg = "Profile Image is Necessary for Identification of Student"
                val snack = Snackbar.make(view.profileRootLayout, msg, Snackbar.LENGTH_LONG)
                snack.setBackgroundTint(Color.WHITE)
                snack.setTextColor(Color.BLACK)
                snack.animationMode = Snackbar.ANIMATION_MODE_SLIDE
                snack.show()
            } else if (rollNum.text.isEmpty() && mProfileFile == null) {
                view.profileRootLayout.showSnackBar("Enter your Roll Number and Select a Picture")
            } else {
                hideSoftKeyboard(activity as AppCompatActivity, view)
                CurrentUserInfo.rollNum = view.rollNum.text.toString()
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    )
                    .replace(R.id.signInContainer, StudentDetailsFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        view.selectProfilePicture.setOnClickListener {
            pickProfileImage()
        }

        return view
    }

    private fun pickProfileImage() {
        ImagePicker.with(this)
            .cropSquare()
            .setImageProviderInterceptor { imageProvider -> // Intercept ImageProvider
                Log.d(TAG, "Selected ImageProvider: " + imageProvider.name)
            }
            .maxResultSize(512, 512)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "Reached in result")
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val file = ImagePicker.getFile(data)!!
                mProfileFile = file
                CurrentUserInfo.image = file
                view?.selectProfilePicture?.setLocalImage(file, true)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(activity, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(activity, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideSoftKeyboard(activity: Activity, view: View) {
        val imm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
    }

    override fun onGlobalLayout() {
        val handler = Handler()
        if (view != null) {
            try {
                handler.postDelayed({
                    val heightDiff =
                        requireView().profileRootLayout.rootView.height - requireView().profileRootLayout.height
                    if (heightDiff > 100) {
                        requireView().profileScrollView.scrollBy(
                            0,
                            requireView().profileScrollView.bottom
                        )
                    }
                }, 10)
            } catch (e: Exception) {

            }

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