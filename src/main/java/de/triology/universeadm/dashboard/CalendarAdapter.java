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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author yassine
 */
public class CalendarAdapter extends XmlAdapter<String, Calendar>{

    DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
   
    @Override
    public Calendar unmarshal(String v) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(df.parse(v));
        return calendar;
    }

    @Override
    public String marshal(Calendar v) throws Exception {
        return df.format(v.getTime());
    }

    
}
