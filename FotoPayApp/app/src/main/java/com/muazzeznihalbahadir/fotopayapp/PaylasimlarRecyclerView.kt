package com.muazzeznihalbahadir.fotopayapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_fotograf_paylas.view.*
import kotlinx.android.synthetic.main.recycler_row.view.*

class PaylasimlarRecyclerView(val postList : ArrayList<Post>) : RecyclerView.Adapter <PaylasimlarRecyclerView.PostHolder> (){
    class PostHolder (itemView: View): RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaylasimlarRecyclerView.PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PaylasimlarRecyclerView.PostHolder, position: Int) {
        holder.itemView.recycler_row_kullanici_email.text = postList[position].kullaniciEmail
        holder.itemView.recycler_row_kullanici_yorum.text = postList[position].kullaniciYorum
        Picasso.get().load(postList[position].gorselUrl).into(holder.itemView.recycler_row_imageView)
    }

    override fun getItemCount(): Int {
        return  postList.size
    }
}