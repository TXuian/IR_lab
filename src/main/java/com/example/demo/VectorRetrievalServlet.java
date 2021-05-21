package com.example.demo;

import Retrieval.core.BooleanDocAns;
import Retrieval.core.RetrievalCore;
import Retrieval.core.VectorDocAns;
import Retrieval.core.utils.DocStruct;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeSet;

@WebServlet(name = "VectorRetrievalServlet", value = "/VectorRetrievalServlet")
public class VectorRetrievalServlet extends HttpServlet {
    private RetrievalCore rCore= new RetrievalCore();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        // get post params
        String searchInfo="";
        searchInfo= request.getParameter("vectorSearchInput");

        TreeSet<VectorDocAns> ans=rCore.vectorSearch(searchInfo);
        LinkedHashMap<VectorDocAns, DocStruct> hm_docs=new LinkedHashMap<>();
        // handle tree-set

        VectorDocAns tempAns;
        for(Iterator iter = ans.descendingIterator(); iter.hasNext(); ){

            tempAns=(VectorDocAns)iter.next();
            DocStruct docStruct =rCore.getDAO().getPoemById(tempAns.docNo);
            hm_docs.put(tempAns, docStruct);
        }
        request.setAttribute("DocStruct", hm_docs);
        request.getRequestDispatcher("/showVectorResult.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}
