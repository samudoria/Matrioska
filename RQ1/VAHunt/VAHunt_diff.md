# List of changes to VAHunt

As mentioned in Section 4.3, we have made some changes to VAHunt to work with our experimental environment.

Here, we illustrate a `git diff` of all the modifications.

We primarily introduced support for Unix systems, which was initially missing.
We modified some terminal commands to work on Unix-like machines and replaced the binaries accordingly.

We re-implemented some methods, maintaining their original logic, and fixed an important issue in the `extractCode` method, where the `dexdump` output is piped to a new file. However, the developers only wait 10 seconds for the process to finish before proceeding. In cases where this requires more time, incomplete or incorrect files are created.

Additionally, we introduced some code to save statistics related to the analysis.

## VAMalDetection

```
diff --git a/./VAHunt/VAHunt/VAMalDetection.py b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/VAMalDetection.py
index 6eb2cbb..62613d3 100644
--- a/./VAHunt/VAHunt/VAMalDetection.py
+++ b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/VAMalDetection.py
@@ -1,285 +1,85 @@
-# encoding: utf-8
 import os
-import zipfile
-import time
 import glob
-import shutil
-import threading
-import threadpool
-from CFGparse import CFGparse
-from ObjectAnalyzenew import ObjectAnalyzenew
-from ExtractCFGnew import ExtractCFGnew
-from Config import Config
+import zipfile
+import subprocess
 from testShell import testShell
 from data_extract import data_extract
+from ExtractCFGnew import ExtractCFGnew

-def file_name(file_dir):
-    files = []
-    for root, dirs, files in os.walk(file_dir):
-        print files
-    return files
-
-
-def unzip_file(zip_src, dst_dir):
-    try:
-        r = zipfile.is_zipfile(zip_src)
-        if r:
-            try:
-                fz = zipfile.ZipFile(zip_src, 'r')
-                for file in fz.namelist():
-                    if file.endswith(".dex"):
-                        fz.extract(file, dst_dir)
-            except Exception as e:
-                print('unnormal IOError：', e)
-            # finally:
-            # print("unzip IOError")
-        else:
-            print('This is not zip')
-    except Exception as e:
-        print('unnormal IOError：', e)
-
-
-# 字节bytes转化kb\m\g
-def formatSize(bytes):
-    try:
-        bytes = float(bytes)
-        kb = bytes / 1024
-    except:
-        print("传入的字节格式不对")
-        return "Error"
-
-    if kb >= 1024:
-        M = kb / 1024
-        if M >= 1024:
-            G = M / 1024
-            return "%fG" % (G)
-        else:
-            return "%fM" % (M)
-    else:
-        return "%fkb" % (kb)
-
-# 获取文件大小
-def getDocSize(path):
-    try:
-        size = os.path.getsize(path)
-        print "file size"
-        print size
-        return formatSize(size)
-    except Exception as err:
-        print(err)
-
-def search_file(dir,sname,classeslist):
-    if sname in os.path.split(dir)[1]:
-        # 检验文件名里是否包含sname
-        # 打印相对路径，相对指相对于当前路径
-        # print(os.path.relpath(dir))
-        # 打印文件名
-        # print os.path.split(dir)[1]
-        classeslist.append(os.path.split(dir)[1])
-    if os.path.isfile(dir):
-        # 如果传入的dir直接是一个文件目录 他就没有子目录，就不用再遍历它的子目录了
-        return
-    for dire in os.listdir(dir):
-        # 遍历子目录  这里的dire为当前文件名
-        search_file(os.path.join(dir,dire),sname,classeslist)
-        # join一下就变成了当前文件的绝对路径
-        # 对每个子目录路劲执行同样的操作
-    return classeslist
-
-def delfile(path):
-    #   read all the files under the folder
-    fileNames = glob.glob(path + r'\*')
-    for fileName in fileNames:
-        try:
-            # delete file
-            os.remove(fileName)
-        except:
-            try:
-                # delete empty folders
-                os.rmdir(fileName)
-            except:
-                # Not empty, delete files under folders
-                delfile(fileName)
-                # now, folders are empty, delete it
-                os.rmdir(fileName)
-
-# apk-manifest文件的生成
-def extractManifest(recordfile, file_manifesttxt, apkfile, broken_directory, apkpath, apk):
-    if not os.path.exists(file_manifesttxt):
-        command = "aapt.exe l -a " + apkfile + " > " + file_manifesttxt
-        print "command"
-        print command
-        result = os.popen(command)
-        time.sleep(3)
-        if os.path.exists(file_manifesttxt):
-            print "提取manifest文件成功！"
-        else:
-            print "提取manifest文件失败！"
-            # 移动文件
-            if os.path.exists(apkfile):
-                shutil.move(apkfile, broken_directory)
-            recordfile.write("未能正确提取manifest文件——失败\n")
-            return 0
-
-        # 修改后缀名，修改.apk为.zip
-        portion = os.path.splitext(apk)  # 分离文件名字和后缀
-        if portion[1] == ".apk":  # 根据后缀来修改,如无后缀则空
-            newname = apkpath + portion[0] + ".zip"  # 要改的新后缀
-            os.rename(apkfile, newname)
-            print "重命名成功！" + newname
-    return 1
-
-# apk-code文件的生成
-def extractCode(recordfile, apkfile, broken_directory, dexPath, file_codetxt):
-    if not os.path.exists(file_codetxt):
-        unzip_file(apkfile, dexPath)
-        # 处理多dex问题
-        classeslist = []
-        classeslist = search_file(dexPath, 'classes', classeslist)
-        print classeslist
-        print len(classeslist)
-
-        if len(classeslist) > 1:
-            num = 0
-            for i in classeslist:
-                dexname = dexPath + i
-                if num == 0:
-                    command0 = "dexdump.exe -d " + dexname + " > " + file_codetxt
-                    result0 = os.popen(command0)
-                    time.sleep(10)
-                    file_size = getDocSize(file_codetxt)
-                else:
-                    command1 = "dexdump.exe -d " + dexname + " >> " + file_codetxt
-                    result1 = os.popen(command1)
-                num = num + 1
-        elif len(classeslist) == 1:
-            dexname = dexPath + "classes.dex"
-            command2 = "dexdump.exe -d " + dexname + " > " + file_codetxt
-            result1 = os.popen(command2)
-
-        time.sleep(3)
-        if os.path.exists(file_codetxt):
-            print "提取code文件成功！"
-        else:
-            print "提取code文件失败！"
-            # 移动文件
-            if os.path.exists(apkfile):
-                shutil.move(apkfile, broken_directory)
-            recordfile.write("未能正确提取code文件——失败\n")
-            return 0
-    return 1
-
-# 获取还未处理的apk列表
-def read_apk_list(apkpath, apkfinishfilename):
-    # apk_list = Config.read_file(apkfilename)
-    apk_list = file_name(apkpath)
-    print(len(apk_list))
-    finish_apk_list = Config.read_file(apkfinishfilename)
-    print(len(set(finish_apk_list)))
-    for x in range(len(finish_apk_list)):
-        finish_apk_list[x] = (finish_apk_list[x].replace('\n', ''))
-    wait_apk_list = []
-    for apk in apk_list:
-        if apk not in finish_apk_list:
-            wait_apk_list.append(apk)
-    # wait_apk_list = list(set(apk_list) ^ set(finish_apk_list))
-    print "============="
-    print(len(wait_apk_list))
-    return wait_apk_list
-
-def startMalDetect(apkpath, recordfilename, apkfinishfilename):
-    # 检测恶意性
-    rootPath = os.getcwd()
-    cfgtxtlist = read_apk_list(apkpath, apkfinishfilename)
-    broken_directory = Config.BROKEN_PATH
-    count = 0
-
-    for apk in cfgtxtlist:
-        apkname = apk.split(".zip")[0]  # apk名，不带后缀
-        apkfile = apkpath + apk
-        manifestfile = apkname + ".zip-manifest.txt"
-        codefile = apkname + "-code.txt"
-        finishrecordfile = open(apkfinishfilename, "a+")
-        file_codetxt = Config.CODE_PATH + codefile
-        file_manifesttxt = Config.MANIFEST_PATH + manifestfile
-        dexPath = rootPath + "\\dex\\"
-        count = count + 1
-        print "count------"
-        print count
-        recordfile = open(recordfilename, "a+")
-        recordfile.write(apkname + "\n")
-        recordfile.close()
-        recordfile = open(recordfilename, "a+")
-
-
-        # 生成apk-manifest文件
-        mflag = extractManifest(recordfile, file_manifesttxt, apkfile, broken_directory, apkpath, apk)
-        if mflag == 0:
-            # 移动文件
-            finishrecordfile.write(apk + "\n")
-            if os.path.exists(apkfile):
-                shutil.move(apkfile, broken_directory)
-            continue
-        # 生成apk-code文件
-        cflag = extractCode(recordfile, apkfile, broken_directory, dexPath, file_codetxt)
-        if cflag == 0:
-            # 移动文件
-            finishrecordfile.write(apk + "\n")
-            if os.path.exists(apkfile):
-                shutil.move(apkfile, broken_directory)
-            continue
-
-        # 检测加壳
-        ts = testShell()
-        permres = ts.detectShell(file_codetxt)
-        if permres == 0:
-            print "权限提取——失败"
-            recordfile.write("权限提取——失败\n")
-            continue
-        elif permres == 2:
-            print "packed apk---1"
-            recordfile.write("未能提取出正常代码——packed apk——失败\n")
-            continue
-
-        # 提取apk入口
-        de = data_extract()
-        ret, applicationName, launchActivities = de.extractLauncher(file_manifesttxt)
-        print "launchActivities"
-        print launchActivities
-        # 获取加载策略相关
-        extractcfg = ExtractCFGnew()
-        if os.path.exists(file_codetxt):
-            print "There exists codefilePath " + file_codetxt
-            ret = extractcfg.extractCFGinCode(file_codetxt, count, apkpath, apk, recordfilename, applicationName, launchActivities)
-            if ret == 0:
-                print "code file broken!"
-            elif ret == 2:
-                print "packed apk"
-        else:
-            print "There exists none code file."
-            recordfile.write("code文件不存在——失败\n")
-
-        # 删除文件
-        delfile(dexPath)
-        finishrecordfile.write(apk + "\n")
-        finishrecordfile.close()
-        recordfile.close()
-
+root = os.getcwd()
+samples_path = root + '/APKs/'
+record_path = root + '/resdata/VAH_malrecord.csv'
+tmp_path = root + '/tmp/'
+
+
+if os.path.exists(record_path):
+	record_file = open(record_path, 'a')
+else:
+	# csv head
+	record_file = open(record_path, 'w')
+	record_file.write('Name, Result\n')
+
+# get samples list
+apk_list = glob.glob(samples_path+'*.apk')
+progress = 0
+for apk in apk_list:
+	splt = apk.split('/')
+	sample_name = splt[len(splt)-1]
+	progress += 1
+	print '['+str(progress)+'/'+str(len(apk_list))+'] Analyzing '+sample_name
+
+	# unzip dex files
+	if not zipfile.is_zipfile(apk):
+		record_file.write(sample_name+', ZIP_BROKEN\n')
+		print '------ ZIP BROKEN'
+		continue
+	fz = zipfile.ZipFile(apk, 'r')
+	for f in fz.namelist():
+		if f.startswith('classes') and f.endswith('.dex'):
+			fz.extract(f, tmp_path)
+
+	# extract manifest
+	manifest_path = tmp_path+'manifest.txt'
+	manifest_file = open(manifest_path, 'w')
+	subprocess.call(['./aapt', 'l','-a',apk], stdout=manifest_file)
+	manifest_file.close()
+
+	# disassemble with dexdump
+	code_path = tmp_path+'code.txt'
+	code_file = open(code_path, 'w')
+	for dex in glob.glob(tmp_path+'*.dex'):
+		subprocess.call(['./dexdump', '-d',dex], stdout=code_file)
+	code_file.close()
+
+	try:
+		# checks the number of classes extracted and also checks the presence of some specific classes
+		ts = testShell().detectShell(code_path)
+		if ts == 0:
+			record_file.write(sample_name+', Permissions extraction failed\n')
+			print '------ Permissions extraction failed'
+			continue
+		if ts == 2:
+			record_file.write(sample_name+', PACKED_APK\n')
+			print '------ PACKED_APK'
+			continue
+
+		# extract main activities
+		r, n, activities = data_extract().extractLauncher(manifest_path)
+
+		# analyze
+		result = ExtractCFGnew().extractCFGinCode(code_path, apk, n, activities)
+
+	except Exception as e:
+		print e
+		result = 'BROKEN'
+
+	# delete tmp files
+	for f in glob.glob(tmp_path+'*'):
+		os.remove(f)
+
+	# print result
+	print '------ '+result
+	record_file.write(sample_name+', '+result+'\n')
+record_file.close()

-if __name__=="__main__":
-    start = time.time()
-    # 多线程检测
-    rootPath = os.getcwd()
-    APKpath = rootPath + "\\APKs\\"
-    recordMalname1 = rootPath + Config.RESULT_RECORD_MALNAME
-    apkfinishMalname1 = rootPath + Config.MALVA_FINISH_SUBNAME1
-    filePath1 = [APKpath, recordMalname1, apkfinishMalname1]
-    par_list = [(filePath1, None)]
-    pool = threadpool.ThreadPool(32)
-    requests = threadpool.makeRequests(startMalDetect, par_list)
-    [pool.putRequest(req) for req in requests]
-    pool.wait()

-    end = time.time()
-    print "运行时间"
-    print (str(end-start))
```

