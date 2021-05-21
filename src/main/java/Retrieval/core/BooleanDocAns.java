package Retrieval.core;

public class BooleanDocAns {
    public int docNo = -1;
    public double sum_tf_idf = 0;
    public double sum_wf_idf = 0;

    public int getDocNo(){return docNo;}
    public double getSum_tf_idf(){return this.sum_tf_idf;}
    public double getSum_wf_idf() {return this.sum_wf_idf;}
}
