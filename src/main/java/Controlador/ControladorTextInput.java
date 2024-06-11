/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;


import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextPane;
  
     

/**
 * Allows to use the {@link #textPaneX} (from witch the Automata takes its "commands") in two ways
 *    - As a textInput.
 *    - As a view of the current 'step' during the 'step by step' mode using a HTML-Style.
 *
 * During the 'step by step' Mode, if you Double-Click it: the styledHTML is transformed into Plain Text and will be modifiable again.
 * 
 * @author Jtorr
 */
public class ControladorTextInput implements MouseListener{    
  
    /**
     * The {@link JTextPane} that is controlled.
     */
    private JTextPane textPaneX = null;
    /**
     * The input text in plainText format.
     * 
     * Before the textInput is turned into a Viewer (HTML-Mode). 
     * its 'plainText' needs to be retrieved, because during the HTML-Mode it will be impossible 
     * to get the index of the character (that represent the currentCommand in the 'step by step' Mode).
     * 
     * @see #setStepIndex(int, boolean) 
     */
    private String plainTextFormat = "";
    
    // To enable/disable the html style, the we need change the 'contentType' of the textPane
    /**
     * Plain text format
     */
    private final String CONTENT_TYPE__PLAIN_TEXT = "text/plain";   // PLAIN Text formatMode
    /**
     * HTML text format
     */
    private final String CONTENT_TYPE__HTML_STYLE = "text/html";    // HTML Style formatMode 
  
    /**
     * @see #setStepIndex(int, boolean) 
     */
    private final String VALID_TRANSACTION__COLOR = "green";
    /**
     * @see #setStepIndex(int, boolean) 
     */
    private final String INVALID_TRANSACTION__COLOR = "red";
   
    
    
    // INIT --------------------------------------------------------------------
    /**
     * Main constructor.
     * 
     * @param textPane the textInput to controll and use in both ways: input and viewer.
     */
    public ControladorTextInput(JTextPane textPane){
        this.textPaneX = textPane;
        this.addListeners();
        this.textPaneX.setEditable(true);

        //this.enableHTML();
        //this.disableHTML();
    }
    /**
     * Init listeners
     */
    private void addListeners(){
        this.textPaneX.addMouseListener(this);   
        //this.textPaneX.setFocusTraversalKeysEnabled(false);
    }
    
   
 
