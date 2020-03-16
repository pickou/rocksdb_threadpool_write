package com.proj.utils;

public class ExceptionUtil {
    /**
     * @param e
     * @return string encoding of the exception <code>e</code>.
     */
    public static String getStackInfo(Throwable e) {
        if (e == null) return "";
        String error_stack = "" + e + "\n";
        StackTraceElement[] stack_traces = e.getStackTrace();
        for (int i = 0; i < stack_traces.length; i++) error_stack += stack_traces[i].toString() + "\n";
        return error_stack;
    }
}
