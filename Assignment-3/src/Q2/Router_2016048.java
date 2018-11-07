// Kaustav Vats
import java.io.*;
import java.util.*;
import java.math.*;
import java.lang.*;


public class Router_2016048 {
    private int N;
    private int ID;
    private int[][] Graph;

    public Router_2016048(int n, int id) {
        this.N = n;
        this.ID = id;
        this.Graph = new int[this.N][this.N];
        Initialize();
    }

    private void Initialize() {
        for ( int i=0; i<this.N; i++ ) {
            for ( int j=0; j<this.N; j++ ) {
                this.Graph[i][j] = Integer.MAX_VALUE;
            }
        }
        this.Graph[this.ID][this.ID] = 0;
    }

    public int getN() {
        return this.N;
    }

    public int getID() {
        return this.ID;
    }

    public int[][] getGraph() {
        return this.Graph;
    }

    public boolean update(Router_2016048 router) {
        boolean flag = true;
        int routerID = router.ID;
        for ( int i=0; i<this.N; i++ ) {
            if ( this.Graph[routerID][i] != router.Graph[routerID][i] ) {
                this.Graph[routerID][i] = router.Graph[routerID][i];
                flag = false;
            }
        }
        return flag;
    }

    public Router_2016048 getNode() {
        Router_2016048 abc = new Router_2016048(this.N, this.ID);
        for ( int i=0; i<this.N; i++ ) {
            for ( int j=0; j<this.N; j++ ) {
                abc.Graph[i][j] = this.Graph[i][j];
            }
        }
        abc.Graph[this.ID][this.ID] = 0;
        return abc;
    }

    public String toString() {
        String out = this.ID + "\n";
        for ( int i=0; i<N; i++ ) {
            for ( int j=0; j<N; j++ ) {
                out += Graph[i][j] + " ";
            }
            out += "\n";
        }
        return out;
    }
}