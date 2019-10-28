/*
 * Copyright 2015 Terracotta, Inc., a Software AG company.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terracotta.ipceventbus.proc;

import java.lang.reflect.Field;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

/**
 * @author Mathieu Carbou
 */
class Jna {

  static long getWindowsPid(Process process) {
    if (process.getClass().getName().equals("java.lang.Win32Process")
        || process.getClass().getName().equals("java.lang.ProcessImpl")) {
      try {
        Field f = process.getClass().getDeclaredField("handle");
        f.setAccessible(true);
        long handl = f.getLong(process);
        Kernel32 kernel = Kernel32.INSTANCE;
        WinNT.HANDLE handle = new WinNT.HANDLE();
        handle.setPointer(Pointer.createConstant(handl));
        return kernel.GetProcessId(handle);
      } catch (Throwable ignored) {
      }
    }
    return -1;
  }

}
