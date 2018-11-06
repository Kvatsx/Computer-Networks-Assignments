// Kaustav Vats
import java.io.*;
import java.util.*;
import java.math.*;
import java.lang.*;


public class Q2_2016048 {
    private int RouterCount;
    private ArrayList<Router_2016048> routerList;

    public Q2_2016048(int count) {
        this.RouterCount = count;
        this.routerList = new ArrayList<Router_2016048>();
    }

    public boolean check() {
        boolean flag = true;
        for ( int i=1; i<RouterCount; i++ ) {
            for ( int x=0; x<RouterCount; x++ ) {
                for ( int y=0; y<RouterCount; y++ ) {
                    if ( routerList.get(0).getGraph()[x][y] != routerList.get(i).getGraph()[x][y] ) {
                        flag = false;
                        return flag;
                    }
                }
            }
        }
        return flag;
    }

    public void DistanceVectorProtocol() {
        int iterations = 1;
        int count = 0;
        boolean flag2 = true;
        while (true) {
            boolean Flag = true;
            
            for ( int i=0; i<RouterCount; i++ ) {
                for ( int j=0; j<RouterCount; j++ ) {
                    for ( int k=0; k<RouterCount; k++ ) {
                        if ( j != k ) {
                            routerList.get(j).update(routerList.get(k));
                        }
                    }
                    System.out.println(routerList.get(j));
                }   
                for ( int j=0; j<RouterCount; j++ ) {
                    for ( int k=0; k<RouterCount; k++ ) {
                        if ( i != j && j != k && i != k) {
                            if ( flag2 && routerList.get(i).getGraph()[i][j] > routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j] ) {
                                routerList.get(i).getGraph()[i][j] = routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j];
                                Flag = false;
                            }
                            else if ( flag2 == false ) {
                                routerList.get(i).getGraph()[i][j] = routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j];                                
                            }
                        }
                    }
                }
            }

            Flag = check();
            if ( Flag == true ) {
                count++;
                for (Router_2016048 node : this.routerList) {
                    System.out.println(node);
                }
                if ( count == 1 ) {
                    routerList.get(0).getGraph()[0][1] = 60;
                    routerList.get(1).getGraph()[1][0] = 60;
                    System.out.println("Hey");
                    flag2 = false;
                    // for ( int i=0; i<RouterCount; i++ ) {
                    //     System.out.println(routerList.get(i));
                    // }
                }
                else {
                    break;
                }
            }
            iterations++;
            System.out.println("Network Stable? "+Flag);
        }
        System.out.println("Iterations: "+iterations);
        
    }

    public static void main(String[] args) throws IOException{
        Reader.init(System.in);
        System.out.print("Total no of Routers: ");
        int n = Reader.nextInt();
        Q2_2016048 q2 = new Q2_2016048(n);
        for ( int i=0; i<n; i++ ) {
            q2.routerList.add(new Router_2016048(n, i));
        }
        // TODO: Need to take input and initialize matrix.
        System.out.print("Enter Edges: ");
        int e = Reader.nextInt();
        for ( int i=0; i<e; i++ ) {
            int r1 = Reader.nextInt();
            int r2 = Reader.nextInt();
            int d = Reader.nextInt();
            q2.routerList.get(r1).getGraph()[r1][r2] = d;
            q2.routerList.get(r2).getGraph()[r2][r1] = d;
        }
        q2.DistanceVectorProtocol();
    }
}
/**
 * Input
 3
 3
 0 1 4
 0 2 50
 1 2 1
 */


/** Class for buffered reading int and double values */
class Reader {
    static BufferedReader reader;
    static StringTokenizer tokenizer;

    /** call this method to initialize reader for InputStream */
    static void init(InputStream input) {
        reader = new BufferedReader(
                     new InputStreamReader(input) );
        tokenizer = new StringTokenizer("");
    }

    /** get next word */
    static String next() throws IOException {
        while ( ! tokenizer.hasMoreTokens() ) {
            //TODO add check for eof if necessary
            tokenizer = new StringTokenizer(
                   reader.readLine() );
        }
        return tokenizer.nextToken();
    }

    static int nextInt() throws IOException {
        return Integer.parseInt( next() );
    }

    static long nextLong() throws IOException {
        return Long.parseLong( next() );
    }
	
    static double nextDouble() throws IOException {
        return Double.parseDouble( next() );
    }
}