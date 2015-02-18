package Structures;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class CompMatch implements Comparator<CompMatch>{
    
    public int k;
    public int[] v;
    
    public CompMatch(){
        k = 0;
        v = null;
    }
    
    public CompMatch(int key, int[] value){
        k = key;
        v = value;
    }
    
    public int compare(CompMatch x, CompMatch y) {
        
        int key = y.k-x.k; //reverse order
        
        if (key == 0){
            return x.v[0]-y.v[0]; //shorter at the top            
        }
        
        return key;
    }
    
    // returns number of matches for specified arrays at specified indexes
    public static <T extends Comparable<T>> int compArray(int[] l, T[] a, T[] b) {
        
        int max = 0;
        
        for (int i = 0; i < l[0]; i++){                
            max += (a[i+l[1]].compareTo(b[i+l[2]]) == 0) ? 1 : 0; 
        }
    
        return max;
    }
    
    // returns number of matches for specified arrays at specified indexes and the indexes for that max
    public static <T extends Comparable<T>> PriorityQueue<CompMatch> compArray(List<int[]> list, T[] a, T[] b, PriorityQueue<CompMatch> q) {
        
        PriorityQueue<CompMatch> queue = (q == null) ? new PriorityQueue<CompMatch>(10, new CompMatch()) : q;
               
        for (int[] l : list){            
            int max = compArray(l, a, b);            
            queue.add(new CompMatch(max, l));
        }
        
        return queue;
    }
}
