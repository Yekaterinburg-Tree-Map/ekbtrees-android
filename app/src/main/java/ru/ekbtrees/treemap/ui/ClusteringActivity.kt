package ru.ekbtrees.treemap.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.ekbtrees.treemap.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class ClusteringActivity : Activity(), ClusterListener, ClusterTapListener {
    /**
     * Replace "your_api_key" with a valid developer key.
     * You can get it at the https://developer.tech.yandex.ru/ website.
     */
    private val MAPKIT_API_KEY = "f1c6f8d7-d45f-4abf-9376-853f7ecf0c46"
    private var mapView: MapView? = null

    inner class TextImageProvider(private val text: String) : ImageProvider() {
        override fun getId(): String {
            return "text_$text"
        }

        override fun getImage(): Bitmap {
            val metrics = DisplayMetrics()
            val manager = getSystemService(WINDOW_SERVICE) as WindowManager
            manager.defaultDisplay.getMetrics(metrics)
            val textPaint = Paint()
            textPaint.textSize = FONT_SIZE * metrics.density
            textPaint.textAlign = Align.CENTER
            textPaint.style = Paint.Style.FILL
            textPaint.isAntiAlias = true
            val widthF = textPaint.measureText(text)
            val textMetrics = textPaint.fontMetrics
            val heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top)
            val textRadius = Math.sqrt((widthF * widthF + heightF * heightF).toDouble())
                .toFloat() / 2
            val internalRadius = textRadius + MARGIN_SIZE * metrics.density
            val externalRadius = internalRadius + STROKE_SIZE * metrics.density
            val width = (2 * externalRadius + 0.5).toInt()
            val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val backgroundPaint = Paint()
            backgroundPaint.isAntiAlias = true
            backgroundPaint.color = Color.RED
            canvas.drawCircle(
                (width / 2).toFloat(),
                (width / 2).toFloat(),
                externalRadius,
                backgroundPaint
            )
            backgroundPaint.color = Color.WHITE
            canvas.drawCircle(
                (width / 2).toFloat(),
                (width / 2).toFloat(),
                internalRadius,
                backgroundPaint
            )
            canvas.drawText(
                text, (
                        width / 2).toFloat(),
                width / 2 - (textMetrics.ascent + textMetrics.descent) / 2,
                textPaint
            )
            return bitmap
        }
    }

    inner class TestImageProvider(private val size: Float) : ImageProvider() {
        override fun getId(): String {
            return ""
        }

        override fun getImage(): Bitmap {
            val metrics = DisplayMetrics()
            val manager = getSystemService(WINDOW_SERVICE) as WindowManager
            manager.defaultDisplay.getMetrics(metrics)
            val textPaint = Paint()
            textPaint.textSize = FONT_SIZE * metrics.density
            textPaint.textAlign = Align.CENTER
            textPaint.style = Paint.Style.FILL
            textPaint.isAntiAlias = true
            val widthF = size
            val textMetrics = textPaint.fontMetrics
            val heightF = Math.abs(textMetrics.bottom) + Math.abs(textMetrics.top)
            val textRadius = Math.sqrt((widthF * widthF + heightF * heightF).toDouble())
                .toFloat() / 2
            val internalRadius = textRadius + MARGIN_SIZE * metrics.density
            val externalRadius = (internalRadius + STROKE_SIZE * metrics.density) * size * 0.1f
            val width = (2 * externalRadius + 0.5).toInt()
            val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val backgroundPaint = Paint()
            backgroundPaint.isAntiAlias = true
            backgroundPaint.color = Color.argb(
                120,
                (50 * size % 256).toInt(),
                (80 * size % 256).toInt(),
                (20 * size % 256).toInt()
            )
            canvas.drawCircle(
                (width / 2).toFloat(),
                (width / 2).toFloat(),
                externalRadius,
                backgroundPaint
            )
            return bitmap
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        setContentView(R.layout.clustering)
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
        val imageProvider = ImageProvider.fromBitmap(bitmap)

        // Note that application must retain strong references to both
        // cluster listener and cluster tap listener
        val clusterizedCollection = mapView!!.map.mapObjects.addClusterizedPlacemarkCollection(this)
        val trees = createTrees()
        for (i in trees.indices) {
            val tree = trees[i]
            val diameter = tree.DiameterOfCrown
            //            CircleMapObject obj = objects.addCircle(new Circle(new Point(tree.Latitude, tree.Longitude), diameter), 100, 7, 5);
//            obj.setUserData(new Point(tree.Latitude, tree.Longitude));
            clusterizedCollection.addPlacemark(
                Point(
                    tree.Latitude.toDouble(),
                    tree.Longitude.toDouble()
                ), TestImageProvider(diameter), IconStyle()
            )
        }
        //        clusterizedCollection.addPlacemarks(trees, new TestImageProvider(""), new IconStyle());
        mapView!!.map.move(
            CameraPosition(
                Point(
                    trees[0].Latitude.toDouble(),
                    trees[0].Longitude.toDouble()
                ), 15F, 0F, 0F
            )
        )

        // Placemarks won't be displayed until this method is called. It must be also called
        // to force clusters update after collection change
        clusterizedCollection.clusterPlacemarks(15.0, 30)
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

    override fun onClusterAdded(cluster: Cluster) {
        // We setup cluster appearance and tap handler in this method
        cluster.appearance.setIcon(
            TextImageProvider(Integer.toString(cluster.size))
        )
        cluster.addClusterTapListener(this)
    }

    override fun onClusterTap(cluster: Cluster): Boolean {
        Toast.makeText(
            applicationContext,
            String.format(getString(R.string.cluster_tap_message), cluster.size),
            Toast.LENGTH_SHORT
        ).show()

        // We return true to notify map that the tap was handled and shouldn't be
        // propagated further.
        return true
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createTrees(): List<Tree> {
        var string: String? = ""
        val stringBuilder = StringBuilder()
        val `is` = this.resources.openRawResource(R.raw.trees)
        val reader = BufferedReader(InputStreamReader(`is`))
        while (true) {
            try {
                if (reader.readLine().also { string = it } == null) break
            } catch (e: IOException) {
                e.printStackTrace()
            }
            stringBuilder.append(string)
        }
        try {
            `is`.close()
        } catch (e: Exception) {
        }
        val output = stringBuilder.toString()
        val gson = Gson()
        val itemsListType = object : TypeToken<ArrayList<Tree?>?>() {}.type
        return gson.fromJson(output, itemsListType)
    }

    companion object {
        private const val FONT_SIZE = 15f
        private const val MARGIN_SIZE = 3f
        private const val STROKE_SIZE = 3f
    }
}