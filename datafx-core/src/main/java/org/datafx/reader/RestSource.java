/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datafx.reader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.datafx.reader.util.Converter;
import org.datafx.reader.util.InputStreamConverter;
import org.datafx.reader.util.OAuth;

/**
 *
 * @author johan
 */
public class RestSource <T> extends AbstractDataReader<T> {
    
    private InputStreamConverter<T> converter;
    private String host;
    private String path;
    private String urlBase;
    private String consumerKey;
    private String consumerSecret;
    private boolean requestMade;
    private Map<String, String> requestProperties;
    private Map<String, String> queryParams = new HashMap<String, String>();
    private Map<String, String> formParams = new HashMap<String, String>();
    private String dataString;
    private String requestMethod;
    private InputStream is;
    
    public RestSource(String host,InputStreamConverter converter) {
        this.host = host;
        this.converter= converter;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
        public T getData() {
        if (isSingle()) {
            T answer = converter.convert(is);
            return answer;
        }
        else {
            T answer = converter.next(is);
            return answer;
        }
    }

    public boolean hasMoreData() {
        return converter.hasMoreData(is);
    }
    
    private synchronized void createRequest () {
        InputStream is = null;
        try {
            if (requestMade) {
                return;
            }
            is = getInputStream();
            requestMade = true;
        } catch (IOException ex) {
            Logger.getLogger(RestSource.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(RestSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
     public InputStream getInputStream() throws IOException {
        URL url = new URL(host);
        URLConnection connection = url.openConnection();
        if (getConsumerKey() != null) {
            try {
                Map<String, String> allParams = new HashMap<String, String>();
                allParams.putAll(getQueryParams());
                allParams.putAll(getFormParams());
                String header = OAuth.getHeader(getRequestMethod(), urlBase, allParams, getConsumerKey(), getConsumerSecret());
                connection.addRequestProperty("Authorization", header);

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(RestSource.class.getName()).log(Level.SEVERE, null, ex);
            } catch (GeneralSecurityException ex) {
                Logger.getLogger(RestSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (getRequestMethod() != null) {
            ((HttpURLConnection) connection).setRequestMethod(getRequestMethod());
        }

        if (getRequestProperties() != null) {
            for (Map.Entry<String, String> requestProperty : getRequestProperties().entrySet()) {
                connection.addRequestProperty(requestProperty.getKey(), requestProperty.getValue());
            }
        }

        if (getDataString() != null) {
            connection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(getDataString());
            outputStreamWriter.close();
        }

        InputStream is = connection.getInputStream();
        return is;
    }

    /**
     * @return the consumerKey
     */
    public String getConsumerKey() {
        return consumerKey;
    }

    /**
     * @param consumerKey the consumerKey to set
     */
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    /**
     * @return the consumerSecret
     */
    public String getConsumerSecret() {
        return consumerSecret;
    }

    /**
     * @param consumerSecret the consumerSecret to set
     */
    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    /**
     * @return the requestProperties
     */
    public Map<String, String> getRequestProperties() {
        return requestProperties;
    }

    /**
     * @param requestProperties the requestProperties to set
     */
    public void setRequestProperties(Map<String, String> requestProperties) {
        this.requestProperties = requestProperties;
    }

    /**
     * @return the queryParams
     */
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    /**
     * @param queryParams the queryParams to set
     */
    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    /**
     * @return the formParams
     */
    public Map<String, String> getFormParams() {
        return formParams;
    }

    /**
     * @param formParams the formParams to set
     */
    public void setFormParams(Map<String, String> formParams) {
        this.formParams = formParams;
    }

    /**
     * @return the dataString
     */
    public String getDataString() {
        return dataString;
    }

    /**
     * @param dataString the dataString to set
     */
    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    /**
     * @return the requestMethod
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * @param requestMethod the requestMethod to set
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    
}