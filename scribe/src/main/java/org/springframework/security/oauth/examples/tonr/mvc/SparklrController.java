package org.springframework.security.oauth.examples.tonr.mvc;

import org.betfair.security.oauth2.OAuthExecContextTemplate;
import org.betfair.security.oauth2.OAuthSupport;
import org.springframework.security.oauth.examples.tonr.SparklrService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ryan Heaton
 */
public class SparklrController extends AbstractController {

  private OAuthSupport oauthSupport;
  private SparklrService sparklrService;

  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

    return new OAuthExecContextTemplate() {
      @Override
      public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
          throws Exception {
        return new ModelAndView("sparklr", "photoIds", getSparklrService().getSparklrPhotoIds());
      }
    }.handleRequest(oauthSupport, request, response);

  }

  public SparklrService getSparklrService() {
    return sparklrService;
  }

  public void setSparklrService(SparklrService sparklrService) {
    this.sparklrService = sparklrService;
  }

  public void setOauthSupport(OAuthSupport oauthSupport) {
    this.oauthSupport = oauthSupport;
  }

}
