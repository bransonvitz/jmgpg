# Introduction
This is a GnuPG plugin for Apache JMeter.

## Prerequisites
-	A GnuPG installation with the `gpg` command in the execution path of the
	environment that JMeter will be running in
-	A password-less GnuPG key, with `ultimate` trusted status, in the keychain
	of the login that JMeter will be running as
-	Asset file(s) encrypted for that key, with ascii-armoring enabled, named
	following a pattern like: `assetname`.`extension`.asc, and located in
	the working directory that JMeter will be running under

# Usage
A configuration item of this plugin's `GnuPG` type can be added into the test
plan for each asset (file) that will need to be decrypted.  Decryption will
take place at test launch, before any of the test's threads start.

The properties of an item are fairly self-explanatory, with the respective
path fields being optional.

It is also possible to specify only the output file name, in which case the
input file name will be implied as `outputfile.name`.asc and assumed to be
encrypted with ascii-armoring enabled.

# Example
Consider the scenario where a file named `credentials.json` is to be encrypted
for use (after decryption) during JMeter execution.

The public key for the key pair that will be present/used in the environment
that JMeter will be running in should first be acquired and added to the
keyring in a working environment with GnuPG installed.

The asset file, named `credentials.json`, should be encrypted like:
```
gpg --encrypt --armor --output credentials.json.asc -r thekey@address.com credentials.json
```

... this will create a file named `credentials.json.asc`, which is what should
be present for JMeter to use at execution time.

When the `GnuPG` configuration item is added to the JMeter test plan, the
output file name field could be set to `credentials.json`, and the `.asc`
would be implied and internally added to input file name at decryption time.

Alternately, an explicit input file name could also be specified.

# Building

## Prerequisites
-	Apache JMeter
-	The OpenJDK jdk, version >= 8, ideally matching JMeter's pre-requisite
-	Adjustment of the `Makefile` to suit local filesystem and version details

## Compilation
After verifying the list of JMeter JAR versions in the Makefile's `LCP`
variable, `make` is used to build the JAR file in the current directory:
```
make
```

