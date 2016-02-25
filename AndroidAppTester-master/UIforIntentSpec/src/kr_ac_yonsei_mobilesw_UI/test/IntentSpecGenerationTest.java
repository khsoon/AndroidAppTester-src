package kr_ac_yonsei_mobilesw_UI.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import kr_ac_yonsei_mobilesw_UI.InterfaceWithExecution;
import kr_ac_yonsei_mobilesw_parser.MalformedIntentException;
import kr_ac_yonsei_mobilesw_parser.ParserOnIntent;
import kr_ac_yonsei_mobilesw_shell.ExecuteShellCommand;

import org.junit.Test;

public class IntentSpecGenerationTest {
	private static String osNameStr="windows";
	
	static {
		String osName = System.getProperty("os.name");
		String osNameMatch = osName.toLowerCase();
		
		if(osNameMatch.contains("linux")) {
			osNameStr = "linux";
		} else if(osNameMatch.contains("windows")) {
			osNameStr = "windows";
		} else if(osNameMatch.contains("mac os") || osNameMatch.contains("macos") || osNameMatch.contains("darwin")) {
			osNameStr = "mac";
		}else {
			osNameStr = "windows"; // Windows OS by default
		}	
	}

	// Put APK files or AndroidManifest.xml files into the following directory.
	private static String pathToTestAPKorAndroidManifestFiles = 
			"./src/kr_ac_yonsei_mobilesw_UI/test/files/";
	
	// Intent Spec. Generation Tests & Java Intent Parser Tests
	@Test
	public void test() {
		File file = new File(pathToTestAPKorAndroidManifestFiles);
		
		File[] files = file.listFiles();
		
		System.out.println("Total " + files.length + " files");
		
		if (files!=null) {
			try {
				for (File subfile : files) {
					
					String command = "java -cp \"" 
							+ System.getProperty("user.dir") + "/bin;" 
							+ System.getProperty("user.dir") + "/lib/*\" " 
							+ "com.example.java.GenIntentSpecFromAPK " 
							+ "\"" + subfile.getAbsolutePath() + "\""; // ' ' in the file name
					
					System.out.println(command);
					
					Process p;
					
					if ("mac".equals(osNameStr)) {
						p = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });
					}
					else {
						p = Runtime.getRuntime().exec(command);
					}
					
					// Stdout Messages
					while(p.isAlive())
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						StringBuffer sb = new StringBuffer();
						String line = "";
						
						while ((line = reader.readLine())!= null) 
						{
							if(line.equals("") == false)
							{
								//ui.appendTxt_adbCommand(line + "\n");
								System.out.println(line);
								sb.append(line + " ");
							}
						}
						
						// Parse each Intent specification
						new ParserOnIntent().parse(sb.toString());
					}
					
					// Error Messages
					BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					String line = "";
					boolean flag = false;
					
					while ((line = reader.readLine())!= null) 
					{						
						if (flag == false) {
							System.err.println("Error: \n");
							flag = true;
						}
						if(line.equals("") == false)
						{
							System.err.println(line + "\n");
						}
					}
					
					p.destroy();
					
					if (flag)
						throw new IOException("There is some error message.");
				}
				
			} catch (IOException e) {
				fail(e.getStackTrace().toString());
			} catch (MalformedIntentException e) {
				fail("Parse Error on Intent(" + e.getNumber() + "): " + e.getMsg());
			}
			
		}
	}

}
