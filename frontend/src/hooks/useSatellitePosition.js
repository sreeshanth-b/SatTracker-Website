import { useEffect, useState } from "react";
import { getPosByCity } from "../api/orbitApi";

export function useSatellitePosition(satellite, city) {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!satellite || !city) return;

    const fetchData = async () => {
      try {
        const res = await getPosByCity(satellite, city);
        setData(res);
        setError(null);
      } catch (err) {
        setError(err.message);
      }
    };

    fetchData();
    const id = setInterval(fetchData, 2000);

    return () => clearInterval(id);
  }, [satellite, city]);

  return { data, error };
}
