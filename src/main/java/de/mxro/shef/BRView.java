package de.mxro.shef;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.StyleSheet;

import de.mxro.shef.icons.TextEditIcons;


public class BRView extends InlineView implements ImageObserver {

    
    public static final String
    	TOP = "top",
    	TEXTTOP = "texttop",
    	MIDDLE = "middle",
    	ABSMIDDLE = "absmiddle",
    	CENTER = "center",
    	BOTTOM = "bottom";
    


    /**
     * creates a special sign for newline
     *
     * @param elem the element to create a view for
     */
    public BRView(Element elem) {
    	super(elem);
    	this.initialize(elem);
	final StyleSheet sheet = this.getStyleSheet();
	this.attr = sheet.getViewAttributes(this);
	
    }
    
    
    private void initialize( Element elem ) {
	synchronized(this) {
	    this.loading = true;
	    this.fWidth = this.fHeight = 0;
	}
	int width = 0;
	int height = 0;
	boolean customWidth = false;
	boolean customHeight = false;
	try {
	    this.fElement = elem;
        
	    // Request image from document's cache:
	    final AttributeSet attr = elem.getAttributes();
	    URL src;
		try {
			src = new URL("http://islocal.de");
		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    /*if( src != null ) {
		Dictionary cache = (Dictionary) getDocument().getProperty(IMAGE_CACHE_PROPERTY);
		if( cache != null )
		    fImage = (Image) cache.get(src);
		else
		    fImage = Toolkit.getDefaultToolkit().getImage(src);
	    }*/
	   
		this.fImage = TextEditIcons.singelton.getIcon("br.png").getImage();
		
	
	    // Get height/width from params or image or defaults:
	    height = this.getIntAttr(HTML.Attribute.HEIGHT,-1);
	    customHeight = (height>0);
	    if( !customHeight && this.fImage != null ) {
			height = this.fImage.getHeight(this);
		}
	    if( height <= 0 ) {
			height = DEFAULT_HEIGHT;
		}
		
	    width = this.getIntAttr(HTML.Attribute.WIDTH,-1);
	    customWidth = (width>0);
	    if( !customWidth && this.fImage != null ) {
			width = this.fImage.getWidth(this);
		}
	    if( width <= 0 ) {
			width = DEFAULT_WIDTH;
		}

	    // Make sure the image starts loading:
	    if( this.fImage != null ) {
			if( customWidth && customHeight ) {
				Toolkit.getDefaultToolkit().prepareImage(this.fImage,height,
								     width,this);
			} else {
				Toolkit.getDefaultToolkit().prepareImage(this.fImage,-1,-1,
								     this);
			}
		}
	
	    if( DEBUG ) {
		if( this.fImage != null ) {
			System.out.println("ImageInfo: new on "+src+
				       " ("+this.fWidth+"x"+this.fHeight+")");
		} else {
			System.out.println("ImageInfo: couldn't get image at "+
				       src);
		}
		if(this.isLink()) {
			System.out.println("           It's a link! Border = "+
				       this.getBorder());
		//((AbstractDocument.AbstractElement)elem).dump(System.out,4);
		}
	    }
	} finally {
	    synchronized(this) {
		this.loading = false;
		if (customWidth || this.fWidth == 0) {
		    this.fWidth = width;
		}
		if (customHeight || this.fHeight == 0) {
		    this.fHeight = height;
		}
	    }
	}
    }
    
    /**
     * Fetches the attributes to use when rendering.  This is
     * implemented to multiplex the attributes specified in the
     * model with a StyleSheet.
     */
    @Override
	public AttributeSet getAttributes() {
	return this.attr;
    }

    /** Is this image within a link? */
    boolean isLink( ) {
        //! It would be nice to cache this but in an editor it can change
        // See if I have an HREF attribute courtesy of the enclosing A tag:
	final AttributeSet anchorAttr = (AttributeSet)
	    this.fElement.getAttributes().getAttribute(HTML.Tag.A);
	if (anchorAttr != null)
		return anchorAttr.isDefined(HTML.Attribute.HREF);
	return false;
    }
    
    /** Returns the size of the border to use. */
    int getBorder( ) {
        return this.getIntAttr(HTML.Attribute.BORDER, this.isLink() ?DEFAULT_BORDER :0);
    }
    
    /** Returns the amount of extra space to add along an axis. */
    int getSpace( int axis ) {
    	return this.getIntAttr( axis==X_AXIS ?HTML.Attribute.HSPACE :HTML.Attribute.VSPACE,
    			   0 );
    }
    
    /** Returns the border's color, or null if this is not a link. */
    Color getBorderColor( ) {
    	final StyledDocument doc = (StyledDocument) this.getDocument();
        return doc.getForeground(this.getAttributes());
    }
    
    /** Returns the image's vertical alignment. */
    float getVerticalAlignment( ) {
	String align = (String) this.fElement.getAttributes().getAttribute(HTML.Attribute.ALIGN);
	if( align != null ) {
	    align = align.toLowerCase();
	    if( align.equals(TOP) || align.equals(TEXTTOP) )
			return 0.0f;
		else if( align.equals(BRView.CENTER) || align.equals(MIDDLE)
					       || align.equals(ABSMIDDLE) )
			return 0.5f;
	}
	return 1.0f;		// default alignment is bottom
    }
    
    boolean hasPixels( ImageObserver obs ) {
        return this.fImage != null && this.fImage.getHeight(obs)>0
			      && this.fImage.getWidth(obs)>0;
    }
    

    
    /** Look up an integer-valued attribute. <b>Not</b> recursive. */
    private int getIntAttr(HTML.Attribute name, int deflt ) {
    	final AttributeSet attr = this.fElement.getAttributes();
    	if( attr.isDefined(name) ) {		// does not check parents!
    	    int i;
 	    final String val = (String) attr.getAttribute(name);
 	    if( val == null ) {
			i = deflt;
		} else {
			try{
 	            i = Math.max(0, Integer.parseInt(val));
 	    	}catch( final NumberFormatException x ) {
 	    	    i = deflt;
 	    	}
		}
	    return i;
	} else
			return deflt;
    }
    

    /**
     * Establishes the parent view for this view.
     * Seize this moment to cache the AWT Container I'm in.
     */
    @Override
	public void setParent(View parent) {
	super.setParent(parent);
	this.fContainer = parent!=null ?this.getContainer() :null;
	if( parent==null && this.fComponent!=null ) {
	    this.fComponent.getParent().remove(this.fComponent);
	    this.fComponent = null;
	}
    }

    /** My attributes may have changed. */
    @Override
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
if(DEBUG) {
	System.out.println("ImageView: changedUpdate begin...");
}
    	super.changedUpdate(e,a,f);
    	final float align = this.getVerticalAlignment();
    	
    	final int height = this.fHeight;
    	final int width  = this.fWidth;
    	
    	this.initialize(this.getElement());
    	
    	final boolean hChanged = this.fHeight!=height;
    	final boolean wChanged = this.fWidth!=width;
    	if( hChanged || wChanged || this.getVerticalAlignment()!=align ) {
    	    if(DEBUG) {
				System.out.println("ImageView: calling preferenceChanged");
			}
    	    this.getParent().preferenceChanged(this,hChanged,wChanged);
    	}
if(DEBUG) {
	System.out.println("ImageView: changedUpdate end; valign="+this.getVerticalAlignment());
}
    }


