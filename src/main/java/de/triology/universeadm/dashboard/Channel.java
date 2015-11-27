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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author yassine
 */
@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class Channel {

    private static final Logger LOG = Logger.getLogger(Channel.class.getName());
    
    @XmlElement(name = "title")
    private String title;
    @XmlElement(name = "link")
    private String link;
    @XmlElement(name = "lastBuildDate")
    private String lastBuildDate;
    @XmlElement(name = "generator")
    private String generator;
    
    @XmlElement(name = "item")
    private List<Item> items;

    public Channel() {
        super();
    }

    public Channel(String title, String link, String lastBuildDate, String generator, List<Item> item) {
        super();
        LOG.info("instance of Chanel ");
        this.title = title;
        this.link = link;
        this.lastBuildDate = lastBuildDate;
        this.generator = generator;
        this.items = item;
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

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public List<Item> getItem() {
        return items;
    }

    public void setItem(List<Item> item) {
        this.items = item;
    }


    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.title);
        hash = 71 * hash + Objects.hashCode(this.link);
        hash = 71 * hash + Objects.hashCode(this.lastBuildDate);
        hash = 71 * hash + Objects.hashCode(this.generator);
        hash = 71 * hash + Objects.hashCode(this.items);
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
        final Channel other = (Channel) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.link, other.link)) {
            return false;
        }
        if (!Objects.equals(this.lastBuildDate, other.lastBuildDate)) {
            return false;
        }
        if (!Objects.equals(this.generator, other.generator)) {
            return false;
        }
        if (!Objects.equals(this.items, other.items)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Channel{" + "title=" + title + ", link=" + link + ", lastBuildDate=" + lastBuildDate + ", generator=" + generator + ", item=" + items + '}';
    }
    
}
