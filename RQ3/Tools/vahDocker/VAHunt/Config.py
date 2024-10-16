# encoding: utf-8
import os

# to configure file paths and running parameters
class Config:

    @staticmethod
    def read_file(filename):
        #ret is useless
        ret = []
        with open(filename, "r") as fin:
            return fin.readlines()

    rootPath = os.getcwd()
    
	
    # sample path of vahunt, it will contain the directory "APKs" with the samples
    # it's actually useless because apks are not accessed through it,
    # feel free to add a placeholder
    
    VA_SAMPLE_PATH = "/APKs/"
    VA_SUBNAME1 = "/config/va-success-apklist.txt" #use \\ instead of / on Windows
    VA_SUBNAME2 = "/config/va-apklist.txt" #use \\ instead of / on Windows
    #list of apks that have been already analyzed and to exclude 
    VA_FINISH_SUBNAME1 = "/resdata/finishAPK-record.txt" #use \\ instead of / on Windows
    MALVA_FINISH_SUBNAME1 = "/resdata/finishMAL-record.txt" #use \\ instead of / on Windows
    #where results will be stored
    RESULT_RECORD_FILENAME = "/resdata/Record.txt" #use \\ instead of / on Windows
    RESULT_RECORD_MALNAME = "/resdata/MalRecord.txt" #use \\ instead of / on Windows
    #contains information about "broken" apks
    BROKEN_PATH =  "/broken/"
    #where manifest will be stored
    MANIFEST_PATH = rootPath + "/APPmanifest/" #use \\ instead of / on Windows
    #where app code will be stored
    CODE_PATH = rootPath + "/APPcode/" #use \\ instead of / on Windows
    TRAIN_NUM = "/trainNumber.txt" #use \\ instead of / on Windows
    RES_DIR = "/res/" #use \\ instead of / on Windows

if __name__ == '__main__':
    print(Config.VA_SAMPLE_PATH)

