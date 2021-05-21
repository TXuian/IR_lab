package Retrieval.core.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

class DocStructComparatorByTf implements Comparator<BooleanDocNode>{

    @Override
    public int compare(BooleanDocNode o1, BooleanDocNode o2) {
        if(o1.tf==o2.tf){return 0;}
        return o1.tf>o2.tf?1:-1;
    }
}

public class BooleanTermStruct {
    public char term;
    public HashMap<Integer, BooleanDocNode> docList;
    public TreeSet<BooleanDocNode> docRanking;
    public int cf;
    public double idf;

    public BooleanTermStruct(char _term){
        this.term=_term;
        docList=new HashMap<Integer, BooleanDocNode>();
        docRanking= new TreeSet<>(new DocStructComparatorByTf());
        cf=0;
    }

    public boolean addDoc(int docNo){
        // first time this doc is scanned
        // add docNode to docList
        if(!docList.containsKey(docNo)){
            BooleanDocNode booleanDocNode_temp =new BooleanDocNode(docNo);
            docList.put(docNo, booleanDocNode_temp);
        }
        // plus tf in docNode
        docList.get(docNo).addTf();
        this.cf++;
        return true;
    }

    public int getDf(){
        return docList.size();
    }

    public void completeUpdate(int n, HashMap<Integer, Integer> doSizes){
        this.idf=Math.log(n/(getDf()+1));
        for(int docNo: docList.keySet()){
            docList.get(docNo).updateArgument(idf, doSizes.get(docNo));
        }

        for(Map.Entry en: docList.entrySet()){
            docRanking.add((BooleanDocNode) en.getValue());
        }
    }
}
