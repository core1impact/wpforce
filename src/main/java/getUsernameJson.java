import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class getUsernameJson implements Runnable {
    private final String domain;
    public static ArrayList<String> userlist = new ArrayList<>();

    public getUsernameJson(String domain) {
        this.domain = domain;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(domain + "/wp-json/wp/v2/users");
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            InputStream in = connect.getInputStream();
            connect.setInstanceFollowRedirects(true);
            connect.connect();
            if (connect.getResponseCode() == HttpURLConnection.HTTP_OK) {

                String encoding = connect.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String json = IOUtils.toString(in, encoding);
                JSONArray users = new JSONArray(json);

                for (int i = 0; i < users.length(); i++) {
                    JSONObject object = users.getJSONObject(i);
                    userlist.add(object.get("slug").toString());
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
        return;
    }
}
