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

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.BeanComparator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author mbehlendorf
 */
public class JenkinsRestClient {

  JenkinsServer jenkins;

  public JenkinsRestClient() throws URISyntaxException, UnsupportedEncodingException {
    //String url = "https://ci.exoplatform.org/";
    String url = "http://192.168.115.130:49154";
    //String url = "http://qa.coreboot.org/";
    this.jenkins = new JenkinsServer(new URI(URLEncoder.encode(url, "UTF-8")));
  }

  private JSONArray convertLastBuildsToJSON(ArrayList<BuildWithDetails> lastBuilds) {
    JSONObject jsonObj = new JSONObject();
    JSONArray jsonArr = new JSONArray();
    for (BuildWithDetails build : lastBuilds) {
      jsonObj.put("BuildName", build.getFullDisplayName());
      jsonObj.put("BuildResult", build.getResult().name());

      long buildTime = System.currentTimeMillis() - build.getTimestamp();
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

  public JSONArray getLastBuilds(int quantity) {
    if (quantity < 0) {
      throw new IllegalArgumentException("Must be > 0!");
    }
    ArrayList<BuildWithDetails> buildList = new ArrayList<>();
    JobWithDetails job;
    Map<String, Job> jobs;
    try {
      jobs = jenkins.getJobs();
      for (Entry<String, Job> entry : jobs.entrySet()) {

        try {
          job = jobs.get(entry.getKey()).details();
          buildList.add(job.getLastCompletedBuild().details());
        }
        catch (IOException ex) {
          Logger.getLogger(JenkinsRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NullPointerException ex) {
          Logger.getLogger(JenkinsRestClient.class.getName()).log(Level.SEVERE, null, ex);
        }

      }
    }
    catch (IOException ex) {
      Logger.getLogger(JenkinsRestClient.class.getName()).log(Level.SEVERE, null, ex);
    }

    sortBuildList(buildList);
    if (quantity > buildList.size()) {
      quantity = buildList.size();
    }
    ArrayList<BuildWithDetails> lastBuilds = new ArrayList<>(buildList.subList(0, quantity));
    JSONArray json = convertLastBuildsToJSON(lastBuilds);
    return json;
  }

  private ArrayList<BuildWithDetails> sortBuildList(ArrayList<BuildWithDetails> buildList) {
    ArrayList<BuildWithDetails> sortedBuildList = buildList;
    Collections.sort(sortedBuildList, new Comparator<BuildWithDetails>() {
      @Override
      public int compare(BuildWithDetails job1, BuildWithDetails job2) {
        long diff;
        int result = 0;
        diff = job2.getTimestamp() - job1.getTimestamp();
        if (diff < 0) {
          result = -1;
        }
        else if (diff > 0) {
          result = 1;
        }
        return result;
      }
    });
    return sortedBuildList;
  }

  private void printError(String message, String variable) {
    System.out.println(message + variable);
  }

  public JSONArray getAllJobs() throws IOException {
    Map<String, Job> jobs = jenkins.getJobs();
    JSONObject jsonObj = new JSONObject();
    JSONArray jsonArr = new JSONArray();
    for (Entry<String, Job> entry : jobs.entrySet()) {
      jsonObj.put("title", jobs.get(entry.getKey()).getName());
      jsonArr.add(jsonObj.clone());
    }
    return jsonArr;
  }

  public JSONArray getBuilds(String job,int quantity) {
    Map<String, Job> jobs;
    printError("Job: ", job);
    JobWithDetails jobWithDetails = null;
    ArrayList<BuildWithDetails> buildWithDetailsList = new ArrayList<>();
    try {
      jobWithDetails = jenkins.getJob(job);
      List<Build> buildList = jobWithDetails.getBuilds();

      for (Build build : buildList) {
        buildWithDetailsList.add(build.details());
      }
      buildWithDetailsList = sortBuildList(buildWithDetailsList);
      if (quantity > buildList.size()) {
        quantity = buildList.size();
      }
    }
    catch (IOException | NullPointerException ex) {
      Logger.getLogger(JenkinsRestClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    ArrayList<BuildWithDetails> lastBuilds = new ArrayList<>(buildWithDetailsList.subList(0, quantity));
    JSONArray json = convertLastBuildsToJSON(lastBuilds);
    return json;

  }
}
