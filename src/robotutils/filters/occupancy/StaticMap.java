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

package robotutils.filters.occupancy;

import java.util.Arrays;

/**
 * This is a map implementation that just uses a large internal array.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class StaticMap implements GridMap {
    byte[] map;
    int[] dims;
    int[] cumDims;

    public void resize(int[] dims) {
        this.dims = Arrays.copyOf(dims, dims.length);
        this.cumDims = new int[dims.length + 1];

        this.cumDims[0] = 1;
        for (int i = 1; i < dims.length; i++) {
            this.cumDims[i] = cumDims[i-1] * dims[i-1];
        }

        this.map = new byte[cumDims[dims.length - 1] * dims[dims.length - 1]];
    }

    int index(int[] idx) {
        int linIdx = 0;

        for (int i = 0; i < dims.length; i++) {
            if (idx[i] < 0) return -1;
            if (idx[i] >= dims[i]) return -1;

            linIdx += cumDims[i]*idx[i];
        }

        return linIdx;
    }

    public byte get(int[] idx) {
        int i = index(idx);
        return (i < 0) ? 0 : map[i];
    }

    public void set(int[] idx, byte val) {
        int i = index(idx);
        if (i < 0) return;
        map[i] = val;
    }

    public int size(int dim) {
        return dims[dim];
    }
}
