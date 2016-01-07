package cz.janlinka.via.backend

import cz.janlinka.via.backend.lastfm.downloadMbids
import cz.janlinka.via.backend.merger.ColoredResponse
import cz.janlinka.via.backend.merger.countryStats
import cz.janlinka.via.backend.merger.writeCountry
import cz.janlinka.via.backend.songkick.*
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class ViaMainController {
    @RequestMapping("/artistId/{query}")
    fun searchArtist(@PathVariable query: String): List<Artist> {
        val artists = downloadArtists(query)
        return artists;
    }

    @RequestMapping("/futureEvents/{user}")
    fun searchFutureByUser(@PathVariable user: String): ColoredResponse {
        val mbids = downloadMbids(user)
        val concerts = ArrayList<Concert>()
        for (mbid in mbids) {
            concerts.addAll(downloadFutureConcerts(mbid))
        }
        val locatedConcerts = concerts.map {it -> writeCountry(it)}
        val stats = countryStats(locatedConcerts)

        val maxEvents = stats.maxBy { it -> it.count  }?.count ?: 0

        return ColoredResponse(locatedConcerts, stats, maxEvents)
    }

    @RequestMapping("/pastEvents/{user}")
    fun searchPastByUser(@PathVariable user: String): ColoredResponse {
        val mbids = downloadMbids(user)
        val concerts = ArrayList<Concert>()
        for (mbid in mbids) {
            concerts.addAll(downloadPastConcerts(mbid, 100))
        }
        val locatedConcerts = concerts.map {it -> writeCountry(it)}
        val stats = countryStats(locatedConcerts)

        val maxEvents = stats.maxBy { it -> it.count  }?.count ?: 0

        return ColoredResponse(locatedConcerts, stats, maxEvents)
    }

    @RequestMapping("lastFMTest/{user}")
    fun lasfFMTest(@PathVariable user: String): List<String> {
        val mbids = downloadMbids(user)
        return mbids
    }

    @RequestMapping("geocoderTest/{query}")
    fun testGeocoder(@PathVariable query: String): List<Concert> {
        val artists = downloadArtists(query)
        val topHit = artists[0]
        val concerts = downloadFutureConcerts(topHit.id)
        return concerts.map {it -> writeCountry(it)}
    }
}