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

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mbehlendorf
 */
public class JenkinsRestClient {

  private String TIMESTAMP = "timestamp";
  private String LAST_BUILD = "lastBuild";
  private String LAST_BUILD_NAME = "fullDisplayName";
  private String RESULT = "result";
  private String NAME = "name";
  private String urlString = "http://qa.coreboot.org/";
  //private String urlString = "https://ci.exoplatform.org/";

  public JenkinsRestClient() {
  }

  public JSONArray getLastBuilds(int quantity) {
    URL url;
    try {
      url = new URL(urlString + "api/json?tree=jobs[lastBuild[fullDisplayName,id,timestamp,result]]");
      String response = new Scanner(url.openStream()).useDelimiter("\\Z").next();
      JSONObject jsonObj;
      JSONArray jsonArrJobs;
      JSONParser jsonPar = new JSONParser();
      jsonObj = new JSONObject((JSONObject) jsonPar.parse(response));
      jsonArrJobs = (JSONArray) jsonObj.get("jobs");
      JSONArray jsonArrBuilds = new JSONArray();
      for (Object job : jsonArrJobs) {
        jsonObj = (JSONObject) job;
        jsonArrBuilds.add((JSONObject) jsonObj.get(LAST_BUILD));
      }
      jsonArrBuilds.sort(new Comparator<JSONObject>() {
        @Override
        public int compare(JSONObject job1, JSONObject job2) {
          long time1 = (long) job1.get(TIMESTAMP);
          long time2 = (long) job2.get(TIMESTAMP);
          int diff = (int) ((time1 - time2) / 1000);
          return -diff;
        }
      });
      List<JSONObject> jobList;
      if (quantity > jsonArrBuilds.size()) {
        quantity = jsonArrBuilds.size();
      }
      jobList = jsonArrBuilds.subList(0, quantity);
      jsonArrBuilds = convertLastBuildsToJSON(jobList);
      return jsonArrBuilds;
    }
    catch (IOException | ParseException ex) {
      Logger.getLogger(JenkinsRestClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public JSONArray getBuilds(String jobName, int quantityInt) {
    URL url;
    try {
      url = new URL(urlString + "job/" + jobName + "/api/json?tree=builds[fullDisplayName,result,timestamp]{," + quantityInt + "}");
      String response = new Scanner(url.openStream()).useDelimiter("\\Z").next();
      JSONObject jsonObj;
      JSONParser jsonPar = new JSONParser();
      jsonObj = new JSONObject((JSONObject) jsonPar.parse(response));
      JSONArray jsonArrBuilds;
      jsonArrBuilds = (JSONArray) jsonObj.get("builds");
      jsonArrBuilds = convertLastBuildsToJSON(jsonArrBuilds);
      return jsonArrBuilds;
    }
    catch (IOException | ParseException ex) {
      Logger.getLogger(JenkinsRestClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public JSONArray getAllJobs() {
    URL url;
    try {
      url = new URL(urlString + "api/json?tree=jobs[name]");
      String response = new Scanner(url.openStream()).useDelimiter("\\Z").next();
      JSONObject jsonObj;
      JSONArray jsonArrJobs;
      JSONParser jsonPar = new JSONParser();
      jsonObj = new JSONObject((JSONObject) jsonPar.parse(response));
      jsonArrJobs = (JSONArray) jsonObj.get("jobs");
      return jsonArrJobs;
    }
    catch (IOException | ParseException ex) {
      Logger.getLogger(JenkinsRestClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  private JSONArray convertLastBuildsToJSON(List<JSONObject> lastBuilds) {
    JSONObject jsonObj = new JSONObject();
    JSONArray jsonArr = new JSONArray();
    for (JSONObject build : lastBuilds) {
      jsonObj.put("BuildName", build.get(LAST_BUILD_NAME));
      jsonObj.put("BuildResult", build.get(RESULT));

      long buildTime = System.currentTimeMillis() - (long) build.get(TIMESTAMP);
      jsonObj.put("BuildTime", formatDuration(buildTime));
      jsonArr.add(jsonObj.clone());
    }

    return jsonArr;
  }

  private String formatDuration(long dur) {
    String result = "";
    String monthsStr, daysStr, hoursStr, minutesStr;
    int months = (int) (TimeUnit.MILLISECONDS.toDays(dur) / 30);
    int days = (int) (TimeUnit.MILLISECONDS.toDays(dur) % 30);
    int hours = (int) (TimeUnit.MILLISECONDS.toHours(dur) % 24);
    int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(dur) % 60);

    if (months > 1) {
      monthsStr = months + " months ";
    }
    else if (months == 1) {
      monthsStr = months + " month ";
    }
    else {
      monthsStr = "";
    }

    if (days == 1) {
      daysStr = days + " day ";
    }
    else {
      daysStr = days + " days ";
    }

    if (hours > 1) {
      hoursStr = hours + " hours ";
    }
    else if (hours == 1) {
      hoursStr = hours + " hour ";
    }
    else {
      hoursStr = "";
    }

    if (minutes == 1) {
      minutesStr = minutes + " minute ";
    }
    else {
      minutesStr = minutes + " minutes ";
    }

    if (days >= 2) {
      result += monthsStr + daysStr;
    }
    else {
      result += hoursStr + minutesStr;
    }
    return result;
  }

}
