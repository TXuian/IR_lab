package Retrieval.core.utils;

public class DocStruct {
    public int id;
    public String head;
    public String author;
    public String content;

    public void setId(int i){this.id=i;}
    public void setHead(String s){this.head=s;}
    public void setAuthor(String s){this.author=s;}
    public void setContent(String s){this.content=s;}
    public String getHead(){return head;}
    public String getAuthor(){return author;}
    public String getContent(){return content;}
    public int getId(){return id;}
}
