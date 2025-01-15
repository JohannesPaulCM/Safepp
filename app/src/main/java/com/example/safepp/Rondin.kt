package com.example.safepp

import androidx.room.Entity
import java.io.Serializable
import androidx.room.PrimaryKey
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Entity(tableName = "rondines")
class Rondin (val zona:String,
              val time:String,
              val latitud:String,
              val longitud:String,
              val novedad:String,
              val imagen:Int,
             @PrimaryKey(autoGenerate=true)
              var idRondin: Int=0

):Serializable

{


}
