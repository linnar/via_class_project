package cz.janlinka.via.backend.merger

import cz.janlinka.via.backend.Geocoder
import cz.janlinka.via.backend.songkick.Concert
import java.util.*

/**
 * functions for data merging
 */

fun writeCountry(concert: Concert): Concert {
    if (concert.location.lat != 0.0 && concert.location.lon != 0.0) {
        val countryString = Geocoder.findCountry(concert.location.lat, concert.location.lon)
        return concert.copy(location = concert.location.copy(country = countryString))}
    else {
        return concert
    }
}

fun countryStats(concerts: List<Concert>): List<Country> {
    val counts = HashMap<String, Int>()
    for (concert in concerts) {
        counts.put(concert.location.country, counts.getOrDefault(concert.location.country, 0) + 1)
    }
    val countries = ArrayList<Country>(counts.size)
    for ((k, v) in counts) {
        countries.add(Country(k, v))
    }
    return countries
}
