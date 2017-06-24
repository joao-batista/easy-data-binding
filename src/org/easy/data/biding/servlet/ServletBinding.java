package org.easy.data.biding.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easy.data.biding.util.BidingGenerator;


@WebServlet("/ServletBinding")
public class ServletBinding extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ServletBinding() {
        super();
    }

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BidingGenerator.generateBinding(this, request);
		super.service(request, response);
	}

}
