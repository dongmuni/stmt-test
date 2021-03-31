package com.tidesquare.toac.stmt;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int x = 0;
        
        outer: for ( int i = 0 ; i < 100 ; i++ ) {
        	for ( int j = 0 ; j < 100 ; j++ ) {
        		if ( x++ == 500 ) {
        			break outer;
        		}
        	}
        	System.out.format("i=%d\n", i);
        }
        
        System.out.println( "Hello World!" );
    }
}
