/*
 *  The MIT License
 * 
 *  Copyright 2010 pkv.
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

package robotutils.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import robotutils.io.FileBuffer.Entry;

/**
 * Test harness for FileBuffer, a file-backed Map implementation.
 * 
 * @author pkv
 */
public class FileBufferTest {
    public static File testFile;
    public static File tempFile;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Create temp file.
        tempFile = File.createTempFile("FileBufferTempFile", ".dat");

        // Create test file (with known contents)
        testFile = File.createTempFile("FileBufferTestFile", ".dat");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        // Delete temp file
        if (!tempFile.delete())
            throw new IOException("Temp file cleanup failed: " + tempFile);

        // Delete test file
        if (!testFile.delete())
            throw new IOException("Test file cleanup failed: " + testFile);
    }

    /**
     * Test of add method, of class FileBuffer.
     */
    @Test
    public void testAdd() throws Exception {
        System.out.println("add");

        int numTests = 100;
        List<Serializable> contents = new ArrayList<Serializable>(numTests);
        List<Long> addrs = new ArrayList<Long>(numTests);
        
        for (int i = 0; i < numTests; i++) {
            contents.add("FOOBAR" + i);
        }

        try {
            FileBuffer instance = new FileBuffer(tempFile);
            for (Serializable obj : contents) {
                addrs.add(instance.add(obj));
            }

            for (int i = 0; i < addrs.size(); i++) {
                assertEquals( contents.get(i), instance.get(addrs.get(i)) );
            }

        } catch (FileNotFoundException ex) {
            fail("Did not find data file: " + ex);
        }
    }

    /**
     * Test of addAll method, of class FileBuffer.
     */
    @Test
    public void testAddAll() throws Exception {
        System.out.println("addAll");

        int numTests = 100;
        List<Serializable> contents = new ArrayList<Serializable>(numTests);
        List<Long> addrs = null;

        for (int i = 0; i < numTests; i++) {
            contents.add("FOOBAR" + i);
        }

        try {
            FileBuffer instance = new FileBuffer(tempFile);
            addrs = instance.addAll(contents);

            for (int i = 0; i < addrs.size(); i++) {
                assertEquals( contents.get(i), instance.get(addrs.get(i)) );
            }

        } catch (FileNotFoundException ex) {
            fail("Did not find data file: " + ex);
        }
    }

    /**
     * Test of isValid method, of class FileBuffer.
     */
    @Test
    public void testIsValid() {
        System.out.println("isValid");
        long uid = 0L;
        FileBuffer instance = null;
        boolean expResult = false;
        boolean result = instance.isValid(uid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readHeader method, of class FileBuffer.
     */
    @Test
    public void testReadHeader() throws Exception {
        System.out.println("readHeader");
        long uid = 0L;
        FileBuffer instance = null;
        Entry expResult = null;
        Entry result = instance.readHeader(uid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class FileBuffer.
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        long uid = 0L;
        FileBuffer instance = null;
        Entry expResult = null;
        Entry result = instance.read(uid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of write method, of class FileBuffer.
     */
    @Test
    public void testWrite() throws Exception {
        System.out.println("write");
        Serializable obj = null;
        FileBuffer instance = null;
        long expResult = 0L;
        long result = instance.write(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of size method, of class FileBuffer.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        FileBuffer instance = null;
        int expResult = 0;
        int result = instance.size();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of entrySet method, of class FileBuffer.
     */
    @Test
    public void testEntrySet() {
        System.out.println("entrySet");
        FileBuffer instance = null;
        Set expResult = null;
        Set result = instance.entrySet();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isEmpty method, of class FileBuffer.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        FileBuffer instance = null;
        boolean expResult = false;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of containsKey method, of class FileBuffer.
     */
    @Test
    public void testContainsKey() {
        System.out.println("containsKey");
        Object uid = null;
        FileBuffer instance = null;
        boolean expResult = false;
        boolean result = instance.containsKey(uid);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of containsValue method, of class FileBuffer.
     */
    @Test
    public void testContainsValue() {
        System.out.println("containsValue");
        Object obj = null;
        FileBuffer instance = null;
        boolean expResult = false;
        boolean result = instance.containsValue(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of keySet method, of class FileBuffer.
     */
    @Test
    public void testKeySet() {
        System.out.println("keySet");
        FileBuffer instance = null;
        Set expResult = null;
        Set result = instance.keySet();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of values method, of class FileBuffer.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        FileBuffer instance = null;
        Collection expResult = null;
        Collection result = instance.values();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class FileBuffer.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Object key = null;
        FileBuffer instance = null;
        Object expResult = null;
        Object result = instance.get(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of put method, of class FileBuffer.
     */
    @Test
    public void testPut() {
        System.out.println("put");
        Long k = 23111L;
        Serializable v = "foobar";
        
        try {
            FileBuffer instance = new FileBuffer(tempFile);
            instance.put(k, v);
        } catch (UnsupportedOperationException ex) {
            System.out.println("Threw " + ex);
            return;
        } catch (FileNotFoundException ex) {
            fail("Did not find data file: " + ex);
        }

        fail("Exception was not thrown by put().");
    }

    /**
     * Test of putAll method, of class FileBuffer.
     */
    @Test
    public void testPutAll() {
        System.out.println("putAll");
        Map<Long, Object> map = new HashMap<Long, Object>();
        
        try {
            FileBuffer instance = new FileBuffer(tempFile);
            instance.putAll(map);
        } catch (UnsupportedOperationException ex) {
            System.out.println("Threw " + ex);
            return;
        } catch (FileNotFoundException ex) {
            fail("Did not find data file: " + ex);
        }
        
        fail("Exception was not thrown by putAll().");
    }

    /**
     * Test of remove method, of class FileBuffer.
     */
    @Test
    public void testRemove() {
        System.out.println("remove");
        Serializable o = "foobar";

        try {
            FileBuffer instance = new FileBuffer(tempFile);
            instance.remove(o);
        } catch (UnsupportedOperationException ex) {
            System.out.println("Threw " + ex);
            return;
        } catch (FileNotFoundException ex) {
            fail("Did not find data file: " + ex);
        }

        fail("Exception was not thrown by remove().");
    }

    /**
     * Test of clear method, of class FileBuffer.
     */
    @Test
    public void testClear() {
        System.out.println("clear");

        try {
            FileBuffer instance = new FileBuffer(tempFile);
            instance.clear();
        } catch (UnsupportedOperationException ex) {
            System.out.println("Threw " + ex);
            return;
        } catch (FileNotFoundException ex) {
            fail("Did not find data file: " + ex);
        }

        fail("Exception was not thrown by clear().");
    }

}