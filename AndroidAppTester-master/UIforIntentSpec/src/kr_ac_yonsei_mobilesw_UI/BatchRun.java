package kr_ac_yonsei_mobilesw_UI;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import kr_ac_yonsei_mobilesw_shell.ExecuteShellCommand;

public class BatchRun {
	private static String osNameStr="windows";
	private static String genCommand="gen.exe";
	
	static {
		String osName = System.getProperty("os.name");
		String osNameMatch = osName.toLowerCase();
		
		if(osNameMatch.contains("linux")) {
			osNameStr = "linux";
			genCommand = "gen_linux";
		} else if(osNameMatch.contains("windows")) {
			osNameStr = "windows";
			genCommand = "gen.exe";
		} else if(osNameMatch.contains("mac os") || osNameMatch.contains("macos") || osNameMatch.contains("darwin")) {
			osNameStr = "mac";
			genCommand = "gen_mac";
		}else {
			osNameStr = "windows"; // Windows OS by default
			genCommand = "gen.exe"; // Windows OS by default
		}	
	}
	
	private boolean stop = false;
	private String pkg_name;
	
	/*
	 * 
	 * apkfile : APK file
	 * pkgname_file : APK package name file
	 * intentspecfile  : Intent specification file (Text)
	 * random_type : 0, 1, 2
	 * num_of_intents : Number of intents to generate
	 * adbcmdfile : ADB Command File (Text)
	 * adb_path : Path to Android adb tool
	 * log_file_name : Log file name
	 * 
	 */
	public void execute(File apkfile, String pkgname_file,
			File intentspecfile, int random_type, int num_of_intents, 
			File adbcmdfile, 
			String adb_path, String log_file_name) {
		try {
			// Step 1: Generate an intent specification (with APK package name in pkg_name)
			execute_GenIntentSpecFromAPK(apkfile, pkgname_file, intentspecfile);
			
			// Step 2: Generate a list of ADB commands
			execute_MakeTestArtifacts(intentspecfile, random_type, num_of_intents, adbcmdfile, adb_path);
			
			// Step 3: Install the APK file
			boolean install_flag = execute_install(apkfile, adb_path);
			
			if (install_flag) {
				// Step 4: Run the APK file with the list of ADB commands
				execute_ADBcommands(adbcmdfile, adb_path, log_file_name, pkg_name);
				
				// Step 5: Uninstall the APK file
				//execute_uninstall(pkg_name, adb_path, log_file_name);
			}
		} catch (IOException e) {
			fail(e.getStackTrace().toString());
		} 
		
	}

	// Step 1:
	private void execute_GenIntentSpecFromAPK(File apkfile, String pkgname_file, File intentspecfile)
			throws IOException {
		String command = JavaCommand.javaCmd() 
//				"java -cp \"" 
//				+ System.getProperty("user.dir") + "/bin;" 
//				+ System.getProperty("user.dir") + "/lib/*\" " 
				+ "com.example.java.GenIntentSpecFromAPK -PRINT_PKG_NAME " + "\"" + pkgname_file + "\" "
				+ "\"" + apkfile.getAbsolutePath() + "\"" // ' ' in the file name
				+ " > \"" + intentspecfile.getAbsolutePath() + "\"";
		
		System.out.println(command);
		
		Process p;
		
		if ("mac".equals(osNameStr)) {
			p = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });
		}
		else {
			p = Runtime.getRuntime().exec(command);
		}
		
		// Stdout Messages
		StringBuffer sb = new StringBuffer();
		
		pkg_name = ""; // For uninstallation with $ adb uninstall [pkg_name]
		
		boolean once = true;
		
		while(p.isAlive())
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			
//			if (once && (pkg_name = reader.readLine()) != null)
//				once = false;
			
			while ((line = reader.readLine())!= null) 
			{
				if(line.equals("") == false)
				{
					//ui.appendTxt_adbCommand(line + "\n");
					System.out.println(line);
					sb.append(line + " ");
				}
			}
		}
		System.out.println("mid");
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
		
		FileReader fr = new FileReader(pkgname_file);
		BufferedReader br = new BufferedReader(fr);
		pkg_name = br.readLine();
		br.close();
		//fr.close();
		
		System.out.println("End of GenIntentSpecFromAPK");

	}
	
	
	// Step 2: 
	boolean blocking;
	
	private void execute_MakeTestArtifacts(File intentspecfile, int random_type, int num_of_intents, File adbcmdfile,
			String adb_path) {
		
		blocking = true;
		
		// Generate ADB Commands
		InterfaceWithExecution ie = new InterfaceWithExecution() {

			@Override
			public void appendTxt_testArtifacts(String str) {
				System.out.println(str);
			}

			@Override
			public void done_testArtifacts(boolean isfail_flag) {
				if(isfail_flag) {
					stop = true;
				}
				else {
				}
				blocking = false;
			}

			@Override
			public void appendTxt_intentSpec(String str) {
			}

			@Override
			public void done_intentSpec() {
			}
			
		};
		
		String genAdbCommand = System.getProperty("user.dir") 
				+ "/../GenTestsfromIntentSpec/bin/" + genCommand + " AdbCommand "; 
		
		
//				String intentSpec = sb.toString();
//				String path = JavaCommand.buildIntentSpecParam("param"+count+".is", intentSpec);
		
//				intentSpec = " -ftmp " + path;
		
		String intentSpec = " -ftmp " + intentspecfile.getAbsolutePath();
		
		//if(intentSepc.charAt(0) != '"')
		//{
			//intentSepc = "\"" + intentSepc;
		//}
		//if(intentSepc.charAt(intentSepc.length() - 1) != '"')
		//{
			//intentSepc = intentSepc + "\""; 
		//}
		
		genAdbCommand = genAdbCommand 
				+ " " + Integer.toString(random_type) + " " 
				+ " " + Integer.toString(num_of_intents) + " " 
				+ intentSpec + " "
				+ " > " + "\"" + adbcmdfile.getAbsolutePath() + "\"";
		
		System.out.println("RUN: " + genAdbCommand);
		
		Thread thread = ExecuteShellCommand.executeMakeTestArtifacts(ie, genAdbCommand);
		
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
//		while ( blocking )
//			;
		
		System.out.println("End of MakeTestArtifacts");
	}

	// Step 3: 
	boolean execute_install(File apkfile, String adb_path) {
		String cmd = adb_path + "adb install " + apkfile.getAbsolutePath();
		int error_code = -1;
		
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			error_code = p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("End of APK Installation");
		
		if (error_code == 0) 
			return true;
		else
			return false;
	}
	
	// Step 4:
	private void execute_ADBcommands(File adbcmdfile, String adb_path, String log_file_name, String pkg_name) {
		String[] args = { "-BATCH", adb_path, adbcmdfile.getAbsolutePath(), log_file_name, pkg_name };

		Benchmark.main(args);
		
		//System.out.println("End of Running ADB Commands");

	}
	
	// Step 5:
	void execute_uninstall(String apk_pkg_name, String adb_path, String log_file_name) {
		new Thread(){
			public void run(){
				File file = new File(log_file_name + ".lock");
				while(file.exists()) 
					;
				System.out.println("not exist lock");
				
				String cmd = adb_path + "adb uninstall " + apk_pkg_name;
				
				int error_code = -1;
				
				try {
					Process p = Runtime.getRuntime().exec(cmd);
					error_code = p.waitFor();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (error_code == 0) {
				}
				else {
					System.err.println("Uninstallation failed");
				}
				
				System.out.println("End of APK File Uninstallation");
			}
		}.start();
		
	}
}
