# RQ1: How effective are existing defence mechanisms against VBR malware?

The directory contains datasets, source code, compiled code, and scripts to answer RQ1, as presented in Section 4 of the paper. In particular:

* `antivirtualization_libraries` - an app containing the four anti-virtualization libraries and a customized container applying hooks to bypass the anti-virtualization checks, as described in Section 4.1;
* `defeating_MARVEL`- a script for defeating the MARVEL protection and a set of 10 protected apps that can be automatically reconstructed through the script, as described in Section 4.2;
* `VAHunt` - a diff file highlighting the changes applied to the original VAHunt tool to run it, as described in Section 4.3. 
