/*
 * $Id: BibParser.java,v 1.2 2006/09/11 15:48:43 Moritz.Ringler Exp $
 *
 * Copyright (c) 2004-2005 Oskar Ojala
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
package net.sourceforge.texlipse.bibparser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;

import net.sourceforge.texlipse.bibparser.lexer.LexerException;
import net.sourceforge.texlipse.bibparser.node.Start;
import net.sourceforge.texlipse.bibparser.parser.Parser;
import net.sourceforge.texlipse.bibparser.parser.ParserException;
import net.sourceforge.texlipse.model.ParseErrorMessage;

//import org.eclipse.core.resources.IMarker;


/**
 * BibTeX parser front-end. After creation, the parsing is done by calling
 * the getEntries() -method, after which getAbbrevs() and getErrors() should
 * be called (otherwise the data returned by these two is essentially meaningless.)
 *
 * @author Oskar Ojala
 */
public class BibParser {

    private String filename;
    private Reader reader;

    private ArrayList errors;
    private Start ast;
    public static final int SEVERITY_ERROR = 2;

    /**
     * Constructs a new BibTeX parser.
     *
     * @param filename The file to parse
     */
    public BibParser(String filename) {
        this.filename = filename;
        this.errors = new ArrayList();
    }

    /**
     * Constructs a new BibTeX parser.
     *
     * @param r A reader to the BibTeX-data to parse
     */
    public BibParser(Reader r) {
        this.reader = r;
        this.errors = new ArrayList();
    }

    /**
     * Parses the document, constructs a list of the entries and returns
     * them.
     *
     * @return BibTeX entries (<code>ReferenceEntry</code>)
     */
    public ArrayList getEntries() throws IOException, FileNotFoundException {
        try {
            BibLexer l;
            if (filename != null) {
                l = new BibLexer(new PushbackReader (new FileReader(filename), 1024));
            } else {
                l = new BibLexer(new PushbackReader(reader, 1024));
            }

            Parser p = new Parser(l);
            this.ast = p.parse();

            EntryRetriever er = new EntryRetriever();
            ast.apply(er);

            return er.getEntries();

            // TODO modularize SableCC error parsing
        } catch (LexerException le) {
            String msg = le.getMessage();
            int first = msg.indexOf('[');
            int last = msg.indexOf(']');
            String numseq = msg.substring(first + 1, last);
            String[] numbers = numseq.split(",");
            this.errors.add(new ParseErrorMessage(Integer.parseInt(numbers[0]),
                    Integer.parseInt(numbers[1]) - 1,
                    2,
                    msg.substring(last+2),
                    /*IMarker.*/SEVERITY_ERROR));
        } catch (ParserException pe) {
            String msg = pe.getMessage();
            int last = msg.indexOf(']');
            this.errors.add(new ParseErrorMessage(pe.getToken().getLine(),
                    pe.getToken().getPos(),
                    pe.getToken().getText().length(),
                    msg.substring(last+2),
                    /*IMarker.*/SEVERITY_ERROR));
        }
        return null;
    }

    /**
     * @return Returns the abbreviations (<code>ReferenceEntry</code>)
     */
    public ArrayList getAbbrevs() {
        if (ast != null) {
            AbbrevRetriever ar = new AbbrevRetriever();
            ast.apply(ar);
            return ar.getAbbrevs();
        }
        return null;
    }

    /**
     * @return Returns the errors.
     */
    public ArrayList getErrors() {
        return errors;
    }
}