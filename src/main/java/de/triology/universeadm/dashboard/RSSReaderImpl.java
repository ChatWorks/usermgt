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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yassine
 */
public class RSSReaderImpl implements RSSReader{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RSSReaderImpl.class.getName());
    
    @Override
    public String getRSSFeed(String url) {
        if (FormConverter.checkURL(url)) {
            
        HttpClient httpClient;
        HttpResponse response = null;
        
        String parseRSSFeeds = "";
        try {
            httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet(url);
            getRequest.addHeader("accept", "application/xml");
            
            response = httpClient.execute(getRequest);
//            HttpEntity entity = response.getEntity();
//            showContentType(entity);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : http error code : " + response.getStatusLine().getStatusCode());
            }
            parseRSSFeeds = FormConverter.parseRSSFeeds(response);
        } catch (IOException ex) {
            Logger.getLogger(RSSResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(RSSReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }   
            return parseRSSFeeds;
        } 
        return  null;
    }
    
    private void showContentType(HttpEntity entity) {
        ContentType contentType = ContentType.getOrDefault(entity);
        String mimeType = contentType.getMimeType();
        Charset charset = contentType.getCharset();
        logger.info("\nMimeType = " + mimeType);
        logger.info("Charset  = " + charset);
    }
    
}