Java.perform(function () {
    var argument = "";
    var index = 0;

    const hookMainClass = Java.use("lab.galaxy.yahfa.HookMain");

    hookMainClass.findAndBackupAndHook.implementation = function (
        class_name,
        method,
        str2,
        hook,
        backup
    ) {
        argument = hook + "->" + class_name.getName() + "-" + method;

        console.log(
            "=============================================================="
        );

        send(argument);
        return this.findAndBackupAndHook(
            class_name,
            method,
            str2,
            hook,
            backup
        );
    };

    const Helper = Java.use("com.lody.virtual.custom.Helper");

    Helper.writeFileOnInternalStorage.implementation = function (
        context,
        fileName,
        folderName,
        body
    ) {
        let result = this.writeFileOnInternalStorage(
            context,
            fileName,
            folderName,
            body
        );
        this.writeFileOnInternalStorage(
            context,
            "class-" + index + ".dex",
            "/dump",
            body
        );

        index = index + 1;

        return result;
    };

    var memoryclassLoader = Java.use("dalvik.system.InMemoryDexClassLoader");
    memoryclassLoader.$init.overload(
        "java.nio.ByteBuffer",
        "java.lang.ClassLoader"
    ).implementation = function (dexbuffer, loader) {
        var object = this.$init(dexbuffer, loader);

        var remaining = dexbuffer.remaining();
        const filename =
            "/data/user/0/com.example.trustedcontainervirtualapp/files/dump/class-" +
            index +
            ".dex";

        index = index + 1;

        const f = new File(filename, "wb");
        var buf = new Uint8Array(remaining);
        for (var i = 0; i < remaining; i++) {
            buf[i] = dexbuffer.get();
            //debug: console.log("buf["+i+"]="+buf[i]);
        }
        f.write(buf);
        f.close();

        // checking
        remaining = dexbuffer.remaining();
        if (remaining > 0) {
            console.log(
                "[-] Error: There are " + remaining + " remaining bytes!"
            );
        } else {
            console.log("[+] Dex dumped successfully in " + filename);
        }

        console.log(
            "=============================================================="
        );

        return object;
    };
});
