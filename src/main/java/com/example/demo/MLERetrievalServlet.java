package com.example.demo;

import Retrieval.core.BooleanDocAns;
import Retrieval.core.MLEDocAns;
import Retrieval.core.RetrievalCore;
import Retrieval.core.utils.DocStruct;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeSet;

@WebServlet(name = "MLERetrievalServlet", value = "/MLERetrievalServlet")
public class MLERetrievalServlet extends HttpServlet {
    private RetrievalCore rCore= new RetrievalCore();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        // get post params
        String searchInfo="";
        searchInfo= request.getParameter("MLESearchInput");

        TreeSet<MLEDocAns> ans=rCore.MLESearch(searchInfo);
        LinkedHashMap<MLEDocAns, DocStruct> hm_docs=new LinkedHashMap<>();
        // handle tree-set

        MLEDocAns tempAns;
        for(Iterator iter = ans.descendingIterator(); iter.hasNext(); ){

            tempAns=(MLEDocAns)iter.next();
            DocStruct docStruct =rCore.getDAO().getPoemById(tempAns.getDocNo());
            hm_docs.put(tempAns, docStruct);
        }
        request.setAttribute("DocStruct", hm_docs);
        request.getRequestDispatcher("/showMLEResult.jsp").include(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}