    // --- Painting --------------------------------------------------------

    /**
     * Paints the image.
     *
     * @param g the rendering surface to use
     * @param a the allocated region to render into
     * @see View#paint
     */
    @Override
	public void paint(Graphics g, Shape a) {
	final Color oldColor = g.getColor();
	this.fBounds = a.getBounds();
        int border = this.getBorder();
	int x = this.fBounds.x + border + this.getSpace(X_AXIS);
	int y = this.fBounds.y + border + this.getSpace(Y_AXIS);
	int width = this.fWidth;
	int height = this.fHeight;
	final int sel = this.getSelectionState();
	
	 
		    
	// Draw image:
	if( this.fImage != null ) {
	    g.drawImage(this.fImage,x, y,width,height,this);
	    // Use the following instead of g.drawImage when
	    // BufferedImageGraphics2D.setXORMode is fixed (4158822).

	    //  Use Xor mode when selected/highlighted.
	    //! Could darken image instead, but it would be more expensive.
/*
	    if( sel > 0 )
	    	g.setXORMode(Color.white);
	    g.drawImage(fImage,x, y,
	    		width,height,this);
	    if( sel > 0 )
	        g.setPaintMode();
*/
	}
	
	// If selected exactly, we need a black border & grow-box:
	Color bc = this.getBorderColor();
	if( sel == 2 ) {
	    // Make sure there's room for a border:
	    final int delta = 2-border;
	    if( delta > 0 ) {
	    	x += delta;
	    	y += delta;
	    	width -= delta<<1;
	    	height -= delta<<1;
	    	border = 2;
	    }
	    bc = null;
	    g.setColor(Color.black);
	    // Draw grow box:
	    g.fillRect(x+width-5,y+height-5,5,5);
	}

	// Draw border:
	if( border > 0 ) {
	    if( bc != null ) {
			g.setColor(bc);
		}
	    // Draw a thick rectangle:
	    for( int i=1; i<=border; i++ ) {
			g.drawRect(x-i, y-i, width-1+i+i, height-1+i+i);
		}
	    g.setColor(oldColor);
	}
    }

