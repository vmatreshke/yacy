// serverMemory.java
// -------------------------------------------
// (C) 2005 by Michael Peter Christen; mc@yacy.net, Frankfurt a. M., Germany
// first published 22.09.2005 on http://yacy.net
//
// $LastChangedDate: 2005-09-21 16:21:45 +0200 (Wed, 21 Sep 2005) $
// $LastChangedRevision: 763 $
// $LastChangedBy: orbiter $
//
// LICENSE
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


package de.anomic.server;

import java.text.DecimalFormat;

public class serverMemory {

    public static boolean vm14 = System.getProperty("java.vm.version").startsWith("1.4");
    public static final long max = (vm14) ? computedMaxMemory() : Runtime.getRuntime().maxMemory() ; // patch for maxMemory bug in Java 1.4.2
    private static final Runtime runtime = Runtime.getRuntime();
    
    public static long free() {
        // memory that is free without increasing of total memory taken from os
        return runtime.freeMemory();
    }
   
    public static boolean available(long memory, boolean gciffail) {
        if (available() >= memory) return true;
        if (!gciffail) return false;
        System.gc();
        return (available() >= memory);
    }
    
    public static long available() {
        // memory that is available including increasing total memory up to maximum
        return max - runtime.totalMemory() + runtime.freeMemory();
    }
    
    public static long used() {
        // memory that is currently bound in objects
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    public static String bytesToString(long byteCount) {
        try {
            final StringBuffer byteString = new StringBuffer();

            final DecimalFormat df = new DecimalFormat( "0.00" );
            if (byteCount > 1073741824) {
                byteString.append(df.format((double)byteCount / (double)1073741824 ))
                          .append(" GB");
            } else if (byteCount > 1048576) {
                byteString.append(df.format((double)byteCount / (double)1048576))
                          .append(" MB");
            } else if (byteCount > 1024) {
                byteString.append(df.format((double)byteCount / (double)1024))
                          .append(" KB");
            } else {
                byteString.append(Long.toString(byteCount))
                .append(" Bytes");
            }

            return byteString.toString();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    private static int computedMaxMemory() {
        // there is a bug in java 1.4.2 for maxMemory()
        // see for bug description:
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4686462
        // to get the correct maxMemory, we force a OutOfMemoryError here to measure the 'real' maxMemory()
        int mb = 1024 * 1024;
        byte[][] x = new byte[2048][];
        for (int i = 0; i < x.length; i++) {
            try {
                x[i] = new byte[mb];
            } catch (OutOfMemoryError e) {
                x = null; // free memory
                //System.out.println("* computed maxMemory = " + i + " mb");
                return (int) Math.max(i * mb, Runtime.getRuntime().totalMemory());
            }
        }
        return 2048 * mb;
    }
    
    public static void main(String[] args) {
        // try this with a jvm 1.4.2 and with a jvm 1.5 and compare results
        int mb = 1024 * 1024;
        System.out.println("vm: " + System.getProperty("java.vm.version"));
        System.out.println("computed max = " + (computedMaxMemory() / mb) + " mb");
        int alloc = 10000;
        Runtime rt = Runtime.getRuntime();
        byte[][] x = new byte[100000][];
        for (int i = 0; i < 100000; i++) {
            x[i] = new byte[alloc];
            if (i % 100 == 0) System.out.println("used = " + (i * alloc / mb) +
                    ", total = " + (rt.totalMemory() / mb) +
                    ", free = " + (rt.freeMemory() / mb) +
                    ", max = " + (rt.maxMemory() / mb) +
                    ", avail = " + ((rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / mb));
        }

    }
    
}
