# Unpack Apps

The `frida_intercept.py` script uses the `agent.js` Frida hooks to retrieve dynamically loaded code in packed apps.

The `agent.js` was generated using the Medusa Framework.

## Instructions

Install requirements

`pip install -r requirements.txt`

Then, make sure to have a rooted device with Frida connected through ADB.

Create on the device the following directory structure `/sdcard/dexdump`.

## Example Usage

`python3 frida_intercept.py <apk_dir>`

where `apk_dir` is the path of the directory with the packed apps to analyze.