    // SETTERs -----------------------------------------------------------------
    /**
     * Colours the character in the {@code stepIndex} position. The {@code foundPath} flag
     * lets us to know if that character (that represents a {@link Modelo.Interfaces.Transaction#command} )
     * is a badEnd or not; depending on that flag the character is coloured in a different colour.
     * 
     * @param stepIndex Represents the index of the current command during the 'step by step' "animation".  Its used to know witch character of the textInput we need to colour.
     * @param isBadEnd If that 'command' has a badEnd or not; its used to colour in different colours.
     * 
     * @see #VALID_TRANSACTION__COLOR
     * @see #INVALID_TRANSACTION__COLOR
     */
    public void setStepIndex(int stepIndex, boolean isBadEnd){ 
        
        if(this.plainTextFormat.isEmpty()==false){
            //System.out.println("stepIndex: "+stepIndex);
            final String COMMAND_COLOR = (isBadEnd)? this.INVALID_TRANSACTION__COLOR:this.VALID_TRANSACTION__COLOR;

            String prevPart = "", nextPart =this.getText();
            Character commandX = null;

            // [EJ] "<strong>T<span style='color:red;'>E</span>st</strong>"
            if(stepIndex>0){
                if(stepIndex==0){
                    commandX = this.plainTextFormat.charAt(0);
                    nextPart = this.plainTextFormat.substring(0);            
                }
                else{    
                    prevPart = this.plainTextFormat.substring(0, stepIndex-1);
                    commandX = this.plainTextFormat.charAt(stepIndex-1);
                    nextPart = this.plainTextFormat.substring(stepIndex);
                }
            }
            
            // To be able to write html tags and not interfere with the html format of the HTML-Mode.
            prevPart = prevPart.replace("<", "&lt;");
            prevPart = prevPart.replace(">", "&gt;");
            nextPart = nextPart.replace("<", "&lt;");
            nextPart = nextPart.replace(">", "&gt;");
            String commandX_aux = (commandX!=null)? commandX+"":"";
            if (commandX_aux.contains("<")){commandX_aux = "&lt;";}
            else if (commandX_aux.contains(">")){commandX_aux = "&gt;";}
            

            String formattedString ="<strong style='font-size:28px; font-family:serif;'>"+ prevPart + "<span style='color:"+COMMAND_COLOR+";'>"+commandX_aux+"</span>"+nextPart+"</strong>";
            this.textPaneX.setText(formattedString);
        }
    }
    /**
     * Turns the textInput in a 'Viewer' (HTML-Mode)).
     * Locks the textInput and turns its format into an HTML-Style.
     * 
     * (NOTE: before change to the HTML-Format, the plainText is saved in {@link #plainTextFormat})
     * @see #plainTextFormat
     */
    public void enableHTML(){
        if(this.isPlainTextMode()){
            this.plainTextFormat = this.getText();
            if(this.plainTextFormat.isEmpty()==false){
                this.textPaneX.setContentType(this.CONTENT_TYPE__HTML_STYLE);
                this.textPaneX.setEditable(false);
                this.textPaneX.setText("<strong>"+"<span>"+this.plainTextFormat+"</span>"+"</strong>");
            }
        }
    }
    /**
     * Turns the textInput in its original mode (as a plainText input).
     * Unlocks the textInput: passing from the HTMLStyle to PlainText
     * NOTE: To do so, you'll need to doubleClick the textInput (so "simple" click wont work).
     * (NOTE: also returns ist original plainText)
     * 
     * @see #plainTextFormat
     * @see #mouseClicked(java.awt.event.MouseEvent) 
     */
    public void disableHTML(){
        if(this.isPlainTextMode()==false){
            this.textPaneX.setContentType(this.CONTENT_TYPE__PLAIN_TEXT);
            this.textPaneX.setText(" ");
            this.textPaneX.setCaretColor(new java.awt.Color(0, 0, 0));     
            this.textPaneX.setCaretPosition(0);
            this.textPaneX.setEditable(true);
            this.textPaneX.setText(this.plainTextFormat);
            this.textPaneX.requestFocus();       
            
            // To keep showing the 'caret'
            Container lastParent = this.textPaneX.getParent();
            while(lastParent.getParent()!=null){ lastParent = lastParent.getParent(); }
            lastParent.requestFocus();
        }
    }
     


            
    // GETTERs -----------------------------------------------------------------   
    /**
     * The input that an {@link  Modelo.Interfaces.AbstractAutomata} will use as commands to travel throught {@link  Modelo.Interfaces.Transaction}'s.
     * (And is needed to 'run' it)
     * 
     * @return the textInput that the {@link  Modelo.Interfaces.AbstractAutomata} will use as a list of 'commands'. During 'ViewerMode' returns {@link #plainTextFormat} instead of the text in htmlStyle.
     * @see  Modelo.Interfaces.AbstractAutomata#runAutomata(java.lang.String) 
     * @see  Modelo.Interfaces.Transaction#command
     * @see  javax.swing.JTextPane#getText() 
     * @see #plainTextFormat
     */
    public String getText(){   
      
        String textInput = null;
        if (this.isPlainTextMode()){ textInput = this.textPaneX.getText().trim(); }
        else { textInput = this.plainTextFormat; }
        
        return textInput;
    }
   
 
 
    // UTILs -------------------------------------------------------------------
    /**
     * Let you know in what 'state' is: as a textInput or as a 'step by step' viewer (HTML-Mode)
     * NOTE: since during the 'HTML-Mode' the text cant be edited, we simply use that flag ('isEditable') to check the mode.
     * 
     * @return {@code true} if it works as a textInput; {@code false} if it works as a 'Viewer'.
     */
    private boolean isPlainTextMode() { return this.textPaneX.isEditable(); }
    /**
     * If during HTML-Mode the {@link #textPaneX} is double-clicked: disable the HTML-Mode.
     * 
     * @param evt The {@link MouseEvent} that triggers the {@link MouseListener}.
     * @see #disableHTML() 
     */
    @Override   
    public void mouseClicked(MouseEvent evt) {
        // (fromInternet) [SOURCE] https://stackoverflow.com/questions/4051659/identifying-double-click-in-java
        if (evt.getClickCount() == 2 && evt.getButton() == MouseEvent.BUTTON1) {
            System.out.println("double clicked");
            if (this.isPlainTextMode()==false){
                this.disableHTML();
            }
        }
    }
    
    
    
    
    // UNUSED-LISTENERS --------------------------------------------------------
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
