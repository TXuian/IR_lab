package Retrieval.core.utils;

import java.util.HashMap;

public class BIMTermProbList {
    private HashMap<Character, BIMTermProb> termProbList;

    public BIMTermProbList(){
        termProbList=new HashMap<>();
    }

    public void addTermProb(BIMTermProb tp){
        termProbList.put(tp.getTerm(), tp);
    }
    public HashMap<Character, BIMTermProb> getTermProbList() {
        return termProbList;
    }
}
