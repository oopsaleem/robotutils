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

package robotutils.examples;

import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.JGraphLayout;
import com.jgraph.layout.organic.JGraphFastOrganicLayout;
import java.awt.Color;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import org.jgraph.JGraph;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import robotutils.data.Coordinate;
import robotutils.data.GridMapGenerator;
import robotutils.data.GridMapUtils;
import robotutils.data.StaticMap;
import robotutils.planning.GraphAStar;

/**
 * Creates a randomized small worlds graph and solves a path between two random
 * locations using A-Star search.
 * @author Prasanna Velagapudi <psigen@gmail.com>
 */
public class GraphAStarPlanning {
    public static Random rnd = new Random();
    public static final int GRAPH_SIZE = 100;

    public static void main(String args[]) {

        // Generate a random blocky map (using cellular automata rules)
        StaticMap sm = GridMapGenerator.createRandomMazeMap2D(10, 10);
        Graph<Coordinate, ? extends DefaultEdge> g = GridMapUtils.toGraph(sm);
        Coordinate[] vertices = g.vertexSet().toArray(new Coordinate[0]);

        // Use jgraph to lay out the nodes in some reasonable way
        JGraphModelAdapter jgAdapter = new JGraphModelAdapter( g );
        JGraph jgraph = new JGraph( jgAdapter );

        JGraphFacade facade = new JGraphFacade( jgraph );
        JGraphLayout layout = new JGraphFastOrganicLayout();
        layout.run( facade );
        Map nested = facade.createNestedMap(true, true);
        jgraph.getGraphLayoutCache().edit(nested);
        
        // Create a display panel to draw the results
        JFrame jf = new JFrame("Graph");
        jf.setBounds(10, 10, 810, 610);
        jf.getContentPane().add(new JScrollPane(jgraph));
        jf.setVisible(true);

        // Find a random start location
        int startIdx = rnd.nextInt(vertices.length);
        Coordinate start = vertices[startIdx];

        // Find a random goal location
        int goalIdx = rnd.nextInt(GRAPH_SIZE);
        Coordinate goal = vertices[goalIdx];

        // Print and display start and goal locations
        System.out.println("Picked endpoints: " + start + "->" + goal);

        Map nestedEndpts = new Hashtable();

        Map attrStart = jgraph.getAttributes(jgAdapter.getVertexCell(start));
        GraphConstants.setBackground(attrStart, Color.GREEN);
        nestedEndpts.put(jgAdapter.getVertexCell(start), attrStart);

        Map attrGoal = jgraph.getAttributes(jgAdapter.getVertexCell(goal));
        GraphConstants.setBackground(attrGoal, Color.RED);
        nestedEndpts.put(jgAdapter.getVertexCell(goal), attrGoal);

        jgraph.getGraphLayoutCache().edit(nestedEndpts, null, null, null);
        jgraph.refresh();
        

//        // Perform A* search
//        GraphAStar astar = new GraphAStar(g) {
//
//            @Override
//            protected double h(Object a, Object b) {
//                throw new UnsupportedOperationException("Not supported yet.");
//            }
//        };
//
//        List<? extends Coordinate> path = astar.search(start, goal);

//        // Print and display resulting lowest cost path
//        if (path.isEmpty()) {
//            System.out.println("No path found!");
//        } else {
//            System.out.println("Solution path: " + path);
//
//            for (int i = 1; i < path.size() - 1; i++) {
//                Coordinate c = path.get(i);
//                mp.setDotIcon("p" + i, Color.BLUE, 11, 11, c.get(0) + 0.5, c.get(1) + 0.5, 0.05);
//            }
//        }
    }
}
