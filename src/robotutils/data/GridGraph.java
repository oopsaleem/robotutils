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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.AbstractGraph;

/**
 * Creates a graph representation of an underlying grid data structure.
 * @author Prasanna Velagapudi <psigen@gmail.com>
 */
public class GridGraph extends AbstractGraph<int[], int[][]> {

    GridMap _map;

    public GridGraph(GridMap map) {
        _map = map;
    }

    public Set<int[][]> getAllEdges(int[] v, int[] v1) {

        int[][] edge = getEdge(v, v1);

        if (edge != null) {
            return Collections.singleton(edge);
        } else {
            return Collections.emptySet();
        }
    }

    public int[][] getEdge(int[] v, int[] v1) {
        int[][] edge = new int[][] {v, v1};

        if (containsEdge(edge)) {
            return edge;
        } else {
            return null;
        }
    }

    public EdgeFactory<int[], int[][]> getEdgeFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int[][] addEdge(int[] v, int[] v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addEdge(int[] v, int[] v1, int[][] e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addVertex(int[] v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean containsEdge(int[][] e) {
        if (e.length != 2) {
            return false;
        }

        if (!containsVertex(e[0])) {
            return false;
        }

        if (!containsVertex(e[1])) {
            return false;
        }

        int idxDiff = 0;
        for (int i = 0; i < _map.dims(); i++) {
            idxDiff += Math.abs(e[0][i] - e[1][i]);
        }

        if (idxDiff == 1) {
            return true;
        } else {
            return false;
        }
    }

    public boolean containsVertex(int[] v) {
        if (v.length != _map.dims()) {
            return false;
        }

        for (int i = 0; i < _map.dims(); i++) {
            if (v[i] < 0 || v[i] >= _map.size(i)) {
                return false;
            }
        }

        return true;
    }

    public Set<int[][]> edgeSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<int[][]> edgesOf(int[] v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int[][] removeEdge(int[] v, int[] v1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeEdge(int[][] e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeVertex(int[] v) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set<int[]> vertexSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int[] getEdgeSource(int[][] e) {
        return e[0];
    }

    public int[] getEdgeTarget(int[][] e) {
        return e[1];
    }

    public double getEdgeWeight(int[][] e) {
        return (double)(_map.get(e[0]) + _map.get(e[1]))/2.0;
    }
}
