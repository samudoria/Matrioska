//----------------------begin of globals.js-------------------------------------

"use strict";

var FLAG_SECURE_VALUE = "";
var mode = "";
var methodURL = "";
var requestHeaders = "";
var requestBody = "";
var responseHeaders = "";
var responseBody = "";
var filterKeyWords = [
    "fragment",
    "browser",
    "destin",
    "url",
    "path",
    "uri",
    "page",
    "attachment",
    "file",
    "dir",
    "http",
    "navigat",
    "link",
    "redir",
    "web",
    "intent",
    "html",
    "domain",
];

var index = 0;

const jni_struct_array = [
    "reserved0",
    "reserved1",
    "reserved2",
    "reserved3",
    "GetVersion",
    "DefineClass",
    "FindClass",
    "FromReflectedMethod",
    "FromReflectedField",
    "ToReflectedMethod",
    "GetSuperclass",
    "IsAssignableFrom",
    "ToReflectedField",
    "Throw",
    "ThrowNew",
    "ExceptionOccurred",
    "ExceptionDescribe",
    "ExceptionClear",
    "FatalError",
    "PushLocalFrame",
    "PopLocalFrame",
    "NewGlobalRef",
    "DeleteGlobalRef",
    "DeleteLocalRef",
    "IsSameObject",
    "NewLocalRef",
    "EnsureLocalCapacity",
    "AllocObject",
    "NewObject",
    "NewObjectV",
    "NewObjectA",
    "GetObjectClass",
    "IsInstanceOf",
    "GetMethodID",
    "CallObjectMethod",
    "CallObjectMethodV",
    "CallObjectMethodA",
    "CallBooleanMethod",
    "CallBooleanMethodV",
    "CallBooleanMethodA",
    "CallByteMethod",
    "CallByteMethodV",
    "CallByteMethodA",
    "CallCharMethod",
    "CallCharMethodV",
    "CallCharMethodA",
    "CallShortMethod",
    "CallShortMethodV",
    "CallShortMethodA",
    "CallIntMethod",
    "CallIntMethodV",
    "CallIntMethodA",
    "CallLongMethod",
    "CallLongMethodV",
    "CallLongMethodA",
    "CallFloatMethod",
    "CallFloatMethodV",
    "CallFloatMethodA",
    "CallDoubleMethod",
    "CallDoubleMethodV",
    "CallDoubleMethodA",
    "CallVoidMethod",
    "CallVoidMethodV",
    "CallVoidMethodA",
    "CallNonvirtualObjectMethod",
    "CallNonvirtualObjectMethodV",
    "CallNonvirtualObjectMethodA",
    "CallNonvirtualBooleanMethod",
    "CallNonvirtualBooleanMethodV",
    "CallNonvirtualBooleanMethodA",
    "CallNonvirtualByteMethod",
    "CallNonvirtualByteMethodV",
    "CallNonvirtualByteMethodA",
    "CallNonvirtualCharMethod",
    "CallNonvirtualCharMethodV",
    "CallNonvirtualCharMethodA",
    "CallNonvirtualShortMethod",
    "CallNonvirtualShortMethodV",
    "CallNonvirtualShortMethodA",
    "CallNonvirtualIntMethod",
    "CallNonvirtualIntMethodV",
    "CallNonvirtualIntMethodA",
    "CallNonvirtualLongMethod",
    "CallNonvirtualLongMethodV",
    "CallNonvirtualLongMethodA",
    "CallNonvirtualFloatMethod",
    "CallNonvirtualFloatMethodV",
    "CallNonvirtualFloatMethodA",
    "CallNonvirtualDoubleMethod",
    "CallNonvirtualDoubleMethodV",
    "CallNonvirtualDoubleMethodA",
    "CallNonvirtualVoidMethod",
    "CallNonvirtualVoidMethodV",
    "CallNonvirtualVoidMethodA",
    "GetFieldID",
    "GetObjectField",
    "GetBooleanField",
    "GetByteField",
    "GetCharField",
    "GetShortField",
    "GetIntField",
    "GetLongField",
    "GetFloatField",
    "GetDoubleField",
    "SetObjectField",
    "SetBooleanField",
    "SetByteField",
    "SetCharField",
    "SetShortField",
    "SetIntField",
    "SetLongField",
    "SetFloatField",
    "SetDoubleField",
    "GetStaticMethodID",
    "CallStaticObjectMethod",
    "CallStaticObjectMethodV",
    "CallStaticObjectMethodA",
    "CallStaticBooleanMethod",
    "CallStaticBooleanMethodV",
    "CallStaticBooleanMethodA",
    "CallStaticByteMethod",
    "CallStaticByteMethodV",
    "CallStaticByteMethodA",
    "CallStaticCharMethod",
    "CallStaticCharMethodV",
    "CallStaticCharMethodA",
    "CallStaticShortMethod",
    "CallStaticShortMethodV",
    "CallStaticShortMethodA",
    "CallStaticIntMethod",
    "CallStaticIntMethodV",
    "CallStaticIntMethodA",
    "CallStaticLongMethod",
    "CallStaticLongMethodV",
    "CallStaticLongMethodA",
    "CallStaticFloatMethod",
    "CallStaticFloatMethodV",
    "CallStaticFloatMethodA",
    "CallStaticDoubleMethod",
    "CallStaticDoubleMethodV",
    "CallStaticDoubleMethodA",
    "CallStaticVoidMethod",
    "CallStaticVoidMethodV",
    "CallStaticVoidMethodA",
    "GetStaticFieldID",
    "GetStaticObjectField",
    "GetStaticBooleanField",
    "GetStaticByteField",
    "GetStaticCharField",
    "GetStaticShortField",
    "GetStaticIntField",
    "GetStaticLongField",
    "GetStaticFloatField",
    "GetStaticDoubleField",
    "SetStaticObjectField",
    "SetStaticBooleanField",
    "SetStaticByteField",
    "SetStaticCharField",
    "SetStaticShortField",
    "SetStaticIntField",
    "SetStaticLongField",
    "SetStaticFloatField",
    "SetStaticDoubleField",
    "NewString",
    "GetStringLength",
    "GetStringChars",
    "ReleaseStringChars",
    "NewStringUTF",
    "GetStringUTFLength",
    "GetStringUTFChars",
    "ReleaseStringUTFChars",
    "GetArrayLength",
    "NewObjectArray",
    "GetObjectArrayElement",
    "SetObjectArrayElement",
    "NewBooleanArray",
    "NewByteArray",
    "NewCharArray",
    "NewShortArray",
    "NewIntArray",
    "NewLongArray",
    "NewFloatArray",
    "NewDoubleArray",
    "GetBooleanArrayElements",
    "GetByteArrayElements",
    "GetCharArrayElements",
    "GetShortArrayElements",
    "GetIntArrayElements",
    "GetLongArrayElements",
    "GetFloatArrayElements",
    "GetDoubleArrayElements",
    "ReleaseBooleanArrayElements",
    "ReleaseByteArrayElements",
    "ReleaseCharArrayElements",
    "ReleaseShortArrayElements",
    "ReleaseIntArrayElements",
    "ReleaseLongArrayElements",
    "ReleaseFloatArrayElements",
    "ReleaseDoubleArrayElements",
    "GetBooleanArrayRegion",
    "GetByteArrayRegion",
    "GetCharArrayRegion",
    "GetShortArrayRegion",
    "GetIntArrayRegion",
    "GetLongArrayRegion",
    "GetFloatArrayRegion",
    "GetDoubleArrayRegion",
    "SetBooleanArrayRegion",
    "SetByteArrayRegion",
    "SetCharArrayRegion",
    "SetShortArrayRegion",
    "SetIntArrayRegion",
    "SetLongArrayRegion",
    "SetFloatArrayRegion",
    "SetDoubleArrayRegion",
    "RegisterNatives",
    "UnregisterNatives",
    "MonitorEnter",
    "MonitorExit",
    "GetJavaVM",
    "GetStringRegion",
    "GetStringUTFRegion",
    "GetPrimitiveArrayCritical",
    "ReleasePrimitiveArrayCritical",
    "GetStringCritical",
    "ReleaseStringCritical",
    "NewWeakGlobalRef",
    "DeleteWeakGlobalRef",
    "ExceptionCheck",
    "NewDirectByteBuffer",
    "GetDirectBufferAddress",
    "GetDirectBufferCapacity",
    "GetObjectRefType",
];

