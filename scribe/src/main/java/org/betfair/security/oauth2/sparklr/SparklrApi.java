package org.betfair.security.oauth2.sparklr;

import static org.scribe.utils.URLUtils.formURLEncode;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.Preconditions;


/**
 * Scribe specific way to specify the OAuth end-points (for authorization and token exchange).
 *
 * NOTE: the type of access grant is specified manually in AUTHORIZE_URL (response_type=code)
 * Also I had to customize the AccessTokenExtractor and the way requests are signed (used the Authorization header).
 *
 * @author cotarlas
 */
public class SparklrApi
    extends DefaultApi20 {

  private static final String AUTHORIZE_URL = "http://localhost:8080/sparklr/oauth/user/authorize?client_id=%s&redirect_uri=%s&response_type=code";
  private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

  private static final String TOKEN_URL = "http://localhost:8080/sparklr/oauth/authorize";

  @Override
  public String getAccessTokenEndpoint() {
    return TOKEN_URL;
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback");

    // Append scope if present
    if (config.hasScope()) {
      return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), formURLEncode(config.getCallback()),
          formURLEncode(config.getScope()));
    } else {
      return String.format(AUTHORIZE_URL, config.getApiKey(), formURLEncode(config.getCallback()));
    }
  }

  public OAuthService createService(OAuthConfig config) {
    return new OAuth20ServiceImpl(this, config) {
      @Override
      public void signRequest(Token accessToken, OAuthRequest request) {
        request.addHeader(OAuthConstants.HEADER, "OAuth2 " + accessToken.getToken());
      }
    };
  }

  @Override
  public AccessTokenExtractor getAccessTokenExtractor() {
    return JsonTokenExtractor.INSTANCE;
  }

  private static class JsonTokenExtractor implements AccessTokenExtractor {

    public static JsonTokenExtractor INSTANCE = new JsonTokenExtractor();

    public Token extract(String response) {
      JsonFactory jsonFactory = new JsonFactory();
      try {
        JsonParser parser = jsonFactory.createJsonParser(response);
        parser.setCodec(new ObjectMapper());
        JsonNode node = parser.readValueAsTree();
        String accessToken = node.get("access_token").getTextValue();
        return new Token(accessToken, "");
      } catch (Exception e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

}
