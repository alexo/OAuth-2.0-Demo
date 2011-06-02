package org.springframework.security.oauth.examples.tonr.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.betfair.security.oauth2.OAuthNotAuthorizedException;
import org.betfair.security.oauth2.OAuthSupport;
import org.springframework.security.oauth.examples.tonr.SparklrService;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author cotarlas
 */
public class SparklrServiceImpl
    implements SparklrService {

  private OAuthSupport oauthSupport;
  private String sparklrPhotoListURL;
  private String sparklrPhotoURLPattern;

  public List<String> getSparklrPhotoIds()
      throws OAuthNotAuthorizedException {
    try {

      InputStream photosXML = oauthSupport.getBinaryResource(getSparklrPhotoListURL());

      final List<String> photoIds = new ArrayList<String>();
      SAXParserFactory parserFactory = SAXParserFactory.newInstance();
      parserFactory.setValidating(false);
      parserFactory.setXIncludeAware(false);
      parserFactory.setNamespaceAware(false);
      SAXParser parser = parserFactory.newSAXParser();
      parser.parse(photosXML, new DefaultHandler() {
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
          if ("photo".equals(qName)) {
            photoIds.add(attributes.getValue("id"));
          }
        }
      });
      return photoIds;
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } catch (SAXException e) {
      throw new IllegalStateException(e);
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException(e);
    }
  }

  public InputStream loadSparklrPhoto(String id)
      throws OAuthNotAuthorizedException {
    return oauthSupport.getBinaryResource(String.format(getSparklrPhotoURLPattern(), id));
  }

  public String getSparklrPhotoURLPattern() {

    return sparklrPhotoURLPattern;
  }

  public void setSparklrPhotoURLPattern(String sparklrPhotoURLPattern) {
    this.sparklrPhotoURLPattern = sparklrPhotoURLPattern;
  }

  public String getSparklrPhotoListURL() {
    return sparklrPhotoListURL;
  }

  public void setSparklrPhotoListURL(String sparklrPhotoListURL) {
    this.sparklrPhotoListURL = sparklrPhotoListURL;
  }

  public void setOauthSupport(OAuthSupport oauthSupport) {
    this.oauthSupport = oauthSupport;
  }

}
