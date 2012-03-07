package com.googlecode.iptableslog;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;

public class ApplicationsTracker {
  public static ArrayList<AppEntry> installedApps;
  public static Hashtable<String, AppEntry> installedAppsHash;

  public static class AppEntry {
    String name;
    Drawable icon;
    int uid;
  }

  public static void restoreData(IptablesLogData data) {
    installedApps = data.applicationsTrackerInstalledApps;
    installedAppsHash = data.applicationsTrackerInstalledAppsHash;
  }

  public static void getInstalledApps(Context context) {
    installedApps = new ArrayList<AppEntry>();
    installedAppsHash = new Hashtable<String, AppEntry>();

    List<ApplicationInfo> apps = new ArrayList<ApplicationInfo>();
    PackageManager pm = context.getPackageManager();

    apps = pm.getInstalledApplications(0);

    for(ApplicationInfo app : apps) {
      String name = app.loadLabel(pm).toString();
      // todo: load icons on background thread
      // todo: load icons as copies to not leak context
      Drawable icon = app.loadIcon(pm); // fixme: leaking context
      int uid = app.uid;
      String sUid = Integer.toString(uid);

      AppEntry entryHash = installedAppsHash.get(sUid);

      AppEntry entry = new AppEntry();
      entry.name = name;
      entry.icon = icon;
      entry.uid = uid;

      installedApps.add(entry);

      if(entryHash != null) {
        entryHash.name.concat("; " + name);
      } else {
        installedAppsHash.put(sUid, entry);
      } 
    }

    AppEntry entry = new AppEntry();
    entry.name = "Unspecified";
    entry.icon = null;
    entry.uid = -1;

    installedApps.add(entry);
    installedAppsHash.put("-1", entry);

    AppEntry entryHash = installedAppsHash.get("0");
    if(entryHash == null) {
      entry = new AppEntry();
      entry.name = "Root";
      entry.icon = null;
      entry.uid = 0;

      installedApps.add(entry);
      installedAppsHash.put("0", entry);
    }
  }
}