/* This class initializes wordnet via the jwi interface.*/

package de.dhbw.text2process.wrapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.dhbw.text2process.config.PropertiesWithJavaConfig;
import edu.mit.jwi.IRAMDictionary;
import edu.mit.jwi.RAMDictionary;
import edu.mit.jwi.data.ILoadPolicy;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import edu.mit.jwi.item.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordNetInitializer {

  // Initialize log4j to log information into the console
  Logger logger = LoggerFactory.getLogger(WordNetInitializer.class);

  private static HttpClient httpClient;
  private static Gson gson;

  private static WordNetInitializer wni;

  private String wordNetPath;

  /**
   *
   *
   * <h1>Constructor of the WordNetInitializer</h1>
   *
   * <p>This Class is made to provide an WordNet dictionary instance wrapped into the
   * WordNetInitializer
   */
  private WordNetInitializer() {
    logger.info("Initializing WordNet dictionary ...");

    //        ApplicationHome ah = new ApplicationHome(this.getClass());
    //        wordNetPath = ah.getDir().getPath() + wordNetPath;

    String uriString = PropertiesWithJavaConfig.wordnetHost;
    if (PropertiesWithJavaConfig.wordnetPort.length() > 0) {
      uriString += ":" + PropertiesWithJavaConfig.wordnetPort;
    }
    wordNetPath = uriString;

    logger.debug("WordNetUrl: " + wordNetPath);

    httpClient = HttpClient.newHttpClient();
    gson = new Gson();

  }

  /**
   * Method to get an Instance of WordNetInitializer
   *
   * @return wni WordNetInitializer
   */
  public static synchronized WordNetInitializer getInstance() {
    if (wni == null) {
      synchronized (FrameNetInitializer.class) {
        if (wni == null) {
          wni = new WordNetInitializer();
          wni.init();
        }
      }
    }
    return wni;
  }

  public static synchronized void resetInstance() {
    wni = null;
  }

  //public synchronized IRAMDictionary getDict() {
  //  return dict;
  //}

  /**
   * Initialization of the actual WordNet dictionary. Loading it into the memory an providing access
   * trough the wmi object.
   */
  private synchronized void init() {

      logger.debug("Creating connection to dictionary");

      long t = System.currentTimeMillis();

      try {
        String uriString = wordNetPath + "/healthcheck";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriString))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("done (" + (System.currentTimeMillis() - t) + " msec )");

      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
        logger.error("Problem connceting to wordnet!");
      }
  }

  public String getIndexWord(String word, POS pos){
    return getIndexWord(pos, word);
  }
  public String getIndexWord(POS pos, String word) {

    Map<String, String> data = new HashMap<>() {{
      put("word", word);
      put("pos", ""+pos.getTag());
    }};

    Type typeObject = new TypeToken<HashMap>() {}.getType();
    String jsonObject = gson.toJson(data, typeObject);
    String uriString = PropertiesWithJavaConfig.wordnetHost;
    if (PropertiesWithJavaConfig.wordnetPort.length() > 0) {
      uriString += ":" + PropertiesWithJavaConfig.wordnetPort;
    }
    uriString += "/" + PropertiesWithJavaConfig.worndnetUri;
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uriString))
            .POST(HttpRequest.BodyPublishers.ofString(jsonObject))
            .header("Content-Type", "application/json")
            .build();

    try {
      HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

      Type type = new TypeToken<Map<String, String>>(){}.getType();
      Map<String, String> responseMap = gson.fromJson(response.body(), type);

      String responseWord = responseMap.get("word");

      logger.warn("Got response: " + responseWord);

      String _idw = responseWord;
      return _idw;

    } catch (IOException | InterruptedException e) {
      return null;
    }

  }
}
