package com.mms.web.utils.github.webhook;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class implements utility methods for interacting with shell scripts.
 * 
 * @author manik.magar
 *
 */
public class ShellScriptUtil {

	public static void executeScript(String path){
		
		File f = new File(path);
		if (!f.exists() || !f.canExecute()){
			System.err.println("Unable to find/read target shell script at -"+ path);
			return;
		}
		
		
		ProcessBuilder pb = new ProcessBuilder("/bin/sh",path);
		pb.redirectErrorStream(true);
		Process p = null;
		try {
            p = pb.start();
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // read the output from the command
            System.out.println("### Script output Starts:\n");

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            System.out.println("### Script Execution finished.:\n");
            
        } catch (IOException ex) {	
           System.err.println("Error while executing script"); 
           ex.printStackTrace();
        }
       
	}
}
