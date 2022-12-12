// Copyright (c) 2020-2023 bransonvitz@protonmail.com All Rights Reserved.
//
// This file is part of jmgpg.
//
// jmgpg is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as
// published by the Free Software Foundation, either version 3 of
// the License, or (at your option) any later version.
//
// jmgpg is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the
// GNU General Public License along with this software.
// If not, see <http://www.gnu.org/licenses>.
package org.apache.jmeter;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;

public class GnuPGPlugin extends AbstractTestElement implements TestStateListener {

	public static class InputPipe implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public InputPipe(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(GnuPGPlugin.class);

//	Config keys
	private static final String URL = "Config.url";
	private static final String LOC = "Config.loc";
	private static final String NAME = "Config.name";
	private static final String RM = "Config.remove";

	private File assetFile;

//	Accessors for config values
	public void setURL(String url) { setProperty(URL,url); }
	public String getURL() { return getPropertyAsString(URL,""); }
	public void setLoc(String location) { setProperty(LOC,location); }
	public String getLoc() { return getPropertyAsString(LOC,""); }
	public void setFileName(String fileName) { setProperty(NAME,fileName); }
	public String getFileName() { return getPropertyAsString(NAME,"credentials"); }
	public void setRemove(Boolean rm) { setProperty(RM,rm); }
	public Boolean getRemove() { return getPropertyAsBoolean(RM,false); }

	public GnuPGPlugin() { super(); }

	@Override
	public void testStarted(String host) {
	}

	@Override
	public void testStarted() {
		String sHome = System.getProperty("user.home");
		boolean windows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	//	String sURL = getURL();
	//	LOG.info("Reading from {}", sURL);
	//	LOG.info("Working Directory = " + System.getProperty("user.dir"));
		String sFileName = getFileName();
		String loc = getLoc();
		assetFile	= (loc.equals("") || loc.equals(null))
						? new File(sFileName)
						: new File(new File(loc),sFileName);
		String sOut = assetFile.getAbsolutePath();
		try {
			List<String> args = new ArrayList<String>();
			if (windows) {
				args.add("cmd.exe");
				args.add("/c");
			} else {
				args.add("bash");
				args.add("-c");
			}
			args.add("gpg --decrypt --output "+sOut+" "+sFileName+".asc");
			ProcessBuilder cmd = new ProcessBuilder(args);
		//	cmd.directory(new File(sHome));
		//	LOG.info("Home: {}",sHome);
		//	LOG.info("Command ...");
		//	for (String arg : args) { LOG.info("Arg: {}",arg); }
			Process prc = cmd.start();
			InputPipe pipe = new InputPipe(prc.getInputStream(),System.out::println);
			Future<?> ftr = Executors.newSingleThreadExecutor().submit(pipe);
			int exitCode = prc.waitFor();
		//	LOG.info("exitCode: {}",exitCode);
		//	assert exitCode == 0;
			ftr.get(10,TimeUnit.SECONDS);
		} catch (Exception e) {
			LOG.info("exception: {}", e);
		}
	}

	@Override
	public void testEnded() {
		if (getRemove()) {
			LOG.info("Attempting to delete file {}", assetFile.getAbsolutePath());
			if (assetFile.exists() && assetFile.isFile()) {
				if (assetFile.delete()) {
					LOG.info("Successfully deleted file");
				} else LOG.info("Failed to delete file");
			}
		}
	}

	@Override
	public void testEnded(String host) {
	}
}
