package Retrieval.core.utils;

public class BooleanDocNode {
    public int docNo;
    public double tf;
    public double tf_idf;
    public double wf_idf;

    public BooleanDocNode(int docno){
        docNo=docno;
        tf=0;
        tf_idf=0;
        wf_idf=0;
    }

    public void addTf(){
        this.tf++;
    }

    public void updateArgument(double idf, Integer docSize){
//        this.tf/=docSize;
        double wf=0;
        if(tf!=0){
            wf=1+Math.log10(tf);
        }
        tf_idf=tf*idf;
        wf_idf=wf*idf;
    }
}
