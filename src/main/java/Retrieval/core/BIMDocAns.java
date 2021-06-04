package Retrieval.core;

public class BIMDocAns {
    private int docNo = -1;
    private double RSV=0;

    public double getRSV() { return RSV; }

    public void setRSV(double prob) { this.RSV = prob; }

    public void setDocNo(int _docNo){ this.docNo=_docNo; }
    public int getDocNo(){ return this.docNo; }
}
