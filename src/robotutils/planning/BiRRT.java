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

class Condition {
    static int ADVANCED;
    static int TRAPPED;
    static int REACHED;
}

abstract class StateSampler<T0, T1> {

    static class StateCommandPair<T0, T1> {
        T0 state;
        T1 action;
        public StateCommandPair() {
        }
    }

    public abstract StateCommandPair extend(T0 a, T0 b);
    public abstract T0 random();
}

/**
 *
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class BiRRT<State, Action> {
    public static int DEFAULT_ITERATION_LIMIT = 2000;
    public static int TRAPPED = -2;
    public static int ADVANCED = -1;

    protected class Node {
        public Node(State s, Action a, int p) {
            state = s;
            parent = p;
            action = a;
        }

        public final State state;
        public final Action action;
        protected final int parent;
    }

    DistanceMetric<State> metric;
    StateSampler<State, Action> sampler;

    protected int getNearestNeighbor(List<Node> t, State x) {
        double minDist = Double.MAX_VALUE;
        int minIdx = -1;
        Node curNode = null;
        double curDist;

        for (int idx = 0; idx < t.size(); idx++) {
            curNode = t.get(idx);
            curDist = metric.distance(curNode.state, x);

            if (curDist < minDist) {
                minIdx = idx;
                minDist = curDist;
            }
        }

        assert(minIdx >= 0);
        return minIdx;
    }

    protected int extend(List<Node> t, State x) {
        int iNear = getNearestNeighbor(t, x);

        StateSampler.StateCommandPair<State, Action> xuNew = sampler.extend(t.get(iNear).state, x);
        if (xuNew != null) {
            t.add(new Node(xuNew.state, xuNew.action, iNear));

            if (xuNew.state == x) {
                return 0;
            } else {
                return Condition.ADVANCED;
            }
        }

        return Condition.TRAPPED;
    }

    private List<Node> path(List<Node> tA, List<Node> tB) {
        assert(!tA.isEmpty());
        assert(!tB.isEmpty());
        
        LinkedList<Node> path = new LinkedList<Node>();

        // Add path to starting node
        Node nA = tA.get(tA.size() - 1);
        path.add(nA);
        while (nA.parent > 0) {
            nA = tB.get(nA.parent);
            path.addFirst(nA);
        }

        // Add path to ending node
        Node nB = tB.get(tB.size() - 1);
        while (nB.parent > 0) {
            nB = tB.get(nB.parent);
            path.addLast(nB);
        }

        return path;
    }

    public List plan(State init, State goal, int iterations) {
        List<Node> tmp = null;

        List<Node> tA = new ArrayList();
        tA.add(new Node(init, null, -1));

        List<Node> tB = new ArrayList();
        tB.add(new Node(goal, null, -1));

        for (int k = 0; k < iterations; k++) {
            State xRand = sampler.random();
            State xNew = null;

            if (!(extend(tA, xRand) == Condition.TRAPPED)) {
                if (extend(tB, xNew) == Condition.REACHED) {
                    return path(tA, tB);
                }
            }

            // SWAP(Ta, Tb);
            tmp = tA;
            tA = tB;
            tB = tmp;
        }

        return null;
    }
}
