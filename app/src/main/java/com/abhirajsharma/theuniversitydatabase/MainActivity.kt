package com.abhirajsharma.theuniversitydatabase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abhirajsharma.theuniversitydatabase.others.SharedPreference
import com.abhirajsharma.theuniversitydatabase.ui.studentDashboardFrags.StudentHomeFragment
import com.abhirajsharma.theuniversitydatabase.ui.teacherDashboardFrags.TeacherHomeFragment

class MainActivity : AppCompatActivity() {

    val ss = SharedPreference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ss.getUser(this) == "teachers") {
            supportFragmentManager.beginTransaction().add(R.id.mainContainer, TeacherHomeFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction().add(R.id.mainContainer, StudentHomeFragment())
                .commit()
        }
    }
}