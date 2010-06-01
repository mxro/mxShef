/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mxro.shef;


import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;
import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction;
import org.bushe.swing.action.ActionList;
import org.bushe.swing.action.BasicAction;

/**
 *
 * @author mx
 */
public class MxroTextPaneFactory {
    private TextPaneActions buttons = new TextPaneActions();

    public TextPaneActions getButtons() {
        return buttons;
    }

    public void selectTextPane(JTextPane pane) {
        this.getButtons().assignActions(pane);
    }

    
    private class FocusHandler implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            if (e.getComponent() instanceof JTextPane) {
                selectTextPane((JTextPane) e.getComponent());
            }
        }

        @Override
        public void focusLost(FocusEvent e) {

            if (e.getComponent() instanceof JEditorPane) {
            }
        }
    }

    public MxroTextPane createTextPane() {
        MxroTextPane pane = new MxroTextPane(this);
        pane.setEditorKitForContentType("text/html", new WysiwygHTMLEditorKit());

        pane.addFocusListener(new FocusHandler());
        pane.addCaretListener(this.getButtons().getCaretHandler());
        /*HTMLDocument htmlDoc = (ExtendedHTMLDocument) (htmlKit.createDefaultDocument());
        htmlDoc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        htmlDoc.setPreservesUnknownTags(true);*/
        //StyleSheet styleSheet = htmlDoc.getStyleSheet();

        pane.setContentType("text/html");
        HTMLDocument document = (HTMLDocument) pane.getDocument();
        CompoundUndoManager cuh = new CompoundUndoManager(document, new UndoManager());
        document.addUndoableEditListener(cuh);
        //this.set

        //this.setSourceDocument(htmlDoc);
        //((HTMLEditorKit) pane).getEditorKit().setDefaultCursor(new Cursor(Cursor.TEXT_CURSOR));




        pane.initialize();
        //this.jtpMain = pane;

        return pane;
    }

   
    private static MxroTextPaneFactory singelton;

    public static MxroTextPaneFactory getInstance() {
        if (singelton == null) {
            singelton = newInstance();
        }
        return singelton;
    }

    protected static MxroTextPaneFactory newInstance() {

        MxroTextPaneFactory ekit = new MxroTextPaneFactory();

        return ekit;
    }

    /*public static class MyEditorKit extends ExtendedHTMLEditorKit {

        private static final long serialVersionUID = 1L;

        public static class MyHTMLFactory extends HTMLEditorKit.HTMLFactory {

            @Override
            public View create(Element elem) {
                if (elem.getName().equals("br")) {
                    return new BRView(elem);
                }
                return super.create(elem);
            }
        }

        @Override
        public ViewFactory getViewFactory() {
            // TODO Auto-generated method stub
            return new MyHTMLFactory();
        }
    }*/
}
