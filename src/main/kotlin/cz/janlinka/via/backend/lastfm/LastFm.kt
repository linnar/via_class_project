package cz.janlinka.via.backend.lastfm

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.util.*

/**
 * Extract data from last.fm - the API is backed by XML so it is parsed by hand
 */

val userUrl = "http://ws.audioscrobbler.com/2.0/?method=user.gettopartists&user=%s&format=json&limit=20&period=12month&api_key=" + System.getenv("LAST_FM_KEY")

fun downloadMbids(username: String): List<String> {
    val client = OkHttpClient()
    val request = Request.Builder().url(userUrl.format(username)).build()
    val response = client.newCall(request).execute()
    val json = response.body().string()

    val results = ArrayList<String>()
    val m = jacksonObjectMapper()
    val artists = m.readTree(json.toString()).path("topartists").path("artist")

    for (artist in artists) {
        val mbid = artist.path("mbid").textValue()
        results.add(mbid)
    }

    return results
}