## Replaced Windows-specific binaries

```
./VAHunt/VAHunt/aapt.exe deleted
./VAHunt/VAHunt/dexdump.exe deleted
```

## Changes to the Config.py file

```
diff --git a/./VAHunt/VAHunt/Config.py b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/Config.py
index 9bf9e81..0c0eb8e 100644
--- a/./VAHunt/VAHunt/Config.py
+++ b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/Config.py
@@ -6,24 +6,35 @@ class Config:

     @staticmethod
     def read_file(filename):

-                        #ret is useless
                         ret = []
                         with open(filename, "r") as fin:
                             return fin.readlines()

    rootPath = os.getcwd()

*
*   VA_SAMPLE_PATH = "E:\\samples\\va\\"
*   VA_SUBNAME1 = "\\config\\va-success-apklist.txt"
*   VA_SUBNAME2 = "\\config\\va-apklist.txt"
*   VA_FINISH_SUBNAME1 = "\\resdata\\finishAPK-record.txt"
*   MALVA_FINISH_SUBNAME1 = "\\resdata\\finishMAL-record.txt"
*   RESULT_RECORD_FILENAME = "\\resdata\\Record.txt"
*   RESULT_RECORD_MALNAME = "\\resdata\\MalRecord.txt"
*   BROKEN_PATH = "E:\\samples\\broken\\"
*   MANIFEST_PATH = rootPath + "\\APPmanifest\\"
*   CODE_PATH = rootPath + "\\APPcode\\"
*   TRAIN_NUM = "\\trainNumber.txt"
*   RES_DIR = "\\res\\"

-
-
-   # sample path of vahunt, it will contain the directory "APKs" with the samples
-   # it's actually useless because apks are not accessed through it,
-   # feel free to add a placeholder
-
-   VA_SAMPLE_PATH = "/APKs/"
-   VA_SUBNAME1 = "/config/va-success-apklist.txt" #use \\ instead of / on Windows
-   VA_SUBNAME2 = "/config/va-apklist.txt" #use \\ instead of / on Windows
-   #list of apks that have been already analyzed and to exclude
-   VA_FINISH_SUBNAME1 = "/resdata/finishAPK-record.txt" #use \\ instead of / on Windows
-   MALVA_FINISH_SUBNAME1 = "/resdata/finishMAL-record.txt" #use \\ instead of / on Windows
-   #where results will be stored
-   RESULT_RECORD_FILENAME = "/resdata/Record.txt" #use \\ instead of / on Windows
-   RESULT_RECORD_MALNAME = "/resdata/MalRecord.txt" #use \\ instead of / on Windows
-   #contains information about "broken" apks
-   BROKEN_PATH = "/broken/"
-   #where manifest will be stored
-   MANIFEST_PATH = rootPath + "/APPmanifest/" #use \\ instead of / on Windows
-   #where app code will be stored
-   CODE_PATH = rootPath + "/APPcode/" #use \\ instead of / on Windows
-   TRAIN_NUM = "/trainNumber.txt" #use \\ instead of / on Windows
-   RES_DIR = "/res/" #use \\ instead of / on Windows

if '__name__' == '__main__':
    print(Config.VA_SAMPLE_PATH)
```

