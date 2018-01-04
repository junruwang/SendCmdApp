/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tongy
 *         串口操作API
 */
public class SerialPort {

    private static final String TAG = "SerialPort";

    /*
         * Do not remove or rename the field mFd: it is used by native method close();
         */
    private File device;
    private int baudrate;
    private int flags;
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    //add by Jason Tsang 2017/2/15
    private static Map<String, SerialPort> serialPortMap = new HashMap<>();
    public static SerialPort getInstance(File device, int baudrate, int flags) {
        if(serialPortMap.containsKey(device.getAbsolutePath())) {
            SerialPort serialPort = serialPortMap.get(device.getAbsolutePath());
            if((serialPort.getBaudrate() == baudrate) && (serialPort.getFlags() == flags)) {
                return serialPort;
            }else {
                serialPort.close();
                try
                {
                    serialPort = new SerialPort(device, baudrate, flags);
                    serialPortMap.put(device.getAbsolutePath(), serialPort);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return serialPort;
            }
        }else {
            SerialPort serialPort = null;
            try {
                serialPort = new SerialPort(device, baudrate, flags);
                serialPortMap.put(device.getAbsolutePath(), serialPort);
            }catch (Exception e) {
                e.printStackTrace();
            }
            return serialPort;
        }
    }

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        this.device = device;
        this.baudrate = baudrate;
        this.flags = flags;

		/* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public File getDevice() {
        return device;
    }

    public int getBaudrate() {
        return baudrate;
    }

    public int getFlags() {
        return flags;
    }

    // JNI
    private native static FileDescriptor open(String path, int baudrate, int flags);

    public native void close();

    public void close(boolean remove) {
        this.close();
        if(remove) {
            serialPortMap.remove(this.getDevice().getAbsolutePath());
        }
    }

    static {
        System.loadLibrary("serial_port");
    }
}
