import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

/*
 *  The program prompts the user for a topic at the terminal, and then prints out the introductory paragraph of the
 *  Wikipedia page for that topic, without HTML tags. The program asks the user to enter the query at a prompt. If
 *  the user does not enter a topic (enters a blank one), they will be prompted again. If there is no page for that
 *  topic the program prints "Not found." and terminates. The program also terminates after printing a successful
 *  result. The topic can be provided as a command-line argument as well as at a prompt if no argument is provided.
 *
 *  @author Srinivas Narne
 *  @version 1.0
 *  @since 2017-07-13
 */

public class Application {
    private static final String USER_AGENT = "Mozilla/5.0";
    /*
     *  This is the main function which fetches the topic from the user through command line. If no arguments are given
     *  it prompts the user to enter a topic.
     *  @param args Used to get the topic name from the user
     *  @return Nothing
     */
    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.println("******************************************");
        System.out.println();
        System.out.println("********** Welcome to Wikipedia **********");
        System.out.println();
        System.out.println("******************************************");
        System.out.println();

        String topic = "";

        if (args.length == 0) { // If no arguments are given the user is prompted to enter a topic to search.
            while (topic.length() == 0) {
                System.out.println("Please enter the topic that you are interested in: ");
                topic = input.nextLine();
                if (topic.length() == 0) {
                    System.out.println("Please try again.");
                }
            }

        } else {
            /*
             *  If the topic is provided as a command-line argument,
             *  each of the words are taken by the program as a separate argument.
             *  All these words should be combined into a single string to generate the topic name.
             */
            StringBuilder sb = new StringBuilder();
            for (String str: args) {
                sb.append(str);
                sb.append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            topic = sb.toString();
        }
        System.out.println();
        String wikiURL = generateURL(topic);
        printWikiIntro(wikiURL);
        System.out.println("Thank you for using our service.");
    }

    /*
     *  This method is used to fetch and parse the JSON object related to the topic given by user. It parses the object
     *  and prints the extract to the console. If the topic could not be found on wikipedia it prints "Not Found."
     *  @param wikiURL This is the URL from where the URL from where the object is to be fetched
     *  @return Nothing
     */
    private static void printWikiIntro(String wikiURL) throws Exception {
        URL newURL = new URL(wikiURL);
        HttpURLConnection con = (HttpURLConnection) newURL.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        /*
         *  The JSON object received from Wikipedia needs to parsed to obtain "extract". The received object here
         *  consists of nested JSON objects. So each of these objects are extracted before extracting the desired
         *  "extract".
         */
        JSONObject obj = new JSONObject(response.toString());
        JSONObject query = obj.getJSONObject("query");
        JSONObject pages = (query.getJSONObject("pages"));

        Iterator keysToCopyIterator = pages.keys();
        List<String> keysList = new ArrayList<>();
        while(keysToCopyIterator.hasNext()) {
            String key = (String) keysToCopyIterator.next();
            keysList.add(key);
        }
        String str = (keysList.get(0));
        if (str.equals("-1")) { // The received JSON object is checked for a valid wikipedia entry.
            System.out.println("Sorry, the requested topic cannot be found on Wikipedia.");
            return;
        }
        JSONObject mainContent = pages.getJSONObject(str);
        System.out.println(mainContent.getString("extract"));

    }
    /*
     *  This method generated the URL for the JSON object from the topic entered by the user. Wikipedia uses certain
     *  formatting to represent special characters in it's URLs. This method converts user query into wikipedia URLs.
     *  @param topic The topic entered by the user
     *  @return String Returns the URL from where the JSON object is to be fetched
     */
    private static String generateURL(String topic) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=");
        char[] topicArray = topic.toCharArray();
        for (char ch: topicArray) {
            switch (ch) {
                case ' ' :
                    sb.append("%20");
                    break;
                case ',' :
                    sb.append("%2C");
                    break;
                case ';':
                    sb.append("%3B");
                    break;
                case '<':
                    sb.append("%3C");
                    break;
                case '>' :
                    sb.append("%20");
                    break;
                case '?' :
                    sb.append("%2C");
                    break;
                case '[':
                    sb.append("%3B");
                    break;
                case ']':
                    sb.append("%3C");
                    break;
                case '{' :
                    sb.append("%20");
                    break;
                case '|' :
                    sb.append("%2C");
                    break;
                case '}':
                    sb.append("%3B");
                    break;
                default:
                    sb.append(ch);
            }
        }
        return sb.toString();
    }
}