const BASE_URL = "http://localhost:8080/api/orbit";

const enc = (v) => encodeURIComponent(v);

export async function getPosByCity(satellite, city) {
  const res = await fetch(
    `${BASE_URL}/pos/${enc(satellite)}?city=${enc(city)}`
  );

  if (!res.ok) {
    throw new Error("Failed to fetch satellite position");
  }

  return res.json();
}

export async function getOrbitTrack(satellite) {
  const res = await fetch(
    `${BASE_URL}/track/${enc(satellite)}`
  );

  if (!res.ok) {
    throw new Error("Failed to fetch orbit track");
  }

  return res.json();
}
