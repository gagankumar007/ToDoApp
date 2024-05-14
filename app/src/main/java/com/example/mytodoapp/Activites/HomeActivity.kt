package com.example.mytodoapp.Activites

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlintodopractice.utils.adapter.TaskAdapter
import com.example.mytodoapp.Fragment.ToDoDialogFragment
import com.example.mytodoapp.databinding.ActivityHomeBinding
import com.example.mytodoapp.models.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity(), TaskAdapter.TaskAdapterInterface,
    ToDoDialogFragment.OnDialogNextBtnClickListener {

    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val mDbRef by lazy {
        FirebaseDatabase.getInstance().getReference()
    }
    private val authId by lazy {
        val currentUser = mAuth.currentUser
        currentUser?.uid ?: ""
    }

    private val database by lazy {
        mDbRef.child("Tasks").child(authId)
    }

    private val toDoData: ArrayList<ToDoData> by lazy {
        ArrayList<ToDoData>()
    }

    private val adapter: TaskAdapter by lazy {
        TaskAdapter(toDoData)
    }
    private var frag: ToDoDialogFragment? = null
    val firebaseAuth = FirebaseAuth.getInstance()//used both type of initialization
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        getTaskFromFirebase()
        initializer()

    }

    private fun initializer() {

        adapter.setListener(this)
        binding.rvTaskList.adapter = adapter
        //layout manager is add in xml file
        binding.btnAddTask.setOnClickListener {
            frag = ToDoDialogFragment()
            frag!!.setListener(this)

            frag!!.show(supportFragmentManager, ToDoDialogFragment.TAG)
        }
        binding.txtLogOut.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoData.clear()
                for (taskSnapshot in snapshot.children) {
                    val todoTask =
                        taskSnapshot.key?.let { ToDoData(it, taskSnapshot.value.toString()) }
                    if (todoTask != null) {
                        toDoData.add(todoTask)
                    }
                }
                Log.d("DATA--->", "onDataChange: " + toDoData)
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        database.child(toDoData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {
        if (frag != null)
            supportFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogFragment.newInstance(toDoData.taskId, toDoData.task)
        frag!!.setListener(this)
        frag!!.show(
            supportFragmentManager,
            ToDoDialogFragment.TAG
        )
    }


    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {

        database
            .push().setValue(todoTask)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null

                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()

    }


    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId] = toDoData.task
        database.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
            frag!!.dismiss()
        }
    }


}