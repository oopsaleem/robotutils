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
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE PROJECT AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package robotutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;

/**
 * A static autoconfiguration class to naively load and save parameters in a
 * flat file.  This functionality is achieved by iterating through key-value 
 * pairs in the file and replacing any primitive public static class variables 
 * with the values loaded from file.
 * 
 * @author Prasanna Velagapudi <pkv@cs.cmu.edu>
 */
public class Config {
    public static int ninja;
    
    /**
     * Load a set of properties from an input stream, and attempt to set the 
     * values loaded to any public static variables in the specified classes.
     * 
     * Properties are specified in key-value pairs delimited by the first "=". 
     * For example, "robotutils.foo.bar = 234" would try to set a public static
     * variable called bar in the robotutils.foo class to the value 234.
     * 
     * @param stream the input stream of property key-value pairs.
     * @return the success of the load operation
     */
    public static boolean load(InputStream stream) {
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        
        try {
            String line = in.readLine();
            while (line != null) {
                // Ignore comment lines
                if (line.trim().startsWith("#")) continue;
                
                // Parse out key and value
                String[] tuple = line.split("=", 1);
                if (tuple.length < 2) continue;
                
                // Isolate key and value
                String key = tuple[0];
                String value = tuple[1];
                
                // Try to set variable to this value
                setValue(key, value);
                
                // Read the next line
                line = in.readLine();
            }
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static boolean save(OutputStream stream, Class c) {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(stream));
        
        for (Field f : c.getFields()) {
            // Construct the key
            String key = c.getCanonicalName() + "." + f.getName();
            
            // Search for the value
            // TODO: Handle writing NULLs to file
            String value = getValue(key);
            
            // Write the value to file
            if (value != null) {
                out.println(key + " = " + value);
            }
        }
        
        return true;
    }
    
    protected static boolean setValue(String key, String value) {
        // Split at the variable name
        int splitIdx = key.lastIndexOf(".");
        
        try {
            // Get class and variable name
            String className = key.substring(0, splitIdx);
            String varName = key.substring(splitIdx);
            
            // Reflect to find the static field
            Class c = Class.forName(className);
            Field f = c.getDeclaredField(varName);
            
            // We can only handle primitive classes properly
            if (!f.getType().isPrimitive()) {
                return false;
            }
            
            // Use the appropriate primitive parsing routine
            if (f.getType() == Boolean.TYPE) {
                f.setBoolean(f, Boolean.parseBoolean(value));
            } else if (f.getType() == Byte.TYPE) {
                f.setByte(f, Byte.parseByte(value));
            } else if (f.getType() == Character.TYPE) {
                f.setChar(f, value.charAt(0));
            } else if (f.getType() == Short.TYPE) {
                f.setShort(f, Short.parseShort(value));
            } else if (f.getType() == Integer.TYPE) {
                f.setInt(f, Integer.parseInt(value));
            } else if (f.getType() == Long.TYPE) {
                f.setLong(f, Long.parseLong(value));
            } else if (f.getType() == Float.TYPE) {
                f.setFloat(f, Float.parseFloat(value));
            } else if (f.getType() == Double.TYPE) {
                f.setDouble(f, Double.parseDouble(value));
            } else {
                // TODO: log this error
            }
            
            f.set(null, value);
            return true;
        } catch (IndexOutOfBoundsException e) {
            // TODO: Log this error
            return false;
        } catch (ClassNotFoundException e) {
            // TODO: Log this error
            return false;
        } catch (NoSuchFieldException e) {
            // TODO: Log this error
            return false;
        } catch (NullPointerException e) {
            // TODO: Log this error
            return false;
        } catch (IllegalAccessException e) {
            // TODO: Log this error
            return false;
        } catch (NumberFormatException e) {
            // TODO: Log this error
            return false;
        }
    }
    
    protected static String getValue(String key) {
        // Split at the variable name
        int splitIdx = key.lastIndexOf(".");
        
        try {
            // Get class and variable name
            String className = key.substring(0, splitIdx);
            String varName = key.substring(splitIdx);
            
            // Reflect to find the static field
            Class c = Class.forName(className);
            Field f = c.getDeclaredField(varName);
            
            // We can only handle primitive classes properly
            if (!f.getType().isPrimitive()) {
                // TODO: Log this warning
            }
            
            return f.get(null).toString();
        } catch (IndexOutOfBoundsException e) {
            // TODO: Log this error
            return null;
        } catch (ClassNotFoundException e) {
            // TODO: Log this error
            return null;
        } catch (NoSuchFieldException e) {
            // TODO: Log this error
            return null;
        } catch (NullPointerException e) {
            // TODO: Log this error
            return null;
        } catch (IllegalAccessException e) {
            // TODO: Log this error
            return null;
        }
    }
}