var Color = {
    RESET: "\x1b[39;49;00m",
    Black: "0;01",
    Blue: "4;01",
    Cyan: "6;01",
    Gray: "7;11",
    Green: "2;01",
    Purple: "5;01",
    Red: "1;01",
    Yellow: "3;01",
    Light: {
        Black: "0;11",
        Blue: "4;11",
        Cyan: "6;11",
        Gray: "7;01",
        Green: "2;11",
        Purple: "5;11",
        Red: "1;11",
        Yellow: "3;11",
    },
};

const StyleLogColorset = {
    red: [255, 0, 0],
    green: [0, 255, 0],
    blue: [0, 0, 255],
    yellow: [255, 255, 0],
    magenta: [255, 0, 255],
    cyan: [0, 255, 255],
    white: [255, 255, 255],
    black: [0, 0, 0],
    orange: [255, 165, 0],
    purple: [128, 0, 128],
    pink: [255, 192, 203],
    gold: [255, 215, 0],
    teal: [0, 128, 128],
    lime: [0, 255, 0],
    maroon: [128, 0, 0],
    navy: [0, 0, 128],
    olive: [128, 128, 0],
    silver: [192, 192, 192],
    gray: [128, 128, 128],
    brown: [165, 42, 42],
};

//----------------------end of globals.js-------------------------------------

//----------------------begin of beautifiers.js-------------------------------------

var colorLog = function (input, kwargs) {
    kwargs = kwargs || {};
    var logLevel = kwargs["l"] || "log",
        colorPrefix = "\x1b[3",
        colorSuffix = "m";
    if (typeof input === "object")
        input = JSON.stringify(input, null, kwargs["i"] ? 2 : null);
    if (kwargs["c"])
        input = colorPrefix + kwargs["c"] + colorSuffix + input + Color.RESET;
    console[logLevel](input);
};

function log(str) {
    console.log(str);
}

