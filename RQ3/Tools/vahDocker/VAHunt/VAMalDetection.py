import os
import glob
import zipfile
import subprocess
from testShell import testShell
from data_extract import data_extract
from ExtractCFGnew import ExtractCFGnew

root = os.getcwd()
samples_path = root + '/APKs/'
record_path = root + '/resdata/VAH_malrecord.csv'
tmp_path = root + '/tmp/'


if os.path.exists(record_path):
	record_file = open(record_path, 'a')
else:
	# csv head
	record_file = open(record_path, 'w')
	record_file.write('Name, Result\n')

# get samples list
apk_list = glob.glob(samples_path+'*.apk')
progress = 0
for apk in apk_list:
	splt = apk.split('/')
	sample_name = splt[len(splt)-1]
	progress += 1
	print '['+str(progress)+'/'+str(len(apk_list))+'] Analyzing '+sample_name
	
	# unzip dex files
	if not zipfile.is_zipfile(apk):
		record_file.write(sample_name+', ZIP_BROKEN\n')
		print '------ ZIP BROKEN'
		continue
	fz = zipfile.ZipFile(apk, 'r')
	for f in fz.namelist():
		if f.startswith('classes') and f.endswith('.dex'):
			fz.extract(f, tmp_path)
	
	# extract manifest
	manifest_path = tmp_path+'manifest.txt'
	manifest_file = open(manifest_path, 'w')
	subprocess.call(['./aapt', 'l','-a',apk], stdout=manifest_file)
	manifest_file.close()
	
	# disassemble with dexdump
	code_path = tmp_path+'code.txt'
	code_file = open(code_path, 'w')
	for dex in glob.glob(tmp_path+'*.dex'):
		subprocess.call(['./dexdump', '-d',dex], stdout=code_file)
	code_file.close()
	
	try:
		# checks the number of classes extracted and also checks the presence of some specific classes
		ts = testShell().detectShell(code_path)
		if ts == 0:
			record_file.write(sample_name+', Permissions extraction failed\n')
			print '------ Permissions extraction failed'
			continue
		if ts == 2:
			record_file.write(sample_name+', PACKED_APK\n')
			print '------ PACKED_APK'
			continue
		
		# extract main activities
		r, n, activities = data_extract().extractLauncher(manifest_path)
		
		# analyze
		result = ExtractCFGnew().extractCFGinCode(code_path, apk, n, activities)
		
	except Exception as e:
		print e
		result = 'BROKEN'
	
	# delete tmp files
	for f in glob.glob(tmp_path+'*'):
		os.remove(f)
	
	# print result
	print '------ '+result
	record_file.write(sample_name+', '+result+'\n')
record_file.close()