## Adding support to Linux to ExtractCFGnew.py

```
diff --git a/./VAHunt/VAHunt/ExtractCFGnew.py b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/ExtractCFGnew.py
index df82faf..13ef02a 100644
--- a/./VAHunt/VAHunt/ExtractCFGnew.py
+++ b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/ExtractCFGnew.py
@@ -43,6 +43,7 @@ class ExtractCFGnew:
apkname = apk.split(".zip")[0]
manifestfile = apkname + ".zip-manifest.txt"
file_manifesttxt = Config.MANIFEST_PATH + manifestfile

-                                file_manifesttxt = os.getcwd() + '/tmp/manifest.txt' # ALTERED

                                   with open(file_manifesttxt, "r") as f:
                                       lines = f.readlines()

    @@ -58,7 +59,7 @@ class ExtractCFGnew:
    i = i + 1
    return xmlname

*   def checkXMLitems(self, apkpath, apk, xmlnum):

-   def checkXMLitems(self, apk, xmlnum): # 查看 xml 中的 Item
    image = 0
    textview = 0
    @@ -68,18 +69,22 @@ class ExtractCFGnew:
    print xmlname # aapt dump xmlstrings 725.apk res/layout/item_clone_app.xml
    if xmlname != "default":

*                                    apkfile = apkpath + apk

-                                    apkfile = apk
                                     xmlfile = "res/layout/" + xmlname + ".xml"

*                                    xmlconstruct = rootPath + "\\resdata\\" + xmlname + ".txt"
*                                    command = "aapt.exe dump xmlstrings " + apkfile + " " + xmlfile + " > " + xmlconstruct

-                                    xmlconstruct = rootPath + "/tmp/" + xmlname + ".txt"
-                                    command = "./aapt dump xmlstrings " + apkfile + " " + xmlfile + " > " + xmlconstruct
-                                    if os.name == 'nt':
-                                        xmlfile = "res/layout/" + xmlname + ".xml"
-                                        xmlconstruct = rootPath + "\\tmp\\" + xmlname + ".txt"
-                                        command = "aapt.exe dump xmlstrings " + apkfile + " " + xmlfile + " > " + xmlconstruct
                                     print "command"
                                     print command
                                     result = os.popen(command)
                                     time.sleep(0.5)
                                     if os.path.exists(xmlconstruct):

*                                        print "提取XML文件内容成功！"

-                                        print "The XML file content was extracted successfully!"
                                     else:

*                                        print "提取XML文件失败！"

-                                        print "Failed to extract XML file!"
                                           return 0, 0

                                       with open(xmlconstruct, "r") as f:

    @@ -103,7 +108,7 @@ class ExtractCFGnew:

                               # extractPerminCode
                               # 对外调用的函数

*   def extractCFGinCode(self, file_codetxt, apkcount, apkpath, apk, recordfilename, applicationName, launchActivities):

-   def extractCFGinCode(self, file_codetxt, apk, applicationName, launchActivities):
    selfperm = []
    install = 0
    hide = 0
    @@ -121,7 +126,7 @@ class ExtractCFGnew:
    with open(file_codetxt, "rU") as f:
    lines = f.readlines()
    lines_length = len(lines)

*                                    ret = 0

-                                    ret = 'BROKEN'
                                       print "code file lines_length"
                                       print lines_length
                                       if lines_length < 10:
    @@ -246,7 +251,7 @@ class ExtractCFGnew:
    print "xmlnum"
    print xmlnum # 判断是否有 xml 中是否有 ListView

*                                                                    imageflag, textviewflag = self.checkXMLitems(apkpath, apk, xmlnum)

-                                                                    imageflag, textviewflag = self.checkXMLitems(apk, xmlnum)
                                                                       print "image = " + str(imageflag)
                                                                       print "textview = " + str(textviewflag)
                                                                       if imageflag == 1 and textviewflag == 1:
    @@ -413,13 +418,7 @@ class ExtractCFGnew:
    i = i + 1
    i = i + 1

*                                    if class_count == 0:
*                                        ret = 0
*                                    elif len(class_shell) != 0:
*                                        ret = 2
*                                    else:
*                                        ret = 1
*

-                                      # print "UIClasslist & linkClasslist"
                                       # print UIClasslist
                                       # print linkClasslist

    @@ -430,11 +429,16 @@ class ExtractCFGnew:

                                       if invoked == 1:
                                           show = 1

*
*                                    recordfile = open(recordfilename, "a+")
*                                    recordfile.write("silent = " + str(slient) +"\n")
*                                    recordfile.close()
*                                    print "silent---------" + str(slient)

-
-                                    if class_count == 0:
-                                        ret = 'BROKEN'
-                                    elif len(class_shell) != 0:
-                                        ret = 'PACKED_APK'
-                                    else:
-                                        if slient == 1:
-                                        	ret = 'SILENT'
-                                        else:
-                                        	ret = 'NOT_SILENT'

                                       # 从这里开始寻找UI界面的调用链
                                       # ui = UIAnalyzer()
```

