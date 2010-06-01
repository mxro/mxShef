/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mxro.shef;


import de.mxro.utils.FileHandler;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author mx
 */
public class MxroTextPane extends JTextPane {
    private final MxroTextPaneFactory factory;


    public Action getBoldAction() {
        return this.getActionMap().get("font-bold");
    }

     public void adjustSize() {

	}

    Vector<FileHandler> fileHandler;


     /**
	 * see GoogleDocsFileHandler
	 * @param handler
	 */
	public void addFileHandler(FileHandler handler) {
		fileHandler.add(handler);
	}


	public String handleFile(java.io.File file) {
		for (FileHandler handler : fileHandler) {
			if (handler.canHandle(file)) {
				return handler.uploadFile(file);
			}
		}
		return null;
	}


    public MxroTextPane initialize() {

        final StyleSheet css = ((HTMLEditorKit) this.getEditorKit()).getStyleSheet();
		css.addRule("p {margin-top: 0pt; }");
		css.addRule("p {padding-top: 0pt; }");
		//css.addRule("a {text-decoration:none; color:black}");
		this.putClientProperty(JTextPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);




        MyLinkController controller = new MyLinkController();
    	this.addMouseListener(controller);
    	this.addMouseListener(controller);

        this.addHyperlinkListener(new HyperlinkListener() {
            @Override
    		public void hyperlinkUpdate(HyperlinkEvent evt) {
    			if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
    			      //System.out.println("URL: " + evt.getURL());
    			    }
    			if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
    	            try {

    	            	Desktop d = Desktop.getDesktop();
    	        		d.browse(new URI(evt.getURL().toString()));
    	            } catch (Exception e) {
    	            }
    	        }
    	    }
    	});

        new EditorDropTarget(this);

        /*this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "insert-break");
		this.getActionMap().put("insert-break", new AbstractAction() {

			private static final long serialVersionUID = 1L;

            @Override
			public void  actionPerformed(ActionEvent e) {
				try {
					//System.out.println("break inserted");
                    final int offset = getCaretPosition();
					((HTMLEditorKit) getEditorKit())
                            .insertHTML((HTMLDocument) getDocument(), offset, "<br>", 0, 0, HTML.Tag.BR);

					setCaretPosition(offset+1);
					adjustSize();
				}
				catch (final Exception exc) {
					exc.printStackTrace();
				}
		    }
		});*/

        return this;
    }


    @Override
    public String getText() {
        String res = super.getText();
        // there seems to be a bug with google docs attaching this many times
        
            return res.replaceAll("<!--\\[if IE\\]>\n[\t ]*<\\?XML:NAMESPACE PREFIX = GDOC />\n[\t ]*<!\\[endif\\]-->\n", "");

        
    }



    @Override
    public void setText(String text) {
        
        super.setText(text);
    }

    public MxroTextPane(MxroTextPaneFactory factory) {

        fileHandler = new Vector<FileHandler>();
        this.factory = factory;


    }
}
