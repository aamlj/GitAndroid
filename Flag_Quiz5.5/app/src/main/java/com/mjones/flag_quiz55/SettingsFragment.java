// SettingsFragment.java
// Subclass of PreferenceFragment for managing app settings
package com.mjones.flag_quiz55;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment
{   
   // creates preferences GUI from preferences.xml file in res/xml
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.preferences); // load from XML
   } 
} // end class SettingsFragment



