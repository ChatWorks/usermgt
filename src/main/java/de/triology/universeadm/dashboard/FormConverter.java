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

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author yassine
 */
public class FormConverter {

    private static final Logger LOG = Logger.getLogger(FormConverter.class.getName());
    
    public static String parseRSSFeeds(HttpResponse response) {
        List<RSSFeeds> list = new ArrayList<RSSFeeds>();
		// Reading the feed
		SyndFeedInput input = new SyndFeedInput();
                StringBuilder sb = new StringBuilder();
                try {
                SyndFeed feed = input.build(new InputStreamReader(response.getEntity().getContent()));
		List entries = feed.getEntries();
		Iterator itEntries = entries.iterator();
 
		while (itEntries.hasNext()) {
                    SyndEntry entry = (SyndEntry) itEntries.next();
                    list.add(new RSSFeeds(entry.getTitle(), entry.getLink()));
		}
        } catch (IOException e) {
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(FormConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FeedException ex) {
            Logger.getLogger(FormConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
             return JSONObjWithRSSFeeds(list);
    }
    
    private  static String JSONObjWithRSSFeeds( List<RSSFeeds>  pListOfRSSFeeds) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper objectMapper = new ObjectMapper();
        
        try {
            objectMapper.writeValue(out, pListOfRSSFeeds);
        } catch (IOException ex) {
            Logger.getLogger(FormConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        final byte[] data = out.toByteArray();
        return new String(data);
    }
    
    public static boolean checkURL(String pURL) {
        Pattern p = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");  

        Matcher m = p.matcher(pURL);
        return m.matches();
    }
    
    
}
