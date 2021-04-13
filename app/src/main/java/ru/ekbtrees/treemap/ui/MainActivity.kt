package ru.ekbtrees.treemap.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RawTile
import com.yandex.mapkit.ZoomRange
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.geo.Projection
import com.yandex.mapkit.geometry.geo.Projections
import com.yandex.mapkit.geometry.geo.XYPoint
import com.yandex.mapkit.layers.Layer
import com.yandex.mapkit.layers.LayerOptions
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapType
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.resource_url_provider.DefaultUrlProvider
import com.yandex.mapkit.resource_url_provider.ResourceUrlProvider
import com.yandex.mapkit.tiles.TileProvider
import ru.ekbtrees.treemap.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


class MainActivity : AppCompatActivity() {

    private var mapview: MapView? = null
    private var projection: Projection? = null
    private var urlProvider: DefaultUrlProvider? = null
    private var tileProvider: TileProvider? = null
    private val MAX_ZOOM = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f1c6f8d7-d45f-4abf-9376-853f7ecf0c46")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)
        // Укажите имя activity вместо map.
        mapview = findViewById(R.id.mapview) as MapView
//        mapview!!.map.mapType = MapType.VECTOR_MAP
        mapview!!.map.move(
                CameraPosition(Point(48.88835537150087, 2.3480827168727205), 14.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0F),
                null)
        tileProvider = createTileProvider()
        urlProvider = DefaultUrlProvider()

        // Client code must retain strong references to providers and projection
        projection = Projections.getWgs84Mercator()
        val layer = mapview!!.map.addGeoJSONLayer("geo_json_layer",
                style(),
                LayerOptions(),
                tileProvider!!,
                urlProvider!!,
                projection!!,
                ArrayList<ZoomRange>())
        layer.invalidate("0")
    }

    override fun onStop() {
        super.onStop()
        mapview!!.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview!!.onStart()
        MapKitFactory.getInstance().onStart()
    }
    @Throws(IOException::class)
    private fun getJsonResource(name: String): String {
        val builder = StringBuilder()
        val resourceIdentifier = resources.getIdentifier(name, "raw", packageName)
        val `is` = resources.openRawResource(resourceIdentifier)
        val reader = BufferedReader(InputStreamReader(`is`))
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
        } catch (ex: IOException) {
            reader.close()
            Log.e("TAG", "Cannot read JSON resource $name")
            throw ex
        }
        return builder.toString()
    }
    @Throws(IOException::class)
    private fun style(): String {
        return getJsonResource("geo_json_style_example")
    }

    @Throws(IOException::class)
    private fun createTileProvider(): TileProvider? {
        val jsonTemplate = getJsonResource("geo_json_example_template")
        return TileProvider { tileId, version, etag ->
            val tileSize = 1 shl MAX_ZOOM - tileId.z
            val left = tileId.x * tileSize
            val right = left + tileSize
            val bottom = tileId.y * tileSize
            val top = bottom + tileSize
            val leftBottom = projection!!.xyToWorld(XYPoint(left.toDouble(), bottom.toDouble()), MAX_ZOOM)
            val rightTop = projection!!.xyToWorld(XYPoint(right.toDouble(), top.toDouble()), MAX_ZOOM)
            val tileLeft = leftBottom.longitude
            val tileRight = rightTop.longitude
            val tileBottom = leftBottom.latitude
            val tileTop = rightTop.latitude
            val map = HashMap<String, Double>()
            map["@POINT_X@"] = 0.7 * tileLeft + 0.3 * tileRight
            map["@POINT_Y@"] = 0.7 * tileBottom + 0.3 * tileTop
            map["@LINE_X0@"] = 0.9 * tileLeft + 0.1 * tileRight
            map["@LINE_Y0@"] = 0.9 * tileBottom + 0.1 * tileTop
            map["@LINE_X1@"] = 0.9 * tileLeft + 0.1 * tileRight
            map["@LINE_Y1@"] = 0.1 * tileBottom + 0.9 * tileTop
            map["@LINE_X2@"] = 0.1 * tileLeft + 0.9 * tileRight
            map["@LINE_Y2@"] = 0.1 * tileBottom + 0.9 * tileTop
            map["@LINE_X3@"] = 0.1 * tileLeft + 0.9 * tileRight
            map["@LINE_Y3@"] = 0.9 * tileBottom + 0.1 * tileTop
            map["@POLYGON_X0@"] = 0.2 * tileLeft + 0.8 * tileRight
            map["@POLYGON_Y0@"] = 0.8 * tileBottom + 0.2 * tileTop
            map["@POLYGON_X1@"] = 0.5 * tileLeft + 0.5 * tileRight
            map["@POLYGON_Y1@"] = 0.5 * tileBottom + 0.5 * tileTop
            map["@POLYGON_X2@"] = 0.2 * tileLeft + 0.8 * tileRight
            map["@POLYGON_Y2@"] = 0.2 * tileBottom + 0.8 * tileTop
            map["@TEXTURED_POLYGON_X0@"] = 0.8 * tileLeft + 0.2 * tileRight
            map["@TEXTURED_POLYGON_Y0@"] = 0.2 * tileBottom + 0.8 * tileTop
            map["@TEXTURED_POLYGON_X1@"] = 0.2 * tileLeft + 0.8 * tileRight
            map["@TEXTURED_POLYGON_Y1@"] = 0.2 * tileBottom + 0.8 * tileTop
            map["@TEXTURED_POLYGON_X2@"] = 0.5 * tileLeft + 0.5 * tileRight
            map["@TEXTURED_POLYGON_Y2@"] = 0.5 * tileBottom + 0.5 * tileTop
            var json = jsonTemplate
            for ((key, value) in map) {
                json = json.replace(key, value.toString())
            }
            RawTile(version, etag, RawTile.State.NOT_MODIFIED, json.toByteArray())
        }
    }
}