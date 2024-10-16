This directory contains the results shown in Figure 3 (static analysis) and Figure 4 (dynamic analysis) and discussed in Section 9.3 of the paper.

## Static analysis

* `s1_s2_matrioska.csv` - contains Matrioska's S1 and S2 signatures applied over the 152,602 malware samples to search for virtualization engines;
* `s1_s2_matrioska_vahunt.csv` - contains Matrioska's S1 and S2 signatures and VAHunt first detection mechanism, applied over the 152,602 malware samples to search for virtualization engines.

### Legend for s1_s2_matrioska.csv

* Name - Package name or File name of the sample;
* VAScore - score obtained with S1 and S2 signatures;
* AssetsAPK - number of apk files found in the sample's assets directory (static analysis);
* StubComponents - number of stub components identified in the sample's manifest (static analysis);
* StubProcesses - number of stub processes identified in the sample's manifest (static analysis);
* #Permissions - number of permissions identified in the sample's manifest (static analysis);
* #Activities - number of activities identified in the sample's manifest (static analysis);
* #Components - number of components identified in the sample's manifest (static analysis);
* #Processes - number of processes identified in the sample's manifest (static analysis).
* virtualapp - reports if VirtualApp framework package appears in the sample;
* droidplugin - reports if DroidPlugin framework package appears in the sample.

### Legend for s1_s2_matrioska_vahunt.csv

* sha256: Hash of the sample as found on AndroZoo;
* Name - Package name or File name of the sample;
* AVScore - score obtained with S1 and S2 signatures;
* VAHunt - response of VAHunt app-virtualization detection script;
* VAH_mal - response of VAHunt malware detection script;
* Packed -  if the app is packed;
* Framework - if any famous framework package appears in the sample.

## Dynamic analysis

* `malware_reverseLabel.csv` - contains the final classification outcome (benign vs malicious) from both Matrioska and VAHunt over the 1764 samples found to have a virtualization engine. Moreover, it shows the outcome of the classification manually performed;
* `matrioska7_32_results_00.csv` - first outcome of experiment to evaluate all Matrioska's signatures over the 1764 samples found to have the virtualization technique. The experiment has been performed on an ARM32 device running Android 7;
* `matrioska7_32_results_01.csv` - second outcome of experiment to evaluate all Matrioska's signatures over the 1764 samples found to have the virtualization technique. The experiment has been performed on an ARM32 device running Android 7;
* `matrioska7_32_results_02.csv` - third outcome of experiment to evaluate all Matrioska's signatures over the 1764 samples found to have the virtualization technique. The experiment has been performed on an ARM32 device running Android 7;
* `matrioska7_results_00.csv`- outcome of experiment to evaluate all Matrioska's signatures over the 1764 samples found to have the virtualization technique. The experiment has been performed on an ARM64 device running Android 7;
* `matrioska9_32_results.csv` - outcome of experiment to evaluate all Matrioska's signatures over the 1764 samples found to have the virtualization technique. The experiment has been performed on an ARM32 device running Android 9;
* `matrioska9_results.csv` - outcome of experiment to evaluate all Matrioska's signatures over the 1764 samples found to have the virtualization technique. The experiment has been performed on an ARM64 device running Android 9;

### Legend for malware_reverseLabel.csv

* sha256: Hash of the sample as found on AndroZoo;
* Name - Package name or File name of the sample;
* Packed -  if the app is packed;
* Framework - if any famous framework package appears in the sample.
* AVScore - core obtained with S1 and S2 signatures;
* VAHunt - final outcome of VAHunt where SILENT = malicious, NOT_SILENT = not malicious;
* Matrioska - final outcome of Matrioska where SILENT = malicious, NOT_SILENT = not malicious;
* SameCertificates - if the sample and the spawned apks or assets apks have the same certificates;
* ReverseLabel - outcome of the manual classification where: silent = plugin started without user interaction (malicious); start-silent = like silent, but the plugin is started at app start-up (malicious); 0f3b = similar to one of the first samples we analyzed whose hash started with 0f3b (malicious); FalsePositive = false positive; packed = packed app;
* targetSdk - malware sample targetSdk.

### Legend for matrioskaXX_results_XX.csv

* Name - Package name or File name of the sample;
* Error - if Matrioska did not complete the analysis;
* VAScore - score obtained with S1 and S2 signatures;
* AssetsAPK - number of apk files found in the sample's assets directory;
* StubComponents - number of stub components identified in the sample's manifest;
* StubProcesses - number of stub processes identified in the sample's manifest;
* #Permissions - number of permissions identified in the sample's manifest;
* #Activities - number of activities identified in the sample's manifest;
* #Components - number of components identified in the sample's manifest;
* #Processes - number of processes identified in the sample's manifest;
* SpawnApk - number of apks loaded at runtime by the sample;
* SpawnProcesses - number of processes executed at runtime by the sample;
* StubIntent - whether an intent pointed at one stub component has been identified;
* #Packages - number of packages executed at runtime;
* UnknownIntent - number of intents targeting unknown components, i.e. components belonging to packages not installed on the device.
