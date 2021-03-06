package kr_ac_yonsei_mobilesw_shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kr_ac_yonsei_mobilesw_UI.BenchAdd;
import kr_ac_yonsei_mobilesw_UI.BenchResult;
import kr_ac_yonsei_mobilesw_UI.Benchmark;
import kr_ac_yonsei_mobilesw_UI.InterfaceWithExecution;

public class ExecuteShellCommand {
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
	
	public static void executeCommand(Benchmark ui, String command) 
	{		
		Thread worker = new Thread()
		{
			public void run()
			{
				Process p = null;
				try {					
					p = Runtime.getRuntime().exec(command);
					
					ui.appendTxt_adbCommandLog("> " + command);
					
					while(p.isAlive())
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line = "";
						
						while ((line = reader.readLine())!= null) 
						{
							if(line.equals("") == false)
							{
								ui.appendTxt_adbCommandLog("\n" + line);
							}
						}
						
						ui.appendTxt_adbCommandLog("\n\n");
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				p.destroy();
			}
		};
		
		worker.start();
	}
	
	public static void showLogcat(Benchmark ui,  String command)
	{
		Thread worker = new Thread()
		{
			public void run()
			{
				Process p;
				try {
					p = Runtime.getRuntime().exec(command);
					
					while(p.isAlive())
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line = "";
						
						while ((line = reader.readLine())!= null) 
						{
							if(line.equals("") == false)
							{
								ui.appendTxt_logcat(line);
								ui.showLogcat();
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		worker.start();
	}
	public static void readDevice(BenchResult ui, String command) 
	{		
		Thread worker = new Thread()
		{
			public void run()
			{
				Process p = null;
				try {
					
					for(int i = 0; i < 3; i ++)
					{
						p = Runtime.getRuntime().exec(command);
						
						while(p.isAlive())
						{
							if(i == 2)
							{
								BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
								String line = "";
								
								while ((line = reader.readLine())!= null) 
								{
									if(line.equals("") == false)
									{
										ui.showDeviceList(line);
									}
								}
							}
						}
					}
					
					p.destroy();
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		worker.start();
	}
	
	public static void readDevice(Benchmark ui, String command) 
	{		
		Thread worker = new Thread()
		{
			public void run()
			{
				Process p = null;
				try {
					
					for(int i = 0; i < 3; i ++)
					{
						p = Runtime.getRuntime().exec(command);
						
						while(p.isAlive())
						{
							if(i == 2)
							{
								BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
								String line = "";
								
								while ((line = reader.readLine())!= null) 
								{
									if(line.equals("") == false)
									{
										ui.showDeviceList(line);
									}
								}
							}
						}
					}
					
					p.destroy();
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		
		worker.start();
	}
		
	public static Thread executeMakeTestArtifacts(InterfaceWithExecution ui, String command) 
	{		
		Thread worker = new Thread()
		{
			public void run()
			{
				Process p = null;
				try {				
					if ("mac".equals(osNameStr)) {
						p = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });
					}
					else {
						p = Runtime.getRuntime().exec(command);
					}
					
					//ui.appendTxt_adbCommand("> " + command);
					
					while(p.isAlive())
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line = "";
						
						while ((line = reader.readLine())!= null) 
						{
							if(line.equals("") == false)
							{
								//ui.appendTxt_adbCommand(line + "\n");
								ui.appendTxt_testArtifacts(line + "\n");
							}
						}
						
						reader.close();
					}
				
				
					boolean flag = false;
					
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String line = "";
						
						
						while ((line = reader.readLine())!= null) 
						{						
							if (flag == false) {
								ui.appendTxt_testArtifacts("Error: \n");
								flag = true;
							}
							if(line.equals("") == false)
							{
								ui.appendTxt_testArtifacts(line + "\n");
							}
						}
						
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					} 
					
					p.destroy();
					
					ui.done_testArtifacts(flag);
				
				}
				catch (Exception e)
				{
					e.printStackTrace();
					ui.appendTxt_testArtifacts(e.getMessage());
				}
			}
		};
		
		worker.start();
		
		return worker;
	}
	
	public static void executeImportIntentSpecCommand(InterfaceWithExecution ui, String command) 
	{		
		Thread worker = new Thread()
		{
			public void run()
			{
				Process p = null;
				try {					
					if ("mac".equals(osNameStr)) {
						p = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });
					}
					else {
						p = Runtime.getRuntime().exec(command);
					}
					
					//ui.appendTxt_adbCommand("> " + command);
					
					while(p.isAlive())
					{
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
						String line = "";
						
						while ((line = reader.readLine())!= null) 
						{
							if(line.equals("") == false)
							{
								ui.appendTxt_intentSpec(line + "\n");
							}
						}
						
						reader.close();
					}
					
					ui.done_intentSpec();
					
					try {
						BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
						String line = "";
						boolean flag = false;
						
						while ((line = reader.readLine())!= null) 
						{
							if (flag == false) {
								ui.appendTxt_intentSpec("Error: \n");
								flag = true;
							}
							if(line.equals("") == false)
							{
								ui.appendTxt_intentSpec(line + "\n");
							}
						}
						
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					p.destroy();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				
				
			}
		};
		
		worker.start();
	}
	
}
