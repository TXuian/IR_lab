package Retrieval.core.utils;
import java.util.HashMap;

public class BooleanDictionary {
    // members
    public HashMap<Character,
            BooleanTermStruct> dictionary;
    public HashMap<Integer, Integer> docSizes;
    private int NDoc;
    private int lastDocNo=-1;

    // methods
    public BooleanDictionary() {
        docSizes= new HashMap<>();
        dictionary = new HashMap<Character, BooleanTermStruct>();
    }

    public boolean existChar(char c){
        return this.dictionary.containsKey(c);
    }

    public void addTerm(int docNo, char c){
        if(!dictionary.containsKey(c)){
            BooleanTermStruct term=new BooleanTermStruct(c);
            dictionary.put(c, term);
        }
        if(!docSizes.containsKey(docNo)){docSizes.put(docNo, 0);}
        docSizes.put(docNo, docSizes.get(docNo)+1);
        dictionary.get(c).addDoc(docNo);
        if(docNo!=lastDocNo){
            NDoc++;
            lastDocNo=docNo;
        }
    }

    public BooleanTermStruct getTerm(char c){
        if(!dictionary.containsKey(c)){return new BooleanTermStruct(' ');}
        BooleanTermStruct term=dictionary.get(c);
        return term;
    }

    public void completeUpdate(){
        for (Character c: dictionary.keySet()) {
            dictionary.get(c).completeUpdate(NDoc, docSizes);
        }
    }
}
