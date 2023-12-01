package com.example.gson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private val URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"
    private var okHttpClient: OkHttpClient = OkHttpClient()
    private var links : Array<String> = arrayOf()
    private lateinit var  recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plant(Timber.DebugTree());

        setContentView(R.layout.activity_main)
        getJSONFromServer();
        recyclerView = findViewById(R.id.rView)



    }

    private fun getJSONFromServer() {
        val request : Request = Request.Builder().url(URL).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonFromServer = response.body?.string()
                paseJSON(jsonFromServer)
            }
        })
    }

    private fun paseJSON(jsonFromServer: String?) {
        val builder : GsonBuilder = GsonBuilder()
        val gson: Gson = builder.create()


        val result : Wrapper = gson.fromJson(jsonFromServer, Wrapper::class.java)

        for (i in 0..<result.photos.photo.size){
            links+=( "https://farm"+result.photos.photo[i].farm+".staticflickr.com/"+result.photos.photo[i].server+"/"+result.photos.photo[i].id+"_"+result.photos.photo[i].secret+"_z.jpg")
            Timber.d("Photo_Log", links.get(i))
        }

        runOnUiThread{
            recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerView.adapter = MyRecyclerAdapter(this,links)
        }
    }

}



// {"id":"2671271409","owner":"90731709@N00","secret":"6e07e09976","server":"3284","farm":4,"title":"g\u00f6zlerime inanam\u0131yorum !","ispublic":1,"isfriend":0,"isfamily":0
data class Photo ( val id : Number, val owner: String, val secret: String, val server: Number, val farm: Number, val title: String, val isPublic: Boolean, val isFriend: Boolean, val isFamily: Boolean)

// page":1,"pages":1009,"perpage":100,"total":100808,"photo"
data class Page( val page : Number, val pages: Number, val perpage: Number, val photo: Array<Photo>, val stat: String)

data class Wrapper(val photos : Page)
