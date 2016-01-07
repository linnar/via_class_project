package cz.janlinka.via.backend

import geocode.ReverseGeoCode
import org.springframework.core.io.ClassPathResource

/**
 * The events produced too many calls to use originally intended online reverse geocoding API
 */
object Geocoder {
    private val geocoder: ReverseGeoCode
    init {
        val classPathResource = ClassPathResource("cities15000.txt")

        geocoder = ReverseGeoCode(classPathResource.inputStream, true)
    }

    public fun findCountry(lat: Double, lon: Double): String {
        val place = geocoder.nearestPlace(lat, lon)
        return place?.country ?: ""
    }
}
