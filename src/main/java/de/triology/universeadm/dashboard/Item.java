/*
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */
package de.triology.universeadm.dashboard;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author yassine
 */
//@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
public class Item {

    private static final Logger LOG = Logger.getLogger(Item.class.getName());
    @XmlElement(name = "title")
    private String title;
    @XmlElement(name = "link")
    private String link;
    @XmlElement(name = "comments")
    private String comments;
    
    @XmlElement(name = "pubDate")
    //@XmlSchemaType(name = "date")
    //@XmlJavaTypeAdapter(CalendarAdapter.class)
    private String pubDate;
    
    public Item() {
        super();
    }
    
    public Item(String title, String link, String comments, String pubDate, List<Guid> guid) {
        super();
        LOG.info("instance of Item ");
        this.title = title;
        this.link = link;
        this.comments = comments;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.title);
        hash = 47 * hash + Objects.hashCode(this.link);
        hash = 47 * hash + Objects.hashCode(this.comments);
        hash = 47 * hash + Objects.hashCode(this.pubDate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.comments, other.comments)) {
            return false;
        }
        if (!Objects.equals(this.pubDate, other.pubDate)) {
            return false;
        }
        return true;
    }
    @Override
    public String toString() {
        return "Item{" + "title=" + title + ", link=" + link + ", comments=" + comments + ", pubDate=" + pubDate + ", guid=" +'}';
    }
    
}
