package com.muazzeznihalbahadir.fotopayapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_paylasimlar.*

class PaylasimlarActivity : AppCompatActivity() {
    private lateinit var auth :FirebaseAuth
    private  lateinit var firestore : FirebaseFirestore
    private lateinit var adapter : PaylasimlarRecyclerView

    var postListesi = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paylasimlar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.secenekler_menu,menu)

        auth = FirebaseAuth.getInstance()
        firestore= FirebaseFirestore.getInstance()

        veriAl()

        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager

        adapter= PaylasimlarRecyclerView(postListesi)
        recyclerView.adapter=adapter
        return super.onCreateOptionsMenu(menu)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun veriAl(){
        try {
            firestore.collection("Post").orderBy("tarih",Query.Direction.DESCENDING).addSnapshotListener { snapshot, exception ->
                if(exception !=null){
                    Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }else{
                    if(snapshot!=null){
                        if(!snapshot.isEmpty){
                            val documents =snapshot.documents
                            postListesi.clear()
                            for (document in documents){
                                val kullaniciEmail = document.get("kullaniciEmail") as String
                                val kullaniciYorum = document.get("kullaniciYorum") as String
                                val gorselUrl = document.get("gorselUri") as String

                                val indirilenPost = Post(kullaniciEmail,kullaniciYorum,gorselUrl)
                                postListesi.add(indirilenPost)
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.fotograf_paylas){
            //foto paylaş
            val intent = Intent(this,FotografPaylasActivity::class.java)
            startActivity(intent)

        }else if (item.itemId==R.id.cikis_yap){
            auth.signOut()
            val intent = Intent(this,KullaniciActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}