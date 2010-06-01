package de.mxro.shef;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

//import org.dts.spell.swing.JTextComponentSpellChecker;


import de.mxro.swing.JMyPanel;
import de.mxro.transferable.ClipboardFacade;
import de.mxro.transferable.CutAndPaste;
import de.mxro.shef.EditorDropTarget;
import de.mxro.utils.FileHandler;




public class MxroEditorPane extends JEditorPane {
	
	
	Vector<FileHandler> fileHandler;
	
	
private static final long serialVersionUID = 1L;


public static class MyEditorKit extends HTMLEditorKit {
		
	
		
	
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
			// TODO Auto-generated method stub
			return new MyHTMLFactory();
		}

		
	}
	
	public MxroEditorPane() {
		super();
		
		fileHandler = new Vector<FileHandler>();
		
		
		
		this.setOpaque(false);
		this.setContentType("text/html");
		final HTMLEditorKit kit = new MyEditorKit();
		
		this.setEditorKit(kit);
		kit.setDefaultCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.TEXT_CURSOR));
		
		this.setEditable(true);
		
		
		final StyleSheet css = kit.getStyleSheet();
		css.addRule("p {margin-top: 0pt; }");
		css.addRule("p {padding-top: 0pt; }");
		//css.addRule("a {text-decoration:none; color:black}");
		this.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		
		initializeEvents();
		
		new EditorDropTarget(this);
		
		
	}
	
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
	
    public void adjustSize() {
		
	}
	
	
    public void initializeEvents() {
    	
    	MyLinkController controller = new MyLinkController();
    	this.addMouseListener(controller);
    	this.addMouseListener(controller);
    	
    	
    	/*.addHyperlinkListener(new HyperlinkListener() {
    		public void hyperlinkUpdate(HyperlinkEvent evt) {
    			if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
    			      System.out.println("URL: " + evt.getURL());
    			    }
    			if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
    	            try {
    	                
    	            	Desktop d = Desktop.getDesktop();
    	        		d.browse(new URI(evt.getURL().toString()));
    	            } catch (Exception e) {
    	            }
    	        }
    	    }
    	});*/
    	
    	
    	this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.META_DOWN_MASK), "paste");
		this.getActionMap().put("paste", new AbstractAction() {
		    
			private static final long serialVersionUID = 1L;

			public void  actionPerformed(ActionEvent e) {
				
				try {
					
					final String text = ClipboardFacade.getText();
					if (text != null) {
						CutAndPaste.insertUnformatted(text, MxroEditorPane.this);
						adjustSize();
					}
				}
				catch (final Exception exc) {
					exc.printStackTrace();	
				}
		    }
		});
    	
    	
    	this.addCaretListener(new CaretListener() {

			public void caretUpdate(CaretEvent arg0) {
				if (arg0.getMark() > getDocument().getLength()-1) {
					
					if (getDocument().getLength()>0) {
						setCaretPosition(getDocument().getLength()-1);
					}
					
				}
				
			}
			
		});
    	
    	
    	this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "insert-break");
		this.getActionMap().put("insert-break", new AbstractAction() {
		    
			private static final long serialVersionUID = 1L;

			public void  actionPerformed(ActionEvent e) {
				try {
					final int offset = getCaretPosition();
					((HTMLEditorKit) getEditorKit()).insertHTML((HTMLDocument) getDocument(), offset, "<br>", 0, 0, HTML.Tag.BR);
					
					setCaretPosition(offset+1);
					adjustSize();
				}
				catch (final Exception exc) {
					exc.printStackTrace();	
				}
		    }
		});
    	
    	
    	
    	this.getDocument().addDocumentListener(new DocumentListener() {

			public void changedUpdate(DocumentEvent arg0) {
				
			}

			public void insertUpdate(DocumentEvent arg0) {
				
			}

			public void removeUpdate(DocumentEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					
					public void run() {
						if (!((HTMLDocument) MxroEditorPane.this.getDocument()).getCharacterElement(MxroEditorPane.this.getDocument().getLength()-1).getName().equals("br")) {
							try {
								final int offset = MxroEditorPane.this.getCaretPosition();
								((HTMLEditorKit) MxroEditorPane.this.getEditorKit()).insertHTML((HTMLDocument) MxroEditorPane.this.getDocument(), MxroEditorPane.this.getDocument().getLength(), "<br>", 0, 0, HTML.Tag.BR);
								MxroEditorPane.this.setCaretPosition(offset+1);
							} catch (final BadLocationException e1) {
								
								e1.printStackTrace();
							} catch (final IOException e1) {
								
								e1.printStackTrace();
							}
						}
					}
				});
			}
			
		});
    }


    /*@Override
     public boolean getScrollableTracksViewportWidth() {
            return false;
        }*/

    /*@Override
		public Dimension getPreferredSize() {
			
       Dimension superPrefSize =  super.getPreferredSize() ;
       System.out.println(superPrefSize.getWidth() + ":" +superPrefSize.getHeight());
       this.setSize(superPrefSize);
       this.setMinimumSize(superPrefSize);
      // this.getParent().setMinimumSize(superPrefSize);
       //this.getRootPane().getContentPane().
       return superPrefSize;

		}*/

}
