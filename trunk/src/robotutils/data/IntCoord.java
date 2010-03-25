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

package robotutils.data;

import java.util.Arrays;

/**
 *
 * @author Prasanna Velagapudi <psigen@gmail.com>
 */
public class IntCoord implements Coordinate {

    final int[] _coords;

    public IntCoord(int... values) {
        _coords = Arrays.copyOf(values, values.length);
    }

    public int[] get() {
        return _coords;
    }

    public double get(int dim) {
        return (double)_coords[dim];
    }

    public int dims() {
        return _coords.length;
    }

    @Override
    public String toString() {
        return "IntCoord[" + Arrays.toString(_coords) + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IntCoord) {
            IntCoord that = (IntCoord)obj;
            return Arrays.equals(this._coords, that._coords);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.hashCode(this._coords);
        return hash;
    }
}
