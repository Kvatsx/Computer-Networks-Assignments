// Kaustav Vats
import java.io.*;
import java.util.*;
import java.math.*;
import java.lang.*;


public class Q2_2016048 {
    private int RouterCount;
    private ArrayList<Router_2016048> routerList;
    private boolean[] stable;
    private boolean[][] Padosi;
    private Router_2016048[] checkList;
    private static final int INFINITE = 1000000000;

    public Q2_2016048(int count) {
        this.RouterCount = count;
        this.routerList = new ArrayList<Router_2016048>();
        this.stable = new boolean[RouterCount];
        this.Padosi = new boolean[RouterCount][RouterCount];
        this.checkList = new Router_2016048[RouterCount];
    }

    public boolean check() {
        // boolean flag = true;
        // for ( int i=1; i<RouterCount; i++ ) {
        //     for ( int x=0; x<RouterCount; x++ ) {
        //         for ( int y=0; y<RouterCount; y++ ) {
        //             if ( routerList.get(0).getGraph()[x][y] != routerList.get(i).getGraph()[x][y] ) {
        //                 flag = false;
        //                 return flag;
        //             }
        //         }
        //     }
        // }
        for ( int i=0; i<RouterCount; i++ ) {
            if ( stable[i] == false ) {
                return false;
            }
        }
        return true;
    }

    private void updateList() {
        for (int i=0; i<RouterCount; i++) {
            checkList[i] = routerList.get(i).getNode();
        }
    }

    private void active(int x, int y, int z) {
        routerList = new ArrayList<Router_2016048>();
        for ( int i=0; i<RouterCount; i++ ) {
            routerList.add(checkList[i].getNode());
        }
        routerList.get(x).getGraph()[x][y] = z;
        routerList.get(y).getGraph()[y][x] = z;
    }

    private void updatePadosi(int id) {
        System.out.println("ID: "+id);
        for ( int i=0; i<RouterCount; i++ ) {
            if ( i != id && Padosi[id][i] && stable[i] ) {
                System.out.println("i: "+i);
                
                stable[i] = false;
            }
        }
    }

    private void ThirdEye(int x, int y, int z) {
        active(x,y,z);
        while (true) {
            boolean flag = true;

            for ( int j=0; j<RouterCount; j++ ) {
                boolean temp = true;
                for ( int k=0; k<RouterCount; k++ ) {
                    boolean flag2;
                    if ( j != k ) {
                        flag2 = routerList.get(j).update(routerList.get(k));
                        if ( flag2 == false ) {
                            temp = false;
                        }
                    }
                } 
                stable[j] = temp;
            }
            System.out.println("DV Exchanged and Updated!");
            for ( int i=0; i<RouterCount; i++ ) {
                System.out.println(routerList.get(i));  
            }

            for ( int i=0; i<RouterCount; i++ ) {
                // if Router not stable, make it stable
                if ( stable[i] == false ) {
                    for ( int j=0; j<RouterCount; j++ ) {
                        for ( int k=0; k<RouterCount; k++ ) {
                            if ( i != j && j != k && i != k) {
                                if ( routerList.get(i).getGraph()[i][j] > routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j] ) {
                                    routerList.get(i).getGraph()[i][j] = routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j];
                                }
                            }
                        }
                    }
                }  
            }
            flag = check();
            if ( flag == true ) {
                break;
            }
            System.out.println("Network Stable? "+flag);
        }
    
        for ( int i=0; i<RouterCount; i++ ) {
            System.out.println(routerList.get(i));
        }
    }

    public void DistanceVectorProtocol() {
        updateList();
        int iterations = 2;
        
        while (true) {
            boolean flag = true;

            for ( int j=0; j<RouterCount; j++ ) {
                boolean temp = true;
                for ( int k=0; k<RouterCount; k++ ) {
                    boolean flag2;
                    if ( j != k ) {
                        flag2 = routerList.get(j).update(routerList.get(k));
                        if ( flag2 == false ) {
                            temp = false;
                        }
                    }
                } 
                stable[j] = temp;
            }
            System.out.println("DV Exchanged and Updated!");
            for ( int i=0; i<RouterCount; i++ ) {
                System.out.println(routerList.get(i));  
            }

            for ( int i=0; i<RouterCount; i++ ) {
                // if Router not stable, make it stable
                if ( stable[i] == false ) {
                    for ( int j=0; j<RouterCount; j++ ) {
                        for ( int k=0; k<RouterCount; k++ ) {
                            if ( i != j && j != k && i != k) {
                                // if ( flag5 && (updateX == i && updateY == j)) {
                                //     routerList.get(i).getGraph()[i][j] = routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j];
                                // }
                                if ( routerList.get(i).getGraph()[i][j] > routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j] ) {
                                    routerList.get(i).getGraph()[i][j] = routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j];
                                }
                            }
                        }
                    }
                }  
            }
            for ( int i=0; i<RouterCount; i++ ) {
                System.out.print(stable[i]+" ");
            }
            System.out.println();

            flag = check();
            if ( flag == true ) {
                for (Router_2016048 node : this.routerList) {
                    System.out.println(node);
                }
                // routerList.get(0).getGraph()[0][1] = 60;
                // routerList.get(1).getGraph()[1][0] = 60;
                System.out.println("Finally Stable");
                UpdateDV(0, 1, 60);
                break;
            }
            iterations++;
            System.out.println("Network Stable? "+flag);
        }
        // System.out.println("Iterations: "+iterations);
    }

    private void UpdateDV(int x, int y, int c) {
        active(x,y,c);
        while (true) {
            boolean flag = true;

            for ( int j=0; j<RouterCount; j++ ) {
                boolean temp = true;
                for ( int k=0; k<RouterCount; k++ ) {
                    boolean flag2;
                    if ( j != k ) {
                        flag2 = routerList.get(j).update(routerList.get(k));
                        if ( flag2 == false ) {
                            temp = false;
                        }
                    }
                } 
                stable[j] = temp;
            }
            System.out.println("DV Exchanged and Updated!");
            for ( int i=0; i<RouterCount; i++ ) {
                System.out.println(routerList.get(i));  
            }

            for ( int i=0; i<RouterCount; i++ ) {
                // if Router not stable, make it stable
                if ( stable[i] == false ) {
                    for ( int j=0; j<RouterCount; j++ ) {
                        for ( int k=0; k<RouterCount; k++ ) {
                            if ( i != j && j != k && i != k) {
                                if ( routerList.get(i).getGraph()[i][j] > routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j] ) {
                                    routerList.get(i).getGraph()[i][j] = routerList.get(i).getGraph()[i][k]+routerList.get(i).getGraph()[k][j];
                                }
                            }
                        }
                    }
                }  
            }
            flag = check();
            if ( flag == true ) {
                ThirdEye(0, 1, INFINITE);
                break;
            }
            System.out.println("Network Stable? "+flag);
        }
    
        for ( int i=0; i<RouterCount; i++ ) {
            System.out.println(routerList.get(i));
        }
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
        // System.out.print("Enter Edges: ");
        String input = Reader.next();
        String[] in = input.split(";");
        int e = in.length;
        for ( int i=0; i<e; i++ ) {
            String[] xyz = in[i].split(":");
            int r1 = Integer.parseInt(xyz[0]);
            int r2 = Integer.parseInt(xyz[1]);
            int d = Integer.parseInt(xyz[2]);

            q2.Padosi[r1][r2] = true;
            q2.Padosi[r2][r1] = true;
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

 3
 0:1:4;0:2:50;1:2:1
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