package com.extro.vostr.fbchatextro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.extro.vostr.fbchatextro.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Firebase.database
        val myRef = database.getReference("message")


        binding.btSent.setOnClickListener {
            myRef.setValue(binding.etMassage.text.toString())
        }
        onChangeListener(myRef)


    }


    private fun onChangeListener(dRef : DatabaseReference){
        dRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
              binding.apply {
                  rcView.append("\n")
                  rcView.append(snapshot.value.toString())
              }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}