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
	private static final String IFN = "Config.ifn";
	private static final String IFL = "Config.ifl";
	private static final String OFN = "Config.ofn";
	private static final String OFL = "Config.ofl";
	private static final String RM = "Config.remove";

	private File assetFile;

//	Accessors for config values
	public void setIFN(String ifn) { setProperty(IFN,ifn); }
	public String getIFN() { return getPropertyAsString(IFN,"assets.json.asc"); }
	public void setIFL(String path) { setProperty(IFL,path); }
	public String getIFL() { return getPropertyAsString(IFL,""); }
	public void setOFN(String ofn) { setProperty(OFN,ofn); }
	public String getOFN() { return getPropertyAsString(OFN,"assets.json"); }
	public void setOFL(String path) { setProperty(OFL,path); }
	public String getOFL() { return getPropertyAsString(OFL,""); }
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
	//	LOG.info("Working Directory = " + System.getProperty("user.dir"));
		String sIFN = getIFN();
		String sIFL = getIFL();
		String sOFN = getOFN();
		String sOFL = getOFL();
		String fileName = sIFN;
		if (fileName.equals("") || fileName.equals(null)) {
			fileName = sOFN+".asc";
		}
		File inputFile	= (sIFL.equals("") || sIFL.equals(null))
							? new File(fileName)
							: new File(new File(sIFL),fileName);
		if (inputFile.exists() && inputFile.isFile()) {
			assetFile	= (sOFL.equals("") || sOFL.equals(null))
							? new File(sOFN)
							: new File(new File(sOFL),sOFN);
			String sOutputFile = assetFile.getAbsolutePath();
			try {
				List<String> args = new ArrayList<String>();
				if (windows) {
					args.add("cmd.exe");
					args.add("/c");
				} else {
					args.add("bash");
					args.add("-c");
				}
				args.add("gpg --decrypt --output "+sOutputFile+" "+inputFile.getAbsolutePath());
				ProcessBuilder cmd = new ProcessBuilder(args);
			//	LOG.info("Home: {}",sHome);
			//	cmd.directory(new File(sHome));
			//	LOG.info("Command ...");
			//	for (String arg : args) { LOG.info("Arg: {}",arg); }
				Process prc = cmd.start();
				InputPipe pipe = new InputPipe(prc.getInputStream(),System.out::println);
				Future<?> ftr = Executors.newSingleThreadExecutor().submit(pipe);
				int exitCode = prc.waitFor();
			//	LOG.info("exitCode: {}",exitCode);
				ftr.get(10,TimeUnit.SECONDS);
			} catch (Exception e) { LOG.error("exception: {}", e); }
		} else LOG.error("Invalid input file: {}", inputFile.getAbsolutePath());
	}

	@Override
	public void testEnded() {
		if (getRemove()) {
			if (assetFile.exists() && assetFile.isFile()) {
				LOG.info("Attempting to delete file {}", assetFile.getAbsolutePath());
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