    /** Request that this view be repainted.
        Assumes the view is still at its last-drawn location. */
    protected void repaint( long delay ) {
    	if( this.fContainer != null && this.fBounds!=null ) {
	    this.fContainer.repaint(delay,
	   	      this.fBounds.x,this.fBounds.y,this.fBounds.width,this.fBounds.height);
    	}
    }
    
    /** Determines whether the image is selected, and if it's the only thing selected.
    	@return  0 if not selected, 1 if selected, 2 if exclusively selected.
    		 "Exclusive" selection is only returned when editable. */
    protected int getSelectionState( ) {
    	final int p0 = this.fElement.getStartOffset();
    	final int p1 = this.fElement.getEndOffset();
	if (this.fContainer instanceof JTextComponent) {
	    final JTextComponent textComp = (JTextComponent)this.fContainer;
	    final int start = textComp.getSelectionStart();
	    final int end = textComp.getSelectionEnd();
	    if( start<=p0 && end>=p1 ) {
		if( start==p0 && end==p1 && this.isEditable() )
			return 2;
		else
			return 1;
	    }
	}
    	return 0;
    }
    
    protected boolean isEditable( ) {
    	return this.fContainer instanceof JEditorPane
    	    && ((JEditorPane)this.fContainer).isEditable();
    }
    
    /** Returns the text editor's highlight color. */
    protected Color getHighlightColor( ) {
    	final JTextComponent textComp = (JTextComponent)this.fContainer;
    	return textComp.getSelectionColor();
    }

    // --- Progressive display ---------------------------------------------
    
    // This can come on any thread. If we are in the process of reloading
    // the image and determining our state (loading == true) we don't fire
    // preference changed, or repaint, we just reset the fWidth/fHeight as
    // necessary and return. This is ok as we know when loading finishes
    // it will pick up the new height/width, if necessary.
    public boolean imageUpdate( Image img, int flags, int x, int y,
    				int width, int height ) {
    	if( this.fImage==null || this.fImage != img )
			return false;
    	    
    	// Bail out if there was an error:
        if( (flags & (ABORT|ERROR)) != 0 ) {
            this.fImage = null;
            this.repaint(0);
            return false;
        }
        
        // Resize image if necessary:
	short changed = 0;
        if( (flags & ImageObserver.HEIGHT) != 0 ) {
			if( ! this.getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT) ) {
		changed |= 1;
            }
		}
        if( (flags & ImageObserver.WIDTH) != 0 ) {
			if( ! this.getElement().getAttributes().isDefined(HTML.Attribute.WIDTH) ) {
		changed |= 2;
            }
		}
	synchronized(this) {
	    if ((changed & 1) == 1) {
		this.fWidth = width;
	    }
	    if ((changed & 2) == 2) {
		this.fHeight = height;
	    }
	    if (this.loading)
			// No need to resize or repaint, still in the process of
			// loading.
			return true;
	}
        if( changed != 0 ) {
            // May need to resize myself, asynchronously:
            if( DEBUG ) {
				System.out.println("ImageView: resized to "+this.fWidth+"x"+this.fHeight);
			}
	    
	    final Document doc = this.getDocument();
	    try {
	      if (doc instanceof AbstractDocument) {
		((AbstractDocument)doc).readLock();
	      }
	      this.preferenceChanged(this,true,true);
	    } finally {
	      if (doc instanceof AbstractDocument) {
		((AbstractDocument)doc).readUnlock();
	      }
	    }			
	      
	    return true;
        }
	
	// Repaint when done or when new pixels arrive:
	if( (flags & (FRAMEBITS|ALLBITS)) != 0 ) {
		this.repaint(0);
	} else if( (flags & SOMEBITS) != 0 ) {
		if( sIsInc ) {
			this.repaint(sIncRate);
		}
	}
        
