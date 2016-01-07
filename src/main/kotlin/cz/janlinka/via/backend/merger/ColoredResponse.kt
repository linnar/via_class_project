package cz.janlinka.via.backend.merger

import cz.janlinka.via.backend.songkick.Concert

data class ColoredResponse(val events: List<Concert>, val countries: List<Country>, val maxEvents: Int)
