package com.example.mymaps

import android.app.Activity
import android.content.AbstractThreadedSyncAdapter
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.models.Place
import com.example.mymaps.models.UserMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

const val EXTRA_USER_MAP = "EXTRA_USER_MAP"
private const val REQUEST_CODE :Int = 1234
public const val EXTRA_MAP_TITLE ="EXTRA_MAP_TITLE"
class MainActivity : AppCompatActivity() {

    private lateinit var userMaps: MutableList<UserMap>
    private lateinit var mapAdapter: MapsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userMaps = generateSampleData().toMutableList()
        //set layout manager on the recycler view
        var rvMaps = findViewById<RecyclerView>(R.id.rvMaps)
        rvMaps.layoutManager = LinearLayoutManager(this)

        //set adapter on the recycler view

        //BELOW , we had set the userMaps initialy as an empty list
         mapAdapter = MapsAdapter(this, userMaps,object: MapsAdapter.OnClickListener {
            override fun onItemClick(position: Int) {
                Log.i("ON","On clicked position $position")

                //when user taps view in RV, navigate to new activity
                val intent = Intent(this@MainActivity, DisplayMapActivity::class.java)
                intent.putExtra(EXTRA_USER_MAP,userMaps[position])
                startActivity(intent)
            }

        })

        rvMaps.adapter = mapAdapter

        var fabCreateActivity = findViewById<FloatingActionButton>(R.id.fabCreateMap)

        fabCreateActivity.setOnClickListener{
            Log.i("TAG","Tap on FAB")
            showAlertDialogue()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK){
            val userMap = data?.getSerializableExtra(EXTRA_USER_MAP) as UserMap
            Log.i("TAG","OnActivityResult with new map ${userMap.title}")
            userMaps.add(userMap)
            mapAdapter.notifyItemInserted(userMaps.size -1)

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showAlertDialogue(){
        val mapFormView = LayoutInflater.from(this).inflate(R.layout.dialog_create_map,null)
        var dialog = AlertDialog.Builder(this)
            .setTitle("Map Title")
            .setView(mapFormView)
            .setNegativeButton("cancel",null)
            .setPositiveButton("ok",null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
            var title = mapFormView.findViewById<EditText>(R.id.etMapTitle).text.toString()

            if(title.trim().isEmpty()){
                Toast.makeText(this,"Title must be non-empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //Navigate to create map activity
            val intent = Intent(this,CreateMapActivity::class.java)
            intent.putExtra(EXTRA_MAP_TITLE,title)
            startActivityForResult(intent, REQUEST_CODE)
            dialog.dismiss()
        }

    }

    private fun generateSampleData(): List<UserMap> {
        return listOf(
            UserMap(
                "Memories from University",
                listOf(
                    Place("Branner Hall", "Best dorm at Stanford", 37.426, -122.163),
                    Place("Gates CS building", "Many long nights in this basement", 37.430, -122.173),
                    Place("Pinkberry", "First date with my wife", 37.444, -122.170)
                )
            ),
            UserMap("January vacation planning!",
                listOf(
                    Place("Tokyo", "Overnight layover", 35.67, 139.65),
                    Place("Ranchi", "Family visit + wedding!", 23.34, 85.31),
                    Place("Singapore", "Inspired by \"Crazy Rich Asians\"", 1.35, 103.82)
                )),
            UserMap("Singapore travel itinerary",
                listOf(
                    Place("Gardens by the Bay", "Amazing urban nature park", 1.282, 103.864),
                    Place("Jurong Bird Park", "Family-friendly park with many varieties of birds", 1.319, 103.706),
                    Place("Sentosa", "Island resort with panoramic views", 1.249, 103.830),
                    Place("Botanic Gardens", "One of the world's greatest tropical gardens", 1.3138, 103.8159)
                )
            ),
            UserMap("My favorite places in the Midwest",
                listOf(
                    Place("Chicago", "Urban center of the midwest, the \"Windy City\"", 41.878, -87.630),
                    Place("Rochester, Michigan", "The best of Detroit suburbia", 42.681, -83.134),
                    Place("Mackinaw City", "The entrance into the Upper Peninsula", 45.777, -84.727),
                    Place("Michigan State University", "Home to the Spartans", 42.701, -84.482),
                    Place("University of Michigan", "Home to the Wolverines", 42.278, -83.738)
                )
            ),
            UserMap("Restaurants to try",
                listOf(
                    Place("Champ's Diner", "Retro diner in Brooklyn", 40.709, -73.941),
                    Place("Althea", "Chicago upscale dining with an amazing view", 41.895, -87.625),
                    Place("Shizen", "Elegant sushi in San Francisco", 37.768, -122.422),
                    Place("Citizen Eatery", "Bright cafe in Austin with a pink rabbit", 30.322, -97.739),
                    Place("Kati Thai", "Authentic Portland Thai food, served with love", 45.505, -122.635)
                )
            )
        )
    }
}