package org.betfair.security.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author cotarlas
 */
public abstract class OAuthExecContextTemplate {

  public final ModelAndView handleRequest(OAuthSupport oauthSupport, HttpServletRequest request, HttpServletResponse response) throws Exception {
    try {
      oauthSupport.initContext(request, response);
      oauthSupport.accessAuthorized();

      return handleRequestInternal(request, response);

    } catch (OAuthNotAuthorizedException e) {
      oauthSupport.obtainAccess();
    }  finally {
      oauthSupport.removeContext();
    }
    return null;
  }

  public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
