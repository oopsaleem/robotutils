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

package robotutils.util;

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Prasanna Velagapudi <psigen@gmail.com>
 */
public class PriorityQueueTest {

    private static Random _rnd;

    @BeforeClass
    public static void setUpClass() throws Exception {
        _rnd = new Random();
    }

    /**
     * Test of add method, of class PriorityQueue.
     */
    @Test
    public void testAdd() {
        System.out.println("add");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());

        // When we remove the numbers, they should be in order
        for (int x = 0; x < 100; x++)
            assertEquals(x, instance.poll());
    }

    /**
     * Test of peek method, of class PriorityQueue.
     */
    @Test
    public void testPeek() {
        System.out.println("peek");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());

        // When we remove the numbers, they should be in order
        // Also, peeking should not affect the next element
        for (int x = 0; x < 100; x++) {
            assertEquals(x, instance.peek());
            assertEquals(x, instance.peek());
            assertEquals(x, instance.poll());
        }
    }

    /**
     * Test of poll method, of class PriorityQueue.
     */
    @Test
    public void testPoll() {
        System.out.println("poll");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);
        
        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());

        // When we remove the numbers, they should be in order
        // Also, polling should affect the next element
        for (int x = 0; x < 99; x++) {
            assertEquals(x, instance.poll());
            assertEquals(x+1, instance.peek());
        }
    }

    /**
     * Test of remove method, of class PriorityQueue.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());

        // Remove odd numbers from 1 - 50
        for (int x = 1; x < 50; x+=2)
            instance.remove(x);

        // When we remove the first 25 numbers, they should only be even
        for (int x = 0; x < 25; x++)
            assertEquals(x*2, instance.poll());
        assertEquals(50, instance.size());

        // Now remove some stuff that isn't there, just for effect
        for (int x = 0; x < 25; x++)
            instance.remove(x + 100);

        // The queue should not have changed from the last test
        // (50 is the next element, and there should be 50 elements total)
        assertEquals(50, instance.size());
        assertEquals(50, instance.peek());

        // Now remove the even numbers from the remaining queue
        for (int x = 50; x < 100; x+=2)
            instance.remove(x);
        assertEquals(25, instance.size());

        // When we remove the next 25 numbers, they should only be odd
        for (int x = 0; x < 25; x++)
            assertEquals(x*2 + 51, instance.poll());
    }

    /**
     * Test of clear method, of class PriorityQueue.
     */
    @Test
    public void testClear() {
        System.out.println("clear");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());

        // Make sure that they all get cleared
        instance.clear();
        assertEquals(0, instance.size());

        // Add 100 larger integers to a list
        numbers.clear();
        for (int x = 100; x < 200; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));

        // Make sure none of the older smaller integers appear
        for (int x = 100; x < 200; x++) {
            assertEquals(x, instance.peek());
            assertEquals(x, instance.poll());
        }
    }

    /**
     * Test of elements method, of class PriorityQueue.
     */
    @Test
    public void testElements() {
        System.out.println("elements");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());

        // Add 100 integers to a list
        numbers.clear();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Check that we get all those numbers out of the enumeration
        while (!instance.isEmpty()) {
            Integer next = instance.poll();
            assertTrue(numbers.contains(next));
        }
        assertTrue(numbers.isEmpty());
    }

    /**
     * Test of size method, of class PriorityQueue.
     */
    @Test
    public void testSize() {
        System.out.println("size");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());

        // Randomly add and remove numbers, check the size of the queue
        int size = instance.size();
        for (int i = 0; i < 100; i++) {
            if (_rnd.nextBoolean()) {
                size++;
                instance.add(100 + i);
            } else {
                size--;
                instance.poll();
            }
            assertEquals(size, instance.size());
        }
    }

    /**
     * Test of isEmpty method, of class PriorityQueue.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");

        // Add 100 integers to a list
        Vector<Integer> numbers = new Vector();
        for (int x = 0; x < 100; x++)
            numbers.add(x);

        // Insert those numbers randomly into the queue
        PriorityQueue<Integer> instance = new PriorityQueue();
        while (!numbers.isEmpty())
            instance.add(numbers.remove(_rnd.nextInt(numbers.size())));
        assertEquals(100, instance.size());
        assertFalse(instance.isEmpty());

        // Remove all the numbers
        for (int x = 0; x < 100; x++)
            instance.remove(x);
        assertTrue(instance.isEmpty());
    }

    /**
     * Test of update method, of class PriorityQueue.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        Object x = null;
        PriorityQueue instance = null;
        instance.update(x);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of contains method, of class PriorityQueue.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        Object x = null;
        PriorityQueue instance = null;
        boolean expResult = false;
        boolean result = instance.contains(x);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteElement method, of class PriorityQueue.
     */
    @Test
    public void testDeleteElement() {
        System.out.println("deleteElement");
        int i = 0;
        PriorityQueue instance = null;
        instance.deleteElement(i);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of heapify method, of class PriorityQueue.
     */
    @Test
    public void testHeapify() {
        System.out.println("heapify");
        PriorityQueue instance = null;
        instance.heapify();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of swap method, of class PriorityQueue.
     */
    @Test
    public void testSwap() {
        System.out.println("swap");
        int i = 0;
        int j = 0;
        PriorityQueue instance = null;
        instance.swap(i, j);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}