function styleLog(fullString, highlightedSubstrings, textColor, bgColor) {
    var textColorCode = "\x1b[38;2;" + textColor.join(";") + "m"; // Text color
    var bgColorCode = "\x1b[48;2;" + bgColor.join(";") + "m"; // Background color
    var resetCode = "\x1b[0m";

    var styledMessage = fullString;

    // Apply styling to each substring in the array
    highlightedSubstrings.forEach(function (highlightedSubstring) {
        var highlightedSubstringWithColors =
            textColorCode + bgColorCode + highlightedSubstring + resetCode;
        styledMessage = styledMessage
            .split(highlightedSubstring)
            .join(highlightedSubstringWithColors);
    });

    console.log(styledMessage);
}

//----------------------end of beautifiers.js-------------------------------------

//----------------------begin of utils.js-------------------------------------

function countElements(arr) {
    var counts = {};
    // Iterate over the array elements
    for (var i = 0; i < arr.length; i++) {
        var element = arr[i];

        // Check if the element is already a key in the counts object
        if (counts[element]) {
            // Increment the count for the element
            counts[element]++;
        } else {
            // Initialize the count for the element
            counts[element] = 1;
        }
    }
    return counts;
}

function uniqBy(array, key) {
    var seen = {};
    return array.filter(function (item) {
        var k = key(item);
        return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
}

var Utf8 = {
    encode: function (string) {
        string = string.replace(/\r\n/g, "\n");
        var utftext = "";
        for (var n = 0; n < string.length; n++) {
            var c = string.charCodeAt(n);
            if (c < 128) {
                utftext += String.fromCharCode(c);
            } else if (c > 127 && c < 2048) {
                utftext += String.fromCharCode((c >> 6) | 192);
                utftext += String.fromCharCode((c & 63) | 128);
            } else {
                utftext += String.fromCharCode((c >> 12) | 224);
                utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                utftext += String.fromCharCode((c & 63) | 128);
            }
        }
        return utftext;
    },

    decode: function (utftext) {
        var string = "";
        var i = 0;
        var c = (c1 = c2 = 0);
        while (i < utftext.length) {
            c = utftext.charCodeAt(i);
            if (c < 128) {
                string += String.fromCharCode(c);
                i++;
            } else if (c > 191 && c < 224) {
                c2 = utftext.charCodeAt(i + 1);
                string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
                i += 2;
            } else {
                c2 = utftext.charCodeAt(i + 1);
                c3 = utftext.charCodeAt(i + 2);
                string += String.fromCharCode(
                    ((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63)
                );
                i += 3;
            }
        }
        return string;
    },
};

var printBacktrace = function () {
    Java.perform(function () {
        var android_util_Log = Java.use("android.util.Log"),
            java_lang_Exception = Java.use("java.lang.Exception");
        var exc = android_util_Log.getStackTraceString(
            java_lang_Exception.$new()
        );
        colorLog(exc, { c: Color.Green });
    });
};

var processArgs = function (command, envp, dir) {
    var output = {};
    if (command) {
        console.log("Command: " + command);
        //   output.command = command;
    }
    if (envp) {
        console.log("Environment: " + envp);
        //   output.envp = envp;
    }
    if (dir) {
        console.log("Working Directory: " + dir);
        //   output.dir = dir;
    }
    // return output;
};

var _byteArraytoHexString = function (byteArray) {
    if (!byteArray) {
        return "null";
    }
    if (byteArray.map) {
        return byteArray
            .map(function (byte) {
                return ("0" + (byte & 0xff).toString(16)).slice(-2);
            })
            .join("");
    } else {
        return byteArray + "";
    }
};

var updateInput = function (input) {
    if (input.length && input.length > 0) {
        var normalized = byteArraytoHexString(input);
    } else if (input.array) {
        var normalized = byteArraytoHexString(input.array());
    } else {
        var normalized = input.toString();
    }
    return normalized;
};

var byteArraytoHexString = function (byteArray) {
    if (byteArray && byteArray.map) {
        return byteArray
            .map(function (byte) {
                return ("0" + (byte & 0xff).toString(16)).slice(-2);
            })
            .join("");
    } else {
        return JSON.stringify(byteArray);
    }
};

var hexToAscii = function (input) {
    var hex = input.toString();
    var str = "";
    for (var i = 0; i < hex.length; i += 2)
        str += String.fromCharCode(parseInt(hex.substr(i, 2), 16));
    return str;
};

var displayString = function (input) {
    var str = input.replace("[", "");
    var str1 = str.replace("]", "");
    var res = str1.split(",");
    var ret = "";
    for (var i = 0; i < res.length; i++) {
        if (res[i] > 31 && res[i] < 127) ret += String.fromCharCode(res[i]);
        else ret += " ";
    }

    colorLog("[+] PARSING TO STRING: " + ret, { c: Color.Green });
    colorLog("", { c: Color.RESET });
};

var normalize = function (input) {
    if (input.length && input.length > 0) {
        var normalized = byteArraytoHexString(input);
    } else if (input.array) {
        var normalized = byteArraytoHexString(input.array());
    } else {
        var normalized = input.toString();
    }
    return normalized;
};

var normalizeInput = function (input) {
    if (input.array) {
        var normalized = byteArraytoHexString(input.array());
    } else if (input.length && input.length > 0) {
        var normalized = byteArraytoHexString(input);
    } else {
        var normalized = JSON.stringify(input);
    }
    return normalized;
};

var getMode = function (Cipher, mode) {
    if (mode === 2) {
        mode = "DECRYPT";
    } else if (mode === 1) {
        mode = "ENCRYPT";
    }
    return mode;
};

var getRandomValue = function (arg) {
    if (!arg) {
        return "null";
    }
    var type = arg.toString().split("@")[0].split(".");
    type = type[type.length - 1];
    if (type === "SecureRandom") {
        if (arg.getSeed) {
            return byteArraytoHexString(arg.getSeed(10));
        }
    }
};

var normalizeKey = function (cert_or_key) {
    var type = cert_or_key.toString().split("@")[0].split(".");
    type = type[type.length - 1];
    if (type === "SecretKeySpec") {
        return byteArraytoHexString(cert_or_key.getEncoded());
    } else {
        return (
            "non-SecretKeySpec: " +
            cert_or_key.toString() +
            ", encoded: " +
            byteArraytoHexString(cert_or_key.getEncoded()) +
            ", object: " +
            JSON.stringify(cert_or_key)
        );
    }
};

var stringToByteArray = function (input) {
    const msgString = Java.use("java.lang.String").$new(input);
    const result = msgString.getBytes();
    return result;
};

var byteArrayToString = function (input) {
    var buffer = Java.array("byte", input);
    var result = "";
    for (var i = 0; i < buffer.length; ++i) {
        if (buffer[i] > 31 && buffer[i] < 127)
            result += String.fromCharCode(buffer[i]);
        else result += " ";
    }
    return result;
};

var byteArrayToStringE = function (input) {
    var buffer = Java.array("byte", input);
    var result = "";
    var unprintable = false;
    for (var i = 0; i < buffer.length; ++i) {
        if (buffer[i] > 31 && buffer[i] < 127)
            result += String.fromCharCode(buffer[i]);
        else {
            unprintable = true;
            result = "Input cant be transformed to ascii string";
            break;
        }
    }
    return result;
};
function readStreamToHex(stream) {
    var data = [];
    var byteRead = stream.read();
    while (byteRead != -1) {
        data.push(("0" + (byteRead & 0xff).toString(16)).slice(-2));
        /* <---------------- binary to hex ---------------> */
        byteRead = stream.read();
    }
    stream.close();
    return data.join("");
}

//----------------------end of utils.js-------------------------------------

//----------------------begin of android_core.js-------------------------------------

var containRegExps = new Array();
var notContainRegExps = new Array(RegExp(/\.jpg/), RegExp(/\.png/));
function check(str) {
    str = str.toString();
    if (!(str && str.match)) {
        return false;
    }
    for (var i = 0; i < containRegExps.length; i++) {
        if (!str.match(containRegExps[i])) {
            return false;
        }
    }
    for (var i = 0; i < notContainRegExps.length; i++) {
        if (str.match(notContainRegExps[i])) {
            return false;
        }
    }
    return true;
}

function classExists(className) {
    var exists = false;
    try {
        var clz = Java.use(className);
        exists = true;
    } catch (err) {
        //console.log(err);
    }
    return exists;
}

function describeJavaClass(className) {
    var jClass = Java.use(className);
    console.log(
        JSON.stringify(
            {
                _name: className,
                _methods: Object.getOwnPropertyNames(jClass.__proto__).filter(
                    function (m) {
                        return (
                            !m.startsWith("$") || // filter out Frida related special properties
                            m == "class" ||
                            m == "constructor"
                        ); // optional
                    }
                ),
                _fields: jClass.class.getFields().map(function (f) {
                    return f.toString();
                }),
            },
            null,
            2
        )
    );
}

//------------------------https://github.com/CreditTone/hooker EOF----------------------------
function displayAppInfo() {
    var context = null;
    var ActivityThread = Java.use("android.app.ActivityThread");
    var app = ActivityThread.currentApplication();

    if (app != null) {
        context = app.getApplicationContext();
        var app_classname = app.getClass().toString().split(" ")[1];

        var filesDirectory = context.getFilesDir().getAbsolutePath().toString();
        var cacheDirectory = context.getCacheDir().getAbsolutePath().toString();
        var externalCacheDirectory = context
            .getExternalCacheDir()
            .getAbsolutePath()
            .toString();
        var codeCacheDirectory =
            "getCodeCacheDir" in context
                ? context.getCodeCacheDir().getAbsolutePath().toString()
                : "N/A";
        var obbDir = context.getObbDir().getAbsolutePath().toString();
        var packageCodePath = context.getPackageCodePath().toString();
        var applicationName = app_classname;

        colorLog(
            "\n-------------------Application Info--------------------\n",
            { c: Color.Green }
        );
        colorLog("- Frida version: " + Frida.version, { c: Color.Gray });
        colorLog("- Script runtime: " + Script.runtime, { c: Color.Gray });
        colorLog("- Application Name: " + applicationName, { c: Color.Gray });
        colorLog("- Files Directory: " + filesDirectory, { c: Color.Gray });
        colorLog("- Cache Directory: " + cacheDirectory, { c: Color.Gray });
        colorLog("- External Cache Directory: " + externalCacheDirectory, {
            c: Color.Gray,
        });
        colorLog("- Code Cache Directory: " + codeCacheDirectory, {
            c: Color.Gray,
        });
        colorLog("- Obb Directory: " + obbDir, { c: Color.Gray });
        colorLog("- Package Code Path: " + packageCodePath, { c: Color.Gray });
        colorLog(
            "\n-------------------EOF Application Info-----------------\n",
            { c: Color.Green }
        );

        var info = {};
        info.applicationName = applicationName;
        info.filesDirectory = filesDirectory;
        info.cacheDirectory = cacheDirectory;
        info.externalCacheDirectory = externalCacheDirectory;
        info.codeCacheDirectory = codeCacheDirectory;
        info.obbDir = obbDir;
        info.packageCodePath = packageCodePath;

        send(JSON.stringify(info));
    } else {
        console.log("No context yet!");
    }
}

function dumpIntent(intent, redump = true) {
    if (intent.getStringExtra("marked_as_dumped") && redump === false) return;

    var bundle_clz = intent.getExtras();
    var data = intent.getData();
    var action = intent.getAction();
    colorLog(intent, { c: Color.Cyan });

    var exported = isActivityExported(intent);
    var str = "(The intent is targeting";
    if (exported) colorLog(str + " an EXPORTED component)", { c: Color.Red });
    else colorLog(str + " a NON EXPORTED component)", { c: Color.Green });

    var type = null;
    if (data != null) {
        colorLog("\t\\_data: ", { c: Color.Cyan });
        colorLog("\t\t" + data, { c: Color.Yellow });
    }
    if (action != null) {
        colorLog("\t\\_action: ", { c: Color.Cyan });
        colorLog("\t\t" + action, { c: Color.Yellow });
    }

    if (bundle_clz != null) {
        colorLog("\t\\_Extras: ", { c: Color.Cyan });
        var keySet = bundle_clz.keySet();
        var iter = keySet.iterator();
        while (iter.hasNext()) {
            var currentKey = iter.next();
            var currentValue = bundle_clz.get(currentKey);
            if (currentValue != null) type = currentValue.getClass().toString();
            else type = "undefined";

            var t = type.substring(type.lastIndexOf(".") + 1, type.length);
            if (currentKey != "marked_as_dumped") {
                if (
                    filterKeyWords.some((word) =>
                        currentKey.toString().toLowerCase().includes(word)
                    )
                )
                    colorLog(
                        "\t\t(" + t + ") " + currentKey + " = " + currentValue,
                        { c: Color.Red }
                    );
                else
                    console.log(
                        "\t\t(" + t + ") " + currentKey + " = " + currentValue
                    );
            }
            //console.log( '\t\t('+t+ ') '+ currentKey + ' = ' + currentValue);
        }
    }
    intent.putExtra("marked_as_dumped", "marked");
}

function enumerateModules() {
    var modules = Process.enumerateModules();
    colorLog("[+] Enumerating loaded modules:", { c: Color.Blue });
    for (var i = 0; i < modules.length; i++)
        console.log(modules[i].path + modules[i].name);
}

function getApplicationContext() {
    return Java.use("android.app.ActivityThread")
        .currentApplication()
        .getApplicationContext();
}

/*
Calculate the given funcName address from the JNIEnv pointer
*/
function getJNIFunctionAdress(jnienv_addr, func_name) {
    var offset = jni_struct_array.indexOf(func_name) * Process.pointerSize;
    // console.log("offset : 0x" + offset.toString(16))
    return Memory.readPointer(jnienv_addr.add(offset));
}

// Hook all function to have an overview of the function called
function hook_all(jnienv_addr) {
    jni_struct_array.forEach(function (func_name) {
        // Calculating the address of the function
        if (!func_name.includes("reserved")) {
            var func_addr = getJNIFunctionAdress(jnienv_addr, func_name);
            Interceptor.attach(func_addr, {
                onEnter: function (args) {
                    console.log("[+] Entered : " + func_name);
                },
            });
        }
    });
}

function inspectObject(obj) {
    const Class_X = Java.use("java.lang.Class");
    const obj_class = Java.cast(obj.getClass(), Class_X);
    const fields = obj_class.getDeclaredFields();
    const methods = obj_class.getMethods();
    console.log("Inspecting " + obj.getClass().toString());
    console.log(
        "[+]------------------------------Fields------------------------------:"
    );
    for (var i in fields) console.log("\t\t" + fields[i].toString());
    console.log(
        "[+]------------------------------Methods-----------------------------:"
    );
    for (var i in methods) console.log("\t\t" + methods[i].toString());
}

function isActivityExported(intent) {
    try {
        const context = getApplicationContext();
        const packageManager = context.getPackageManager();
        let resolveInfo = packageManager.resolveActivity(intent, 0);
        return resolveInfo.activityInfo.value.exported.value;
    } catch (error) {
        //console.log(error)
    }
}

function methodInBeat(invokeId, timestamp, methodName, executor) {
    var startTime = timestamp;
    var androidLogClz = Java.use("android.util.Log");
    var exceptionClz = Java.use("java.lang.Exception");
    var threadClz = Java.use("java.lang.Thread");
    var currentThread = threadClz.currentThread();
    var stackInfo = androidLogClz.getStackTraceString(exceptionClz.$new());
    var str =
        "------------startFlag:" +
        invokeId +
        ",objectHash:" +
        executor +
        ",thread(id:" +
        currentThread.getId() +
        ",name:" +
        currentThread.getName() +
        "),timestamp:" +
        startTime +
        "---------------\n";
    str += methodName + "\n";
    str += stackInfo.substring(20);
    str +=
        "------------endFlag:" +
        invokeId +
        ",usedtime:" +
        (new Date().getTime() - startTime) +
        "---------------\n";
    console.log(str);
}

function newMethodBeat(text, executor) {
    var threadClz = Java.use("java.lang.Thread");
    // var androidLogClz = Java.use("android.util.Log");
    // var exceptionClz = Java.use("java.lang.Exception");
    var currentThread = threadClz.currentThread();
    var beat = new Object();
    beat.invokeId = Math.random().toString(36).slice(-8);
    beat.executor = executor;
    beat.threadId = currentThread.getId();
    beat.threadName = currentThread.getName();
    beat.text = text;
    beat.startTime = new Date().getTime();
    //beat.stackInfo = androidLogClz.getStackTraceString(exceptionClz.$new()).substring(20);
    return beat;
}

function notifyNewSharedPreference(key, value) {
    var k = key;
    var v = value;
    Java.use("android.app.SharedPreferencesImpl$EditorImpl").putString.overload(
        "java.lang.String",
        "java.lang.String"
    ).implementation = function (k, v) {
        console.log("[SharedPreferencesImpl]", k, "=", v);
        return this.putString(k, v);
    };
}

function printBeat(beat) {
    colorLog(beat.text, { c: Color.Gray });
}

function traceClass(targetClass, color = "Purple") {
    console.log(
        "\n\x1b[43m\x1b[31m [?] Hooking methods of " + targetClass + "\x1b[0m\n"
    );
    var hook = Java.use(targetClass);
    var methods = hook.class.getDeclaredMethods();
    var parsedMethods = ["$init"];
    methods.forEach(function (method) {
        try {
            parsedMethods.push(
                method
                    .toString()
                    .replace(targetClass + ".", "TOKEN")
                    .match(/\sTOKEN(.*)\(/)[1]
            );
        } catch (err) {}
    });
    var targets = uniqBy(parsedMethods, JSON.stringify);
    var result = "";

    for (var key in parsedMethods) {
        result += parsedMethods[key] + " (" + key + ") ";
    }

    console.log(
        "Hooks " +
            parsedMethods.length +
            ", (method name, number of overloads) => " +
            result
    );
    targets.forEach(function (targetMethod) {
        try {
            traceMethod(targetClass + "." + targetMethod, color);
        } catch (err) {}
    });
    hook.$dispose();
    console.log();
}

function traceMethod(targetClassMethod, color) {
    var delim = targetClassMethod.lastIndexOf(".");
    if (delim === -1) return;
    var targetClass = targetClassMethod.slice(0, delim);
    var targetMethod = targetClassMethod.slice(
        delim + 1,
        targetClassMethod.length
    );
    var hook = Java.use(targetClass);
    var overloadCount12 = hook[targetMethod].overloads.length;

    for (var i = 0; i < overloadCount12; i++) {
        hook[targetMethod].overloads[i].implementation = function () {
            colorLog("\n[ ▶︎▶︎▶︎] Entering: " + targetClassMethod, {
                c: Color[color],
            });
            for (var j = 0; j < arguments.length; j++) {
                console.log("|\t\\_arg[" + j + "]: " + arguments[j]);
            }
            var retval = this[targetMethod].apply(this, arguments);
            colorLog("[ ◀︎◀︎◀︎ ] Exiting " + targetClassMethod, {
                c: Color[color],
            });

            console.log("\t\\_Returns: " + retval + "\n");
            return retval;
        };
    }
}

function tryGetClass(className) {
    var clz = undefined;
    try {
        clz = Java.use(className);
    } catch (e) {}
    return clz;
}

function waitForModule(moduleName) {
    return new Promise((resolve) => {
        const interval = setInterval(() => {
            const module = Process.findModuleByName(moduleName);
            if (module != null) {
                clearInterval(interval);
                resolve(module);
            }
        }, 300);
    });
}

//----------------------end of android_core.js-------------------------------------

Java.perform(function () {
    try {
        setTimeout(displayAppInfo, 500);

        console.log("\n--------DCL hook module by @ch0pin--------------");

        {
            var pathClassLoader = Java.use("dalvik.system.PathClassLoader");

            var dexclassLoader = Java.use("dalvik.system.DexClassLoader");
            var basedexclassLoader = Java.use(
                "dalvik.system.BaseDexClassLoader"
            );
            var delegateLastClassLoader = Java.use(
                "dalvik.system.DelegateLastClassLoader"
            );
            var clazz = Java.use("java.lang.Class");

            dexclassLoader.$init.implementation = function (
                dexPath,
                optimizedDirectory,
                librarySearchPath,
                parent
            ) {
                colorLog("DexClassLoader called:", { c: Color.Green });

                console.log("dexPath=" + dexPath);
                var filename = dexPath.replace(/^.*[\\/]/, "");
                copyfile(dexPath, "/sdcard/dexdump/" + filename);
                console.log("optimizedDirectory=" + optimizedDirectory);
                console.log("librarySearchPath=" + librarySearchPath);
                console.log("parent=" + parent);

                return this.$init(
                    dexPath,
                    optimizedDirectory,
                    librarySearchPath,
                    parent
                );
            };

            basedexclassLoader.$init.overloads[0].implementation = function (
                dexPath,
                optimizedDirectory,
                librarySearchPath,
                parent
            ) {
                colorLog("BaseDexClassLoader called:", { c: Color.Green });
                console.log("dexPath=" + dexPath);
                var filename = dexPath.replace(/^.*[\\/]/, "");
                copyfile(dexPath, "/sdcard/dexdump/" + filename);
                console.log("optimizedDirectory=" + optimizedDirectory);
                console.log("librarySearchPath=" + librarySearchPath);
                console.log("parent=" + parent);
                return this.$init(
                    dexPath,
                    optimizedDirectory,
                    librarySearchPath,
                    parent
                );
            };

            delegateLastClassLoader.$init.overloads[0].implementation =
                function (dexPath, parent) {
                    colorLog("DelegateLastClassLoader called:", {
                        c: Color.Green,
                    });
                    console.log("dexPath=" + dexPath);
                    var filename = dexPath.replace(/^.*[\\/]/, "");
                    copyfile(dexPath, "/sdcard/dexdump/" + filename);
                    console.log("parent=" + parent);
                    return this.$init(dexPath, parent);
                };

            delegateLastClassLoader.$init.overloads[1].implementation =
                function (dexPath, librarySearchPath, parent) {
                    colorLog("DelegateLastClassLoader called:", {
                        c: Color.Green,
                    });
                    console.log("dexPath=" + dexPath);
                    var filename = dexPath.replace(/^.*[\\/]/, "");
                    copyfile(dexPath, "/sdcard/dexdump/" + filename);
                    console.log("librarySearchPath=" + librarySearchPath);
                    console.log("parent=" + parent);
                    return this.$init(dexPath, librarySearchPath, parent);
                };

            //A ClassLoader implementation that loads classes from a buffer containing a DEX file. This can be used to execute code that has not been written to the local file system.

            traceClass("dalvik.system.InMemoryDexClassLoader");

            //Creates a PathClassLoader that operates on a given list of files and directories.

            pathClassLoader.$init.overloads[0].implementation = function (
                dexPath,
                parent
            ) {
                colorLog("PathClassLoader called:", { c: Color.Green });
                console.log("dexPath=" + dexPath);
                var filename = dexPath.replace(/^.*[\\/]/, "");
                copyfile(dexPath, "/sdcard/dexdump/" + filename);
                console.log("parent=" + parent);
                return this.$init(dexPath, parent);
            };

            pathClassLoader.$init.overloads[1].implementation = function (
                dexPath,
                librarySearchPath,
                parent
            ) {
                colorLog("PathClassLoader called:", { c: Color.Green });
                console.log("dexPath=" + dexPath);
                var filename = dexPath.replace(/^.*[\\/]/, "");
                copyfile(dexPath, "/sdcard/dexdump/" + filename);
                console.log("librarySearchPath=" + librarySearchPath);
                console.log("parent=" + parent);
                return this.$init(dexPath, librarySearchPath, parent);
            };

            //  clazz.getMethod.overloads[0].implementation = function(name,params){
            //      colorLog('Loading class method:', {c: Color.Red});
            //      return this.getMethod(name,params);
            //  }
            //  clazz.getMethod.overloads[1].implementation = function(name,params,bl){
            //     colorLog('Loading class method:', {c: Color.Red});
            //     return this.getMethod(name,params,bl);
            // }
            // .overload('[Ljava.nio.ByteBuffer;', 'java.lang.String', 'java.lang.ClassLoader')
            // .overload('java.lang.String', 'java.lang.String', 'java.lang.ClassLoader', '[Ljava.lang.ClassLoader;')
            // .overload('java.lang.String', 'java.io.File', 'java.lang.String', 'java.lang.ClassLoader')
            // .overload('java.lang.String', 'java.io.File', 'java.lang.String', 'java.lang.ClassLoader', 'boolean')
            // .overload('java.lang.String', 'java.lang.String', 'java.lang.ClassLoader', '[Ljava.lang.ClassLoader;', 'boolean')

            // dexclassLoader.$init.overload("[Ljava.nio.ByteBuffer;", "java.lang.ClassLoader").implementation = function(byteBuffer,   parent){
            //     console.log(++index +"、--------------------------");
            //     console.log("byteBuffer=" + byteBuffer );
            //     console.log("parent=" + parent);
            //     this.$init(byteBuffer, parent);
            // }

            //  var dexclassLoader = Java.use("dalvik.system.BaseDexClassLoader");
            // var ClassUse = Java.use("java.lang.Class");
            //  dexclassLoader.loadClass.overload("java.lang.String").implementation = function(className){
            //      colorLog("[i] DEX Class loader for ClassName=" + className+'\n',{c: Color.Green});
            //      var result = this.loadClass(className);
            //      var resultCast = Java.cast(result, ClassUse);
            //      console.log("[+] Methods=" + resultCast.getMethods()+'\n');
            //      return result;
            //  }

            // }
        }

        {
            var classLoaderDef = Java.use("java.lang.ClassLoader");
            var loadClass = classLoaderDef.loadClass.overload(
                "java.lang.String",
                "boolean"
            );
            var internalClasses = [
                "android.",
                "org.",
                "com.google.",
                "java.",
                "androidx.",
            ];

            /* taken from https://github.com/eybisi/nwaystounpackmobilemalware/blob/master/dereflect.js */
            loadClass.implementation = function (class_name, resolve) {
                var isGood = true;
                for (var i = 0; i < internalClasses.length; i++) {
                    if (class_name.startsWith(internalClasses[i])) {
                        isGood = false;
                    }
                }
                if (isGood) {
                    console.log("loadClass: " + class_name);
                }
                return loadClass.call(this, class_name, resolve);
            };
        }

        /*
        console.log(
            "\n--------Native load hook module by @ch0pin--------------\n"
        );

        {
            var systemA = Java.use("java.lang.System");
            const System = Java.use("java.lang.System");
            const Runtime_1 = Java.use("java.lang.Runtime");
            const SystemLoad_2 =
                System.loadLibrary.overload("java.lang.String");
            const VMStack = Java.use("dalvik.system.VMStack");

            SystemLoad_2.implementation = function (library) {
                colorLog(
                    "[+] The application is loading the following library (second):" +
                        library,
                    { c: Color.Cyan }
                );
                return this.load(library);
                try {
                    const loaded = Runtime_1.getRuntime().loadLibrary0(
                        VMStack.getCallingClassLoader(),
                        library
                    );
                    return loaded;
                } catch (ex) {
                    console.log(ex);
                }
            };
            

            systemA.load.implementation = function (filename) {
                colorLog(
                    "[+] The application is loading the following library: " +
                        filename,
                    { c: Color.Cyan }
                );
                return this.load(filename);
            };
            //systemA.loadLibrary.implementation = function(libname){
            //    colorLog('[+] The application is loading the following library:'+libname,{c: Color.Cyan});
            //    return this.loadLibrary(libname);
            //}

            systemA.mapLibraryName.implementation = function (libname) {
                var ret = this.mapLibraryName(libname);
                colorLog("[+] The application maps " + libname + " to " + ret, {
                    c: Color.Cyan,
                });
                return ret;
            };

            Interceptor.attach(
                Module.findExportByName(null, "android_dlopen_ext"),
                {
                    onEnter: function (args) {
                        // first arg is the path to the library loaded
                        var library_path = Memory.readCString(args[0]);

                        if (library_path.startsWith("/data/user"))
                            colorLog(
                                "[...] Loading library : " + library_path,
                                { c: Color.Red }
                            );
                        else
                            console.log(
                                "[...] Loading library : " + library_path
                            );
                        //library_loaded = 1
                    },
                    onLeave: function (args) {
                        // if it's the library we want to hook, hooking it
                        // if(library_loaded ==  1){
                        //     console.log("[+] Loaded")
                        //     library_loaded = 0
                        // }
                    },
                }
            );
            */

        /*
    static void	load(String filename)
    Loads the native library specified by the filename argument.

    static void	loadLibrary(String libname)
    Loads the native library specified by the libname argument.

    static String	mapLibraryName(String libname)
    Maps a library name into a platform-specific string representing a native library.

    */
        /*
        }
    */

        function copyfile(source, destination) {
            const File = Java.use("java.io.File");
            const FileInputStream = Java.use("java.io.FileInputStream");
            const FileOutputStream = Java.use("java.io.FileOutputStream");
            const BufferedInputStream = Java.use("java.io.BufferedInputStream");
            const BufferedOutputStream = Java.use(
                "java.io.BufferedOutputStream"
            );
            var sourceFile = File.$new
                .overload("java.lang.String")
                .call(File, source);
            if (sourceFile.exists() && sourceFile.canRead()) {
                var destinationFile = File.$new
                    .overload("java.lang.String")
                    .call(File, destination);
                destinationFile.createNewFile();
                var fileInputStream = FileInputStream.$new
                    .overload("java.io.File")
                    .call(FileInputStream, sourceFile);
                var fileOutputStream = FileOutputStream.$new
                    .overload("java.io.File")
                    .call(FileOutputStream, destinationFile);
                var bufferedInputStream = BufferedInputStream.$new
                    .overload("java.io.InputStream")
                    .call(BufferedInputStream, fileInputStream);
                var bufferedOutputStream = BufferedOutputStream.$new
                    .overload("java.io.OutputStream")
                    .call(BufferedOutputStream, fileOutputStream);
                var data = 0;
                while ((data = bufferedInputStream.read()) != -1) {
                    bufferedOutputStream.write(data);
                }
                console.log("finished copying to : " + destination);
                bufferedInputStream.close();
                fileInputStream.close();
                bufferedOutputStream.close();
                fileOutputStream.close();
            } else {
                console.log("Error : File cannot read.");
            }
        }
    } catch (error) {
        colorLog("------------Error Log start-------------", { c: Color.Red });
        console.log(error.stack);
        colorLog("------------Error Log EOF---------------", { c: Color.Red });
    }
});
