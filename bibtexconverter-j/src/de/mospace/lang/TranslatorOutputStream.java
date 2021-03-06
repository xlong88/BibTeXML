package de.mospace.lang;
/*
* $Id: XSLTUtils.java 326 2007-08-23 15:19:05Z ringler $
*
* Copyright (c) 2007 Moritz Ringler
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
import java.io.IOException;
import java.io.FilterOutputStream;
import java.io.OutputStream;

/** Translates all occurrences of the <code>from</code> byte sequence to
   the <code>to</code> byte sequence. **/
public class TranslatorOutputStream extends FilterOutputStream{
    private final byte[] from;
    private final byte[] to;
    transient private int idx = 0;

    public TranslatorOutputStream(OutputStream os, byte[] from, byte[] to){
        super(os);
        if(from.length == 0){
            throw new IllegalArgumentException("from must not be empty.");
        }
        this.from = from.clone();
        this.to = to.clone();
    }

    @Override
    public void write(int b) throws IOException{
        if(b == from[idx]){
            idx++;
            if(idx == from.length){
                out.write(to);
                idx = 0;
            }
        } else if (idx != 0){
            out.write(from, 0, idx);
            out.write(b);
        } else {
            out.write(b);
        }
    }

    @Override
    public void flush() throws IOException{
        if(idx != 0){
            out.write(from, 0, idx);
        }
        super.flush();
    }
}
