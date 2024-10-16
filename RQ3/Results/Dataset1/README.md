This directory contains the results presented in Table 1 and discussed in Section 9.2 of the paper. In particular:

* `true_positive_s1_s2_matrioska_performance.csv` - Matrioska performance in detecting virtualization engines in virtualization-based apps;
* `true_positive_vahunt_performance.csv` - VAHunt performance in detecting virtualization engines in virtualization-based apps;
* `true_negative_s1_s2_matrioska_performance.csv` - Matrioska performance in detecting virtualization engines in NON virtualization-based apps;
* `true_negative_vahunt_performance.csv` - VAHunt performance in detecting virtualization engines in NON virtualization-based apps;

### Legend for true_negative_s1_s2_matrioska_performance.csv and true_positive_s1_s2_matrioska_performance.csv

* Name - Package name or File name of the sample;
* VAScore - score obtained with S1 and S2 signatures;
* AssetsAPK - number of apk files found in the sample's assets directory (static analysis);
* StubComponents - number of stub components identified in the sample's manifest (static analysis);
* StubProcesses - number of stub processes identified in the sample's manifest (static analysis);
* #Permissions - number of permissions identified in the sample's manifest (static analysis);
* #Activities - number of activities identified in the sample's manifest (static analysis);
* #Components - number of components identified in the sample's manifest (static analysis);
* #Processes - number of processes identified in the sample's manifest (static analysis).
* Framework - if any famous framework package appears in the sample.
