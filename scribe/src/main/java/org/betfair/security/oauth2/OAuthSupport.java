package org.betfair.security.oauth2;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.InitializingBean;

/**
 * Abstracts the OAuth client API (scribe API) and supports configuration with Spring.
 *
 * @author cotarlas
 */
public class OAuthSupport implements InitializingBean {

  private String providerClass;
  private String clientId;
  private String clientSecret;
  private String redirectUrl;

  private OAuthService service;

  public void afterPropertiesSet() {
    try {
      service = new ServiceBuilder()
      .provider((Class<? extends Api>) Class.forName(providerClass))
      .apiKey(clientId)
      .apiSecret(clientSecret)
      .callback(redirectUrl)
      .build();
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  public void initContext(HttpServletRequest request, HttpServletResponse response) {
    OAuthContext.setContext(new OAuthContext(clientId, request, response));
  }

  public void removeContext() {
    OAuthContext.remove();
  }

  public void accessAuthorized() throws OAuthNotAuthorizedException {

    HttpServletRequest request = OAuthContext.getContext().getRequest();

    if (!OAuthContext.hasAccessToken()) {

      String grantCode = request.getParameter("code");
      if (grantCode != null) {
        // we have an access grant - exchange it for an access token
        Verifier verifier = new Verifier(grantCode);
        Token accessToken = service.getAccessToken(null, verifier);
        OAuthContext.storeAccessToken(accessToken.getToken());
      } else {
        throw new OAuthNotAuthorizedException("Authorization required.");
      }
    }

  }

  public void obtainAccess() {
    try {
      OAuthContext.getContext().getResponse().sendRedirect(service.getAuthorizationUrl(null));
    } catch (IOException ioe) {
      throw new RuntimeException(ioe.getMessage(), ioe);
    }
  }

  public String getResource(String url) throws OAuthNotAuthorizedException {
    return getResponse(url).getBody();
  }

  public InputStream getBinaryResource(String url) throws OAuthNotAuthorizedException {
    return getResponse(url).getStream();
  }

  private Response getResponse(String url) throws OAuthNotAuthorizedException {

    OAuthRequest request = new OAuthRequest(Verb.GET, url);
    service.signRequest(new Token(OAuthContext.getAccessToken(), null), request);

    Response response = request.send();

    int httpStatus = response.getCode();
    if (401 == httpStatus) {
      throw new OAuthNotAuthorizedException("Access token expired.");
    } else if (200 != httpStatus) {
      throw new RuntimeException("Failed to access resource.");
    }

    return response;
  }



  public void setProviderClass(String providerClass) {
    this.providerClass = providerClass;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

}
