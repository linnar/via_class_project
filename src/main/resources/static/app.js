/// <reference path="lib/jquery.d.ts" />
var ApiReponse = (function () {
    function ApiReponse() {
    }
    return ApiReponse;
})();
var ApiEvent = (function () {
    function ApiEvent() {
    }
    return ApiEvent;
})();
var ApiLocation = (function () {
    function ApiLocation() {
    }
    return ApiLocation;
})();
var ApiCountry = (function () {
    function ApiCountry() {
    }
    return ApiCountry;
})();
window.onload = function () {
    $("#progress").hide();
    mapboxgl.accessToken = 'pk.eyJ1IjoiamFubGlua2EiLCJhIjoiNWIyMDIwNWZjYWQyZjI1ODEyYzQxNTY1NDM4MjZkMjUifQ.1fVWlwvpTbeNlEimutsUIA';
    map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/dark-v8',
        center: [14.416667, 20],
        zoom: 2 // starting zoom
    });
    map.on('style.load', function () {
        map.addSource('countries', {
            type: 'vector',
            url: 'mapbox://janlinka.ca4fpx8l'
        });
        map.addLayer({
            "id": "countries",
            "type": "line",
            "source": "countries",
            "source-layer": "ne_10m_admin_0_countries_lakes",
            "layout": {
                "line-join": "round",
                "line-cap": "round"
            },
            "paint": {
                "line-color": "#ff69b4",
                "line-width": 0
            }
        });
        // When a click event occurs near a marker icon, open a popup at the location of
        // the feature, with description HTML from its properties.
        map.on('click', function (e) {
            map.featuresAt(e.point, { layer: 'markers', radius: 10, includeGeometry: true }, function (err, features) {
                if (err || !features.length)
                    return;
                var feature = features[0];
                new mapboxgl.Popup()
                    .setLngLat(feature.geometry.coordinates)
                    .setHTML(feature.properties.description)
                    .addTo(map);
            });
        });
        // Use the same approach as above to indicate that the symbols are clickable
        // by changing the cursor style to 'pointer'.
        map.on('mousemove', function (e) {
            map.featuresAt(e.point, { layer: 'markers', radius: 10 }, function (err, features) {
                map.getCanvas().style.cursor = (!err && features.length) ? 'pointer' : '';
            });
        });
        // enable search
        setUp();
    });
};
function setUp() {
    $("#find-button").click(request);
    $("#username").keyup(function (event) {
        if (event.keyCode == 13) {
            request();
        }
    });
}
function request() {
    $("#progress").show();
    var username = $("#username").val();
    $.get("https://agile-plateau-1737.herokuapp.com/futureEvents/" + username)
        .done(onDataReceived)
        .always(function () { $("#progress").hide(); });
}
function countryAlpha(country, maxCount) {
    return 0.2 + 0.8 * (country.count / (maxCount));
}
function onDataReceived(json) {
    json.countries.forEach(function (country) {
        var countryLayer = {
            "id": country.name,
            "type": "fill",
            "source": "countries",
            "source-layer": "ne_10m_admin_0_countries_lakes",
            "filter": ["==", "ISO_A2", country.name],
            "paint": {
                "fill-color": "rgba(28, 139, 28," + countryAlpha(country, json.maxEvents) + ")"
            },
            "interactive": false
        };
        map.addLayer(countryLayer);
    });
    var markers = [];
    json.events.forEach(function (event) {
        var marker = {
            "type": "Feature",
            "properties": {
                "description": "<div class=\"marker-title\">" + event.name +
                    "</div><p><a href=\"" + event.uri + "\" target=\"_blank\" title=\"Opens in a new window\">Songkick event</a></p>",
                "marker-symbol": "music"
            },
            "geometry": {
                "type": "Point",
                "coordinates": [event.location.lon, event.location.lat]
            }
        };
        markers.push(marker);
    });
    var geomarkers = {
        "type": "FeatureCollection",
        "features": markers
    };
    map.addSource("markers", {
        "type": "geojson",
        "data": geomarkers
    });
    // Add a layer showing the markers.
    map.addLayer({
        "id": "markers",
        "interactive": true,
        "type": "symbol",
        "source": "markers",
        "layout": {
            "icon-image": "default_marker"
        }
    });
}
//# sourceMappingURL=app.js.map