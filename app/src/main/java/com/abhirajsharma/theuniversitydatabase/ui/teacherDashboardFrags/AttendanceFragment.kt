package com.abhirajsharma.theuniversitydatabase.ui.teacherDashboardFrags

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.abhirajsharma.theuniversitydatabase.R
import com.abhirajsharma.theuniversitydatabase.adapter.CardStackAdapter
import com.abhirajsharma.theuniversitydatabase.model.UsersItemModel
import com.abhirajsharma.theuniversitydatabase.others.CardStackCallback
import com.google.firebase.firestore.FirebaseFirestore
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFragment : Fragment(), CardStackListener {

    private var myClassList = arrayListOf<UsersItemModel>()

    private var presentUsers = hashSetOf<Int>()
    private var absentUsers = hashSetOf<Int>()
    private lateinit var date: String
    private val TAG = "checkMe"
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val manager by lazy { CardStackLayoutManager(activity, this) }
    private val adapter by lazy { CardStackAdapter(addList()) }
    private val classSection = "18cseA"
//    private var currentRollNo = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        view.cardStackView.layoutManager = manager
        view.cardStackView.adapter = adapter
        view.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

        val skip = view.findViewById<View>(R.id.absentBtn)
        skip.setOnClickListener {
            view.absentBtn.expand()
            view.presentBtn.expand()
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            view.cardStackView.swipe()
        }

        val rewind = view.findViewById<View>(R.id.rewindBtn)
        rewind.setOnClickListener {
            view.absentBtn.expand()
            view.presentBtn.expand()
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            view.cardStackView.rewind()
        }

        val like = view.findViewById<View>(R.id.presentBtn)
        like.setOnClickListener {
            view.absentBtn.expand()
            view.presentBtn.expand()
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            view.cardStackView.swipe()
        }
        return view
    }

    private fun paginate() {
        val old = adapter.getItems()
        val new = old.plus(addList())
        val callback = CardStackCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setItems(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addList(): ArrayList<UsersItemModel> {
        val items = arrayListOf<UsersItemModel>()
        db.collection("students").document("2018").collection("CSE-A")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val img = document.data["img"].toString()
                        val name = document.data["name"].toString()
                        val rollNum = document.data["rollNo"].toString()
                        val branch = document.data["branch"].toString()
//                        here document.id = 18cse006
//                        document.id.toString(),
                        items.add(UsersItemModel(img, name, rollNum, branch))
                        myClassList.add(UsersItemModel(img, name, rollNum, branch))
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
        return items
    }

    //this is called second
    override fun onCardDragging(direction: Direction, ratio: Float) {
        requireView().presentBtn.collapse()
        requireView().absentBtn.collapse()
        Log.d(TAG, "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    //this is called fourth
    override fun onCardSwiped(direction: Direction) {
        Log.d(TAG, "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        val thisDirection = direction.toString()
        if (thisDirection == "Right") {
//            presentUsers.add(currentRollNo)
//            markPresent(currentRollNo)
//            Log.d(TAG, "Present: $presentUsers")
        } else {
//            absentUsers.add(currentRollNo)
            Log.d(TAG, "Absent: $absentUsers")
        }
        if (manager.topPosition == adapter.itemCount - 5) {
            paginate()
        }
    }

//    private fun markAbsent(position: Int) {
//        val presentStudents: MutableMap<String, Int> = HashMap()
//        presentStudents["rollNo"] = myClassList[position].rollNo
//        db.collection("attendance").document(classSection).collection(date).document("absent")
//            .set(presentStudents)
//            .addOnSuccessListener {
//
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//            }
//    }

//    private fun markPresent(current: Int) {
//        Log.d(TAG, this.currentRollNo.toString())
//        val presentStudents: MutableMap<String, Int> = HashMap()
//        presentStudents["rollNo"] = current
//        db.collection("attendance").document(classSection).collection(date).document("OOP")
//            .collection("present")
//            .add(presentStudents)
//            .addOnSuccessListener {
//
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document", e)
//            }
//    }

    //this is called when rewind button is clicked
    override fun onCardRewound() {
        Log.d(TAG, "onCardRewound: ${manager.topPosition}")
    }

    //this may called third
    override fun onCardCanceled() {
        Log.d(TAG, "onCardCanceled: ${manager.topPosition}")
    }

    //this is called first
    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d(TAG, "onCardAppeared: ($position) ${textView.text}")
//        currentRollNo = myClassList[position].rollNo

    }

    //this may called third
    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d(TAG, "onCardDisappeared: ($position) ${textView.text}")
    }


}