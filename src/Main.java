import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.*;
import java.util.*;
import java.io.*;

/**
 * Finds 100 pages and all the emails they contain, excluding repeats
 */
public class Main {
    private static HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
    private static Set<String> emails = new HashSet<String>();

    /**
     * Reads a webpage line by line for both new urls and emails and saves them into a hashmap or a set, respectively
     * @param url of page that will be scanned
     * @throws IOException if error in reading page
     */
    public static void findUrlsOnPage(URL url) throws IOException
    {
        BufferedReader rdr = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = rdr.readLine()) != null) {

            //find urls
            Pattern link = Pattern.compile("<a\\s*?href=\"(http:.*?)\"");
            Matcher matcher = link.matcher(line);
            if (matcher.find())
            {
                if(!hashMap.containsKey(matcher.group(1)))
                {
                    hashMap.put(matcher.group(1), false);
                    //System.out.println("Found urls on page " +url + "      " + matcher.group(1));
                }
            }

            //find emails
            Pattern email = Pattern.compile("\"mailto:(.*?)\"");
            Matcher emailMatcher = email.matcher(line);
            if(emailMatcher.find())
            {
                emails.add(emailMatcher.group(1));
            }
        }
    }

    /**
     * Takes user-inputted url string and attempts to visit 100 pages from it. It will collect emails as it goes
     * @param args
     */
    public static void main(String[] args) {
        System.out.print("What would you like to search? ");
        Scanner in = new Scanner(System.in);
        String s = in.next();
        //ToPete: I had trouble getting tokens to work with https, so if you need test websites for http, I used
        // "http://www.foxnews.com/" to test emails (theres one email on the page twice)
        // "http://www.wikia.com/explore" to do a full crawl, with no emails. Has a lot of links everywhere though, also shows error handling
        hashMap.put(s, false);

        int visitCount = 0;

        while(visitCount < 100 && hashMap.containsValue(false)) {
            Set<String> keys = hashMap.keySet();
            for (String key : keys) {
                if (hashMap.containsKey(key) && !hashMap.get(key)) {

                    //only counts url towards visit count if no errors occur when accessing it
                    try
                    {
                        findUrlsOnPage(new URL(key));
                        visitCount++;
                    }
                    catch(Exception ex) {
                        System.out.printf("Oops issue with page  %s : %s", key, ex.getMessage());

                    }

                    hashMap.put(key, true);
                    break;
                }
            }
        }

        System.out.println("All emails found from crawl: ");
        for(String email: emails)
        {
            System.out.println(email);
        }
    }
}
