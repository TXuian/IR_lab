package Retrieval.core;
import Retrieval.core.utils.*;

import java.util.*;

import Retrieval.core.dao.InfoDAO;

;

class DocAnsComparatorByTi implements Comparator<BooleanDocAns>{
    @Override
    public int compare(BooleanDocAns o1, BooleanDocAns o2) {
        if(o1.sum_tf_idf!=o2.sum_tf_idf){
            return new Double(o1.sum_tf_idf).compareTo(new Double(o2.sum_tf_idf));
        }else{
            return new Integer(o1.docNo).compareTo(new Integer(o2.docNo));
        }
    }
}

class DocAnsComparatorByWi implements Comparator<BooleanDocAns>{

    @Override
    public int compare(BooleanDocAns da1, BooleanDocAns da2) {
        if(da1.sum_wf_idf!=da2.sum_wf_idf){
            return new Double(da1.sum_wf_idf).compareTo(new Double(da2.sum_wf_idf));
        }else{
            return new Integer(da1.docNo).compareTo(new Integer(da2.docNo));
        }
    }
}

class DocAnsComparatorBySimilarity implements Comparator<VectorDocAns>{

    @Override
    public int compare(VectorDocAns o1, VectorDocAns o2) {
        if(o1.getSimilarity()==o2.getSimilarity()){
            if(o1.getDocNo()==o2.getDocNo()){return 0;}
            else{return o1.getDocNo()>o2.getDocNo()?1:-1;}
        }else{
            return o1.getSimilarity()>o2.getSimilarity()?1:-1;
        }
    }
}

public class RetrievalCore {
    private BooleanDictionary booleanDictionary;
    private BooleanDictionary booleanDictionary_head;
    private BooleanDictionary booleanDictionary_author;
    private VectorDocCollection docCollection;
    private InfoDAO dao;

    // init functions
    public RetrievalCore(){
        try {
            dao = new InfoDAO("root", "qaz9705917");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        booleanDictionary =new BooleanDictionary();
        booleanDictionary_head =new BooleanDictionary();
        booleanDictionary_author =new BooleanDictionary();
        docCollection = new VectorDocCollection();
        generateDictionaries();
        generateDocCollection();
    }

    public void generateDocCollection(){
        ArrayList<DocStruct> docs= dao.getPoems();
        // for each doc in dataset
        for(DocStruct ds: docs){
            // add term in head
            for(int i=0; i<ds.head.length(); ++i){
                docCollection.addTerm(ds.getId(), ds.head.charAt(i));
            }
            // add term in content
            for(int i=0; i<ds.content.length(); ++i){
                docCollection.addTerm(ds.getId(), ds.content.charAt(i));
            }
        }
        // data set import finished

//        docCollection.normalizeDocMap(1);
//        docCollection.normalizeDocMap(2);
        docCollection.normalizeDocMap(3);
    }

    public void generateDictionaries(){
        ArrayList<DocStruct> docs= dao.getPoems();
        // get sentences
        for(DocStruct doc: docs) {
            // for each document
            handleSentence(doc.id, doc.content, booleanDictionary);
            handleSentence(doc.id, doc.head, booleanDictionary_head);
            handleSentence(doc.id, doc.author, booleanDictionary_author);
        }

        // update idf, tf-idf, wf-idf
        booleanDictionary.completeUpdate();
        booleanDictionary_author.completeUpdate();
        booleanDictionary_head.completeUpdate();
    }

    private void handleSentence(int docNo, String sentence, BooleanDictionary d){
        for(int i=0; i<sentence.length(); ++i) {
            // add char to dictionary
            d.addTerm(docNo, sentence.charAt(i));
        }
    }

    // chose 5 for each term in query by default
    public TreeSet<VectorDocAns> vectorSearch(String words){
        if(words.length()==0){return new TreeSet<VectorDocAns>();}

        VectorDocVec query=new VectorDocVec();
//        query.normalizeMapType1();
        HashSet<BooleanDocNode> docCandidate=new HashSet<>();
        // select candidates
        for(int i=0; i<words.length(); ++i){
            int j=0;
            // get candidates
            for(Iterator iter=booleanDictionary.getTerm(words.charAt(i)).docRanking.descendingIterator();
                iter.hasNext();){
                docCandidate.add((BooleanDocNode) iter.next());
                j++;
                if(j>=5){break;}
            }
            // init query
            query.addCountByChar(words.charAt(i));
        }

        PriorityQueue<VectorDocAns> heap=new PriorityQueue<>(5, new DocAnsComparatorBySimilarity());
        for(BooleanDocNode en: docCandidate){
            VectorDocAns docAns=new VectorDocAns();
            docAns.docNo= en.docNo;
//            docAns.setSimilarity(docCollection.getSimilarity(query, en.docNo));
            HashMap<Character, Double> idfMap=new HashMap<>();
            for(Character c: query.getTermList().keySet()){
                idfMap.put(c, booleanDictionary.getTerm(c).idf);
            }
            docAns.setSimilarity(docCollection.getSimilarityUsingIdf(query, en.docNo, idfMap));
            heap.add(docAns);
        }

        TreeSet<VectorDocAns> ret=new TreeSet<>(new DocAnsComparatorBySimilarity());
        ret.addAll(heap);
        return ret;
    }

    public TreeSet<BooleanDocAns> booleanSearch(String words){
        if(words.length()==0){return new TreeSet<BooleanDocAns>();}

        // read query
        HashSet<Character> characters= new HashSet<>();
        Set<Integer> ret=new HashSet<Integer>(booleanDictionary.getTerm(words.charAt(0)).docList.keySet());
        characters.add(words.charAt(0));
        Set<Integer> tempSet;
        for(int i=1;i<words.length();++i){
            // normal char
            if(words.charAt(i)!='|'){
                if(words.charAt(i)=='&'){continue;}
                // default and not op
                if(words.charAt(i)=='!'){
                    i++;
                    if(i>=words.length()){break;}
                    tempSet= booleanDictionary.getTerm(words.charAt(i)).docList.keySet();
                    characters.add(words.charAt(i));
                    ret.removeAll(tempSet);
                    continue;
                }
                // default and op
                tempSet= booleanDictionary.getTerm(words.charAt(i)).docList.keySet();
                characters.add(words.charAt(i));
                ret.retainAll(tempSet);
            }else{
                // or op
                i++;
                if(i>=words.length()){break;}
                tempSet= booleanDictionary.getTerm(words.charAt(i)).docList.keySet();
                characters.add(words.charAt(i));
                ret.addAll(tempSet);
            }
        }

        // get ranking info
        TreeSet<BooleanDocAns> ts_ans=new TreeSet<BooleanDocAns>(new DocAnsComparatorByTi());
        for(Integer docNo: ret){
            BooleanDocAns booleanDocAns =new BooleanDocAns();
            booleanDocAns.docNo=docNo;
            for(Character c: characters){
                if(!booleanDictionary.existChar(c)){continue;}
                booleanDocAns.sum_tf_idf+=(booleanDictionary.getTerm(c).docList.get(docNo)==null?
                        0:
                        booleanDictionary.getTerm(c).docList.get(docNo).tf_idf);
                booleanDocAns.sum_wf_idf+=(booleanDictionary.getTerm(c).docList.get(docNo)==null?
                        0:
                        booleanDictionary.getTerm(c).docList.get(docNo).wf_idf);
            }
            ts_ans.add(booleanDocAns);
        }

        return ts_ans;
    }

    public InfoDAO getDAO(){
        return this.dao;
    }
}
