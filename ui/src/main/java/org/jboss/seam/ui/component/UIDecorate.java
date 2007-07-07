package org.jboss.seam.ui.component;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.jboss.seam.ui.util.Decoration;

public abstract class UIDecorate extends UIComponentBase
{
   
   private static final String COMPONENT_TYPE = "org.jboss.seam.ui.Decorate";

   public boolean hasMessage()
   {
      String clientId = getInputClientId();
      if (clientId==null)
      {
         return false;
      }
      else
      {
         return getFacesContext().getMessages(clientId).hasNext();
      }
   }

   public String getInputId()
   {
      String id = getFor();
      if (id==null)
      {
         UIComponent evh = Decoration.getEditableValueHolder(this);
         return evh==null ? null : evh.getId();
      }
      else
      {
         return id;
      }
   }

   private String getInputClientId()
   {
      String id = getFor();
      if (id==null)
      {
         UIComponent evh = Decoration.getEditableValueHolder(this);
         return evh==null ? null : evh.getClientId( getFacesContext() );
      }
      else
      {
         UIComponent component = findComponent(id);
         return component==null ? null : component.getClientId( getFacesContext() );
      }
   }

   public abstract String getFor();
   

   public abstract void setFor(String forId);
   

   public UIComponent getDecoration(String name)
   {
      return Decoration.getDecoration(name, this);
   }
   
   public static UIDecorate newInstance()
   {
      return (UIDecorate) FacesContext.getCurrentInstance().getApplication().createComponent(COMPONENT_TYPE);
   }
   
}
