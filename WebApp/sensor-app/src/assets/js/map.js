function cargarMapa() {
    var map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/streets-v11',
        center: [-74.50, 40],
        zoom: 9 })

    map.on('load', function() {
        console.log("CARGANDO")
        map.addLayer({
            id: 'terrain-data',
            type: 'line',
            source: {
            type: 'vector',
            url: 'mapbox://mapbox.mapbox-terrain-v2'
            },
            'source-layer': 'contour'
        });
    });
}