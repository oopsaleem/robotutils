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
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR Action PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE PROJECT AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package robotutils.planning;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public abstract class BiRRT<State, Action> {
    
    public static int DEFAULT_ITERATION_LIMIT = 2000;

    public static int TRAPPED = -2;
    public static int ADVANCED = -1;
    public static int REACHED = 0;

    protected final class Node {
        public Node(State state, Action action, int parent) {
            _state = state;
            _action = action;
            _parent = parent;
        }

        public final State _state;
        public final Action _action;
        protected final int _parent;
    }

    protected abstract double distance(State a, State b);
    protected abstract Map.Entry<Action, State> newState(State x, State xNear);
    protected abstract State randomState();

    protected int nearestNeighbor(List<Node> t, State x) {
        double minDist = Double.MAX_VALUE;
        int minIdx = -1;

        Node curNode = null;
        double curDist;

        for (int idx = 0; idx < t.size(); idx++) {
            curNode = t.get(idx);
            curDist = distance(curNode._state, x);

            if (curDist < minDist) {
                minIdx = idx;
                minDist = curDist;
            }
        }

        assert(minIdx >= 0);
        return minIdx;
    }

    protected int extend(List<Node> t, State x) {
        int iNear = nearestNeighbor(t, x);

        Node xuNew = sampler.extend(t.get(iNear).state, x);
        if (xuNew != null) {
            t.add(new Node(xuNew.state, xuNew.action, iNear));

            if (xuNew._state.equals(x)) {
                return REACHED;
            } else {
                return ADVANCED;
            }
        }

        return TRAPPED;
    }

    /**
     * Generates a path from two tree lists in a BiRRT.  Assumes that the last
     * node in the A tree was the one that matched.
     * @param tA the start tree (rooted at the starting node)
     * @param tB the goal tree (rooted at the goal node)
     * @return a list of nodes that connect the two trees.
     */
    private List<Node> path(List<Node> tA, List<Node> tB) {
        
        LinkedList<Node> path = new LinkedList<Node>();

        // Add path to starting node
        Node nA = tA.get(tA.size() - 1);
        path.add(nA);
        while (nA._parent > 0) {
            nA = tB.get(nA._parent);
            path.addFirst(nA);
        }

        // Add path to ending node
        Node nB = tB.get(tB.size() - 1);
        while (nB._parent > 0) {
            nB = tB.get(nB._parent);
            path.addLast(nB);
        }

        return path;
    }

    public List plan(State init, State goal, int iterations) {

        List<Node> tA = new ArrayList();
        tA.add(new Node(init, null, -1));

        List<Node> tB = new ArrayList();
        tB.add(new Node(goal, null, -1));

        for (int k = 0; k < iterations; k++) {
            State xRand = randomState();
            State xNew = null;

            if (!(extend(tA, xRand) == TRAPPED)) {
                if (extend(tB, xNew) == REACHED) {
                    return path(tA, tB);
                }
            }

            // SWAP(Ta, Tb);
            List<Node> tmp = tA;
            tA = tB;
            tB = tmp;
        }

        return null;
    }
}
