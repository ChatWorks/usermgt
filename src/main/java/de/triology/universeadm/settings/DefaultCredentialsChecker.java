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

package de.triology.universeadm.settings;

import com.google.common.annotations.VisibleForTesting;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultCredentialsChecker implements CredentialsChecker
{

  private static final String CHARSET = "UTF-8";

  private static final String REQUEST_METHOD = "POST";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  private static final String HEADER_CONTENT_LENGTH = "Content-Length";
  private static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

  private static final String PARAM_USERNAME = "username";
  private static final String PARAM_PASSWORD = "password";
  private static final String PARAM_ACTION = "action";
  private static final String ACTION_CHECK_CREDENTIALS = "checkCreds";

  private static final String EQUAL = "=";
  private static final String AMPERSAND = "&";

  @VisibleForTesting
  static final String RETURN_VALID = "validUserOrPassword";
  @VisibleForTesting
  static final String RETURN_INVALID = "invalidUserOrPassword";

  private static final Logger logger = LoggerFactory.getLogger(DefaultCredentialsChecker.class);

  @Override
  public boolean checkCredentials(Credentials credentials, String checkUrl) throws IOException
  {
    URL url = new URL(checkUrl);
    HttpURLConnection connection = createUrlConnection(url);
    connection.setRequestMethod(REQUEST_METHOD);
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setUseCaches(false);
    connection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM);
    String body = createPostBody(credentials);
    connection.setRequestProperty(HEADER_CONTENT_LENGTH, String.valueOf(body.length()));
    writeBody(connection, body);
    boolean result = false;
    String returnedBody = readBody(connection);
    if (RETURN_VALID.equals(returnedBody))
    {
      logger.info("successfully checked credentials at {}", checkUrl);
      result = true;
    }
    else
    {
      logger.warn("credentials check at {} returned {}", checkUrl, returnedBody);
    }
    return result;
  }
  
  @VisibleForTesting
  protected HttpURLConnection createUrlConnection(URL url) throws IOException{
    return (HttpURLConnection) url.openConnection();
  }

  private String readBody(URLConnection connection) throws IOException
  {
    String content;
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET)))
    {
      String line = reader.readLine();
      content = line;
      // drain reader
      while (line != null)
      {
        line = reader.readLine();
      }
    }
    return content;
  }

  private void writeBody(URLConnection connection, String body) throws IOException
  {
    try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), CHARSET))
    {
      writer.write(body);
    }
  }

  private String createPostBody(Credentials credentials) throws UnsupportedEncodingException
  {
    StringBuilder post = new StringBuilder();
    post.append(PARAM_USERNAME).append(EQUAL).append(enc(credentials.getUsername()));
    post.append(AMPERSAND);
    post.append(PARAM_PASSWORD).append(EQUAL).append(enc(credentials.getPassword()));
    post.append(AMPERSAND);
    post.append(PARAM_ACTION).append(EQUAL).append(ACTION_CHECK_CREDENTIALS);
    return post.toString();
  }

  private static String enc(String value) throws UnsupportedEncodingException
  {
    return URLEncoder.encode(value, CHARSET);
  }
}
