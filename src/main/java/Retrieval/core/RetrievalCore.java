package Retrieval.core;
import Retrieval.core.utils.*;

import java.util.*;

import Retrieval.core.dao.InfoDAO;

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

class DocAnsComparatorByBIMProb implements Comparator<BIMDocAns>{

    @Override
    public int compare(BIMDocAns o1, BIMDocAns o2) {
        if(o1.getRSV()==o2.getRSV()){
            if(o1.getDocNo()==o2.getDocNo()){return 0;}
            else{return o1.getDocNo()>o2.getDocNo()?1:-1;}
        }else{
            return o1.getRSV()>o2.getRSV()?1:-1;
        }
    }
}

class DocAnsComparatorByMLEProb implements Comparator<MLEDocAns>{

    @Override
    public int compare(MLEDocAns o1, MLEDocAns o2) {
        if(o1.getMLEProb()==o2.getMLEProb()){
            if(o1.getDocNo()==o2.getDocNo()){return 0;}
            else{return o1.getDocNo()>o2.getDocNo()?1:-1;}
        }else{
            return o1.getMLEProb()>o2.getMLEProb()?1:-1;
        }
    }
}

public class RetrievalCore {
    private BooleanDictionary booleanDictionary;
    private BooleanDictionary booleanDictionary_head;
    private BooleanDictionary booleanDictionary_author;
    private VectorDocCollection docCollection;
    private BIMTermProbList termProbList;
//    private
    private InfoDAO dao;
    private final int k=5;
    private final double V=30;
    private final Double MLELambda=0.3;

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
        termProbList= new BIMTermProbList();
        generateDictionaries();
        generateDocCollection();
        generateBIMTermList();
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

    public void generateBIMTermList(){
        // for every term
        for(Character term: booleanDictionary.dictionary.keySet()){
            BIMTermProb termProb=new BIMTermProb();
            termProb.setTerm(term);
            // get relevant set and unRelevant set
            TreeSet<Integer> relevantSet= new TreeSet(); //relevant set
            relevantSet.addAll(booleanDictionary.getTerm(term).docList.keySet());
            TreeSet<Integer> unRelevantSet= new TreeSet<>(); //unRelevant set
            unRelevantSet.addAll(docCollection.getDocMap().keySet());
            unRelevantSet.removeAll(relevantSet);
            // count prob-s
//            // original setting
//            // prob. of occurrence in relevant documents for query
//            termProb.setRi(countCiByTermInSet(term, relevantSet));
//            // prob. of occurrence in non-relevant documents for query
//            termProb.setRi(countCiByTermInSet(term, unRelevantSet));
            // type 2
//            termProb.setPi((relevantSet.size()+0.5)/(V+1));
//            termProb.setRi((unRelevantSet.size()+0.5)/(docCollection.getDocMap().size()-V+1));
            // type 3
            termProb.setPi(0.5);
            termProb.setRi((relevantSet.size()+0.5)/(docCollection.getDocMap().size()+0.5));
//            termProb.setPi(unRelevantSet.size()+0.5);
//            termProb.setRi(relevantSet.size()+0.5);
            termProbList.addTermProb(termProb);
        }
    }

    private double countCiByTermInSet(Character term, TreeSet<Integer> set) {
        double sumOfTermInSet=0.0, sumOfWordsInSet=0.0;
        for(Iterator<Integer> itr = set.descendingIterator(); itr.hasNext();){
            // for every document in unRelevant set
            // get size of document
            Integer unRelevantDocNo=itr.next();
            for(Iterator inner_itr=docCollection.getDocMap().get(unRelevantDocNo).termCountList.entrySet().iterator();
                inner_itr.hasNext();){
                // for terms in doc
                Map.Entry<Character, Integer> countOfTermInDoc = (Map.Entry<Character, Integer>) inner_itr.next();
                sumOfWordsInSet+=countOfTermInDoc.getValue();
                if(term==countOfTermInDoc.getKey()){
                    sumOfTermInSet+=countOfTermInDoc.getValue();
                }
            }
        }
        return (sumOfTermInSet+0.5)/(sumOfWordsInSet+1.0);
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
            for(Iterator iter=booleanDictionary.getTerm(words.charAt(i)).
                    docRanking.descendingIterator();
                iter.hasNext();){
                docCandidate.add((BooleanDocNode) iter.next());
                j++;
                if(j>=k){break;}
            }
            // init query
            query.addCountByChar(words.charAt(i));
        }

