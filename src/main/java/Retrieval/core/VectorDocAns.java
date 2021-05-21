package Retrieval.core;

public class VectorDocAns {
    public int docNo = -1;
    private double similarity=0;

    public void setSimilarity(double d){this.similarity=d;}
    public double getSimilarity(){return this.similarity;}
    public int getDocNo(){return docNo;}
}
