package Retrieval.core.utils;
import java.util.HashMap;

public class BooleanDictionary {
    // members
    public HashMap<Character,
            BooleanTermStruct> dictionary;
    // Map from docNo to docSize
    public HashMap<Integer, Integer> docSizes;
    private HashMap<Character, Integer> termSizeList;
    private Integer termSize;
    private int NDoc;
    private int lastDocNo=-1;

    // methods
    public BooleanDictionary() {
        docSizes= new HashMap<>();
        dictionary= new HashMap<Character, BooleanTermStruct>();
        termSizeList= new HashMap<>();
        termSize=0;
        NDoc=0;
        lastDocNo=0;
    }

    public boolean existChar(char c){
        return this.dictionary.containsKey(c);
    }

    public Double getMc(Character c){
        return (double)termSizeList.get(c)/(double)termSize;
    }

    public void addTerm(int docNo, char c){
        // first time meet term(new term)
        if(!dictionary.containsKey(c)){
            BooleanTermStruct term=new BooleanTermStruct(c);
            dictionary.put(c, term);
            termSizeList.put(c, 0);
        }
        // first time meet this doc(new doc)
        if(!docSizes.containsKey(docNo)){docSizes.put(docNo, 0);}
        docSizes.put(docNo, docSizes.get(docNo)+1);
        dictionary.get(c).addDoc(docNo);
        if(docNo!=lastDocNo){
            NDoc++;
            lastDocNo=docNo;
        }
        termSizeList.put(c, termSizeList.get(c)+1);
        termSize++;
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
