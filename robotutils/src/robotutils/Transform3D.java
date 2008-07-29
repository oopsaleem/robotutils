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

/**
 * A 3D homogenous transformation class.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class Transform3D extends Matrix {

    /**
     * A static representation of the identity matrix used for new matrix
     * construction.
     */
    private static final double[][] identity = {
        {1, 0, 0, 0},
        {0, 1, 0, 0},
        {0, 0, 1, 0},
        {0, 0, 0, 1}
    };

    /**
     * Constructs a 4 x 4 identity matrix.
     */
    public Transform3D() {
        super(identity, 4, 4);
    }

    /**
     * Constructs a 4 x 4 homogeneous transformation matrix.
     * @param m a two-dimensional array of doubles.
     */
    public Transform3D(double[][] m) {
        // Create a matrix around existing array
        super(m, 4, 4);
    }

    /**
     * Constructs a 4 x 4 homogeneous transformation matrix.
     * @param m a one-dimensional array of doubles, packed by <b>columns</b>.
     */
    public Transform3D(double[] m) {
        // Create a matrix around existing array
        super(m, 4);

        // Throw error if matrix is the wrong size
        if (m.length != 16) {
            throw new IllegalArgumentException(
                    "3D homogeneous transformation matrix must be 4 x 4.");
        }
    }

    public static Transform3D getXRotation(double theta) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public static Transform3D getYRotation(double theta) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public static Transform3D getZRotation(double theta) {
        return new Transform3D(new double[][]{
                    {Math.cos(theta), -Math.sin(theta), 0, 0},
                    {Math.sin(theta), Math.cos(theta), 0, 0},
                    {0, 0, 1, 0},
                    {0, 0, 0, 1}
                });
    }

    public static Transform3D getTranslation(double tx, double ty, double tz) {
        return new Transform3D(new double[][]{
                    {1, 0, 0, tx},
                    {0, 1, 0, ty},
                    {0, 0, 1, tz},
                    {0, 0, 0, 1}
                });
    }

    public static Transform3D getScaling(double sx, double sy, double sz) {
        return new Transform3D(new double[][]{
                    {sx, 0, 0, 0},
                    {0, sy, 0, 0},
                    {0, 0, sz, 0},
                    {0, 0, 0, 1}
                });
    }
}
