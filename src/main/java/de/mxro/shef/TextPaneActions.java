package de.mxro.shef;

import javax.swing.Action;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import net.atlanticbb.tantlinger.ui.text.actions.CopyAction;
import net.atlanticbb.tantlinger.ui.text.actions.CutAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLAlignAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInlineAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction;
import net.atlanticbb.tantlinger.ui.text.actions.PasteFormattedAction;
import org.bushe.swing.action.ActionList;
import org.bushe.swing.action.BasicAction;

public class TextPaneActions {

    protected ActionList list;
    protected BasicAction hyperlinkAction;
    Action boldAction;
    Action italicAction;
    Action underlineAction;

    /* ----------------------------------------------------------- */
    Action copyAction;
    Action cutAction;
    Action pasteAction;

    /* ----------------------------------------------------------- */
    Action leftAction;
    Action centerAction;
    Action rightAction;
    Action justifiedAction;

    private void initActionList() {
        list = new ActionList("editor-actions");


        // see HTMLEditorActionFactory

        hyperlinkAction = new net.atlanticbb.tantlinger.ui.text.actions.HTMLLinkAction();
        list.add(hyperlinkAction);


        boldAction = new HTMLInlineAction(HTMLInlineAction.BOLD);
        italicAction = new HTMLInlineAction(HTMLInlineAction.ITALIC);
        underlineAction = new HTMLInlineAction(HTMLInlineAction.UNDERLINE);

        list.add(boldAction);
        list.add(italicAction);
        list.add(underlineAction);

        cutAction = new CutAction();
        pasteAction = new PasteFormattedAction();
        copyAction = new CopyAction();

        list.add(cutAction);
        list.add(pasteAction);
        list.add(copyAction);

        leftAction = new HTMLAlignAction(HTMLAlignAction.LEFT);
        centerAction = new HTMLAlignAction(HTMLAlignAction.CENTER);
        rightAction = new HTMLAlignAction(HTMLAlignAction.RIGHT);
        justifiedAction = new HTMLAlignAction(HTMLAlignAction.JUSTIFY);

        list.add(leftAction);
        list.add(centerAction);
        list.add(rightAction);
        list.add(justifiedAction);

    }

    public TextPaneActions() {
        initActionList();
    }
    javax.swing.AbstractButton boldButton;

    public TextPaneActions registerBoldButton(javax.swing.AbstractButton boldButton) {
        this.boldButton = boldButton;
        return this;
    }
    javax.swing.AbstractButton italicButton;

    public TextPaneActions registerItalicButton(javax.swing.AbstractButton italicButton) {
        this.italicButton = italicButton;
        return this;
    }
    javax.swing.AbstractButton underlineButton;

    public TextPaneActions registerUnderlineButton(javax.swing.AbstractButton underlineButton) {
        this.underlineButton = underlineButton;
        return this;
    }
    /*
     * ========================================================================
     */
    javax.swing.AbstractButton copyButton;

    public TextPaneActions registerCopyButton(javax.swing.AbstractButton copyButton) {
        this.copyButton = copyButton;
        return this;
    }
    javax.swing.AbstractButton pasteButton;

    public TextPaneActions registerPasteButton(javax.swing.AbstractButton button) {
        this.pasteButton = button;
        return this;
    }
    javax.swing.AbstractButton cutButton;

    public TextPaneActions registerCutButton(javax.swing.AbstractButton button) {
        this.cutButton = button;
        return this;
    }
    /*
     * ========================================================================
     */
    javax.swing.AbstractButton rightJButton;

    public TextPaneActions registerRightJButton(javax.swing.AbstractButton button) {
        this.rightJButton = button;
        return this;
    }
    javax.swing.AbstractButton leftJButton;

    public TextPaneActions registerLeftJButton(javax.swing.AbstractButton button) {
        this.leftJButton = button;
        return this;
    }
    javax.swing.AbstractButton centerJButton;

    public TextPaneActions registerCenterJButton(javax.swing.AbstractButton button) {
        this.centerJButton = button;
        return this;
    }
    javax.swing.AbstractButton justifiedJButton;

    public TextPaneActions registerJustifiedJButton(javax.swing.AbstractButton button) {
        this.justifiedJButton = button;
        return this;
    }
    /*
     * ========================================================================
     */
    javax.swing.AbstractButton hyperlinkJButton;

    public TextPaneActions registerHyperlinkJButton(javax.swing.AbstractButton button) {
        this.hyperlinkJButton = button;
        return this;
    }

    /*
     * ========================================================================
     */
    private void setActionForButton(javax.swing.AbstractButton button, Action action) {
        if (button == null) {
            return;
        }
        String text = button.getText();
        button.setAction(action);
        button.setText(text);
    }

    public void assignActions(JTextPane pane) {

        setActionForButton(boldButton, boldAction);
        setActionForButton(italicButton, italicAction);
        setActionForButton(underlineButton, underlineAction);
        /* ----------------------------------------------------------- */

        setActionForButton(copyButton, copyAction);
        setActionForButton(cutButton, cutAction);
        setActionForButton(pasteButton, pasteAction);
        /* ----------------------------------------------------------- */

        setActionForButton(leftJButton, leftAction);
        setActionForButton(centerJButton, centerAction);
        setActionForButton(rightJButton, rightAction);
        setActionForButton(justifiedJButton, justifiedAction);
        //((HTMLTextEditAction) hyperlinkAction).setContext(new HashMap<String, Object>());
        //HTMLTextEditAction hyperlinkAction = ;
        setActionForButton(hyperlinkJButton, hyperlinkAction);

        list.putContextValueForAll(HTMLTextEditAction.EDITOR, pane);
        list.updateEnabledForAll();
    }

    private void updateState() {
        list.updateEnabledForAll();
    }

    public CaretListener getCaretHandler() {
        return new CaretHandler();
    }

    private class CaretHandler implements CaretListener {

        /* (non-Javadoc)
         * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
         */
        @Override
        public void caretUpdate(CaretEvent e) {
            updateState();
        }
    }
}
