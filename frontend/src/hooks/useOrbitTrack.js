import { useEffect, useState } from "react";
import { getOrbitTrack } from "../api/orbitApi";

export function useOrbitTrack(satellite) {
  const [track, setTrack] = useState([]);

  useEffect(() => {
    if (!satellite) return;

    getOrbitTrack(satellite)
      .then(setTrack)
      .catch(console.error);
  }, [satellite]);

  return track;
}
