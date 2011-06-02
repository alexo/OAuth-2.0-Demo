package org.betfair.security.oauth2;

/**
 * Thrown when access token is not available or has expired.
 *
 * @author cotarlas
 */
public class OAuthNotAuthorizedException extends Exception {

  private static final long serialVersionUID = 3899593851602350357L;

  public OAuthNotAuthorizedException(String message) {
    super(message);
  }

  public OAuthNotAuthorizedException(String message, Throwable cause) {
    super(message, cause);
  }

}
