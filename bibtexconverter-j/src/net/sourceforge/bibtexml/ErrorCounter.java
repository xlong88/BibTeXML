package net.sourceforge.bibtexml;
/*
 * $Id$
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
 
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerException;
import net.sourceforge.texlipse.model.ParseErrorMessage;

/** A resettable error handler that counts the errors in a single
* parse or validation. */
public class ErrorCounter extends UniversalErrorHandlerAdapter{
    private int count = 0;
    
    public ErrorCounter(){
    }
    
    @Override
    public void fatalError( SAXParseException e ) throws SAXException{
        count++;
        //100 errors are fatal
        if(count > 100){
            throw new SAXException("Stopped: more than 100 errors.");
        }
    }
    
    
    @Override
    public void error( SAXParseException e ) throws SAXException{
        count++;
        //100 errors are fatal
        if(count > 100){
            throw new SAXException("Stopped: more than 100 errors.");
        }
    }
    
    //ErrorListener
    @Override
    public void fatalError( TransformerException e ) throws TransformerException {
        count++;
        //100 errors are fatal
        if(count > 100){
            throw new TransformerException("Stopped: more than 100 errors.");
        }
    }
    
    @Override    
    public void error( TransformerException e ) throws TransformerException {
        count++;
        //100 errors are fatal
        if(count > 100){
            throw new TransformerException("Stopped: more than 100 errors.");
        }
    }
    
    @Override
    public void error(ParseErrorMessage e){
        count++;
    }
    
    public boolean hasError(){
        return count > 0;
    }
    
    public int getErrorCount(){
        return count;
    }
    
    @Override
    public void reset(){
        count = 0;
    }
}
