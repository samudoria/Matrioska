import os
import glob
import zipfile
import subprocess
from intent_state import intent_state
from Z3judge.intent import intent

root = os.getcwd()
samples_path = root + '/APKs/'
record_path = root + '/resdata/VAH_record.csv'
tmp_path = root + '/tmp/'


if os.path.exists(record_path):
	record_file = open(record_path, 'a')
else:
	# csv head
	record_file = open(record_path, 'w')
	record_file.write('Name,Result\n')

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
	
	# disassemble with dexdump
	code_path = tmp_path+'code.txt'
	code_file = open(code_path, 'w')
	for dex in glob.glob(tmp_path+'*.dex'):
		subprocess.call(['./dexdump', '-d',dex], stdout=code_file)
	code_file.close()
	
	try:
		# extract intent operations
		intent_path = tmp_path+'intent.txt'
		intent_state().IntentRegister(intent_path,  code_path)
		
		# analyze intent operations
		#result = APIbackward().IntentTraceback(intent_path, code_path)
		_, result = intent().IntentSubstitute(intent_path)
	except Exception as e:
		print e
		result = 'BROKEN'
	
	# delete tmp files
	for f in glob.glob(tmp_path+'*'):
		os.remove(f)
	
	# print result
	print '------ '+result
	record_file.write(sample_name+','+result+'\n')
record_file.close()


