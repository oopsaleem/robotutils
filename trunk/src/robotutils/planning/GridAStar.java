/*
 *  The MIT License
 * 
 *  Copyright 2010 Prasanna Velagapudi <psigen@gmail.com>.
 * 
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 * 
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package robotutils.planning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import robotutils.data.GridMap;

/**
 * Implementation of the A* algorithm that solves the planning problem for
 * an N-dimensional lattice, where movement can only occur in the 2N cardinal
 * directions.
 * 
 * @author Prasanna Velagapudi <psigen@gmail.com>
 */
public class GridAStar extends AStar<GridAStar.Coords> {

    /**
     * A simple tuple class that correctly represents an integer coordinate
     * in arbitrary dimensions.  Equality, hashcode and comparisons are all
     * implemented as a lexical ordering over the integer array elements.
     */
    protected static final class Coords implements Comparable<Coords> {
        final int[] X;

        public Coords(int[] c) {
            this.X = Arrays.copyOf(c, c.length);
        }

        public int compareTo(Coords that) {
            if (that == null || this.X.length != that.X.length) {
                throw new IllegalArgumentException("Coordinate lengths don't match.");
            }

            for (int i = 0; i < this.X.length; i++) {
                if (this.X[i] != that.X[i]) {
                    return (that.X[i] - this.X[i]);
                }
            }

            return 0;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + Arrays.hashCode(this.X);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Coords)) {
                return false;
            }

            return Arrays.equals(this.X, ((Coords)obj).X);
        }

        /**
         * Returns Manhattan distance from this point to another.
         * @param that the other point
         * @return the Manhattan distance to the other point
         */
        public int mdist(Coords that) {
            if (that == null || this.X.length != that.X.length) {
                throw new IllegalArgumentException("Coordinate lengths don't match.");
            }

            int dist = 0;
            for (int i = 0; i < this.X.length; i++) {
                dist += Math.abs(that.X[i] - this.X[i]);
            }

            return dist;
        }

        /**
         * Returns Euclidean distance from this point to another.
         * @param that the other point
         * @return the Euclidean distance to the other point
         */
        public double edist(Coords that) {
            if (that == null || this.X.length != that.X.length) {
                throw new IllegalArgumentException("Coordinate lengths don't match.");
            }

            double dist = 0;
            for (int i = 0; i < this.X.length; i++) {
                dist += (that.X[i] - this.X[i])*(that.X[i] - this.X[i]);
            }

            return Math.sqrt(dist);
        }

        @Override
        public String toString() {
            String str = "(";
            for (int i = 0; i < X.length; i++) {
                str += X[i];
                if (i < X.length - 1) {
                    str += ",";
                }
            }
            str += ")";

            return str;
        }
    }

    GridMap _map;

    public GridAStar(GridMap map) {

        _map = map;
    }

    /**
     * Returns a list of neighbors to the current grid cell, excluding neighbor
     * cells that have negative cost values.
     * @param s the current cell
     * @return a list of neighboring cells
     */
    protected Collection<Coords> nbrs(Coords s) {
        List<Coords> nbrs = new ArrayList(2*_map.dims());

        for (int i = 0; i < _map.dims(); i++) {
            int[] up = Arrays.copyOf(s.X, s.X.length);
            up[i] += 1;
            if (_map.get(up) >= 0) {
                nbrs.add(new Coords(up));
            }

            int[] down = Arrays.copyOf(s.X, s.X.length);
            down[i] -= 1;
            if (_map.get(down) >= 0) {
                nbrs.add(new Coords(down));
            }
        }

        return nbrs;
    }

    @Override
    protected Collection<Coords> succ(Coords s) {
        return nbrs(s);
    }

    @Override
    protected double h(Coords a, Coords b) {
        return a.mdist(b);
    }

    @Override
    protected double c(Coords a, Coords b) {

        if (a.mdist(b) != 1) {
            return Double.POSITIVE_INFINITY;
        } else {
            double cA = _map.get(a.X);
            double cB = _map.get(b.X);
            return (cA + cB)/2.0 + 1.0;
        }
    }

}
