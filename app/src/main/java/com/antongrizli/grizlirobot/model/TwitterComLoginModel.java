package com.antongrizli.grizlirobot.model;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TwitterComLoginModel {
    public String loginToTwitter(final String defaultURL, final String login, final String password) {
        try {
            String url = "";
            Connection.Response connection = null;
            // Get authorization URL
            connection = Jsoup.connect(defaultURL).execute();
            Document doc = null;

            doc = connection.parse();

            Element form = doc.getElementsByTag("form").get(0);

            Element authenticity_token = doc.select("input[name=authenticity_token]").first();
            String auth_token = authenticity_token.attr("value");

            Element redirect_after_login = doc.select("input[name=redirect_after_login]").first();
            String redirect = redirect_after_login.attr("value");

            Element tokenElement = doc.getElementById("oauth_token");
            String token = tokenElement.attr("value");

            url = form.attr("action");
            Document document = Jsoup.connect(url)
                    .data("authenticity_token", auth_token)
                    .data("redirect_after_login", redirect)
                    .data("oauth_token", token)
                    .data("session[username_or_email]", login)
                    .data("session[password]", password).cookies(connection.cookies()).post();

            Element pin = document.getElementsByTag("code").first();
            System.out.println(pin.text());
            return pin.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
