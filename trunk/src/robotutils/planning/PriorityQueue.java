/*
 * Copyright (c) 1998-2002 Carnegie Mellon University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY CARNEGIE MELLON UNIVERSITY ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package robotutils.planning;

import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Priority queue.  Objects stored in a priority queue must implement 
 * the Prioritized interface.
 * 
 */
public class PriorityQueue<E> {

    /**
     * The queue of elements.
     */
    private Vector<E> _queue;

    /**
     * The comparator used to order elements.
     */
    private Comparator<? super E> _comparator;
    
    /**
     * Make an empty PriorityQueue.
     * @param comparator comparison operator to use when ordering elements
     */
    public PriorityQueue(Comparator<? super E> comparator) {
        _queue = new Vector();
        _comparator = comparator;
    }

    /**
     * Make an empty PriorityQueue with an initial capacity.
     * @param initialCapacity number of elements initially allocated in queue
     * @param comparator comparison operator to use when ordering elements
     */
    public PriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        _queue = new Vector(initialCapacity);
        _comparator = comparator;
    }

    /**
     * Put an object on the queue.  Doesn't check for
     * duplicate puts.
     * @param x object to put on the queue 
     */
    public synchronized void put(E x) {
        int newSize = _queue.size()+1;
        _queue.setSize (newSize);
        
        int i, p;
        for (i=newSize-1, p = ((i+1)/2)-1; // i's parent
             i > 0 && _comparator.compare(_queue.elementAt(p), x) > 0;
             i = p, p = ((i+1)/2)-1)
            _queue.setElementAt (_queue.elementAt (p), i);

        _queue.setElementAt (x, i);
    }

    /**
     * Get object with lowest priority from queue.
     * @return object with lowest priority, or null if queue is empty
     */
    public synchronized Object getMin() {
        return !empty() ? _queue.elementAt (0) : null;
    }

    /**
     * Get and delete the object with lowest priority.
     * @return object with lowest priority, or null if queue is empty
     */
    public synchronized Object deleteMin() {
        if (empty())
            return null;
        Object obj = _queue.elementAt (0);
        deleteElement (0);
        return obj;
    }

    /**
     * Delete an object from queue.  If object was inserted more than
     * once, this method deletes only one occurrence of it.
     * @param x object to delete
     * @return true if x was found and deleted, false if x not found in queue
     */
    public synchronized boolean delete(E x) {
        int i = _queue.indexOf(x);
        if (i == -1) {
            return false;
        }
        
        deleteElement(i);
        return true;
    }

    /**
     * Remove all objects from queue.
     */
    public synchronized void clear() {
        _queue.removeAllElements ();
    }

    
    /**
     * Enumerate the objects in the queue, in no particular order
     * @return enumeration of objects in queue
     */
    public synchronized Enumeration elements() {
        return _queue.elements ();
    }

    
    /**
     * Get number of objects in queue.
     * @return number of objects
     */
    public synchronized int size() {
        return _queue.size ();
    }

    
    /**
     * Test whether queue is empty.
     * @return true iff queue is empty.
     */
    public synchronized boolean empty() {
        return _queue.isEmpty ();
    }

    /**
     * Rebuild priority queuein case the priorities of its elements 
     * have changed since they were inserted.  If the priority of
     * any element changes, this method must be called to update
     * the priority queue.
     */
    public synchronized void update() {
        for (int i = (_queue.size()/2) - 1; i >= 0; --i)
            heapify (i);
    }

    final void deleteElement(int i) {
        int last = _queue.size()-1;
        _queue.setElementAt (_queue.elementAt (last), i);
        _queue.setElementAt (null, last);    // avoid holding extra reference
        _queue.setSize (last);
        heapify (i);
    }

    /**
     * Establishes the heap property at i's descendents.
     */
    final void heapify(int i) {
        int max = _queue.size();
        while (i < max) {
            int r = 2*(i+1); // right child of i
            int l = r - 1;   // left child of i

            int smallest = i;
            E prioritySmallest = _queue.elementAt(i);
            E priorityR;

            if (r < max && _comparator.compare(priorityR = _queue.elementAt(r), prioritySmallest) < 0) {
                smallest = r;
                prioritySmallest = priorityR;
            }
            if (l < max && _comparator.compare(_queue.elementAt(l), prioritySmallest) < 0) {
                smallest = l;
            }

            if (smallest != i) {
                swap (i, smallest);
                i = smallest;
            }
            else
                break;
        }
    }

    /**
     * Swap elements at positions i and j in the table.
     */
    final void swap(int i, int j) {
        E tmp = _queue.elementAt(i);
        _queue.setElementAt(_queue.elementAt (j), i);
        _queue.setElementAt(tmp, j);
    }
}
