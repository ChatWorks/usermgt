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
package de.triology.universeadm.jenkinsWidget;

import com.google.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;

/**
 *
 * @author mbehlendorf
 */
@Path("jenkins")
public class JenkinsResource {

  JenkinsRestClient jenkinsClient;

  @Inject
  public JenkinsResource() throws URISyntaxException, UnsupportedEncodingException {
    jenkinsClient = new JenkinsRestClient();
  }

  @POST
  @Path("getLastBuilds")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String getLastBuilds(String quantity) {
    int quantityInt = Integer.parseInt(quantity);
    String result;
    result = jenkinsClient.getLastBuilds(quantityInt).toJSONString();
    return result;
  }

  @POST
  @Path("getBuilds")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.TEXT_PLAIN)
  public String getBuilds(JSONObject jobAndQuantity) {
    int quantityInt = Integer.parseInt((String) jobAndQuantity.get("quantity"));
    String job = (String) jobAndQuantity.get("job");
    String result;
    result = jenkinsClient.getBuilds(job, quantityInt).toJSONString();
    return result;
  }

  @GET
  @Path("getAllJobs")
  @Produces(MediaType.TEXT_PLAIN)
  public String getAllJobs() {
    String result;
    result = jenkinsClient.getAllJobs().toJSONString();
    return result;
  }

}
