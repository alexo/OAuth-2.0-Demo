package org.betfair.security.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author cotarlas
 */
public class OAuthContext {

  private static ThreadLocal<OAuthContext> CONTEXT = new ThreadLocal<OAuthContext>();

  public static void setContext(OAuthContext context) {
    CONTEXT.set(context);
  }

  public static OAuthContext getContext() {
    return CONTEXT.get();
  }

  public static void remove() {
    CONTEXT.remove();
  }

  private String name; //
  private HttpServletRequest request;
  private HttpServletResponse response;

  public OAuthContext(String name, HttpServletRequest request, HttpServletResponse response) {
    this.name = name;
    this.request = request;
    this.response = response;
  }

  public String getName() {
    return name;
  }

  public HttpServletRequest getRequest() {
    return request;
  }

  public HttpServletResponse getResponse() {
    return response;
  }

  public static boolean hasAccessToken() {
    return getAccessToken() != null;
  }

  public static void storeAccessToken(String token) {
    OAuthContext context = getContext();
    HttpSession session = context.getRequest().getSession();
    session.setAttribute("access_token_for_" + context.getName(), token);
  }

  public static String getAccessToken() {
    OAuthContext context = getContext();
    HttpSession session = context.getRequest().getSession();
    return (String) session.getAttribute("access_token_for_" + context.getName());
  }

}
