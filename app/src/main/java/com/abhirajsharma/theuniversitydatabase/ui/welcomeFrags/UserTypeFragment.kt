package com.abhirajsharma.theuniversitydatabase.ui.welcomeFrags

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abhirajsharma.theuniversitydatabase.R
import com.abhirajsharma.theuniversitydatabase.others.CurrentTeacherInfo
import com.abhirajsharma.theuniversitydatabase.others.CurrentUserInfo
import com.abhirajsharma.theuniversitydatabase.others.SharedPreference
import kotlinx.android.synthetic.main.fragment_user_type.view.*

class UserTypeFragment : Fragment() {

    val ss = SharedPreference()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_type, container, false)

        view.selectTeacher.setOnClickListener {
            CurrentTeacherInfo.accountType = "teachers"
            ss.setUser(activity, "teachers")
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.signInContainer, TeacherDetailsFragment())
                .addToBackStack(null)
                .commit()
        }

        view.selectStudent.setOnClickListener {
            CurrentUserInfo.accountType = "students"
            ss.setUser(activity, "students")
            requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
                .replace(R.id.signInContainer, ProfilePicFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }


}