import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  Polyline
} from "react-leaflet";
import L from "leaflet";

// Fix Leaflet icon
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
  iconUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});


  function splitOrbitByDateline(track) {
  if (!track || track.length === 0) return [];

  const segments = [];
  let currentSegment = [];

  for (let i = 0; i < track.length; i++) {
    const point = track[i];

    if (currentSegment.length > 0) {
      const prev = currentSegment[currentSegment.length - 1];

      // 🌍 detect longitude jump (dateline crossing)
      if (Math.abs(point.longitude - prev.longitude) > 180) {
        segments.push(currentSegment);
        currentSegment = [];
      }
    }

    currentSegment.push(point);
  }

  if (currentSegment.length > 0) {
    segments.push(currentSegment);
  }

  return segments;
}





function Map2D({ satelliteData, orbitTrack }) {
  return (
    <MapContainer
      center={[17.385, 78.4867]}
      zoom={4}
      style={{ height: "100%", width: "100%" }}
    >
      <TileLayer
        url="https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png"
      />

      {/* Ground Station */}
      <Marker position={[17.385, 78.4867]}>
        <Popup>Hyderabad Ground Station</Popup>
      </Marker>

      {/* Satellite */}
      {satelliteData && (
        <Marker
          position={[
            satelliteData.latitude,
            satelliteData.longitude
          ]}
        >
          <Popup>
            Alt: {(satelliteData.altitude / 1000).toFixed(1)} km
          </Popup>
        </Marker>
      )}

      {/* Orbit Track */}
    {splitOrbitByDateline(orbitTrack).map((segment, index) => (
    <Polyline
       key={index}
       positions={segment.map(p => [p.latitude, p.longitude])}
       pathOptions={{ color: "#00ffcc", weight: 2 }}
      />
    ))}
    </MapContainer>
  );
}

export default Map2D;
