/*
 *   Copyright 2018. AppDynamics LLC and its affiliates.
 *   All Rights Reserved.
 *   This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 *   The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.snmp;

import com.appdynamics.extensions.alerts.customevents.Event;
import com.appdynamics.extensions.alerts.customevents.HealthRuleViolationEvent;
import com.appdynamics.extensions.alerts.customevents.OtherEvent;
import com.google.common.base.Strings;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.snmp4j.smi.TimeTicks;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class CommonUtils {

    private static Logger logger = Logger.getLogger(CommonUtils.class);

    public static String getAlertUrl(Event event) {
        String url = event.getDeepLinkUrl();
        if(event instanceof HealthRuleViolationEvent){
            url += ((HealthRuleViolationEvent) event).getIncidentID();
        }
        else{
            url += ((OtherEvent) event).getEventSummaries().get(0).getEventSummaryId();
        }
        return url;
    }

    public static String cleanUpAccountInfo(String accountId) {
        if(Strings.isNullOrEmpty(accountId)){
            return "";
        }
        int idx = accountId.indexOf("_");
        if(idx != -1){
            return accountId.substring(0,idx);
        }
        return accountId;
    }

    public static long getSysUptime() {
        long _sysUpTime = 0;
        if (SystemUtils.IS_OS_WINDOWS) {
            try {
                Process uptimeProc = Runtime.getRuntime().exec("net stats srv");
                BufferedReader in = new BufferedReader(new InputStreamReader(uptimeProc.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("Statistics since")) {
                        SimpleDateFormat format = new SimpleDateFormat("'Statistics since' MM/dd/yyyy hh:mm:ss a");
                        Date boottime = format.parse(line);
                        _sysUpTime = (System.currentTimeMillis() - boottime.getTime());
                        break;
                    }
                }
            } catch(Exception ex) {
                logger.error(ex.getMessage());
            }
        } else if (SystemUtils.IS_OS_LINUX) {
            Scanner s = null;
            try {
                s = new Scanner(new FileInputStream("/proc/uptime"));
                Float upTime = Float.parseFloat(s.next()) * 1000;
                _sysUpTime = upTime.longValue();
            } catch (Exception ex) {
                logger.error(ex.toString());
            }
            finally {
                if(s != null){
                    s.close();
                }

            }
        } else {
            logger.error("Unsupported platform " + SystemUtils.OS_NAME + ".");
        }
        return _sysUpTime;
    }

    public static TimeTicks getTimeTicks(long upTimeInMs) {
        TimeTicks sysUpTime = new TimeTicks();
        try {
            /*
              Timeticks takes only unsigned int 32-bit values. 4294967295L
              will rollover after 496 days and typecasting it to (int) would return -ve. So absolute
             */
            int upTime = (int)upTimeInMs;
            sysUpTime.fromMilliseconds(Math.abs(upTime));
        }
        catch(IllegalArgumentException e){
            logger.error("Cannot convert to timeticks for value " + upTimeInMs,e);
        }
        return sysUpTime;
    }
}
