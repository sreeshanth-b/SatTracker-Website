function SatelliteSelector({ satellites, value, onChange }) {
  return (
    <div>
      <h4>SATELLITE</h4>

      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        style={{
          width: "100%",
          background: "#02070f",
          color: "#00ffcc",
          border: "1px solid #00ffcc44",
          padding: "6px",
          marginTop: "6px"
        }}
      >
        {satellites.map((sat) => (
          <option key={sat} value={sat}>
            {sat}
          </option>
        ))}
      </select>
    </div>
  );
}

export default SatelliteSelector;
