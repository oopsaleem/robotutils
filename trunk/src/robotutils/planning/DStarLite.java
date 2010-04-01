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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * This class implements the optimized D*-lite algorithm exactly as described
 * in [Koenig 2002]. D*-lite is an incremental variant of the A* search
 * algorithm, meaning that it can cheaply update itself to account for new
 * obstacles.
 *
 * Source: Koenig, S. and Likhachev, M. 2002. D*lite. In Eighteenth National
 * Conference on Artificial intelligence (Edmonton, Alberta, Canada, July 28 -
 * August 01, 2002). R. Dechter, M. Kearns, and R. Sutton, Eds. American
 * Association for Artificial Intelligence, Menlo Park, CA, 476-483
 *
 * @author Prasanna Velagapudi <psigen@gmail.com>
 */
public abstract class DStarLite<State> {

    /**
     * A tuple with two components used to assign priorities to states in the
     * D* search.  Keys are compared according to a lexical ordering, e.g.
     * k &lt k' iff either k1 &lt k'1 or (k1 = k'1 and k2 &lt k'2).
     */
    class Key implements Comparable<Key> {
        
        final double a;
        final double b;

        public Key(double a, double b) {

            this.a = a;
            this.b = b;
        }

        public int compareTo(Key that) {

            if (this.a < that.a) {
                return -1;
            } else if (this.a > that.a) {
                return 1;
            } else {
                if (this.b < that.b) {
                    return -1;
                } else if (this.b > that.b) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    /**
     * A simple wrapper to a HashMap that returns infinity if a match is not
     * found.  This cheaply implements the lookup behavior required by D*.
     */
    class ValueMap extends HashMap<State, Double> {

        @Override
        public Double get(Object key) {

            @SuppressWarnings("element-type-mismatch")
            Double res = super.get(key);

            if (res == null) {
                return Double.POSITIVE_INFINITY;
            } else {
                return res;
            }
        }
    }

    /**
     * A simple wrapper to a priority queue that internally stores a tuple of
     * State and Key with the correct comparison and equals operators.  This
     * cheaply implements the lookup behavior expected by D*.
     */
    class KeyQueue {

        private class StateKey {
            
            final State s;
            final Key k;

            public StateKey(State s, Key k) {
                this.s = s;
                this.k = k;
            }

            @Override
            public boolean equals(Object obj) {

                if (obj == null) {
                    return false;
                } else if (getClass() != obj.getClass()) {
                    return false;
                } else {
                    StateKey that = (StateKey)obj;
                    return (this.s.equals(that.s));
                }
            }

            @Override
            public int hashCode() {
                int hash = 7;
                hash = 53 * hash + (this.s != null ? this.s.hashCode() : 0);
                return hash;
            }
        }

        private PriorityQueue<StateKey> _queue;

        public KeyQueue() {
            _queue = new PriorityQueue(1000, new Comparator<StateKey>() {

                public int compare(StateKey o1, StateKey o2) {
                    return o1.k.compareTo(o2.k);
                }
            });
        }

        public void insert(State s, Key k) {
            _queue.add(new StateKey(s, k));
        }

        public void update(State s, Key k) {
            _queue.remove(new StateKey(s, null));
            _queue.add(new StateKey(s, k));
        }

        public void remove(State s) {
            _queue.remove(new StateKey(s, null));
        }

        public Key topKey() {
            return _queue.peek().k;
        }

        public State top() {
            return _queue.peek().s;
        }

        public boolean contains(State s) {
            return _queue.contains(new StateKey(s, null));
        }
    }

    /**
     * A tuple storing a description of a change to an edge cost.
     */
    class EdgeChange {
        final State a;
        final State b;
        final double cOld;
        final double cNew;

        public EdgeChange(State a, State b, double cOld, double cNew) {
            this.a = a;
            this.b = b;
            this.cOld = cOld;
            this.cNew = cNew;
        }
    }

    /**
     * Returns the set of successor states to the specified state.
     * @param s the specified state.
     * @return A set of successor states.
     */
    protected abstract Collection<State> succ(State s);

    /**
     * Returns the set of predecessor states to the specified state.
     * @param s the specified state.
     * @return A set of predecessor states.
     */
    protected abstract Collection<State> pred(State s);

    /**
     * An admissible heuristic function for the distance between two states.
     * In actual use, the second vertex will always be the goal state.
     *
     * The heuristic must follow these rules:
     * 1) h(a, a) = 0
     * 2) h(a, b) &lt;= c(a, c) + h(c, b) (where a and c are neighbors)
     *
     * @param a some initial state
     * @param b some final state
     * @return the estimated distance between the states.
     */
    protected abstract double h(State a, State b);

    /**
     * An exact cost function for the distance between two <i>neighboring</i>
     * states.  This function is undefined for non-neighboring states.  The
     * neighbor connectivity is determined by the pred() and succ() functions.
     * @param a some initial state
     * @param b some final state
     * @return the actual distance between the states.
     */
    protected abstract double c(State a, State b);

    protected State _start;
    protected final State _goal;

    final ValueMap _rhs = new ValueMap();
    final ValueMap _g = new ValueMap();

    final KeyQueue _U = new KeyQueue();
    double _Km = 0;

    /**
     * Initializes a D* search object with the specified start and goal states.
     * This constructor roughly corresponds to the <i>initialize()<i> function
     * in the pseudocode of the original paper.
     * @param start the desired start state
     * @param goal the desires end state
     */
    public DStarLite(State start, State goal) {

        _start = start;
        _goal = goal;

        _rhs.put(_goal, 0.0);
        _U.insert(goal, new Key(h(_start, _goal), 0));
    }

    Key calculateKey(State s) {

        double k1 = Math.min(_g.get(s), _rhs.get(s)) + h(_start, s) + _Km;
        double k2 = Math.min(_g.get(s), _rhs.get(s));

        return new Key(k1, k2);
    }

    public void updateVertex(State u) {

        if (_g.get(u) != _rhs.get(u) && _U.contains(u)) {
            _U.update(u, calculateKey(u));
        } else if (_g.get(u) != _rhs.get(u) && !_U.contains(u)) {
            _U.insert(u, calculateKey(u));
        } else if (_g.get(u) == _rhs.get(u) && _U.contains(u)) {
            _U.remove(u);
        }
    }

    public void computeShortestPath() {

        while (_U.topKey().compareTo(calculateKey(_start)) < 0 || _rhs.get(_start) > _g.get(_start)) {

            State u = _U.top();
            Key kOld = _U.topKey();
            Key kNew = calculateKey(u);

            if (kOld.compareTo(kNew) < 0) {
                _U.update(u, kNew);
            } else if (_g.get(u) > _rhs.get(u)) {
                _g.put(u, _rhs.get(u));
                _U.remove(u);

                for (State s : pred(u)) {
                    if (!s.equals(_goal)) {
                        _rhs.put(s, Math.min(_rhs.get(s), c(s, u) + _g.get(u)));
                        updateVertex(s);
                    }
                }
            } else {
                double gOld = _g.get(u);
                _g.put(u, Double.POSITIVE_INFINITY);

                Collection<State> preds = pred(u);
                preds.add(u);

                for (State s : preds) {
                    if (_rhs.get(s) == c(s,u) + gOld) {
                        if (!s.equals(_goal)) {
                            double minRhs = Double.POSITIVE_INFINITY;

                            for (State sPrime : succ(s)) {
                                double rhsPrime = c(s, sPrime) + _g.get(sPrime);
                                if (rhsPrime < minRhs) {
                                    minRhs = rhsPrime;
                                }
                            }

                            _rhs.put(s, minRhs);
                        }
                    }
                    updateVertex(s);
                }
            }
        }
    }

    /**
     * Used to indicate that the distance from state A to state B has been
     * changed from cOld to cNew, and need to replanned in the next iteration.
     * @param u some initial state
     * @param v some final state
     * @param cOld the old cost value
     * @param cNew the new cost value
     */
    public void flagChange(State u, State v, double cOld, double cNew) {
        
        if (cOld > cNew) {
            if (!u.equals(_goal)) {
                _rhs.put(u, Math.min(_rhs.get(u), c(u,v) + _g.get(v)));
            }
        } else if (_rhs.get(u) == cOld + _g.get(v)) {
            if (!u.equals(_goal)) {
                if (!u.equals(_goal)) {
                    double minRhs = Double.POSITIVE_INFINITY;

                    for (State sPrime : succ(u)) {
                        double rhsPrime = c(u, sPrime) + _g.get(sPrime);
                        if (rhsPrime < minRhs) {
                            minRhs = rhsPrime;
                        }
                    }

                    _rhs.put(u, minRhs);
                }
            }
        }
    }

    /**
     * Change the start location after initialization.  Used to cheaply move
     * robot along path without needing to replan.
     * @param s the new start state
     */
    public void updateStart(State s) {
        State sLast = _start;
        _start = s;

        _Km += h(sLast, _start);

        _rhs.put(_start, Double.POSITIVE_INFINITY);
        _g.put(_start, Double.POSITIVE_INFINITY);
        _U.update(_start, calculateKey(_start));
    }

    /**
     * Recomputes the lowest cost path through the map, taking into account
     * any changes in start location and edge costs.  If no path can be found
     * this will return an empty list.
     * @return a list of states from start to goal
     */
    public List<State> plan() {

        List<State> path = new ArrayList(100);        
        State s = _start;
        path.add(s);

        computeShortestPath();
        while(!s.equals(_goal)) {
            // If rhs(sStart) == Inf, then there is no known path
            if (_rhs.get(s) == Double.POSITIVE_INFINITY) {
                return Collections.emptyList();
            }

            Collection<State> succs = succ(s);
            Double minRhs = Double.POSITIVE_INFINITY;
            State minS = null;

            for (State sPrime : succs) {
                double rhsPrime = c(s, sPrime) + _g.get(sPrime);
                if (rhsPrime < minRhs) {
                    minRhs = rhsPrime;
                    minS = sPrime;
                }
            }

            s = minS;
            path.add(s);
        }

        return path;
    }
}
