package com.andreydymko.recoginition1.MarkingCore;

import android.graphics.Point;

import java.util.Arrays;

public class PhysicalProperties {

    public static int[] findArea(MarkingCore.MarkingResult markingResult) {
        int[] areaForObject = new int[markingResult.getObjCount()];
        for (int[] arr : markingResult.getData()) {
            for (int value : arr) {
                if (value >= 0) {
                    areaForObject[value]++;
                }
            }
        }
        return areaForObject;
    }

    public static Point[] findCenterOfMass(MarkingCore.MarkingResult markingResult) {
        Point[] centerMassForObj = new Point[markingResult.getObjCount()];
        for (int i = 0; i < centerMassForObj.length; i++) {
            centerMassForObj[i] = new Point(0, 0);
        }
        int[][] data = markingResult.getData();
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] >= 0) {
                    centerMassForObj[data[i][j]].x += j;
                    centerMassForObj[data[i][j]].y += i;
                }
            }
        }
        int[] areaForObj = findArea(markingResult);
        for (int i = 0; i < centerMassForObj.length; i++) {
            centerMassForObj[i].x = (int) Math.round(centerMassForObj[i].x/ (double) areaForObj[i]);
            centerMassForObj[i].y = (int) Math.round(centerMassForObj[i].y/ (double) areaForObj[i]);
        }
        return centerMassForObj;
    }

    public static double[] findPerimeter(MarkingCore.MarkingResult markingResult) {
        int[][] data = markingResult.getData();
        int[] edgesForObject = new int[markingResult.getObjCount()];
        int[] diagonalsForObject = new int[markingResult.getObjCount()];
        int[] counters = new int[markingResult.getObjCount()];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j] >= 0) {
                    // data presented
                    if ((i == 0 || i == (data.length - 1) || j == 0 || j == (data[i].length - 1))) {
                        // if it's on border
                        if ((i == 0 || i == (data.length - 1)) && (j == 0 || j == (data[i].length - 1))) {
                            // corners
                            counters[data[i][j]] += 2;
                        } else {
                            // borders
                            counters[data[i][j]]++;
                        }
                    }
                } else {
                    // no data
                    if (i != 0 && data[i-1][j] >= 0) {
                        counters[data[i-1][j]]++;
                    }
                    if (j != 0 && data[i][j-1] >= 0) {
                        counters[data[i][j-1]]++;
                    }
                    if (j != (data[i].length - 1) && data[i][j+1] >= 0) {
                        counters[data[i][j+1]]++;
                    }
                    if (i != (data.length - 1) && data[i+1][j] >= 0) {
                        counters[data[i+1][j]]++;
                    }
                }
                for (int k = 0; k < counters.length; k++) {
                    switch (counters[k]) {
                        case 0:
                            break;
                        case 2:
                            // replacing edges with hypotenuse
                            diagonalsForObject[k]++;
                            break;
                        case 3:
                            // smoothing out sinkholes
                            edgesForObject[k]++;
                            break;
                        default:
                            edgesForObject[k] += counters[k];
                            break;
                    }
                }
                Arrays.fill(counters, 0);
            }
        }

        double[] perimeterForObject = new double[markingResult.getObjCount()];
        for (int i = 0; i < markingResult.getObjCount(); i++) {
            perimeterForObject[i] = edgesForObject[i] + diagonalsForObject[i]*Math.sqrt(2);
        }
        return perimeterForObject;
    }

    public static double[] findRoundnessFactor(MarkingCore.MarkingResult markingResult) {
        double[] perimeterForObj = findPerimeter(markingResult);
        int[] areaForObj = findArea(markingResult);
        double[] roundnessForObj = new double[markingResult.getObjCount()];
        for (int i = 0; i < roundnessForObj.length; i++) {
            roundnessForObj[i] = perimeterForObj[i]*perimeterForObj[i]/areaForObj[i];
        }
        return roundnessForObj;
    }

    public static PhysProps findAll(MarkingCore.MarkingResult markingResult) {
        return new PhysProps(findArea(markingResult), findCenterOfMass(markingResult),
                findPerimeter(markingResult), findRoundnessFactor(markingResult));
    }

    public static class PhysProps {
        int[] area;
        Point[] centerOfMass;
        double[] perimeter;
        double[] roundness;

        public PhysProps(int[] area, Point[] centerOfMass, double[] perimeter, double[] roundness) {
            this.area = area;
            this.centerOfMass = centerOfMass;
            this.perimeter = perimeter;
            this.roundness = roundness;
        }

        public int[] getArea() {
            return area;
        }

        public Point[] getCenterOfMass() {
            return centerOfMass;
        }

        public double[] getPerimeter() {
            return perimeter;
        }

        public double[] getRoundness() {
            return roundness;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(area.length*10*4);
            for (int i = 0; i < area.length; i++) {
                builder.append("Object ").append(i).append(":\n")
                        .append("Area = ").append(area[i]).append(";\n")
                        .append("Center of mass = ").append(centerOfMass[i]).append(";\n")
                        .append("Perimeter = ").append(perimeter[i]).append(";\n")
                        .append("Roundness = ").append(roundness[i]).append(";\n");
            }
            return builder.toString();
        }
    }
}
