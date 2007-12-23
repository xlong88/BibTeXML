/* This file was generated by SableCC (http://www.sablecc.org/). */

package net.sourceforge.texlipse.bibparser.analysis;

import java.util.*;
import net.sourceforge.texlipse.bibparser.node.*;

public class ReversedDepthFirstAdapter extends AnalysisAdapter
{
    public void inStart(Start node)
    {
        defaultIn(node);
    }

    public void outStart(Start node)
    {
        defaultOut(node);
    }

    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    @Override
    public void caseStart(Start node)
    {
        inStart(node);
        node.getEOF().apply(this);
        node.getPBibtex().apply(this);
        outStart(node);
    }

    public void inABibtex(ABibtex node)
    {
        defaultIn(node);
    }

    public void outABibtex(ABibtex node)
    {
        defaultOut(node);
    }

    @Override
    public void caseABibtex(ABibtex node)
    {
        inABibtex(node);
        {
            List<PEntry> copy = new ArrayList<PEntry>(node.getEntry());
            Collections.reverse(copy);
            for(PEntry e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PPreambleEntry> copy = new ArrayList<PPreambleEntry>(node.getPreambleEntry());
            Collections.reverse(copy);
            for(PPreambleEntry e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PStringEntry> copy = new ArrayList<PStringEntry>(node.getStringEntry());
            Collections.reverse(copy);
            for(PStringEntry e : copy)
            {
                e.apply(this);
            }
        }
        outABibtex(node);
    }

    public void inAStrbraceStringEntry(AStrbraceStringEntry node)
    {
        defaultIn(node);
    }

    public void outAStrbraceStringEntry(AStrbraceStringEntry node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAStrbraceStringEntry(AStrbraceStringEntry node)
    {
        inAStrbraceStringEntry(node);
        if(node.getStringLiteral() != null)
        {
            node.getStringLiteral().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAStrbraceStringEntry(node);
    }

    public void inAStrparenStringEntry(AStrparenStringEntry node)
    {
        defaultIn(node);
    }

    public void outAStrparenStringEntry(AStrparenStringEntry node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAStrparenStringEntry(AStrparenStringEntry node)
    {
        inAStrparenStringEntry(node);
        if(node.getStringLiteral() != null)
        {
            node.getStringLiteral().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAStrparenStringEntry(node);
    }

    public void inAPrebracePreambleEntry(APrebracePreambleEntry node)
    {
        defaultIn(node);
    }

    public void outAPrebracePreambleEntry(APrebracePreambleEntry node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPrebracePreambleEntry(APrebracePreambleEntry node)
    {
        inAPrebracePreambleEntry(node);
        {
            List<PConcat> copy = new ArrayList<PConcat>(node.getConcat());
            Collections.reverse(copy);
            for(PConcat e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getValOrSid() != null)
        {
            node.getValOrSid().apply(this);
        }
        outAPrebracePreambleEntry(node);
    }

    public void inAPreparenPreambleEntry(APreparenPreambleEntry node)
    {
        defaultIn(node);
    }

    public void outAPreparenPreambleEntry(APreparenPreambleEntry node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPreparenPreambleEntry(APreparenPreambleEntry node)
    {
        inAPreparenPreambleEntry(node);
        {
            List<PConcat> copy = new ArrayList<PConcat>(node.getConcat());
            Collections.reverse(copy);
            for(PConcat e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getValOrSid() != null)
        {
            node.getValOrSid().apply(this);
        }
        outAPreparenPreambleEntry(node);
    }

    public void inAEntrybraceEntry(AEntrybraceEntry node)
    {
        defaultIn(node);
    }

    public void outAEntrybraceEntry(AEntrybraceEntry node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEntrybraceEntry(AEntrybraceEntry node)
    {
        inAEntrybraceEntry(node);
        {
            List<PKeyvalDecl> copy = new ArrayList<PKeyvalDecl>(node.getKeyvalDecl());
            Collections.reverse(copy);
            for(PKeyvalDecl e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getEntryDef() != null)
        {
            node.getEntryDef().apply(this);
        }
        outAEntrybraceEntry(node);
    }

    public void inAEntryparenEntry(AEntryparenEntry node)
    {
        defaultIn(node);
    }

    public void outAEntryparenEntry(AEntryparenEntry node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEntryparenEntry(AEntryparenEntry node)
    {
        inAEntryparenEntry(node);
        {
            List<PKeyvalDecl> copy = new ArrayList<PKeyvalDecl>(node.getKeyvalDecl());
            Collections.reverse(copy);
            for(PKeyvalDecl e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        if(node.getEntryDef() != null)
        {
            node.getEntryDef().apply(this);
        }
        outAEntryparenEntry(node);
    }

    public void inAEntryDef(AEntryDef node)
    {
        defaultIn(node);
    }

    public void outAEntryDef(AEntryDef node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEntryDef(AEntryDef node)
    {
        inAEntryDef(node);
        if(node.getEntryName() != null)
        {
            node.getEntryName().apply(this);
        }
        outAEntryDef(node);
    }

    public void inAKeyvalDecl(AKeyvalDecl node)
    {
        defaultIn(node);
    }

    public void outAKeyvalDecl(AKeyvalDecl node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAKeyvalDecl(AKeyvalDecl node)
    {
        inAKeyvalDecl(node);
        {
            List<PConcat> copy = new ArrayList<PConcat>(node.getConcat());
            Collections.reverse(copy);
            for(PConcat e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getValOrSid() != null)
        {
            node.getValOrSid().apply(this);
        }
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAKeyvalDecl(node);
    }

    public void inAConcat(AConcat node)
    {
        defaultIn(node);
    }

    public void outAConcat(AConcat node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAConcat(AConcat node)
    {
        inAConcat(node);
        if(node.getValOrSid() != null)
        {
            node.getValOrSid().apply(this);
        }
        outAConcat(node);
    }

    public void inAValueValOrSid(AValueValOrSid node)
    {
        defaultIn(node);
    }

    public void outAValueValOrSid(AValueValOrSid node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAValueValOrSid(AValueValOrSid node)
    {
        inAValueValOrSid(node);
        if(node.getStringLiteral() != null)
        {
            node.getStringLiteral().apply(this);
        }
        outAValueValOrSid(node);
    }

    public void inANumValOrSid(ANumValOrSid node)
    {
        defaultIn(node);
    }

    public void outANumValOrSid(ANumValOrSid node)
    {
        defaultOut(node);
    }

    @Override
    public void caseANumValOrSid(ANumValOrSid node)
    {
        inANumValOrSid(node);
        if(node.getNumber() != null)
        {
            node.getNumber().apply(this);
        }
        outANumValOrSid(node);
    }

    public void inAIdValOrSid(AIdValOrSid node)
    {
        defaultIn(node);
    }

    public void outAIdValOrSid(AIdValOrSid node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIdValOrSid(AIdValOrSid node)
    {
        inAIdValOrSid(node);
        if(node.getIdentifier() != null)
        {
            node.getIdentifier().apply(this);
        }
        outAIdValOrSid(node);
    }
}
