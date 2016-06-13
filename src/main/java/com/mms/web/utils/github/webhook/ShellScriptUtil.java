package com.mms.web.utils.github.webhook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellScriptUtil {

	public static void executeScript(String path){
		ProcessBuilder pb = new ProcessBuilder("/bin/sh",path);
		Process p = null;
		try {
            p = pb.start();
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            System.out.println("Here is the standard output of the command:\n");

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
           }

            // read any errors from the attempted command
            System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException ex) {
           ex.printStackTrace();
        }

       
	}
}
