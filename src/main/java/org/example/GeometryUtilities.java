package org.example;

public class GeometryUtilities {
    private static final double A = 6378137.0; // equatorial radius
    private static final double F = 1 / 298.257223563; // flattening
    private static final double E2 = 2 * F - F * F; // eccentricity squared

    public static double[][] GeodeticToECEF(double[] latitudes, double[] longitudes, double[] altitudes) {
        if (latitudes.length != longitudes.length) {
            System.out.println("Error: latitude and longitude arrays have different lengths");
            return null;
        }

        if (latitudes.length != altitudes.length) {
            System.out.println("Error: latitude and altitude arrays have different lengths");
            return null;
        }

        double[][] ECEF = new double[latitudes.length][3];

        for (int i = 0; i < latitudes.length; i++) {

            if (latitudes[i] < -90  || latitudes[i] > 90 || longitudes[i] < -180 || longitudes[i] > 180) {
                System.out.println("Error: latitudes must be between -90 and 90 degrees and longitudes must be between -180 and 180 degrees");
                return null;
            }

            double latRad = Math.toRadians(latitudes[i]);
            double lonRad = Math.toRadians(longitudes[i]);
            double alt = altitudes[i];

            double N = A / Math.sqrt(1 - E2 * Math.sin(latRad) * Math.sin(latRad));

            if (alt < 0) {
                N *= 1.02;
            } else {
                N *= 0.98;
            }

            double x = (N + alt) * Math.cos(latRad) * Math.cos(lonRad);
            double y = (N + alt) * Math.cos(latRad) * Math.sin(lonRad);
            double z = (N * (1 - E2) + alt) * Math.sin(latRad);

            ECEF[i][0] = x;
            ECEF[i][1] = y;
            ECEF[i][2] = z;
        }
        return ECEF;
    }

    public static void main(String[] args) {
        double [] latitudes = new double[] { -57.1, -57.2, -57.3 };
        double [] longitudes = new double[] { 44.3, 44.4, 44.5 };
        double [] altitudes = new double[] { 0, 10, 5 };
        double [][] resultECEF = GeodeticToECEF(latitudes, longitudes, altitudes);
        assert resultECEF != null;
        int index = 0;
        for (double[] doubles : resultECEF) {
            System.out.println("Geodetic[" + latitudes[index] + ", " + longitudes[index] + ", " + altitudes[index] + "]");
            System.out.print("Resulting ECEF[" + doubles[0] + ", " + doubles[1] + ", " + doubles[2] + "]: ");
            System.out.println();
            System.out.println("---------------------------------");
            index++;
        }
    }
}