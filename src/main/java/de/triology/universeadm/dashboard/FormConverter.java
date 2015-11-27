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
import java.io.StringReader;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

/**
 *
 * @author yassine
 */
public class FormConverter {

    private static final Logger LOG = Logger.getLogger(FormConverter.class.getName());
    
    public static  RSS xmlToPojo(String xmlData) throws JAXBException
 {
  JAXBContext context = JAXBContext.newInstance(RSS.class);
  Unmarshaller unmarshaller = context.createUnmarshaller();
  StringReader reader = new StringReader(xmlData);
  RSS rssObj = (RSS) unmarshaller.unmarshal(reader);
  return rssObj;
 }
    
    
    public static String pojoToJson(RSS obj) throws JAXBException,
 JsonParseException, JsonMappingException, IOException
 {
  ObjectMapper mapper = new ObjectMapper();
  AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
  mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
  String jsonData = mapper.writeValueAsString(obj);
  return jsonData;
 }
    
    
    private RSS jsonToPojo(String jsonData) throws JAXBException,
 JsonParseException, JsonMappingException, IOException
 {
  ObjectMapper mapper = new ObjectMapper();
  AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
  mapper.getDeserializationConfig().setAnnotationIntrospector(
   introspector);
  RSS rssObj = mapper.readValue(jsonData, RSS.class);
  return rssObj;
 }
 
}
