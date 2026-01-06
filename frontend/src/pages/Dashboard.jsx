import { useState } from "react";
import "../styles/dashboard.css";

import Map2D from "../components/map/Map2D";
import SatellitePanel from "../components/panels/SatellitePanel";
import SatelliteSelector from "../components/controls/SatelliteSelector";

import { useSatellitePosition } from "../hooks/useSatellitePosition";
import { useOrbitTrack } from "../hooks/useOrbitTrack";

function Dashboard() {
  const cityName = "Hyderabad";

  const satellites = [
    "SURCAL 159",
    "ISS (ZARYA)",
    "NOAA 15",
    "NOAA 18"
  ];

  const [satelliteName, setSatelliteName] = useState(satellites[0]);

  const { data, error } = useSatellitePosition(
    satelliteName,
    cityName
  );

  const orbitTrack = useOrbitTrack(satelliteName);

  return (
    <div className="dashboard">

      <div className="header">
        <div>🛰 SatTracker</div>
        <div>{satelliteName} | {cityName}</div>
      </div>

      <div className="main">

        <div className="map-panel">
          <Map2D
            satelliteData={data}
            orbitTrack={orbitTrack}
          />
        </div>

        <div className="right-panel">

          <SatelliteSelector
            satellites={satellites}
            value={satelliteName}
            onChange={setSatelliteName}
          />

          <hr style={{ borderColor: "#00ffcc33", margin: "12px 0" }} />

          {error && <p style={{ color: "red" }}>{error}</p>}
          <SatellitePanel data={data} />

        </div>

      </div>

      <div className="status-bar">
        STATUS: LIVE | ORBIT TRACK ENABLED
      </div>

    </div>
  );
}

export default Dashboard;
