/* This file was generated by SableCC (http://www.sablecc.org/). */

package net.sourceforge.texlipse.bibparser.node;

import java.util.*;
import net.sourceforge.texlipse.bibparser.analysis.*;

@SuppressWarnings("nls")
public final class AStrbraceStringEntry extends PStringEntry
{
    private TIdentifier _identifier_;
    private PValOrSid _valOrSid_;
    private final LinkedList<PConcat> _concat_ = new LinkedList<PConcat>();

    public AStrbraceStringEntry()
    {
        // Constructor
    }

    public AStrbraceStringEntry(
        @SuppressWarnings("hiding") TIdentifier _identifier_,
        @SuppressWarnings("hiding") PValOrSid _valOrSid_,
        @SuppressWarnings("hiding") List<PConcat> _concat_)
    {
        // Constructor
        setIdentifier(_identifier_);

        setValOrSid(_valOrSid_);

        setConcat(_concat_);

    }

    @Override
    public Object clone()
    {
        return new AStrbraceStringEntry(
            cloneNode(this._identifier_),
            cloneNode(this._valOrSid_),
            cloneList(this._concat_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAStrbraceStringEntry(this);
    }

    public TIdentifier getIdentifier()
    {
        return this._identifier_;
    }

    public void setIdentifier(TIdentifier node)
    {
        if(this._identifier_ != null)
        {
            this._identifier_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._identifier_ = node;
    }

    public PValOrSid getValOrSid()
    {
        return this._valOrSid_;
    }

    public void setValOrSid(PValOrSid node)
    {
        if(this._valOrSid_ != null)
        {
            this._valOrSid_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._valOrSid_ = node;
    }

    public LinkedList<PConcat> getConcat()
    {
        return this._concat_;
    }

    public void setConcat(List<PConcat> list)
    {
        this._concat_.clear();
        this._concat_.addAll(list);
        for(PConcat e : list)
        {
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._identifier_)
            + toString(this._valOrSid_)
            + toString(this._concat_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._identifier_ == child)
        {
            this._identifier_ = null;
            return;
        }

        if(this._valOrSid_ == child)
        {
            this._valOrSid_ = null;
            return;
        }

        if(this._concat_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._identifier_ == oldChild)
        {
            setIdentifier((TIdentifier) newChild);
            return;
        }

        if(this._valOrSid_ == oldChild)
        {
            setValOrSid((PValOrSid) newChild);
            return;
        }

        for(ListIterator<PConcat> i = this._concat_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PConcat) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}
