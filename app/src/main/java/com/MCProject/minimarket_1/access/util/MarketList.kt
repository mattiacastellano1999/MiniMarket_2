@file:Suppress("DEPRECATION")

package com.MCProject.minimarket_1.access.util

import android.app.Activity
import android.app.ProgressDialog
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.MCProject.minimarket_1.user.MarketAviableActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class MarketList {
    companion object{
        fun getAllMarket(context: Activity, fr: FirestoreRequest): Task<QuerySnapshot> {
            val tempList: MutableMap<Int, ArrayList<String>> = LinkedHashMap()
            val pd = ProgressDialog(context)
            val lat = MyLocation.myLat
            val lon = MyLocation.myLon
            val nameObtained = fr.db
                .collection("/profili/gestori/dati")
                .get()
                .addOnSuccessListener {
                    pd.dismiss()
                    for ((i, doc) in it.withIndex()) {
                        tempList[i] =
                            arrayListOf(
                                doc["nome azienda"].toString(),
                                doc["email"].toString(),
                                doc["via"].toString(),
                                doc["citta"].toString()
                            )
                    }
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    pd.dismiss()
                    Log.i("HEY", "Failed")
                    Toast.makeText(
                        context,
                        "Error during Document data upload",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnFailureListener
                }
            //ordino i market dal più vicino al più lontano
            val out = nameObtained.addOnCompleteListener {
                //riempio markets
                Log.i("HEY", "before geocoding 1: ${tempList.size}")
                tempList.entries.forEach { entry ->
                    geocoding(entry, context)
                    entry.value
                }

                tempList.entries.forEach { entry ->
                    Log.i("HEY", ""+entry)
                }

                val comp = Comparator<ArrayList<String>> { o, o2 ->
                    Log.e("HEY", ""+o)

                    val result1 = FloatArray(3)
                    Location.distanceBetween(
                        lat,
                        lon,
                        o[4].toDouble(),
                        o[5].toDouble(),
                        result1
                    )
                    val distance1 = result1[0]
                    val result2 = FloatArray(3)
                    Location.distanceBetween(
                        lat,
                        lon,
                        o2[4].toDouble(),
                        o2[5].toDouble(),
                        result2
                    )
                    val distance2 = result2[0]

                    Log.e("HEY", "Distance1 = $distance1, Distance2 = $distance2")
                    compareValues(distance1, distance2)
                }
                val my = tempList.values.toSortedSet(comp)
                Log.i("HEY",my.toString())

                val outArr = mutableSetOf<ArrayList<String>>()
                Log.i("HEY", "FromMeDistance: " + lat +"__"+ lon)
                my.forEach {
                    val result1 = FloatArray(3)
                    Location.distanceBetween(
                        lat,
                        lon,
                        it[4].toDouble(),
                        it[5].toDouble(),
                        result1
                    )
                    if(result1[0] < 10000.00){
                        outArr.add(it)
                    }
                }
                MarketAviableActivity.sortedList = outArr
                return@addOnCompleteListener
            }
            return out
        }

        private fun geocoding(
            locationArray: MutableMap.MutableEntry<Int, ArrayList<String>>,
            context: Activity
        ) {
            var geocodeMatches: List<Address>? = null

            val via = locationArray.value[2]
            val citta = locationArray.value[3]

            Log.i("HEY", "geocoding 1")

            try {
                geocodeMatches = Geocoder(context).getFromLocationName("$citta, $via", 1)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("HEY", ""+e)
            }
            Log.i("HEY", "geocoding 1 : $geocodeMatches")

            if (geocodeMatches != null) {
                val lat = geocodeMatches[0].latitude.toString()
                val lon = geocodeMatches[0].longitude.toString()
                locationArray.value.add(lat)
                locationArray.value.add(lon)
            }
        }
    }
}