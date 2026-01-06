function SatellitePanel({ data }) {
  if (!data) return <p>Loading satellite data...</p>;

  return (
    <>
      <h4>SATELLITE</h4>
      <p>Lat : {data.latitude.toFixed(2)}°</p>
      <p>Lon : {data.longitude.toFixed(2)}°</p>
      <p>Alt : {(data.altitude / 1000).toFixed(1)} km</p>
      <p>Vel : {data.velocity.toFixed(2)} km/s</p>
      <p>Az  : {data.azimuth.toFixed(1)}°</p>
      <p>Time: {data.time}</p>
    </>
  );
}

export default SatellitePanel;
