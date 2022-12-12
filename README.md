# Introduction
This is a GnuPG plugin for Apache JMeter.

# Required ...
-	A GnuPG installation with the `gpg` command in the execution path of the
	environment that JMeter will be running in
-	A password-less GnuPG key, with `ultimate` trusted status, in the keychain
	of the login that JMeter will be running as
-	Asset files encrypted for that key, with ascii-armoring enabled, named
	following a pattern like: `assetname`.`anyextension`.asc, and located in
	the working directory that JMeter will be running under

# Usage
A configuration item of this plugin's `GnuPG` type can be added into the test
plan for each asset (file) that will need to be decrypted.  Decryption will
take place at test launch, before any of the test's threads start.

The properties of an item are fairly self-explanatory.  Remember to align the
respective naming of the asset file(s), as the plugin will conform to these
practices:

-	An asset file will be encrypted with ascii-armoring enabled
-	An asset file will be named with an extension of `.asc`
-	The GnuPG plugin element will be configured with a file name value that
	omits the `.asc` extension, which will be added internally at decryption
	time.

# Example
Consider the scenario where a file named `credentials.json` is to be encrypted
for use (after decryption) during JMeter execution.

The public key for the key pair that will be present/used in the environment
that JMeter will be running in should first be acquired and added to the
keyring in a working environment with GnuPG installed.

The asset file, named `credentials.json`, should be encrypted like:
```
gpg --encrypt --ascii-armor --output credentials.json.asc -r thekey@address.com credentials.json
```

... this will create a file named `credentials.json.asc`.

When the `GnuPG` configuration item is added to the JMeter test plan, the
file name field should be set to `credentials.json`, as the `.asc` will be
internally added at decryption time.

# Building

# Pre-requisites
-	Apache JMeter
-	The OpenJDK jdk, version >= 8, ideally matching JMeter's pre-requisite
-	Adjustment of the `Makefile` to suit local filesystem and version details

After verifying the list of JMeter JAR versions in the Makefile's `LCP`
variable, make is used to build the JAR file in the current directory:
```
make
```

