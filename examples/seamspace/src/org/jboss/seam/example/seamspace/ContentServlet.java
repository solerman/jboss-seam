package org.jboss.seam.example.seamspace;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;

/**
 * Serves images and other member content
 * 
 * @author Shane Bryzak
 */
public class ContentServlet extends HttpServlet
{
   private static final long serialVersionUID = -8461940507242022217L;
   
   private static final String IMAGES_PATH = "/images";

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
       throws ServletException, IOException
   {
      if (IMAGES_PATH.equals(request.getPathInfo()))
      {
         ContentLocal contentAction = (ContentLocal) Component.getInstance(ContentAction.class);
         
         MemberImage img = contentAction.getImage(Integer.parseInt(request.getParameter("id")));
         
         if (img != null)
         {
            response.setContentType(img.getContentType());
            response.getOutputStream().write(img.getData());
            response.getOutputStream().flush();
         }
      }
   }
}
