package com.muazzeznihalbahadir.fotopayapp

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_fotograf_paylas.*
import java.util.*

class FotografPaylasActivity : AppCompatActivity() {

    var secilenGorsel : Uri?=null
    var secilenBitmap : Bitmap? =null

    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var firestore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotograf_paylas)
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }


    fun gorselSec (view: View){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2)
        }
    }
    fun paylas (view: View){
        //depolama
        val uuid = UUID.randomUUID()
        val gorselIsmi = "${uuid}.jpg"

        val referance = storage.reference
        val gorselReferance = referance.child("images").child(gorselIsmi)

        if(secilenGorsel !=null){

            gorselReferance.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapshot ->
                val yuklenenGorselReferance = FirebaseStorage.getInstance().reference.child("images").child(gorselIsmi)
                    yuklenenGorselReferance.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUri = uri.toString()
                        val guncelKullanici = auth.currentUser!!.email.toString()
                        val yorum = yorumText.text.toString()
                        val tarih = Timestamp.now()

                        val postHashMap = hashMapOf<String,Any>()
                        postHashMap.put("gorselUri",downloadUri)
                        postHashMap.put("kullaniciEmail",guncelKullanici)
                        postHashMap.put("kullaniciYorum",yorum)
                        postHashMap.put("tarih",tarih)

                        firestore.collection("Post").add(postHashMap).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                        }

                    }.addOnFailureListener { exception ->
                        Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
                    }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if (grantResults.isNotEmpty() && grantResults.get(0)==PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data !=null){
            secilenGorsel=data.data
            if(Build.VERSION.SDK_INT >=28){
                val source =ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                secilenBitmap=ImageDecoder.decodeBitmap(source)
                imageView.setImageBitmap(secilenBitmap)

            }else{
                secilenBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                imageView.setImageBitmap(secilenBitmap)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}