package com.francisli.processing.doclet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ProcessingDocletTest
    extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ProcessingDocletTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ProcessingDocletTest.class );
    }

    /**
     * This is currently hard-coded to test generate documentation for
     * another project in my local filesystem.
     *
     */
    public void testProcessingDoclet() {
        com.sun.tools.javadoc.Main.execute(new String[] {
            "-doclet", "com.francisli.processing.doclet.ProcessingDoclet",
            "-sourcepath", "../processing-restclient/src/main/java",
            "com.francisli.processing.restclient"
        });
    }
}
