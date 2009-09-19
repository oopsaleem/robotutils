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

package robotutils.planning;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Vector;
import org.jgrapht.graph.UnmodifiableGraph;

/**
 *
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class AStar {
    static final class Score<E> {
        E route = null;
        double g = Double.POSITIVE_INFINITY;
        double h = Double.POSITIVE_INFINITY;
        double f = Double.POSITIVE_INFINITY;
    }

    /**
     * Adapted from wikipedia entry
     */
    public static <V, E> List<E> search(UnmodifiableGraph<V,E> graph,
            NodeDistance<V> heuristic,
            EdgeDistance<E> metric,
            V start, V goal) {
        // Create open and closed sets, and a map to store node meta-info
        Vector<V> closed = new Vector<V>();
        PriorityQueue<V> open = new PriorityQueue<V>();
        HashMap<V, Score<E>> scores = new HashMap<V, Score<E>>();

        // Insert the start node into our search tree
        Score<E> startScore = new Score<E>();
        startScore.g = 0;
        startScore.h = heuristic.distance(start, goal);
        startScore.f = startScore.h;
        scores.put(start, startScore);

        // Search until we find a result or run out of nodes to explore
        while (!open.isEmpty()) {
            // Get the node at the top of the priority queue
            V x = open.poll();

            // If we reach the goal, traverse backwards to build a path
            if (x.equals(goal)) {
                LinkedList<E> path = new LinkedList<E>();
                V curr = goal;
                while (curr != start) {
                    Score<E> currScore = scores.get(curr);
                    path.addFirst(currScore.route);
                    curr = graph.getEdgeSource(currScore.route);
                }
                return path;
            }

            // The node is now closed -- no more searching it!
            closed.add(x);

            // Search each of this node's neighbors
            for (E edge : graph.outgoingEdgesOf(x)) {
                // Find the neighbor and make sure it has metadata
                V y = graph.getEdgeTarget(edge);
                if (!scores.containsKey(y)) scores.put(y, new Score());

                // If the neighbor was already searched, ignore it
                if (closed.contains(x)) continue;

                // Get the current estimate of the distance to goal
                double tentativeGScore = scores.get(x).g + metric.distance(edge);
                boolean tentativeIsBetter = false;

                // If the node is unopened, or we have a better score, update
                if (!open.contains(y)) {
                    open.add(y);
                    tentativeIsBetter = true;
                } else if (tentativeGScore < scores.get(y).g) {
                    tentativeIsBetter = true;
                }

                // Update the node with the new score
                if (tentativeIsBetter == true) {
                    Score yScore = scores.get(y);
                    yScore.route = edge;
                    yScore.g = tentativeGScore;
                    yScore.h = heuristic.distance(y, goal);
                    yScore.f = yScore.g + yScore.h;
                }
            }
        }

        // If we tried all nodes and still didn't find a path, there isn't one.
        return Collections.emptyList();
    }
}
