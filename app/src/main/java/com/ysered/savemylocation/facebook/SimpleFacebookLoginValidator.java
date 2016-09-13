package com.ysered.savemylocation.facebook;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleFacebookLoginValidator implements LoginValidator {

    private static final int EXPECTED_STATUS_CODE = 302;

    public static String getCookies() {
        String cookies = "";
        try {
            URL facebookUrl = new URL("https://www.facebook.com/");
            HttpURLConnection connection = (HttpURLConnection) facebookUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            for (String key : connection.getHeaderFields().keySet()) {
                if (key != null && key.equals("Set-Cookie")) {
                    cookies = connection.getHeaderField(key);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cookies;
    }

    /**
     * Simulates web-browser behaviour for Facebook login.
     * First request reads cookies to pass them in headers for actual login request.
     *
     * With actual browser Facebook behaves in following manner:
     *   - if login is incorrect browser receives 200 status and contains some error message
     *   in web-page body
     *   - if login is correct browser receives 302 redirect code and make next request.
     *
     * This method assumes that 302 response code signals about valid credentials.
     */
    @Override
    public boolean validate(String login, String password) {
        final String cookies = getCookies();

        boolean result = false;
        HttpURLConnection connection;
        try {
            // prepare request
            final URL facebookLoginUrl =
                    new URL("https://www.facebook.com/login.php?login_attempt=1&lwv=110");
            connection = (HttpURLConnection) facebookLoginUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setInstanceFollowRedirects(false);
            connection.setDoOutput(true);

            // headers
            connection.setRequestProperty("accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
            connection.setRequestProperty("accept-language", "en-US,en;q=0.8,ru;q=0.6,uk;q=0.4");
            connection.setRequestProperty("cache-control", "max-age=0");
            connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("save-data", "on");
            connection.setRequestProperty("cookie", cookies);
            connection.setRequestProperty("origin", "https://www.facebook.com/");
            connection.setRequestProperty("referer", "https://www.facebook.com/?stype=lo&jlou=Afcb3VxRgf8-sFGEJMocqHCdfBFnK1gUQ--E3pAVkMu6WVwi-y-twnhUijId5ilTLxGh8q36tfpMws_f81lZfg_U&smuh=41235&lh=Ac9rX4_Jefpg93fw");
            connection.setRequestProperty("save-data", "on");
            connection.setRequestProperty("upgrade-insecure-requests", "1");
            connection.setRequestProperty("user-agent", "Mozilla/5.0");

            // body
            final String requestBody = "lsd=AVpGDi9X" +
                    "&email=" + login +
                    "&pass=" + password +
                    "&persistent=1" +
                    "&default_persistent=1" +
                    "&timezone=360" +
                    "&locale=en_US" +
                    "&lgnrnd=135940_imAl" +
                    "&lgnjs=1375995581" +
                    "&next=https://www.facebook.com/?stype=lo&jlou=Afcb3VxRgf8-sFGEJMocqHCdfBFnK1gUQ--E3pAVkMu6WVwi-y-twnhUijId5ilTLxGh8q36tfpMws_f81lZfg_U&smuh=41235&lh=Ac9rX4_Jefpg93fw";

            // make request proper body
            OutputStream output = null;
            try {
                output = connection.getOutputStream();
                output.write(requestBody.getBytes("UTF-8"));
                if (connection.getResponseCode() == EXPECTED_STATUS_CODE) {
                    result = true;
                }
            } catch (IOException e) {

            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {}
                }
            }
        } catch (IOException e) {

        }

        return result;
    }

}