        return ((flags & ALLBITS) == 0);
    }
					  /*        
    /**
     * Static properties for incremental drawing.
     * Swiped from Component.java
     * @see #imageUpdate
     */
    private static final boolean sIsInc = true;
    private static final int sIncRate = 100;

    // --- Layout ----------------------------------------------------------

    /**
     * Determines the preferred span for this view along an
     * axis.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @returns  the span the view would like to be rendered into.
     *           Typically the view is told to render into the span
     *           that is returned, although there is no guarantee.  
     *           The parent may choose to resize or break the view.
     */
    @Override
	public float getPreferredSpan(int axis) {
//if(DEBUG)System.out.println("ImageView: getPreferredSpan");
        final int extra = 2*(this.getBorder()+this.getSpace(axis));
        
	switch (axis) {
	case View.X_AXIS:
	    return this.fWidth+extra;
	case View.Y_AXIS:
	    return this.fHeight+extra;
	default:
	    throw new IllegalArgumentException("Invalid axis: " + axis);
	}
    }

    /**
     * Determines the desired alignment for this view along an
     * axis.  This is implemented to give the alignment to the
     * bottom of the icon along the y axis, and the default
     * along the x axis.
     *
     * @param axis may be either X_AXIS or Y_AXIS
     * @returns the desired alignment.  This should be a value
     *   between 0.0 and 1.0 where 0 indicates alignment at the
     *   origin and 1.0 indicates alignment to the full span
     *   away from the origin.  An alignment of 0.5 would be the
     *   center of the view.
     */
    @Override
	public float getAlignment(int axis) {
	switch (axis) {
	case View.Y_AXIS:
	    return this.getVerticalAlignment();
	default:
	    return super.getAlignment(axis);
	}
    }

    /**
     * Provides a mapping from the document model coordinate space
     * to the coordinate space of the view mapped to it.
     *
     * @param pos the position to convert
     * @param a the allocated region to render into
     * @return the bounding box of the given position
     * @exception BadLocationException  if the given position does not represent a
     *   valid location in the associated document
     * @see View#modelToView
     */
    @Override
	public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	final int p0 = this.getStartOffset();
	final int p1 = this.getEndOffset();
	if ((pos >= p0) && (pos <= p1)) {
	    final Rectangle r = a.getBounds();
	    if (pos == p1) {
		r.x += r.width;
	    }
	    r.width = 0;
	    return r;
	}
	return null;
    }

    /**
     * Provides a mapping from the view coordinate space to the logical
     * coordinate space of the model.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param a the allocated region to render into
     * @return the location within the model that best represents the
     *  given point of view
     * @see View#viewToModel
     */
    @Override
	public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	final Rectangle alloc = (Rectangle) a;
	if (x < alloc.x + alloc.width) {
	    bias[0] = Position.Bias.Forward;
	    return this.getStartOffset();
	}
	bias[0] = Position.Bias.Backward;
	return this.getEndOffset();
    }

    /**
     * Set the size of the view. (Ignored.)
     *
     * @param width the width
     * @param height the height
     */
    @Override
	public void setSize(float width, float height) {
    	// Ignore this -- image size is determined by the tag attrs and
    	// the image itself, not the surrounding layout!
    }
    
    /** Change the size of this image. This alters the HEIGHT and WIDTH
    	attributes of the Element and causes a re-layout. */
    protected void resize( int width, int height ) {
    	if( width==this.fWidth && height==this.fHeight )
			return;
    	
    	this.fWidth = width;
    	this.fHeight= height;
    	
    	// Replace attributes in document:
	final MutableAttributeSet attr = new SimpleAttributeSet();
	attr.addAttribute(HTML.Attribute.WIDTH ,Integer.toString(width));
	attr.addAttribute(HTML.Attribute.HEIGHT,Integer.toString(height));
	((StyledDocument)this.getDocument()).setCharacterAttributes(
			this.fElement.getStartOffset(),
			this.fElement.getEndOffset(),
			attr, false);
    }
    
    // --- Mouse event handling --------------------------------------------
    
 
    
    // --- Static icon accessors -------------------------------------------

    /**
     * Forces a line break.
     *
     * @return View.ForcedBreakWeight
     */
    @Override
	public int getBreakWeight(int axis, float pos, float len) {
		if (axis == X_AXIS)
			return ForcedBreakWeight;
		else
			return super.getBreakWeight(axis, pos, len);
    }
    
   
    
    @Override
	protected StyleSheet getStyleSheet() {
	final HTMLDocument doc = (HTMLDocument) this.getDocument();
	return doc.getStyleSheet();
    }

    // --- member variables ------------------------------------------------

    private AttributeSet attr;
    private Element   fElement;
    private Image     fImage;
    private int       fHeight,fWidth;
    private Container fContainer;
    private Rectangle fBounds;
    private Component fComponent;
    /** Set to true, while the receiver is locked, to indicate the reciever
     * is loading the image. This is used in imageUpdate. */
    private boolean   loading;
    
    // --- constants and static stuff --------------------------------

    private static final boolean DEBUG = false;
    
    //$ move this someplace public
    static final String IMAGE_CACHE_PROPERTY = "imageCache";
    
    // Height/width to use before we know the real size:
    private static final int
        DEFAULT_WIDTH = 32,
        DEFAULT_HEIGHT= 32,
    // Default value of BORDER param:      //? possibly move into stylesheet?
        DEFAULT_BORDER=  2;

}
