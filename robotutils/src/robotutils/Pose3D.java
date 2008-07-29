/*
 * Copyright (c) 2008, Prasanna Velagapudi <pkv@cs.cmu.edu>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the project nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE PROJECT AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package robotutils;

import Jama.Matrix;
import java.io.Serializable;

/**
 * Immutable 6DOF pose estimate for 3D object.
 * Math adapted from www.euclideanspace.com.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class Pose3D implements Cloneable, Serializable {
    /**
     * This defines the north pole singularity cutoff when converting 
     * from quaternions to Euler angles.
     */
    public static final double SINGULARITY_NORTH_POLE = 0.49999;
    
    /**
     * This defines the south pole singularity cutoff when converting 
     * from quaternions to Euler angles.
     */
    public static final double SINGULARITY_SOUTH_POLE = -0.49999;
    
    /**
     * Determines if a de-serialized file is compatible with this class.
     *
     * Maintainers must change this value if and only if the new version
     * of this class is not compatible with old versions. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html> details. </a>
     */
    public static final long serialVersionUID = 1L;
    
    /**
     * 3D position vector in [x y z] form.
     */
    private final double[] position;
    
    /**
     * 4D rotation quaternion in [w x y z] form.
     */
    private final double[] rotation;
    
    /** 
     * Constructs a new pose.
     * @param position the 3D position of the robot.
     * @param rotation either a 4D quaternion or a 3D roll-pitch-yaw vector.
     */
    public Pose3D(double[] position, double[] rotation) {
        // Fail if position matrix does not match expected size
        if (position.length != 3) 
            throw new IllegalArgumentException();
        this.position = position;
        
        // If we get three rotation params, assume we got RPY format
        if (rotation.length == 3) {
            // convert from RPY to quaternion
            this.rotation = convertEulerToQuat(rotation);
        } else if (rotation.length == 4) {
            // already in quaternion format
            this.rotation = rotation;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    /** 
     * Constructs a new pose from a homogeneous transformation.
     * @param transform a 4x4 homogeneous absolute transformation.
     */
    public Pose3D(Matrix transform) {
        // Fail if transformation matrix does not match expected size
        if ( (transform.getRowDimension() != 4) 
                || (transform.getColumnDimension() != 4) )
            throw new IllegalArgumentException();
        
        this.position = new double[] {
            transform.get(0,3),
            transform.get(1,3),
            transform.get(2,3)
        };
        this.rotation = convertHTToQuat(transform);
    }
    
    /** 
     * Constructs a new pose.
     * @param x the X position of the robot.
     * @param y the Y position of the robot.
     * @param z the Z position of the robot.
     * @param qw the scalar component of the quaternion
     * @param qx the "i" component of the quaternion
     * @param qy the "j" component of the quaternion
     * @param qz the "k" component of the quaternion
     */
    public Pose3D(double x, double y, double z, 
            double qw, double qx, double qy, double qz) {
        this(new double[] {x,y,z}, new double[] {qw, qx, qy, qz});
    }
    
    /** 
     * Constructs a new pose.
     * @param x the X position of the robot.
     * @param y the Y position of the robot.
     * @param z the Z position of the robot.
     * @param roll the positive X axis rotation of the robot.
     * @param pitch the positive Y axis rotation of the robot.
     * @param yaw the positive Z axis rotation of the robot.
     */
    public Pose3D(double x, double y, double z, 
            double roll, double pitch, double yaw) {
        this(new double[] {x,y,z}, new double[] {roll, pitch, yaw});
    }
    
    /**
     * Accessor for the X position of the robot.
     * @return the X position of the robot.
     */
    public double getX() { return position[0]; }
    
    /**
     * Accessor for the Y position of the robot.
     * @return the Y position of the robot.
     */
    public double getY() { return position[1]; }
    
    /**
     * Accessor for the Z position of the robot.
     * @return the Z position of the robot.
     */
    public double getZ() { return position[2]; }
    
    /**
     * Accessor for the position of the robot.
     * @return the 3D position of the robot.
     */
    public double[] getPosition() { return position.clone(); }
    
    /**
     * Accessor for the position of the robot in Jama matrix form.
     * @return the 3D position of the robot as a Jama matrix.
     */
    public Matrix getPositionVector() { return new Matrix(position, 3); }
    
    /**
     * Accessor for the roll of the robot.
     * @return the roll position of the robot.
     */
    public double getRoll() {
        // This is a test for singularities
        double test = rotation[1]*rotation[2] + rotation[3]*rotation[0];
        
        // Special case for north pole
        if (test > SINGULARITY_NORTH_POLE)
            return 0;
        
        // Special case for south pole
        if (test < SINGULARITY_SOUTH_POLE)
            return 0;
            
        return Math.atan2( 
                    2*rotation[1]*rotation[0] - 2*rotation[2]*rotation[3],
                    1 - 2*rotation[1]*rotation[1] - 2*rotation[3]*rotation[3]
                ); 
    }
    
    /**
     * Accessor for the pitch of the robot.
     * @return the pitch of the robot.
     */
    public double getPitch() { 
        // This is a test for singularities
        double test = rotation[1]*rotation[2] + rotation[3]*rotation[0];
        
        // Special case for north pole
        if (test > SINGULARITY_NORTH_POLE)
            return Math.PI/2;
        
        // Special case for south pole
        if (test < SINGULARITY_SOUTH_POLE)
            return -Math.PI/2;
        
        return Math.asin(2*test); 
    }
    
    /**
     * Accessor for the yaw of the robot.
     * @return the yaw of the robot.
     */
    public double getYaw() {
        // This is a test for singularities
        double test = rotation[1]*rotation[2] + rotation[3]*rotation[0];
        
        // Special case for north pole
        if (test > SINGULARITY_NORTH_POLE)
            return 2 * Math.atan2(rotation[1], rotation[0]);
        
        // Special case for south pole
        if (test < SINGULARITY_SOUTH_POLE)
            return -2 * Math.atan2(rotation[1], rotation[0]);
        
        return Math.atan2(
                    2*rotation[2]*rotation[0] - 2*rotation[1]*rotation[3],
                    1 - 2*rotation[2]*rotation[2] - 2*rotation[3]*rotation[3]
                ); 
    }
    
    /**
     * Accessor for the quaternion of the robot orientation in [w x y z] form.
     * @return the 4D quaternion representing robot orientation.
     */
    public double[] getRotation() { return rotation.clone(); }
    
    /**
     * Accessor for the quaternion of the robot orientation in [w x y z] form.
     * @return the 4D quaternion Jama matrix representing robot orientation.
     */
    public Matrix getRotationVector() { return new Matrix(rotation, 4); }
    
    /**
     * Accessor for the robot orientation as a rotation matrix.
     * @return a 3x3 rotation matrix representing robot orientation.
     */
    public Matrix getRotationMatrix() { return convertQuatToMat(rotation); }
    
    /** 
     * Accessor for the robot orientation as a homogenous transformation.
     * @return a 4x4 homogeneous tranformation matrix for robot orientation.
     */
    public Matrix getRotationTransform() { return convertQuatToHT(rotation); }
    
    /** 
     * Accessor for the robot pose as a homogenous transformation.
     * @return a 4x4 homogeneous tranformation matrix for robot pose.
     */
    public Matrix getTransform() {
        Matrix ht = convertQuatToHT(rotation); 
        ht.set(0, 3, position[0]);
        ht.set(1, 3, position[1]);
        ht.set(2, 3, position[2]);
        return ht;
    }
    
    /** 
     * Computes the square of the Euclidean distance between this pose and 
     * the specified pose.  This distance is solely based on 3D position, and
     * does not compare the rotations of the two poses.  
     * 
     * Fields contaning NaN are ignored in the computation.
     * @param p the pose to which distance is calculated
     * @return the square of the Euclidean distance between positions.
     */
    public double getEuclideanDistanceSqr(Pose3D p) {
        double dist = 0.0;
        double tmpDiff = 0.0;
        
        for (int i = 0; i < position.length; i++) {
            // Discount fields that are NaN
            if (Double.isNaN(position[i]) || Double.isNaN(position[i]))
                continue;
            
            // Take the square of the distance in each dimension
            tmpDiff = (position[i] - p.position[i]);
            dist += tmpDiff * tmpDiff;
        }
        
        return dist;
    }
    
    /** 
     * Computes the Euclidean distance between this pose and the specified pose.
     * This distance is solely based on 3D position, and does not compare the 
     * rotations of the two poses.  
     * 
     * Fields contaning NaN are ignored in the computation.  If this computation
     * is being compared to a constant, consider using getEuclideanDistance,
     * as it is faster.
     * @param p the pose to which distance is calculated
     * @return the Euclidean distance between positions.
     */
    public double getEuclideanDistance(Pose3D p) {
        return Math.sqrt(getEuclideanDistanceSqr(p));
    }
    
    /**
     * Checks whether this pose matches the other given pose given rounding 
     * errors and wildcard fields (NaN).
     * 
     * Note that this comparison is reflexive and symmetric, but not transitive.
     * @param p the pose against which to compare.
     * @return true if the poses are equivalent, false otherwise.
     */
    public boolean isEquivalent(Pose3D p) {
        //TODO: finish this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object) 
     */
    public boolean equals(Pose3D p) {
        //TODO: finish this method properly
        if (Math.abs(this.getX() - p.getX()) > 1e-5) return false;
        if (Math.abs(this.getY() - p.getY()) > 1e-5) return false;
        if (Math.abs(this.getZ() - p.getZ()) > 1e-5) return false;
        if (Math.abs(this.getRoll() - p.getRoll()) > 1e-5) return false;
        if (Math.abs(this.getPitch() - p.getPitch()) > 1e-5) return false;
        if (Math.abs(this.getYaw() - p.getYaw()) > 1e-5) return false;
        return true;
    }
    
    private static Matrix convertQuatToMat(double[] quat) {
        double[][] m = new double[3][3];
        
        // Compute necessary components
        double xx = quat[1] * quat[1];
        double xy = quat[1] * quat[2];
        double xz = quat[1] * quat[3];
        double xw = quat[1] * quat[0];
        double yy = quat[2] * quat[2];
        double yz = quat[2] * quat[3];
        double yw = quat[2] * quat[0];
        double zz = quat[3] * quat[3];
        double zw = quat[0] * quat[0];
        
        // Compute rotation tranformation
        // Compute rotation tranformation
        m[0][0] = 1 - 2 * ( yy + zz );
        m[0][1] =     2 * ( xy - zw );
        m[0][2] =     2 * ( xz + yw );
        m[1][0] =     2 * ( xy + zw );
        m[1][1] = 1 - 2 * ( xx + zz );
        m[1][2] =     2 * ( yz - xw );
        m[2][0] =     2 * ( xz - yw );
        m[2][1] =     2 * ( yz + xw );
        m[2][2] = 1 - 2 * ( xx + yy );
        
        // Put into Jama format
        return new Matrix(m);
    }
    
    private static Matrix convertQuatToHT(double[] quat) {
        double[][] m = new double[4][4];
        
        // Compute necessary components
        double xx = quat[1] * quat[1];
        double xy = quat[1] * quat[2];
        double xz = quat[1] * quat[3];
        double xw = quat[1] * quat[0];
        double yy = quat[2] * quat[2];
        double yz = quat[2] * quat[3];
        double yw = quat[2] * quat[0];
        double zz = quat[3] * quat[3];
        double zw = quat[3] * quat[0];
        
        // Compute rotation tranformation
        m[0][0] = 1 - 2 * ( yy + zz );
        m[0][1] =     2 * ( xy - zw );
        m[0][2] =     2 * ( xz + yw );
        m[1][0] =     2 * ( xy + zw );
        m[1][1] = 1 - 2 * ( xx + zz );
        m[1][2] =     2 * ( yz - xw );
        m[2][0] =     2 * ( xz - yw );
        m[2][1] =     2 * ( yz + xw );
        m[2][2] = 1 - 2 * ( xx + yy );
        m[0][3] = m[1][3] = m[2][3] = m[3][0] = m[3][1] = m[3][2] = 0;
        m[3][3] = 1;
        
        // Put into Jama format
        return new Matrix(m);
    }
    
    private static double[] convertHTToQuat(Matrix ht) {
        double[] rot = new double[4];
        double[][] m = ht.getArray();
        
        // Recover the magnitudes
        rot[0] = Math.sqrt( Math.max( 0, 1 + m[0][0] + m[1][1] + m[2][2] ) ) / 2; 
        rot[1] = Math.sqrt( Math.max( 0, 1 + m[0][0] - m[1][1] - m[2][2] ) ) / 2; 
        rot[2] = Math.sqrt( Math.max( 0, 1 - m[0][0] + m[1][1] - m[2][2] ) ) / 2; 
        rot[3] = Math.sqrt( Math.max( 0, 1 - m[0][0] - m[1][1] + m[2][2] ) ) / 2; 
        
        // Recover sign information
        rot[1] *= Math.signum( m[2][1] - m[1][2] ); 
        rot[2] *= Math.signum( m[0][2] - m[2][0] );
        rot[3] *= Math.signum( m[1][0] - m[0][1] ); 
        
        return rot;
    }
    
    private static double[] convertEulerToQuat(double[] rpy) {
        double quat[] = new double[4];
        
        // Apply Euler angle transformations
        // Derivation from www.euclideanspace.com
        double c1 = Math.cos(rpy[2]/2.0);
        double s1 = Math.sin(rpy[2]/2.0);
        double c2 = Math.cos(rpy[1]/2.0);
        double s2 = Math.sin(rpy[1]/2.0);
        double c3 = Math.cos(rpy[0]/2.0);
        double s3 = Math.sin(rpy[0]/2.0);
        double c1c2 = c1*c2;
        double s1s2 = s1*s2;
        
        // Compute quaternion from components
        quat[0] = c1c2*c3 - s1s2*s3;
        quat[1] = c1c2*s3 + s1s2*c3;
        quat[2] = s1*c2*c3 + c1*s2*s3;
        quat[3] = c1*s2*c3 - s1*c2*s3;
        return quat;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pose3D)) return false;
        Pose3D p = (Pose3D)obj;
        
        double dx = p.getX() - this.getX();
        if (!Double.isNaN(dx) && dx > 1e-6) return false;
        
        double dy = p.getY() - this.getY();
        if (!Double.isNaN(dy) && dy > 1e-6) return false;
        
        double dz = p.getZ() - this.getY();
        if (!Double.isNaN(dz) && dz > 1e-6) return false;
        
        double droll = p.getRoll() - p.getRoll();
        if (!Double.isNaN(droll) && droll > 1e-6) return false;
        
        double dpitch = p.getPitch() - p.getPitch();
        if (!Double.isNaN(dpitch) && dpitch > 1e-6) return false;
        
        double dyaw = p.getYaw() - p.getYaw();
        if (!Double.isNaN(dyaw) && dyaw > 1e-6) return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.position != null ? this.position.hashCode() : 0);
        hash = 97 * hash + (this.rotation != null ? this.rotation.hashCode() : 0);
        return hash;
    }
    
    @Override
    public Object clone() {
        return new Pose3D(position, rotation);
    }
    
    @Override
    public String toString() {
        return "{" + getX() + ", " + getY() + ", " + getZ() + ", " 
                + getRoll() + ", " + getPitch() + ", " + getYaw() + "}";
    }
    
    /**
     * Converts the native 6D pose to an (x,y,theta) 3D representation.
     * @return a 3D mapper pose representation
     * @deprecated The Mapper.Pose2D is only 3D, thus its use is discouraged. 
     */
    @Deprecated
    public Pose2D convertToPose2D() {
        return new Pose2D(
                getX(), 
                getY(),
                getYaw());
    }
    
    /**
     * Executes a series of unit tests to ensure the functionality of the 
     * Pose2D class, mostly testing conversion functions.
     * @param args all command line arguments are ignored.
     */
    public static void main(String args[]) {
        Pose3D p = new Pose3D(100.0, 10.0, 20.0, 3.45, -2.34, 1.23);
        Pose3D q = new Pose3D(p.getTransform());

        Matrix m = p.getTransform();
        Matrix t = new Matrix(new double[][] {
            {-0.23248, -0.51489, -0.82512, 100.0},
            {-0.71846,  0.66274, -0.21113, 10.0},
            { 0.65556,  0.54374, -0.52400, 20.0},
            {0, 0, 0, 1}
            });
        Pose3D r = new Pose3D(t);
        
        System.out.println("The following poses should be similar:");
        System.out.println(p);
        System.out.println(q);
        System.out.println(r);
        
        System.out.println("");
        System.out.println("The following matrices should be similar:");
        m.print(4, 4);
        t.print(4, 4);   
    }
}
