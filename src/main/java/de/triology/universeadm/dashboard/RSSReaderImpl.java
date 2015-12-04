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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author yassine
 */
public class RSSReaderImpl implements RSSReader{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RSSReaderImpl.class.getName());
    
    @Override
    public String readRSS(String urlAddress) {
        try {
            URL rssUrl = new URL(urlAddress);
            BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
            String sourceCode = "";
            String line;
            while ((line = in.readLine()) != null) {
                sourceCode += line + "\n";
            }
            in.close();
            return sourceCode;

        } catch (MalformedURLException ex) {
            Logger.getLogger(RSSReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RSSReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getRSSFeed() {
        HttpClient httpClient;
        HttpResponse response = null;
        String sourceCode = "";
        try {

            httpClient = HttpClientBuilder.create().build();
            HttpGet getRequest = new HttpGet("http://www.scm-manager.com/feed/");
            getRequest.addHeader("accept", "application/xml");

            response = httpClient.execute(getRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : http error code : " + response.getStatusLine().getStatusCode());

            }

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output;

            while ((output = br.readLine()) != null) {
                sourceCode += output + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(RSSResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        RSS xmlToPojo = null;
        String pojoToJson = null;
        try {
            xmlToPojo = FormConverter.xmlToPojo(sourceCode);
            pojoToJson = FormConverter.pojoToJson(xmlToPojo);
        
        } catch (JAXBException ex) {
            Logger.getLogger(RSSReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonMappingException ex) {
            Logger.getLogger(RSSReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RSSReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pojoToJson;
    }
    
}
