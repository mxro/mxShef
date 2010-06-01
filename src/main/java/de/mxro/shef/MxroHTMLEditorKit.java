/**
 * 
 */
package de.mxro.shef;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;



public class MxroHTMLEditorKit extends HTMLEditorKit {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static class MyHTMLFactory extends HTMLEditorKit.HTMLFactory {
		
		
		@Override
		public View create(Element elem) {
			if (elem.getName().equals("br"))
				return new BRView(elem);
			return super.create(elem);
		}
		
	}
	
	@Override
	public ViewFactory getViewFactory() {
		return new MyHTMLFactory();
	}

	
	
}