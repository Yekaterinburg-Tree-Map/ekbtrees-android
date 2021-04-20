package ru.ekbtrees.treemap.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import ru.ekbtrees.treemap.BuildConfig
import ru.ekbtrees.treemap.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.math.abs

class MainActivity : Activity() {
    private val TAG = "MainActivity"
    private val EPSILON = 1e-12
    private val MAPKIT_API_KEY = BuildConfig.MAPKIT_API_KEY
    private var mapView: MapView? = null
    private val treeTapListener = TreeTapListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mapView = findViewById<View>(R.id.mapview) as MapView
        val bitmap = Bitmap.createBitmap(15, 15, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val objects = mapView!!.map.mapObjects
        val backgroundPaint = Paint()
        backgroundPaint.isAntiAlias = true
        backgroundPaint.color = Color.BLACK
        backgroundPaint.style = Paint.Style.FILL
        val width = 30
        canvas.drawCircle(
            (width * 0).toFloat(),
            (width * 0).toFloat(),
            (width / 2).toFloat(),
            backgroundPaint
        )
        val trees = parseTrees()
        for (i in trees.indices) {
            val tree = trees[i]
            val diameter = tree.diameterOfCrown
            val h = mapping(diameter, 0.0, 12.0, 50.0, 150.0)
            var arr = floatArrayOf(h.toFloat(), 0.7f, 0.8f)
            val color = Color.HSVToColor(120, arr)
            val obj = objects.addCircle(
                Circle(
                    Point(tree.latitude, tree.longitude),
                    mapping(diameter, 0.0, 12.0, 0.0, 6.0).toFloat()
                ), Color.GREEN,
                0F, color
            );
            obj.userData = TreeData(applicationContext, tree)
        }
        objects.addTapListener(treeTapListener)
        mapView!!.map.move(
            CameraPosition(
                Point(
                    trees[0].latitude,
                    trees[0].longitude
                ), 15F, 0F, 0F
            )
        )
    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    private fun parseTrees(): List<Tree> {
        var string: String? = ""
        val stringBuilder = StringBuilder()
        val inputStream = this.resources.openRawResource(R.raw.trees)
        val reader = BufferedReader(InputStreamReader(inputStream))
        inputStream.use {
            while (true) {
                try {
                    if (reader.readLine().also { string = it } == null) break
                } catch (e: IOException) {
                    Log.e(
                        TAG,
                        "Failed to load local asset. Check asset location and the name of a file.",
                        e
                    )
                }
                stringBuilder.append(string)
            }
        }
        val output = stringBuilder.toString()
        val gson = Gson()
        val itemsListType = object : TypeToken<ArrayList<Tree?>?>() {}.type
        return gson.fromJson(output, itemsListType)
    }

    private fun mapping(
        valueCoord1: Double,
        startCoord1: Double, endCoord1: Double,
        startCoord2: Double, endCoord2: Double
    ): Double {
        if (abs(endCoord1 - startCoord1) < EPSILON) {
            throw ArithmeticException("/ 0")
        }
        val ratio = (endCoord2 - startCoord2) / (endCoord1 - startCoord1)
        return ratio * (valueCoord1 - startCoord1) + startCoord2
    }
}