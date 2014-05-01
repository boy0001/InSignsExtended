package com.empcraft;

import org.bukkit.plugin.Plugin;

import com.empcraft.individualmessages.IndividualMessages;

public class InMeHook {
	 public InMeHook(boolean disabled,Plugin inme) {
		 try {
			 ((IndividualMessages) inme).setDisabled(disabled);
		 }
		 catch (Exception e) {
			 e.printStackTrace();
		 }
		 return;
     }
}
