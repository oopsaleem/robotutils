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

package robotutils.examples;

import java.util.List;
import javax.swing.JFrame;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.UnmodifiableGraph;
import robotutils.data.Coordinate;
import robotutils.data.GridMapGenerator;
import robotutils.data.GridMapUtils;
import robotutils.data.IntCoord;
import robotutils.data.StaticMap;
import robotutils.gui.MapPanel;
import robotutils.planning.AStar;
import robotutils.planning.EdgeDistance;

/**
 * Creates a randomized 2D map and solves a path between two random locations
 * using A-Star search.
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class AStarPlanning {
    public static void main(String args[]) {
        StaticMap sm = GridMapGenerator.createRandomMazeMap2D(100, 100);
        
        MapPanel mp = new MapPanel();
        mp.setMapImage(GridMapUtils.toImage(sm));

        JFrame jf = new JFrame("Map");
        jf.setBounds(10, 10, 810, 610);
        jf.getContentPane().add(mp);
        jf.setVisible(true);

        Coordinate start = new IntCoord(0, 0);
        Coordinate goal = new IntCoord(50, 30);
        
        UnmodifiableGraph<Coordinate, DefaultWeightedEdge> graph = GridMapUtils.toGraph(sm);
        System.out.println("Made Graph");

        List<DefaultWeightedEdge> path = AStar.search(graph,
                new GridMapUtils.ManhattanDistance(),
                new GridMapUtils.GraphDistance<DefaultWeightedEdge>(graph),
                start, goal);
        System.out.println("Done: " + path);

        EdgeDistance d = new GridMapUtils.GraphDistance<DefaultWeightedEdge>(graph);
        for (DefaultWeightedEdge e : path) {
            Coordinate a = graph.getEdgeSource(e);
            Coordinate b = graph.getEdgeTarget(e);

            System.out.println("D: " + d.distance(e));

            mp.getGraphics().drawLine((int)a.get(0), (int)a.get(1), (int)b.get(0), (int)b.get(1));
        }
    }
}