## VADetection

```

diff --git a/./VAHunt/VAHunt/VADetection.py b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/VADetection.py
index 3bd9e72..cf15bd5 100644
--- a/./VAHunt/VAHunt/VADetection.py
+++ b/./Matrioska_appvirtualization/RQ3/Tools/vahDocker/VAHunt/VADetection.py
@@ -1,292 +1,68 @@
-# encoding: utf-8
import os
-import zipfile
-import time
import glob
-import string
-import shutil
-import threadpool

-   -from data_extract import data_extract
    +import zipfile
    +import subprocess
    from intent_state import intent_state
    -from Z3judge.APIbackward import APIbackward
    -from code_api import code_api
    -from testShell import testShell
    -from Config import Config
-
-   -def unzip_file(zip_src, dst_dir):
-   r = zipfile.is_zipfile(zip_src)
-   if r:
-                                try:
-                                    fz = zipfile.ZipFile(zip_src, 'r')
-                                    for file in fz.namelist():
-                                        if file.endswith(".dex"):
-                                            fz.extract(file, dst_dir)
-                                except Exception as e:
-                                    print('unnormal IOError：',e)
-                                # finally:
-                                    # print("unzip IOError")
-   else:
-                                print('This is not zip')
-   -def file_name(file_dir):
-   files = []
-   for root, dirs, files in os.walk(file_dir):
-                                pass
-                                #print files
-   return files
-   -# 字节 bytes 转化 kb\m\g
    -def formatSize(bytes):
-   try:
-                                bytes = float(bytes)
-                                kb = bytes / 1024
-   except:
-                                print("传入的字节格式不对")
-                                return "Error"
-
-   if kb >= 1024:
-                                M = kb / 1024
-                                if M >= 1024:
-                                    G = M / 1024
-                                    return "%fG" % (G)
-                                else:
-                                    return "%fM" % (M)
-   else:
-                                return "%fkb" % (kb)
-   -# 获取文件大小
    -def getDocSize(path):
-   try:
-                                size = os.path.getsize(path)
-                                print "file size"
-                                print size
-                                return formatSize(size)
-   except Exception as err:
-                                print(err)
-   -def search_file(dir,sname,classeslist):
-   if sname in os.path.split(dir)[1]:
-                                # 检验文件名里是否包含sname
-                                # 打印相对路径，相对指相对于当前路径
-                                # print(os.path.relpath(dir))
-                                # 打印文件名
-                                # print os.path.split(dir)[1]
-                                classeslist.append(os.path.split(dir)[1])
-   if os.path.isfile(dir):
-                                # 如果传入的dir直接是一个文件目录 他就没有子目录，就不用再遍历它的子目录了
-                                return
-   for dire in os.listdir(dir):
-                                # 遍历子目录  这里的dire为当前文件名
-                                search_file(os.path.join(dir,dire),sname,classeslist)
-                                # join一下就变成了当前文件的绝对路径
-                                # 对每个子目录路劲执行同样的操作
-   return classeslist
-   -def delfile(path):
-   # read all the files under the folder
-   fileNames = glob.glob(path + r'\*')
-   for fileName in fileNames:
-                                try:
-                                    # delete file
-                                    os.remove(fileName)
-                                except:
-                                    try:
-                                        # delete empty folders
-                                        os.rmdir(fileName)
-                                    except:
-                                        # Not empty, delete files under folders
-                                        delfile(fileName)
-                                        # now, folders are empty, delete it
-                                        # os.rmdir(fileName)
-
-   -# apk-manifest 文件的生成
    -def extractManifest(recordfile, file_manifesttxt, apkfile, broken_directory, apkpath, apk):
-   if not os.path.exists(file_manifesttxt):
-                                command = "aapt.exe l -a " + apkfile + " > " + file_manifesttxt
-                                print "command"
-                                print command
-                                result = os.popen(command)
-                                time.sleep(5)
-                                if os.path.exists(file_manifesttxt):
-                                    print "提取manifest文件成功！"
-                                else:
-                                    print "提取manifest文件失败！"
-                                    # 移动文件
-                                    if os.path.exists(apkfile):
-                                        shutil.move(apkfile, broken_directory)
-                                    recordfile.write("未能正确提取manifest文件——失败\n")
-                                    return 0
-
-                                # 修改后缀名，修改.apk为.zip
-                                portion = os.path.splitext(apk)  # 分离文件名字和后缀
-                                if portion[1] == ".apk":  # 根据后缀来修改,如无后缀则空
-                                    newname = apkpath + portion[0] + ".zip"  # 要改的新后缀
-                                    os.rename(apkfile, newname)
-                                    print "重命名成功！" + newname
-   return 1
-   -# apk-code 文件的生成
    -def extractCode(recordfile, apkfile, broken_directory, dexPath, file_codetxt):
-   if not os.path.exists(file_codetxt):
-                                unzip_file(apkfile, dexPath)
-                                # 处理多dex问题
-                                classeslist = []
-                                classeslist = search_file(dexPath, 'classes', classeslist)
-                                print classeslist
-                                print len(classeslist)
-
-                                if len(classeslist) > 1:
-                                    num = 0
-                                    for i in classeslist:
-                                        dexname = dexPath + i
-                                        if num == 0:
-                                            command0 = "dexdump.exe -d " + dexname + " > " + file_codetxt
-                                            result0 = os.popen(command0)
-                                            time.sleep(10)
-                                            file_size = getDocSize(file_codetxt)
-                                        else:
-                                            command1 = "dexdump.exe -d " + dexname + " >> " + file_codetxt
-                                            result1 = os.popen(command1)
-                                        num = num + 1
-                                elif len(classeslist) == 1:
-                                    dexname = dexPath + "classes.dex"
-                                    command2 = "dexdump.exe -d " + dexname + " > " + file_codetxt
-                                    # command1 = "dexdump.exe -d C:\Users\lu\Desktop\DataExTract-complete-20190114\DataExTract\dex\classes.dex > C:\Users\lu\Desktop\DataExTract-complete-20190114\DataExTract\dex\lest.txt"
-                                    result1 = os.popen(command2)
-
-                                time.sleep(3)
-                                if os.path.exists(file_codetxt):
-                                    print "提取code文件成功！"
-                                else:
-                                    print "提取code文件失败！"
-                                    # 移动文件
-                                    if os.path.exists(apkfile):
-                                        try:
-                                            shutil.move(apkfile, broken_directory)
-                                        except Exception as e:
-                                            print('move error!  ', e)
-                                    recordfile.write("未能正确提取code文件——失败\n")
-                                    return 0
-   return 1
-
-
-   -# 获取还未处理的 apk 列表
    -def read_apk_list(apkpath, apkfinishfilename):
-   # apk_list = Config.read_file(apkfilename)
-   apk_list = file_name(apkpath)
-   print(len(apk_list))
-   # print "**\*\***\*\***\*\***\*\***\*\***\*\***\*\***"
-   # print apk_list[0:10]
-   finish_apk_list = Config.read_file(apkfinishfilename)
-   #print(len(finish_apk_list))
-   print(len(set(finish_apk_list)))
-   #print finish_apk_list[:10]
-   for x in range(len(finish_apk_list)):
-                                finish_apk_list[x] = (finish_apk_list[x].replace('\n', ''))
-   # print finish_apk_list[0:10]
-   wait_apk_list = []
-   for apk in apk_list:
-                                if apk not in finish_apk_list:
-                                    wait_apk_list.append(apk)
-   # wait_apk_list = list(set(apk_list) ^ set(finish_apk_list))
-   print "============="
-   print(len(wait_apk_list))
-   return wait_apk_list
-   -def startVADetect(apkpath, recordfilename, apkfinishfilename):
-   rootPath = os.getcwd()
-   # apklist = file_name(apkpath)
-   print "apkpath"
-   print apkpath
-   apklist = read_apk_list(apkpath, apkfinishfilename)
-   # print "apklist"
-   # print apklist[0:20]
-   count = 0
-   broken_directory = Config.BROKEN_PATH
-
-   for apk in apklist:
-                                count = count + 1
-                                print "count"
-                                print count
-                                finishrecordfile = open(apkfinishfilename, "a+")
-                                apkname = apk.split(".zip")[0]   # apk名，不带后缀
-                                # apk = apk.split("\n")[0]
-                                apkfile = apkpath + apk
-                                manifestfile = apkname + ".zip-manifest.txt"
-                                codefile = apkname + "-code.txt"
-                                intentfuncfile = apkname + "-intentfunc.txt"
-                                file_codetxt = Config.CODE_PATH + codefile
-                                file_manifesttxt = Config.MANIFEST_PATH + manifestfile
-                                file_intentfunc = rootPath + "\\resdata\\" + intentfuncfile
-                                dexPath = rootPath + "\\dex\\"
-                                recordfile = open(recordfilename, "a+")
-                                recordfile.write(apkname + "\n")
-                                recordfile.close()
-                                recordfile = open(recordfilename, "a+")
-
-                                # 生成apk-manifest文件
-                                mflag = extractManifest(recordfile, file_manifesttxt, apkfile, broken_directory, apkpath, apk)
-                                if mflag == 0:
-                                    # 移动文件
-                                    finishrecordfile.write(apk + "\n")
-                                    if os.path.exists(apkfile):
-                                        shutil.move(apkfile, broken_directory)
-                                    continue
-                                # 生成apk-code文件
-                                print "=========="
-                                print apkfile
-                                print dexPath
-                                print file_codetxt
-                                cflag = extractCode(recordfile, apkfile, broken_directory, dexPath, file_codetxt)
-                                if cflag == 0:
-                                    # 移动文件
-                                    finishrecordfile.write(apk + "\n")
-                                    if os.path.exists(apkfile):
-                                        shutil.move(apkfile, broken_directory)
-                                    continue
-
-
-                                # intent相关的寄存器获取
-                                ins = intent_state()
-                                ins.IntentRegister(file_intentfunc, file_codetxt)
-
-                                # 检测组件代理 ###### 单独入口
-                                # z3中的APIbackward
-                                ab = APIbackward()
-                                res = ab.IntentTraceback(file_intentfunc, file_codetxt, recordfilename)
-                                if res == 0:
-                                    print "失败——循环"
-                                    recordfile.write("失败——循环\n")
-                                elif res == 2:
-                                    print "无VA引擎"
-                                    recordfile.write("无VA引擎\n")
-
-                                delfile(dexPath)
-                                finishrecordfile.write(apk + "\n")
-                                finishrecordfile.close()
-                                recordfile.close()
-
-
-   -if **name** == '**main**':
-   start = time.time()
-   rootPath = os.getcwd()
-   APKpath = rootPath + "\\APKs\\"
-   recordfilename1 = rootPath + Config.RESULT_RECORD_FILENAME
-   apkfinishfilename1 = rootPath + Config.VA_FINISH_SUBNAME1
-   filePath1 = [APKpath, recordfilename1, apkfinishfilename1]
-   # par_list = [(filePath1, None), (filePath2, None)]
-   par_list = [(filePath1, None)]
-   pool = threadpool.ThreadPool(32)
-   requests = threadpool.makeRequests(startVADetect, par_list)
-   [pool.putRequest(req) for req in requests]
-   pool.wait()
    +from Z3judge.intent import intent

*   +root = os.getcwd()
    +samples_path = root + '/APKs/'
    +record_path = root + '/resdata/VAH_record.csv'
    +tmp_path = root + '/tmp/'
*
*   +if os.path.exists(record_path):
*   record_file = open(record_path, 'a')
    +else:
*   # csv head
*   record_file = open(record_path, 'w')
*   record_file.write('Name,Result\n')
*   +# get samples list
    +apk_list = glob.glob(samples_path+'\*.apk')
    +progress = 0
    +for apk in apk_list:
*   splt = apk.split('/')
*   sample_name = splt[len(splt)-1]
*   progress += 1
*   print '['+str(progress)+'/'+str(len(apk_list))+'] Analyzing '+sample_name
*
*   # unzip dex files
*   if not zipfile.is_zipfile(apk):
*                             record_file.write(sample_name+', ZIP_BROKEN\n')
*                             print '------ ZIP BROKEN'
*                             continue
*   fz = zipfile.ZipFile(apk, 'r')
*   for f in fz.namelist():
*                             if f.startswith('classes') and f.endswith('.dex'):
*                             	fz.extract(f, tmp_path)
*
*   # disassemble with dexdump
*   code_path = tmp_path+'code.txt'
*   code_file = open(code_path, 'w')
*   for dex in glob.glob(tmp_path+'\*.dex'):
*                             subprocess.call(['./dexdump', '-d',dex], stdout=code_file)
*   code_file.close()
*
*   try:
*                             # extract intent operations
*                             intent_path = tmp_path+'intent.txt'
*                             intent_state().IntentRegister(intent_path,  code_path)
*
*                             # analyze intent operations
*                             #result = APIbackward().IntentTraceback(intent_path, code_path)
*                             _, result = intent().IntentSubstitute(intent_path)
*   except Exception as e:
*                             print e
*                             result = 'BROKEN'
*
*   # delete tmp files
*   for f in glob.glob(tmp_path+'\*'):
*                             os.remove(f)
*
*   # print result
*   print '------ '+result
*   record_file.write(sample_name+','+result+'\n')
    +record_file.close()

-   end = time.time()
-   print "running time"
-   print (str(end - start))

```
