import os, glob
import shutil
from platform import system
from pathlib import Path


def is_tool(name):
    from shutil import which

    return which(name) is not None


def delete_junk():
    path = os.getcwd()

    if system() == "Linux":
        path1 = path + "/Manifest_component_file/*"
        path2 = path + "/APP_code/*"
        path3 = path + "/APKs/*"

    else:
        path1 = path + "\\Manifest_component_file\\*"
        path2 = path + "\\APP_code\\*"
        path3 = path + "\\APKs\\*"
    lista_pattern = [path1, path2, path3]
    for single in lista_pattern:
        fileNames = glob.glob(single)
        for filename in fileNames:
            try:
                os.remove(filename)
            except:
                shutil.rmtree(filename)


def detection():
    rootPath = os.getcwd()
    print(system())
    outcome = rootPath + "/Outcome/"

    for root, dirs, files in os.walk(outcome):
        for apk in dirs:
            # if str(apk) == "RESULT.TXT":
            #     print(apk)
            # print(str(apk))
            # print(str(apk) == "RESULT.txt")
            # print(os.listdir(outcome+apk))
            # print(apk)

            try:
                f = open(outcome + apk + "/RESULT.txt")
                # print(f.read())
                txt = f.read()
                if txt.split()[5] != "NO":
                    print(txt)
                    shutil.copytree(
                        outcome + apk, rootPath + "/results/" + apk
                    )
                    shutil.copy(
                        rootPath + "/StoreAPK/" + apk,
                        rootPath + "/results/" + apk,
                    )
                f.close()

            except Exception as e:
                print(e)
            # if str(apk) == "RESULT.txt":
            #     f = open(outcome+apk,'r')
            #     print(read(f))
            #     f.close()

            # try:

            #     if system() == 'Linux':
            #         os.system(f"cp {temp}{apk} {APKpath} ")
            #     else:
            #         os.system(f"copy {temp}{apk} {APKpath} ")

            #     nome_apk=apk
            # print(Fore.LIGHTBLUE_EX + f"\n{count}) Starting with {nome_apk}\n")
            #     print(Fore.WHITE)
            #     apk=APKpath+nome_apk
            #     nome_file_aapt= manifest + nome_apk + ".txt"
            #     #Generate the APK-manifest file
            #     # print(Fore.RED + "\n1-MANIFEST EXTRACTION & COMPONENT INFORMATION FROM APK FILE...")
            #     # print(Fore.WHITE)
            #     if system() == 'Linux':
            #         answer=is_tool("aapt")
            #         if answer == "False":
            #             command000=f"sudo apt install aapt"
            #             os.system(command000)
            #             command = f"aapt l -a  {apk} >   {nome_file_aapt}"

            #         else:
            #             command = f"aapt l -a  {apk} >   {nome_file_aapt}"
            #     else:
            #         command = f"aapt.exe l -a  {apk} >   {nome_file_aapt}"

            #     result = os.popen(command)
            #     time.sleep(35)
            #     if os.stat(nome_file_aapt).st_size != 0:
            #         # print(Fore.GREEN + "The Manifest extraction worked!!!\n")
            #         # print(Fore.WHITE)
            #         split_manifest_component(nome_file_aapt,nome_apk,manifest)

            #         portion = os.path.splitext(nome_apk)  #  Separate file names and suffixes
            #         if portion[1] == ".apk":  # Edit according to suffix, if there is no suffix then blank
            #             newname = APKpath + portion[0] + ".zip"  # The new suffix to change
            #             os.rename(apk, newname)
            #             # print ("Rename successfully!!!: " + newname)
            #             # print(Fore.RED + "\n2-CODE SMALI EXTRACTION...\n")
            #             # print(Fore.WHITE)
            #             time.sleep(1)
            #             flag=extractCode(newname,APKpath,APPCode,nome_apk)
            #             if flag==0:
            #                 # print (Fore.GREEN + "Extracting the code file successfully！")
            #                 # print(Fore.WHITE)
            #                 path_file_dex= APPCode + nome_apk +".txt"
            #                 # print(Fore.RED + "\n3-READING CODE SMALI...\n")
            #                 time.sleep(2)
            #                 # print(Fore.WHITE)
            #                 path_dir_smali,path_code_smali=Read_code_smali(path_file_dex,nome_apk)
            #                 # print(Fore.RED + "4-FINAL EVALUATION...\n")
            #                 time.sleep(2)
            #                 # print("\n"+ Fore.WHITE)
            #                 tipo,res= IntentTraceback(nome_apk,path_dir_smali,path_code_smali,path_file_dex)

            #                 if res == 0:
            #                     print (Fore.RED + "FINAL ANSWER: Failure - Cycle")
            #                     print("\n"+ Fore.WHITE)
            #                 elif res == 2:
            #                     print (Fore.RED + "FINAL ANSWER: APK without VA")
            #                     print("\n"+ Fore.WHITE)
            #                     Scount = Scount + 1
            #                 elif (res != 0) and (res != 2):
            #                     print (Fore.GREEN + f"FINAL ANSWER: !!!!!!!!!!!!!!!!!!!! APK WITH VA --> {tipo}")
            #                     print("\n"+ Fore.WHITE)
            #                     Scount = Scount + 1
            #                     VirCount = VirCount + 1

            #                 delete_junk()

            #             else:
            #                 print(Fore.RED +"Error: No classes.dex found..\n")
            #                 print(Fore.WHITE)
            #                 delete_junk()
            #                 # sys.exit(0)

            #     else:
            #         print(Fore.RED + "Manifest file extraction failed!!!\n")
            #         print(Fore.WHITE)
            #         delete_junk()
            #         # sys.exit(0)

            # except:
            #     print(Fore.RED + "something went wrong, moving to next file")
            # count= count + 1
            # print("Found " + str(VirCount) + " Virtualizations out of " + str(Scount) + " successful files out of " + str(count) + " total files")


# Function to convert  list to string
def listToString(s):
    str1 = " "
    return str1.join(s)


# split the info of manifest and the component of apk file
def split_manifest_component(manifest, nome_apk, path):
    rootPath = os.getcwd()
    if system() == "Linux":
        outcome = f"{rootPath}/Outcome/{nome_apk}/"
    else:
        outcome = f"{rootPath}\\Outcome\\{nome_apk}\\"
    Path(outcome).mkdir(parents=True, exist_ok=True)
    trovato = False
    with open(manifest, "r") as file1:
        for num, line in enumerate(file1, 1):
            if "Android manifest:" in line:
                trovato = True
                linea = num

    if trovato == True:
        with open(manifest, "r") as file1:
            dati = file1.readlines()[linea - 1 :]
            dati_finali = listToString(dati)
        manifest1 = path + nome_apk + "_manifest.txt"
        with open(manifest1, "w") as file3:
            file3.write(dati_finali)

        if system() == "Linux":
            os.system(f"cp {manifest1} {outcome} ")
        else:
            os.system(f"copy {manifest1} {outcome} ")

        with open(manifest, "r") as file2:
            dati2 = file2.readlines()[: linea - 1]
            dati_finali2 = listToString(dati2)
        components = path + nome_apk + "_components.txt"
        with open(components, "w") as file4:
            file4.write(dati_finali2)

        if system() == "Linux":
            os.system(f"cp {components} {outcome} ")
        else:
            os.system(f"copy {components} {outcome} ")
    else:
        print("It Didn't find any manifest key in the file")


detection()
