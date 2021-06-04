package Retrieval.core.utils;
import java.util.HashMap;
import java.util.Map;

public class VectorDocCollection {
    private HashMap<Integer, VectorDocVec> docMap;

    public VectorDocCollection(){
        docMap=new HashMap<>();
    }

    public HashMap<Integer, VectorDocVec> getDocMap(){
        return this.docMap;
    }

    public void addTerm(Integer docId, Character cTerm){
        if(!docMap.containsKey(docId)){
            docMap.put(docId, new VectorDocVec());
        }
        // assume there is a vectorDocDictionary for docId
        docMap.get(docId).addCountByChar(cTerm);
    }

    public void normalizeDocMap(int type){
        for(Integer k: docMap.keySet()){
            invokeNormalization(k, type);
        }
    }

    public double getSimilarity(VectorDocVec queryVec, int docNo){
        double ret=0;
        VectorDocVec dv=docMap.get(docNo);
        for(Map.Entry en: queryVec.getTermList().entrySet()){
            ret+=((double) en.getValue()) * (dv.getCountByChar((Character) en.getKey()));
        }
        return ret;
    }

    public double getSimilarityUsingIdf(VectorDocVec queryVec, int docNo, HashMap<Character, Double> idfMap){
        double ret=0;
        VectorDocVec dv=docMap.get(docNo);
        for(Map.Entry en: queryVec.getTermList().entrySet()){
            ret+=((double) en.getValue()) *
                    (dv.getCountByChar((Character) en.getKey()) * idfMap.get((Character) en.getKey()));
        }
        return ret;
    }

    private void invokeNormalization(int k, int type){
        switch (type){
            case 3:
                docMap.get(k).normalizeMapType3();break;
            case 2:
                docMap.get(k).normalizeMapType2();break;
            default:
                docMap.get(k).normalizeMapType1();break;
        }
    }
}
