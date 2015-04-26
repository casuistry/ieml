package Structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Utilities {

	//Turns a String into String[]
    public static String[] getChunks(String s){
        String[] a = s.split("");        
        if (a != null && a.length > 0){
            if (a[0] != null && a[0].isEmpty()){
                return Arrays.copyOfRange(a, 1, a.length);
            }
        }
        return a;
    }
        
    //number of elements, start in first, start in second.
    public static List<int[]> convIndex(int s1, int s2, boolean full) throws Exception {
        
        ArrayList<int[]> result = new ArrayList<int[]>();                       
        
        int lower_base = Integer.MIN_VALUE, upper_base = Integer.MAX_VALUE;
        int lower_frag = Integer.MIN_VALUE, upper_frag = Integer.MAX_VALUE;
        
        int start = full ? s2 - 1 : 0;
        int stop = full ? s1 : s1 + s2 - 1;
            
        for (int i = start; i < stop; i++) {
            
            int offset_base = i - s2 + 1;
            
            if (offset_base <= 0){
                lower_base = 0;
                lower_frag = - offset_base;
            }
            else {
                lower_base = Math.min(offset_base, s1);
                lower_frag = 0;
            }
            
            upper_base = Math.min(s2 + offset_base, s1);
            upper_frag = Math.min(s2, s1 - offset_base);            
            
            if (upper_base - lower_base != upper_frag - lower_frag){
                throw new Exception("Indexes do not match up");
            }
            
            result.add(new int[] {upper_base - lower_base, lower_base, lower_frag});
        }  
        
        return result;
    }
}
