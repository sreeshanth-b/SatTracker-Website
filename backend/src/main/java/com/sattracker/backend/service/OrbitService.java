package com.sattracker.backend.service;

import com.sattracker.backend.model.*;
import org.orekit.bodies.*;
import org.orekit.frames.*;
import org.orekit.propagation.analytical.tle.*;
import org.orekit.time.*;
import org.orekit.utils.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrbitService {

    public SatellitePositionResponse computePosition(
            Tle tleEntity,
            GroundStation station,
            String utc
    ) {

        AbsoluteDate date =
                (utc != null && !utc.isBlank())
                        ? new AbsoluteDate(utc, TimeScalesFactory.getUTC())
                        : new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

        TLE tle = new TLE(tleEntity.getLine1(), tleEntity.getLine2());
        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        Frame teme = FramesFactory.getTEME();
        Frame itrf = FramesFactory.getITRF(
                IERSConventions.IERS_2010, true
        );

        PVCoordinates pvTEME =
                propagator.getPVCoordinates(date, teme);

        PVCoordinates pvECEF =
                teme.getTransformTo(itrf, date)
                        .transformPVCoordinates(pvTEME);

        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        // Satellite geographic position
        GeodeticPoint satPoint =
                earth.transform(pvECEF.getPosition(), itrf, date);

        // Ground station
        GeodeticPoint stationGeo =
                new GeodeticPoint(
                        Math.toRadians(station.getLatitude()),
                        Math.toRadians(station.getLongitude()),
                        station.getAltitude()
                );

        TopocentricFrame stationFrame =
                new TopocentricFrame(
                        earth, stationGeo, station.getName()
                );

        double azimuth = Math.toDegrees(
                stationFrame.getAzimuth(
                        pvECEF.getPosition(), itrf, date
                )
        );

        double elevation = Math.toDegrees(
                stationFrame.getElevation(
                        pvECEF.getPosition(), itrf, date
                )
        );

        double range =
                stationFrame.getRange(
                        pvECEF.getPosition(), itrf, date
                );

        SatellitePositionResponse res =
                new SatellitePositionResponse();

        res.setLatitude(Math.toDegrees(satPoint.getLatitude()));
        res.setLongitude(Math.toDegrees(satPoint.getLongitude()));
        res.setAltitude(satPoint.getAltitude());
        res.setVelocity(pvECEF.getVelocity().getNorm());
        res.setAzimuth(azimuth);
        res.setElevation(elevation);
        res.setRange(range/1000);
        res.setTimestamp(date.toString());

        return res;
    }

    public SatellitePassResponse computeNextPass(
            Tle tleEntity,
            GroundStation station
    ) {

        TLE tle = new TLE(
                tleEntity.getLine1(), tleEntity.getLine2()
        );

        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        Frame teme = FramesFactory.getTEME();
        Frame itrf = FramesFactory.getITRF(
                IERSConventions.IERS_2010, true
        );

        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        GeodeticPoint stationGeo =
                new GeodeticPoint(
                        Math.toRadians(station.getLatitude()),
                        Math.toRadians(station.getLongitude()),
                        station.getAltitude()
                );

        TopocentricFrame stationFrame =
                new TopocentricFrame(
                        earth, stationGeo, station.getName()
                );

        AbsoluteDate start =
                new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

        AbsoluteDate end = start.shiftedBy(24 * 3600);

        double prevEl = -90;
        AbsoluteDate aos = null;
        AbsoluteDate los = null;
        double maxEl = 0;

        for (AbsoluteDate t = start;
             t.compareTo(end) < 0;
             t = t.shiftedBy(10)) {

            PVCoordinates pv =
                    propagator.getPVCoordinates(t, teme);

            PVCoordinates pvECEF =
                    teme.getTransformTo(itrf, t)
                            .transformPVCoordinates(pv);

            double el = Math.toDegrees(
                    stationFrame.getElevation(
                            pvECEF.getPosition(), itrf, t
                    )
            );

            if (prevEl < 0 && el > 0 && aos == null) {
                aos = t;
            }

            if (prevEl > 0 && el < 0 && aos != null) {
                los = t;
                break;
            }

            maxEl = Math.max(maxEl, el);
            prevEl = el;
        }

        SatellitePassResponse res =
                new SatellitePassResponse();

        if (aos == null || los == null) {
            res.setAos(null);
            res.setLos(null);
            res.setMaxElevation(0);
            res.setDurationSec(0);
            res.setMessage("No satellite pass in next 24 hours");
            return res;
        }

        res.setAos(aos.toString());
        res.setLos(los.toString());
        res.setMaxElevation(maxEl);
        res.setDurationSec((long) los.durationFrom(aos));

        return res;
    }

    public RadarResponse computeLiveRadar(
            Tle tleEntity,
            GroundStation station
    ) {

        AbsoluteDate now =
                new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

        TLE tle = new TLE(
                tleEntity.getLine1(), tleEntity.getLine2()
        );

        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        Frame teme = FramesFactory.getTEME();
        Frame itrf = FramesFactory.getITRF(
                IERSConventions.IERS_2010, true
        );

        PVCoordinates pv =
                propagator.getPVCoordinates(now, teme);

        PVCoordinates pvECEF =
                teme.getTransformTo(itrf, now)
                        .transformPVCoordinates(pv);

        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        GeodeticPoint stationGeo =
                new GeodeticPoint(
                        Math.toRadians(station.getLatitude()),
                        Math.toRadians(station.getLongitude()),
                        station.getAltitude()
                );

        TopocentricFrame stationFrame =
                new TopocentricFrame(
                        earth, stationGeo, station.getName()
                );

        RadarResponse res = new RadarResponse();

        res.setAzimuth(Math.toDegrees(
                stationFrame.getAzimuth(
                        pvECEF.getPosition(), itrf, now
                )
        ));

        res.setElevation(Math.toDegrees(
                stationFrame.getElevation(
                        pvECEF.getPosition(), itrf, now
                )
        ));

        res.setVisible(res.getElevation() > 0);
        res.setTimestamp(now.toString());

        return res;
    }

    public List<SatelliteTrackPoint> computeOrbitTrack(
            Tle tleEntity
    ) {

        List<SatelliteTrackPoint> track = new ArrayList<>();

        TLE tle = new TLE(
                tleEntity.getLine1(), tleEntity.getLine2()
        );

        TLEPropagator propagator =
                TLEPropagator.selectExtrapolator(tle);

        Frame teme = FramesFactory.getTEME();
        Frame itrf = FramesFactory.getITRF(
                IERSConventions.IERS_2010, true
        );

        OneAxisEllipsoid earth =
                new OneAxisEllipsoid(
                        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                        Constants.WGS84_EARTH_FLATTENING,
                        itrf
                );

        AbsoluteDate start =
                new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

        AbsoluteDate end = start.shiftedBy(90 * 60);

        for (AbsoluteDate t = start;
             t.compareTo(end) < 0;
             t = t.shiftedBy(30)) {

            PVCoordinates pv =
                    propagator.getPVCoordinates(t, teme);

            PVCoordinates pvECEF =
                    teme.getTransformTo(itrf, t)
                            .transformPVCoordinates(pv);

            GeodeticPoint geo =
                    earth.transform(
                            pvECEF.getPosition(), itrf, t
                    );

            SatelliteTrackPoint p =
                    new SatelliteTrackPoint();

            p.setLatitude(Math.toDegrees(geo.getLatitude()));
            p.setLongitude(Math.toDegrees(geo.getLongitude()));
            p.setAltitude(geo.getAltitude());
            p.setTimestamp(t.toString());

            track.add(p);
        }

        return track;
    }


        public Map<String, Object> computeVisibleSatellites(
        List<Tle> tles,
        GroundStation station
) {

    List<VisibleSatelliteResponse> visible = new ArrayList<>();

    AbsoluteDate now =
            new AbsoluteDate(new Date(), TimeScalesFactory.getUTC());

    Frame teme = FramesFactory.getTEME();
    Frame itrf =
            FramesFactory.getITRF(IERSConventions.IERS_2010, true);

    OneAxisEllipsoid earth =
            new OneAxisEllipsoid(
                    Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                    Constants.WGS84_EARTH_FLATTENING,
                    itrf
            );

    GeodeticPoint stationGeo =
            new GeodeticPoint(
                    Math.toRadians(station.getLatitude()),
                    Math.toRadians(station.getLongitude()),
                    station.getAltitude()
            );

    TopocentricFrame stationFrame =
            new TopocentricFrame(earth, stationGeo, station.getName());

    for (Tle tleEntity : tles) {

        try {
            TLE tle = new TLE(
                    tleEntity.getLine1(),
                    tleEntity.getLine2()
            );

            TLEPropagator propagator =
                    TLEPropagator.selectExtrapolator(tle);

            PVCoordinates pv =
                    propagator.getPVCoordinates(now, teme);

            PVCoordinates pvEcef =
                    teme.getTransformTo(itrf, now)
                            .transformPVCoordinates(pv);

            double elevation = Math.toDegrees(
                    stationFrame.getElevation(
                            pvEcef.getPosition(), itrf, now)
            );

            if (elevation > 10) {

                VisibleSatelliteResponse res =
                        new VisibleSatelliteResponse();

                res.setName(tleEntity.getName());
                res.setElevation(elevation);
                res.setAzimuth(Math.toDegrees(
                        stationFrame.getAzimuth(
                                pvEcef.getPosition(), itrf, now)));
                res.setRange(
                        stationFrame.getRange(
                                pvEcef.getPosition(), itrf, now) / 1000.0
                ); // km

                visible.add(res);
            }

        } catch (Exception ignored) {}
    }

    Map<String, Object> response = new HashMap<>();
    response.put("count", visible.size());
    response.put("satellites", visible);
    response.put("timestamp", now.toString());

    return response;
}

}
