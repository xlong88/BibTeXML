package net.sourceforge.bibtexml;
/*
* $Id: BibTeXConverterController.java 201 2007-03-31 21:39:53Z ringler $
* (c) Moritz Ringler, 2006
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.xml.XMLConstants;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ValidationMenu extends JMenu implements ActionListener{
    private static final Preferences PREF =
    Preferences.userNodeForPackage(BibTeXConverterController.class).node("schema");
    final static String VALIDATION_PREFIX = "javax.xml.validation:";
    private final static String VALIDATION_DISABLED = VALIDATION_PREFIX + "disabled";
    private final static String VALIDATION_BUILTIN = VALIDATION_PREFIX + "builtin";
    private final static String VALIDATION_USER = VALIDATION_PREFIX + "user";
    
    private final SchemaSelection schemaSelection = new SchemaSelection();
    private final XMLConverter xmlconv;
    private final AbstractButton disabled = new JRadioButtonMenuItem("Disabled", true);
    private final AbstractButton userSchema = new JRadioButtonMenuItem("Custom schema...");
    private final AbstractButton builtin = new JRadioButtonMenuItem("Built-in schema...");
    private URL userSchemaURL;
    
    public ValidationMenu(XMLConverter converter){
        super("BibXML Validation");
        xmlconv = converter;
        String prefVal = PREF.get("userSchemaURL", null);
        if(prefVal != null){
            try{
                userSchemaURL = new URL(prefVal);
            } catch (Exception ignore){
            }
        }
        init();
    }
    
    private void setValidationSchema(URL schema) throws SAXException{
        xmlconv.setXMLSchema(schema);
        userSchemaURL = schema;
        userSchema.setSelected(true);
        PREF.put("userSchemaURL", schema.toString());
        PREF.put(VALIDATION_PREFIX, VALIDATION_USER);
    }
    
    private void setValidationSchema(File f) throws SAXException{
        try{
            URL schema = f.toURI().toURL();
            setValidationSchema(schema);
        } catch (MalformedURLException ex){
            System.err.println("Warning: cannot load schema " + f.getName());
            System.err.println(ex);
            System.err.flush();
        }
    }
    
    private void setValidationSchema(String resource) throws SAXException{
        URL schema = getClass().getResource(resource);
        if(schema == null){
            System.err.println("Warning: cannot load schema " + resource);
            System.err.println("Resource not found.");
            System.err.flush();
        } else {
            setValidationSchema(schema);
        }
    }
    
    private void init(){
        JMenu menu = this;
        
        String prefval = PREF.get(VALIDATION_PREFIX, VALIDATION_DISABLED);

        ButtonGroup schema = new ButtonGroup();        
        disabled.setActionCommand(VALIDATION_DISABLED);
        menu.add(disabled);
        schema.add(disabled);
        disabled.addActionListener(this);
        
        builtin.setActionCommand(VALIDATION_BUILTIN);
        menu.add(builtin);
        schema.add(builtin);
        builtin.addActionListener(this);
        
        userSchema.setActionCommand(VALIDATION_USER);
        menu.add(userSchema);
        schema.add(userSchema);
        userSchema.addActionListener(this);
        
        if(prefval.equals(VALIDATION_BUILTIN)){
            builtin.doClick();
        } else if(prefval.equals(VALIDATION_USER) && userSchema != null){
            userSchema.doClick();
        } else {
            disabled.doClick();
        }
    }
    
    private void handleButton(AbstractButton c){
        boolean selected = c.isSelected();
        String cmd = c.getActionCommand();
        System.out.println(cmd);
        System.out.flush();
        if(cmd.startsWith(VALIDATION_PREFIX)){
            if(cmd.equals(VALIDATION_DISABLED)){
                setValidationEnabled(false);
            } else if(cmd.equals(VALIDATION_BUILTIN)){
                configureValidation();
            } else if(cmd.equals(VALIDATION_USER)){
                setSchemaFile();
            }
        }
    }
    
    private void configureValidation(){
        PREF.put(VALIDATION_PREFIX, VALIDATION_BUILTIN);
        builtin.setSelected(true);
        if(schemaSelection.showDialog(this)){
            setValidationEnabled(true);
        } else {
            setValidationEnabled(false);
        }
    }
    
    private void setSchemaFile(){
        PREF.put(VALIDATION_PREFIX, VALIDATION_USER);
        userSchema.setSelected(true);
        
        /* configure file chooser */
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new SchemaFileFilter());
        if(userSchemaURL != null){
            try{
                File f = new File(userSchemaURL.toURI());
                chooser.setSelectedFile(f);
            } catch (Exception ignore){
            }
        }
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Choose a Relax NG or W3C Schema file");
        
        //open file selection dialog
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            File f = chooser.getSelectedFile();
            if(f != null){
                try{
                    userSchemaURL = f.toURI().toURL();
                    setValidationEnabled(true);
                    return;
                } catch (MalformedURLException ex){
                    System.err.println("Warning: cannot load schema " + f.getName());
                    System.err.println(ex);
                    System.err.flush();
                }
            }
        }
        setValidationEnabled(false);
    }
    
    public boolean setValidationEnabled(boolean b){
        disabled.setSelected(!b);
        if(b){
            if(!enableValidation()){
                setValidationEnabled(false);
            }
        } else {
            try{
                xmlconv.setXMLSchema(null);
            } catch (SAXException ex){
                /* should never happen */
                throw new RuntimeException(ex);
            }
            PREF.put(VALIDATION_PREFIX, VALIDATION_DISABLED);
        }
        return b;
    }
    
    private boolean enableValidation(){
        boolean ok = false;
        try{
            if(userSchema.isSelected()){
                if(userSchemaURL != null){
                    setValidationSchema(userSchemaURL);
                    ok = true;
                }
            } else if(builtin.isSelected()){
                xmlconv.setXMLSchema(
                schemaSelection.getSchemaSource(XMLConstants.RELAXNG_NS_URI, xmlconv),
                XMLConstants.RELAXNG_NS_URI);
                ok = true;
            }
        } catch (SAXParseException ex){
            System.err.println("Warning: error in schema");
            System.err.println(ex.getSystemId() + " line " + ex.getLineNumber());
            System.err.println(ex);
        } catch (Exception ex){
            System.err.println("Error activating validation.");
            System.err.println(ex);
        } finally {
            System.out.flush();
            System.err.flush();
        }
        return ok;
    }
    
    public void actionPerformed(ActionEvent e){
        Object c = e.getSource();
        
        if(c instanceof AbstractButton){
            handleButton((AbstractButton) c);
        }
    }
    
    private static class SchemaFileFilter extends javax.swing.filechooser.FileFilter{
        public boolean accept(File f){
            return f.isDirectory() || f.getName().endsWith(".xsd") || f.getName().endsWith(".rng");
        }
        
        public String getDescription(){
            return "*.rng, *.xsd";
        }
    }
    
}