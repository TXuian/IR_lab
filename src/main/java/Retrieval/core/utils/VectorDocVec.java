package Retrieval.core.utils;
import java.util.HashMap;
import java.util.Map;

public class VectorDocVec {
    private HashMap<Character, Double> termList;

    public VectorDocVec(){
        termList=new HashMap<>();
    }

    public double getCountByChar(Character c){
        return termList.containsKey(c)?termList.get(c):0;
    }

    public HashMap<Character, Double> getTermList(){return termList;}

    public void addCountByChar(Character c){
        if(termList.containsKey(c)){
            termList.put(c, termList.get(c)+1);
        }else{
            termList.put(c, 1.0);
        }
    }

    public void normalizeMapType1(){
        double ret=0;
        for(Map.Entry en: termList.entrySet()){
            ret+=Math.pow((double) en.getValue(), 2);
        }
        ret=Math.sqrt(ret);
        for(Map.Entry en: termList.entrySet()){
            en.setValue((double)en.getValue()/ret);
        }
    }

    public void normalizeMapType2(){
        double maxF=0;
        for(Map.Entry en: termList.entrySet()){
            if((Double) en.getValue()>maxF){maxF=(Double)en.getValue();}
        }
        for(Character k: termList.keySet()){
            termList.put(k, termList.get(k)/maxF);
        }
    }

    public void normalizeMapType3(){
        double maxF=0;
        for(Map.Entry en: termList.entrySet()){
            if((Double) en.getValue()>maxF){maxF=(Double)en.getValue();}
        }
        for(Character k: termList.keySet()){
            termList.put(k, (0.5+(0.5*termList.get(k))/maxF));
        }
    }
}
