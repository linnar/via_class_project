package cz.janlinka.via.backend.songkick

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.util.*

/**
 * Group of functions extracting information from Songkick API - data transformed from XML so JSON mapping is done by hand
 */

val SONGKICK_KEY = System.getenv("SONGKICK_KEY")

val urlArtists = "http://api.songkick.com/api/3.0/search/artists.json?query=%s&apikey=" + SONGKICK_KEY
val urlConcerts = "http://api.songkick.com/api/3.0/artists/%d/gigography.json?order=desc&apikey=" + SONGKICK_KEY
val urlConcertsMbid = "http://api.songkick.com/api/3.0/artists/mbid:%s/gigography.json?order=desc&apikey=" + SONGKICK_KEY
val urlCalendar = "http://api.songkick.com/api/3.0/artists/%d/calendar.json?apikey=" + SONGKICK_KEY
val urlCalendarMbid = "http://api.songkick.com/api/3.0/artists/mbid:%s/calendar.json?apikey=" + SONGKICK_KEY


fun downloadArtists(query: String): List<Artist> {
    val client = OkHttpClient()
    val request = Request.Builder().url(urlArtists.format(query)).build()
    val response = client.newCall(request).execute()
    val artists = parseArtists(response.body().string())
    return artists
}

fun downloadPastConcerts(id: Long, maxCount: Int = 50): List<Concert> {
    return downloadConcerts(urlConcerts.format(id) + "&page=%d", maxCount)
}

fun downloadPastConcerts(mbid: String, maxCount: Int = 50): List<Concert> {
    return downloadConcerts(urlConcertsMbid.format(mbid) + "&page=%d", maxCount)
}

fun downloadFutureConcerts(id: Long, maxCount: Int = 50): List<Concert> {
    return downloadConcerts(urlCalendar.format(id) + "&page=%d", maxCount)
}

fun downloadFutureConcerts(mbid: String, maxCount: Int = 50): List<Concert> {
    return downloadConcerts(urlCalendarMbid.format(mbid) + "&page=%d", maxCount)
}

fun downloadConcerts(urlBase: String, maxCount: Int, initList: List<Concert> = emptyList(), pageNum: Int = 1): List<Concert> {
    val client = OkHttpClient()
    val request = Request.Builder().url(urlBase.format(pageNum)).build()
    val response = client.newCall(request).execute()
    val json = response.body().string()

    val results = ArrayList<Concert>(initList)
    val m = jacksonObjectMapper()
    val page = m.readTree(json.toString()).path("resultsPage")
    val events  = page.path("results").path("event")

    val total = page.path("totalEntries").longValue()
    for (event in events) {
        val name = event.path("displayName").textValue()
        val concertId = event.path("id").longValue()
        val uri = event.path("uri").textValue()
        val lat = event.path("location").path("lat").doubleValue()
        val lon = event.path("location").path("lng").doubleValue()
        results.add(Concert(concertId, name, uri, Location(lat, lon)))
    }

    if (results.size < total && results.size < maxCount) {
        return downloadConcerts(urlBase, maxCount, results, pageNum + 1)
    } else {
        return results
    }
}

fun parseArtists(json: String): List<Artist> {
    val results = ArrayList<Artist>()
    val m = jacksonObjectMapper()
    val artists = m.readTree(json.toString()).path("resultsPage").path("results").path("artist")
    for (artist in artists) {
        val name = artist.path("displayName").textValue()
        val id = artist.path("id").longValue()
        results.add(Artist(name, id))
    }
    return results
}