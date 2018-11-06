// Kaustav vats(2016048)

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class Q1_2016048 {
    private int N, E;
    private int[][] Graph;
    private int[] Distance;
    private int[] Parent;
    private boolean[] Visited;

    public Q1_2016048(int vertex, int edges) {
        this.N = vertex;
        this.E = edges;
        this.Graph = new int[N][N];
        this.Distance = new int[N];
        this.Parent = new int[N];
        this.Visited = new boolean[N];
        Initialize();
    }

    private void Initialize() {
        for ( int i=0; i<this.N; i++ ) {
            this.Distance[i] = Integer.MAX_VALUE;
            this.Visited[i] = false;
            this.Parent[i] = -1;
        }
    }

    private int ShortestDistance() {
        int min = Integer.MAX_VALUE;
        int indexMin = -1;
        for ( int i=0; i < this.N; i++ ) {
            if ( this.Distance[i] <= min && !this.Visited[i] ) {
                min = this.Distance[i];
                indexMin = i;
            }
        }
        return indexMin;
    }

    private void PrintSolution() {
        System.out.println("Vertex\tParent\tDistance\tPath");
        for ( int i=0; i<this.N; i++ ) {
            if ( !this.Visited[i] ) {
                continue;
            }
            System.out.println(i+"\t"+this.Parent[i]+"\t"+this.Distance[i]+"\t"+PrintPath(i));
        }
        System.out.println("\nAll Vertex Printed!\n");
    }

    private void Dijkstra(int sourceIndex) {
        this.Distance[sourceIndex] = 0;
        for ( int vertex1=0; vertex1 < this.N-1 ; vertex1++ ) {
            int indexMin = ShortestDistance();
            System.out.println("indexMin: "+indexMin);
            this.Visited[indexMin] = true;
            for ( int vertex2=0; vertex2 < this.N; vertex2++ ) {
                if ( this.Graph[indexMin][vertex2] != 0 && !this.Visited[vertex2] && this.Distance[indexMin] != Integer.MAX_VALUE && this.Distance[indexMin]+this.Graph[indexMin][vertex2] < this.Distance[vertex2] ) {        
                    this.Distance[vertex2] = this.Distance[indexMin] + this.Graph[indexMin][vertex2];
                    this.Parent[vertex2] = indexMin;
                }
            }
            PrintSolution();
        }
        PrintSolution();
    }

    private String PrintPath(int var) {
        String path = " -> "+var;
        while( this.Parent[var] != -1 ) {
            path = " -> "+this.Parent[var] + path;
            var = this.Parent[var];
        }
        return path;
    }

    public static void main (String[] args) throws IOException {
        Reader.init(System.in);
        System.out.println("Usage:\nInput should be in Below Format:->\n\tX:Y:Z\nWhere X & Y are Node, Z is Distance between these two nodes.");

        System.out.print("Enter no of Nodes: ");
        int n = Reader.nextInt();
        String input = Reader.next();
        String[] in = input.split(";");
        int e = in.length;
        // System.out.println("Edges "+e);
        Q1_2016048 q1 = new Q1_2016048(n, e);
        for ( int i=0; i<e; i++ ) {
            String[] xyz = in[i].split(":");
            int x = Integer.parseInt(xyz[0]);
            int y = Integer.parseInt(xyz[1]);
            int z = Integer.parseInt(xyz[2]);
            q1.Graph[x][y] = z;
            q1.Graph[y][x] = z;
        }

        // int n = Reader.nextInt();
        // int e = Reader.nextInt();
        
        // for( int i=0; i<e; i++ ) {
        //     int x = Reader.nextInt();
        //     int y = Reader.nextInt();
        //     int distance = Reader.nextInt();
        //     q1.Graph[x][y] = distance;
        //     q1.Graph[y][x] = distance;
        // }
        q1.Dijkstra(0);
    }
/**
Test Input
9 14
0 1 4
0 7 8
1 7 11
1 2 8
7 8 7
7 6 1
2 8 2
8 6 6
2 3 7
2 5 4
6 5 2
3 5 14
3 4 9
5 4 10

9 14
0:1:4;0:7:8;1:7:11;1:2:8;7:8:7;7:6:1;2:8:2;8:6:6;2:3:7;2:5:4;6:5:2;3:5:14;3:4:9;5:4:10
 */
}

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
