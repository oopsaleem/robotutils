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

import java.util.Scanner;
import robotutils.Pose3D;

/**
 * Contains a script to load velodyne data from some random format.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class VelodyneLoader {
    Scanner state;
    Scanner laser;

    public static class Scan {
        public double time;
        public double dist;
        public double phi;
        public double theta;
    }

    public static class Vehicle {
        public double time;
        public Pose3D pose;
    }

    public void load(String stateFile, String laserFile) {
        state = new Scanner(stateFile);
        laser = new Scanner(laserFile);

        double time = Double.NEGATIVE_INFINITY;
    }

    public Object step() {
        // If there are no more laser scans, just return null
        if (!laser.hasNextLine()) return null;

        // Get next laser scan

        // Get vehicle state at time of next laser scan

        // Return next laser scan and state
        return null;
    }

    public Scan parseScan(String line) {
        Scanner s = new Scanner(line);
        s.useDelimiter(",");

        Scan sc = new Scan();
        sc.time = s.nextDouble();
        sc.dist = s.nextDouble();
        sc.phi = s.nextDouble();
        sc.theta = s.nextDouble();
        return sc;
    }

    public Vehicle parseState(String line) {
        Scanner s = new Scanner(line);
        s.useDelimiter(",");

        Vehicle veh = new Vehicle();
        veh.time = s.nextDouble();

        double x = s.nextDouble();
        double y = s.nextDouble();
        double z = s.nextDouble();

        double yaw = s.nextDouble();
        double pitch = s.nextDouble();
        double roll = s.nextDouble();

        veh.pose = new Pose3D(x, y, z, roll, pitch, yaw);
        return veh;
    }
}