        PriorityQueue<VectorDocAns> heap=new PriorityQueue<>(k, new DocAnsComparatorBySimilarity());
        for(BooleanDocNode en: docCandidate){
            VectorDocAns docAns=new VectorDocAns();
            docAns.docNo= en.docNo;
            // get idf info for each term
            HashMap<Character, Double> idfMap=new HashMap<>();
            for(Character c: query.getTermList().keySet()){
                idfMap.put(c, booleanDictionary.getTerm(c).idf);
            }
            // set and calculate similarity for query and each doc
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

    public TreeSet<BIMDocAns> BIMSearch(String words){
        if(words.length()==0){return new TreeSet<BIMDocAns>();}

        // generate query set
        HashSet<Character> querySet= new HashSet<>();
        for(int i=0 ;i<words.length(); ++i){
            querySet.add(words.charAt(i));
        }

        // get candidates
        HashSet<BooleanDocNode> docCandidate=new HashSet<>();
        // select candidates
        for(int i=0; i<words.length(); ++i){
            int j=0;
            // get candidates
            for(Iterator iter=booleanDictionary.getTerm(words.charAt(i)).
                    docRanking.descendingIterator();
                iter.hasNext();){
                docCandidate.add((BooleanDocNode) iter.next());
                j++;
                if(j>=k){break;}
            }
        }

        TreeSet<BIMDocAns> ret=new TreeSet<>(new DocAnsComparatorByBIMProb());
        for(BooleanDocNode candidate: docCandidate){
            // for each candidate
            BIMDocAns docAns= new BIMDocAns();
            docAns.setDocNo(candidate.docNo);
            // get doc content
            DocStruct docStruct=dao.getPoemById(candidate.docNo);
            double docRSV=0.0;
            // count RSV for current doc
            for(int i=0; i<docStruct.content.length(); ++i){
                // for each term in doc
                char termInDoc=docStruct.content.charAt(i);
                BIMTermProb curTermProb=termProbList.getTermProbList().get(termInDoc);
                if(querySet.contains(termInDoc)){
                    docRSV+=Math.log(curTermProb.getPi()*
                            (1-curTermProb.getRi())/(curTermProb.getRi()*(1-curTermProb.getPi())));
                }
//                if(querySet.contains(termInDoc)){
//                    docRSV+=Math.log10(curTermProb.getPi()/
//                            curTermProb.getRi());
//                }else{
//                    docRSV+=Math.log10((1-curTermProb.getPi())/(1-curTermProb.getRi()));
//                }
            }
            docAns.setRSV(docRSV);
            ret.add(docAns);
        }
        return ret;
    }

    public TreeSet<MLEDocAns> MLESearch(String words){
        if(words.length()==0){return new TreeSet<MLEDocAns>();}

        TreeSet<MLEDocAns> ret= new TreeSet<>(new DocAnsComparatorByMLEProb());

        // get candidates
        HashSet<BooleanDocNode> docCandidate=new HashSet<>();
        // select candidates
        for(int i=0; i<words.length(); ++i){
            int j=0;
            // get candidates
            for(Iterator iter=booleanDictionary.getTerm(words.charAt(i)).
                    docRanking.descendingIterator();
                iter.hasNext();){
                docCandidate.add((BooleanDocNode) iter.next());
                j++;
                if(j>=k){break;}
            }
        }

        for(BooleanDocNode docNode: docCandidate){
            // for each doc
            MLEDocAns docAns= new MLEDocAns();
            docAns.setDocNo(docNode.docNo);
            // count MLE prob
            double MLEProb=1;
            char c=' ';
            for(int i=0; i<words.length(); ++i){
                c=words.charAt(i);
                MLEProb*=(MLELambda*docCollection.getDocMap().get(docNode.docNo).getMd(c)+
                        (1-MLELambda)* booleanDictionary.getMc(c));
            }
            docAns.setMLEProb(MLEProb);
            ret.add(docAns);
        }

        return ret;
    }

    public InfoDAO getDAO(){
        return this.dao;
    }
}
