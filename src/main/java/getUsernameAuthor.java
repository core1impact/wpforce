import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class getUsernameAuthor implements Runnable {

    private final String domain;
    ArrayList<String> userlist = new ArrayList<>();

    public getUsernameAuthor(String domain) {
        this.domain = domain;
    }

    @Override
    public void run() {
        System.out.println("getUsernameAuthor");

        try {
            URL url = new URL(domain + "/?author=1");
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            InputStream in = connect.getInputStream();
            connect.connect();

            if (connect.getResponseCode() == HttpURLConnection.HTTP_OK) {

                String encoding = connect.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String body = IOUtils.toString(in, encoding);

                if (connect.getContentType().contains("text/html")) {
                    Document doc = Jsoup.parse(body);
                    Elements imports = doc.select("link[href]");
                    for (Element link : imports) {
                        if (link.attr("rel").contains("canonical")) {
                            String h = link.attr("abs:href");
                            String res = h.toString();
                            if (res.contains("/author/")) {
                                String author = res.substring(res.indexOf("/author/"));
                                if (author.indexOf("/") > 0) {
                                    author = author.substring(8, author.length());
                                } else {
                                    author = author.substring(8, author.length() - 1);
                                }
                                userlist.add(author); //
                            } else {
                                return;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            main.usernames.put(userlist);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
