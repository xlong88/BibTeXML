package net.sourceforge.bibtexml;
/*
 * $Id: TeXLipseParser.java,v 1.5 2006/10/26 19:25:09 Moritz.Ringler Exp $
 * (c) Moritz Ringler, 2006

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import de.mospace.xml.SaxXMLWriter;
import net.sourceforge.texlipse.bibparser.BibParser2;
import org.xml.sax.SAXException;

class TeXLipseParser extends AbstractBibTeXParser{
    BibParser2 parser;

    public TeXLipseParser(String charset)
            throws IllegalCharsetNameException, UnsupportedCharsetException {
        super(charset);
    }

    public TeXLipseParser(String inputChars, String outputChars)
            throws IllegalCharsetNameException, UnsupportedCharsetException {
        super(inputChars, outputChars);
    }

    public TeXLipseParser(String inputChars, String outputChars, boolean cleanInput)
            throws IllegalCharsetNameException, UnsupportedCharsetException {
        super(inputChars, outputChars, cleanInput);
    }

    /** does not close stream */
    protected String[] translateBibTeXStream(BufferedReader reader) throws IOException{
        parser = new BibParser2(reader);
        ArrayList errors = parser.getErrors();
        if(errors != null && errors.size() > 0){
            for(Object error : errors){
                throw new IOException(error.toString());
            }
        }
        return new String[0];
    }

    protected void writeBibXML(String[] data, OutputStream out ) throws IOException{
        try{
            Properties props = new Properties();
            props.put(OutputKeys.ENCODING, getOutputCharset());
            //props.put(OutputKeys.DOCTYPE_PUBLIC, "-//BibTeXML//DTD XML for BibTeX v1.0//EN");
            //props.put(OutputKeys.DOCTYPE_SYSTEM, "bibtexml-strict.dtd");
            SaxXMLWriter xw = new SaxXMLWriter(out, "http://bibtexml.sf.net/", props);
            parser.printXML(xw);
            xw.close();
        } catch (SAXException ex){
            IOException ioex = new IOException(ex.getMessage());
            ioex.initCause(ex);
            throw ioex;
        }
    }
}