

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import java.util.*

/**
 * Created by E811339 on 2/24/2018.
 */

class dump : AppCompatActivity() {

    internal var googleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val search = ArrayList<String>()
        search.add("k")
        val pendingResult = Places.GeoDataApi.getPlaceById(googleApiClient, *search.toTypedArray())
    }


}
