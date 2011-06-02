package org.springframework.security.oauth.examples.tonr;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.consumer.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.consumer.token.OAuth2ClientTokenServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * Discard OAuth2 tokens after logout.
 *
 * @author cotarlas
 */
public class LogoutHandler implements LogoutSuccessHandler {

  private OAuth2ClientTokenServices tokenServices;

  private OAuth2ProtectedResourceDetails facebookResource;
  private OAuth2ProtectedResourceDetails sparklrResource;


  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    tokenServices.removeToken(authentication, facebookResource);
    tokenServices.removeToken(authentication, sparklrResource);

    response.sendRedirect(response.encodeRedirectURL("/tonr/index.jsp"));
  }


  public void setTokenServices(OAuth2ClientTokenServices tokenServices) {
    this.tokenServices = tokenServices;
  }

  public void setFacebookResource(OAuth2ProtectedResourceDetails facebookResource) {
    this.facebookResource = facebookResource;
  }

  public void setSparklrResource(OAuth2ProtectedResourceDetails sparklrResource) {
    this.sparklrResource = sparklrResource;
  }

}
