package org.springframework.security.oauth.examples.tonr.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.betfair.security.oauth2.OAuthExecContextTemplate;
import org.betfair.security.oauth2.OAuthSupport;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * @author cotarlas
 */
public class FacebookController extends AbstractController {

  private OAuthSupport oauthSupport;

  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    return new OAuthExecContextTemplate() {
      @Override
      public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String friendsJson  = oauthSupport.getResource("https://graph.facebook.com/me/friends");
        return new ModelAndView("facebook", "friends", friendsJson);

      }
    }.handleRequest(oauthSupport, request, response);

  }

  public void setOauthSupport(OAuthSupport oauthSupport) {
    this.oauthSupport = oauthSupport;
  }

}
