package org.n3r.eop.demo;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class EopServlet extends HttpServlet {
    private static final long serialVersionUID = 3803792818152637845L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        EopSysBean eopSysBean = new EopSysBean();
        try {
            BeanUtils.populate(eopSysBean, request.getParameterMap());
        } catch (Exception e) {
            // Empty catch. The two possible exceptions are
            // java.lang.IllegalAccessException and
            // java.lang.reflect.InvocationTargetException.
            // In both cases, just skip the bean operation.
        }

        String signSrc = compositSignSrc(request.getParameterMap());
        System.out.println("org src:" + signSrc);
        Md5Sign md5Sign = new Md5Sign();
        String sign = md5Sign.sign(signSrc, "signKey");

        System.out.println(System.currentTimeMillis()/1000);
        System.out.println("src sign:" + eopSysBean.getSign());
        System.out.println("chk sign:" + sign);

        response.setContentType("application/json");
        response.getWriter().print("{rsp:\"OK\", sign:\"" + sign + "\"}");
    }

    private String compositSignSrc(Map<String, String[]> map) {
        TreeMap<String, String[]> treeMap = new TreeMap<String, String[]>(map);
        treeMap.remove("sign");

        StringBuilder sb = new StringBuilder();
        for (Entry<String, String[]> entry : treeMap.entrySet())
            sb.append('$').append(entry.getKey()).append('$').append(entry.getValue()[0]);

        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new EopServlet()), "/eop");

        server.start();
        server.join();
    }
}
