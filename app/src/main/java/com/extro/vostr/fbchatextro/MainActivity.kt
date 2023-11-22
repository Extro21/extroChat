package com.extro.vostr.fbchatextro

import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.extro.vostr.fbchatextro.adapter.MessageAdapter
import com.extro.vostr.fbchatextro.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        setUpActionBar()

        val database = Firebase.database
        val myRef = database.getReference("message")


        binding.btSent.setOnClickListener {
            //child для уникального значения чтобы не было перезаписи
            myRef.child(myRef.push().key ?: "errorPath")
                .setValue(User(auth.currentUser?.displayName, binding.etMassage.text.toString()))
        }
        onChangeListener(myRef)
        initRcView()
        binding.etMassage.text.clear()

    }

    private fun initRcView() = with(binding) {
        adapter = MessageAdapter()
        rvMassages.layoutManager = LinearLayoutManager(this@MainActivity)
        rvMassages.adapter = adapter
    }

    // Добавить меню
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.sign_out) {
            auth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onChangeListener(dRef: DatabaseReference) {
        dRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.apply {
                    val list = ArrayList<User>()
                    for (s in snapshot.children) {
                        val user = s.getValue(User::class.java)
                        if (user != null) {
                            list.add(user)
                        }
                    }
                    adapter.submitList(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun setUpActionBar() {
        val ab = supportActionBar
        Thread {
            val bMap = Picasso.get().load(auth.currentUser?.photoUrl)
                .get()//загружаем картинку в пикассо и переводит в битмап
            val dIcon = BitmapDrawable(
                resources,
                bMap
            ) //превращяет битмап в drawable чтобы передать его в setHomeAsUpIndicator(dIcon)
            runOnUiThread { //запкскаем на основном потоке т.к. идет изменение ui
                ab?.setDisplayHomeAsUpEnabled(true) // появится картинка в левом верхнем углу(home button)
                ab?.setHomeAsUpIndicator(dIcon)
                ab?.title = auth.currentUser?.displayName
            }
        }.start()

    }
}