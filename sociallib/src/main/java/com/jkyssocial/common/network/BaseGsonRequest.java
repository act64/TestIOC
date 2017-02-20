package com.jkyssocial.common.network;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.mintcode.App;
import com.jkys.jkysim.database.KeyValueDBService;
import com.mintcode.util.Keys;
import com.mintcode.util.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * A request for retrieving a json response body at a given URL, allowing for an
 * optional gson to be passed in as part of the request body.
 */
public class BaseGsonRequest<T> extends JsonRequest<T> {

    private static Gson gson = new Gson();

    private Class<T> clazz;
    private String mRequestBody;


    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param requestBody A {@link String} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public BaseGsonRequest(Class<T> clazz, int method, String url, String requestBody,
                             Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener,
                errorListener);
        this.clazz = clazz;
    }

    /**
     * Creates a new request.
     * @param url URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public BaseGsonRequest(Class<T> clazz, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
        this.clazz = clazz;
    }

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public BaseGsonRequest(Class<T> clazz, int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
        this.clazz = clazz;
    }

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public BaseGsonRequest(Class<T> clazz, int method, String url, JSONObject jsonRequest,
                             Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
        this.clazz = clazz;
        mRequestBody=jsonRequest.toString();
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     */
    public BaseGsonRequest(Class<T> clazz, String url, JSONObject jsonRequest, Response.Listener<T> listener,
                             Response.ErrorListener errorListener) {
        this(clazz, jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest,
                listener, errorListener);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            T t = gson.fromJson(jsonString, clazz);
            return Response.success(t,
                    new MedicalEntry(HttpHeaderParser.parseCacheHeaders(response)));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (Exception je) {
            return Response.error(new ParseError(je));
        }
    }

    public class MedicalEntry extends Cache.Entry{
        public MedicalEntry(Cache.Entry entry){
            data = entry.data;
            etag = entry.etag;
            softTtl = entry.softTtl;
            ttl = entry.ttl;
            serverDate = entry.serverDate;
            lastModified = entry.lastModified;
            responseHeaders = entry.responseHeaders;
        }
        @Override
        public boolean refreshNeeded() {
            if (Utils.haveInternet(App.getInstence())){
                return true;
            }
            return false;
        }

        @Override
        public boolean isExpired() {
            if (Utils.haveInternet(App.getInstence())){
                return false;
            }
            return true;
        }
    }

    @Override
    public String getCacheKey() {
        return getUrl()+getToken();
    }



    private  String getToken() {
        String token = KeyValueDBService.getInstance().find(Keys.NEW_TOKEN);
        if (token == null || token.equals("anonymous")) {
            return null;
        }
        return token;
    }
}

