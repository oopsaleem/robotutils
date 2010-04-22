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
import java.util.Collections;
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

    public static final class Tuple<S, A> {

        public final S _state;
        public final A _action;

        public Tuple(S state, A action) {
            _state = state;
            _action = action;
        }
    }

    protected static final class Node<S, A> {

        public final S _state;
        public final A _action;
        public final int _parent;

        public Node(S state, A action, int parent) {
            _state = state;
            _action = action;
            _parent = parent;
        }

        public Tuple<S,A> toTuple() {
            return new Tuple<S, A>(_state, _action);
        }
    }

    protected abstract double distance(State a, State b);
    protected abstract Tuple<State, Action> newState(State x, State xNear);
    protected abstract State randomState();

    protected int nearestNeighbor(List<Node<State, Action> > t, State x) {
        double minDist = Double.MAX_VALUE;
        int minIdx = -1;

        Node<State, Action> curNode = null;
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

    protected int extend(List<Node<State, Action> > t, State x) {
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
     * node in the trees are the ones that matched.
     * @param tStart the start tree (rooted at the starting node)
     * @param tGoal the goal tree (rooted at the goal node)
     * @return a list of states/actions that connect the two trees.
     */
    private List<Tuple<State, Action> > path( List<Node<State, Action> > tStart, List<Node<State, Action> > tGoal) {

        // Create a linked list to assemble from both ends
        LinkedList<Tuple<State, Action> > path = new LinkedList();

        // Add path to starting node
        Node nA = tStart.get(tStart.size() - 1);
        path.add(nA.toTuple());
        while (nA._parent > 0) {
            nA = tGoal.get(nA._parent);
            path.addFirst(nA.toTuple());
        }

        // Add path to ending node
        Node nB = tGoal.get(tGoal.size() - 1);
        while (nB._parent > 0) {
            nB = tGoal.get(nB._parent);
            path.addLast(nB.toTuple());
        }

        return path;
    }

    /**
     * Executes a BiRRT search from the provide start to the provided goal.
     * This version uses the default number of iterations.
     *
     * @see BiRRT#DEFAULT_ITERATION_LIMIT
     * @see BiRRT#plan(java.lang.Object, java.lang.Object, int)
     *
     * @param start the desired initial state
     * @param goal the desired final state
     * @return a list of state/action tuples, or an empty list on failure.
     */
    public List<Tuple<State,Action> > plan(State start, State goal) {
        return plan(start, goal, DEFAULT_ITERATION_LIMIT);
    }

    /**
     * Executes a BiRRT search from the provide start to the provided goal.
     * 
     * @param start the desired initial state
     * @param goal the desired final state
     * @param iterations the number of iterations until terminating with failure.
     * @return a list of state/action tuples, or an empty list on failure.
     */
    public List<Tuple<State,Action> > plan(State start, State goal, int iterations) {

        // Create the start tree and add the starting state
        List<Node<State,Action> > tStart = new ArrayList();
        tStart.add(new Node(start, null, -1));

        // Create the goal tree and add the goal state
        List<Node<State,Action> > tGoal = new ArrayList();
        tGoal.add(new Node(goal, null, -1));

        // Assign the start and goal trees to "A" and "B"
        List<Node<State,Action> > tA = tStart;
        List<Node<State,Action> > tB = tGoal;

        // Iterate until limit is reached
        for (int k = 0; k < iterations; k++) {

            // Pick a random state using the generating function
            State xRand = randomState();

            // Attempt to extend the "A" tree toward the random state
            if (!(extend(tA, xRand) == TRAPPED)) {

                // Get the new state that was added to the "A" tree
                State xNew = tA.get(tA.size() - 1)._state;

                // Attempt to extend the "B" tree to reach this new node
                if (extend(tB, xNew) == REACHED) {

                    // If we succeeded, reconstruct this path
                    return path(tStart, tGoal);
                }
            }

            // Switch the "A" and "B" trees to expand the other one
            // SWAP(Ta, Tb);
            List<Node<State, Action> > tmp = tA;
            tA = tB;
            tB = tmp;
        }

        return Collections.emptyList();
    }
}
