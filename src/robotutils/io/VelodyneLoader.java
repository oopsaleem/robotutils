/*
 *  Copyright (c) 2009, Prasanna Velagapudi <pkv@cs.cmu.edu>
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the project nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ''AS IS'' AND ANY
 *  EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE PROJECT AND CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package robotutils.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;
import robotutils.Pose3D;

/**
 * Contains a script to load velodyne data from some random format.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class VelodyneLoader {
    private static Pattern linePat = Pattern.compile(" *, *");

    BufferedReader state;
    BufferedReader laser;

    Vehicle curState = new Vehicle();

    static class Scan {
        public double time;
        public Pose3D ray;
    }

    static class Vehicle {
        public double time;
        public Pose3D pose;
    }

    public static class Ray {
        double time;
        double pos[];
        double ray[];
    }

    public void load(String stateFile, String laserFile) throws FileNotFoundException {
        state = new BufferedReader(new FileReader(stateFile));
        laser = new BufferedReader(new FileReader(laserFile));

        curState.time = Double.NEGATIVE_INFINITY;
    }

    public Ray step() {
        try {
            // Get next laser scan
            Scan curScan = parseScan(laser.readLine());

            while (curState.time < curScan.time) {
                curState = parseState(state.readLine());
            }

            // Get vehicle state at time of next laser scan
            Ray ray = new Ray();
            ray.time = curScan.time;
            ray.pos = curState.pose.getPosition();
            ray.ray = curState.pose.getRotation().toRotation().operate(curScan.ray.getPosition());

            // Return next laser scan and state
            return ray;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    Scan parseScan(String line) {
        String[] s = linePat.split(line);
        
        Scan sc = new Scan();
        sc.time = Double.parseDouble(s[0]);
        
        double x = Double.parseDouble(s[5]);
        double y = Double.parseDouble(s[6]);
        double z = Double.parseDouble(s[7]);
        
        sc.ray = new Pose3D(x, y, z, 0.0, 0.0, 0.0);
        return sc;
    }

    Vehicle parseState(String line) {
        String[] s = linePat.split(line);
        
        Vehicle veh = new Vehicle();
        veh.time = Double.parseDouble(s[0]);

        double x = Double.parseDouble(s[1]);
        double y = Double.parseDouble(s[2]);
        double z = Double.parseDouble(s[3]);

        double yaw = Double.parseDouble(s[4]);
        double pitch = Double.parseDouble(s[5]);
        double roll = Double.parseDouble(s[6]);

        veh.pose = new Pose3D(x, y, z, roll, pitch, yaw);
        return veh;
    }

    public static void main(String[] args) throws IOException {
        String vehFile = "/Users/pkv/Desktop/velodyne/VehicleState.csv";
        String veloFile = "/Users/pkv/Desktop/velodyne/velodyne_1190939875_091112.txt";

        VelodyneLoader vl = new VelodyneLoader();
        vl.load(vehFile, veloFile);

        Ray r;
        int i = 0;
        while ((r = vl.step()) != null) {
            System.out.println(":" + i++ + ": " + Arrays.toString(r.pos) + ", " + Arrays.toString(r.ray));
        }
    }
}
