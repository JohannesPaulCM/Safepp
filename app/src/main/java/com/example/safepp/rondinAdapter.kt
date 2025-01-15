package com.example.safepp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.list_rondin.view.*

class rondinAdapter(private  val mContext: Context,private val listaRondines:List<Rondin> ) : ArrayAdapter<Rondin>(mContext,0,listaRondines) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       val layout = LayoutInflater.from(mContext).inflate(R.layout.list_rondin,parent,false)
        val rondin =listaRondines[position]
        layout.textViewZona.text=rondin.zona
        layout.textViewTime.text=rondin.time
        layout.textViewLat.text=rondin.latitud
        layout.textViewLo.text=rondin.longitud
        layout.textView_novedad_list.text=rondin.novedad
        layout.imageView2.setImageResource(rondin.imagen)


        return layout
    }

}
