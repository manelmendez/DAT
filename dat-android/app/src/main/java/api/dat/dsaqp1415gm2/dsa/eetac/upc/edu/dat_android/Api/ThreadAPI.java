package api.dat.dsaqp1415gm2.dsa.eetac.upc.edu.dat_android.Api;

/**
 * Created by Manel on 09/04/2015.
 */

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class ThreadAPI {
    private final static String TAG = ThreadAPI.class.getName();
    private static ThreadAPI instance = null;
    private URL url;
    private ThreadRootAPI rootAPI = null;

    private ThreadAPI(Context context) throws IOException, AppException {
        super();

        AssetManager assetManager = context.getAssets();
        Properties config = new Properties();
        config.load(assetManager.open("config.properties"));
        String urlHome = config.getProperty("dat.home.theme");
        url = new URL(urlHome);

        Log.d("LINKS", url.toString());
        getRootAPI();
    }

    public final static ThreadAPI getInstance(Context context) throws AppException {
        if (instance == null)
            try {
                instance = new ThreadAPI(context);
            } catch (IOException e) {
                throw new AppException(
                        "Can't load configuration file");
            }
        return instance;
    }

    private void getRootAPI() throws AppException {
        Log.d(TAG, "getRootAPI()");
        rootAPI = new ThreadRootAPI();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, rootAPI.getLinks());
            JSONArray jsonThreads = jsonObject.getJSONArray("threads");
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Root API");
        }
    }

    public Theme getThreads(int x) throws AppException {
        Log.d(TAG, "getThreads()");
        Theme threads = new Theme();
        String opcion =null;
        if (x==1)
        {
            opcion="tecnologia";
        }
        if (x==2)
        {
            opcion="deportes";
        }
        if (x==3)
        {
            opcion="motor";
        }
        if (x==4)
        {
            opcion="videojuegos";
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get(opcion).getTarget()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            JSONArray jsonThreads = jsonObject.getJSONArray("threads");

            for (int i = 0; i < jsonThreads.length(); i++) {
                    Threadx thread = new Threadx();
                    JSONObject jsonThread = jsonThreads.getJSONObject(i);
                    thread.setContent(jsonThread.getString("content"));
                    thread.setSubject(jsonThread.getString("subject"));
                    thread.setIdtema(jsonThread.getInt("idtema"));
                    thread.setIdthread(jsonThread.getInt("idthread"));
                    thread.setImagen(jsonThread.getString("imagen"));
                    threads.getThreads().add(thread);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Root API");
        }
        return threads;
    }

    private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
            throws AppException, JSONException {
        for (int i = 0; i < jsonLinks.length(); i++) {
            Link link = null;
            try {
                link = SimpleLinkHeaderParser
                        .parseLink(jsonLinks.getString(i));
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
            String rel = link.getParameters().get("rel");
            String rels[] = rel.split("\\s");
            for (String s : rels)
                map.put(s, link);
        }
    }
   /* public Threadx getPosts(int x,int y) throws AppException {
        Log.d(TAG, "getPosts()");
        Threadx thread = new Threadx();
        String opcion =null;
        if (x==1)
        {
            opcion="Tecnologia";
        }
        if (x==2)
        {
            opcion="Deportes";
        }
        if (x==3)
        {
            opcion="Motor";
        }
        if (x==4)
        {
            opcion="Videojuegos";
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get("thread").getTarget()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonPosts = jsonObject.getJSONArray("posts");

            for (int i = 0; i < jsonPosts.length(); i++) {
                Post post = new Post();
                JSONObject jsonThread = jsonPosts.getJSONObject(i);
                post.setContent(jsonThread.getString("content"));
                post.setIdthema(jsonThread.getInt("idthema"));
                post.setIdhilo(jsonThread.getInt("idhilo"));
                post.setIdpost(jsonThread.getInt("idpost"));
                post.setImage(jsonThread.getString("imagelink"));
                thread.getPosts().add(post);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Root API");
        }
        return thread;
    }*/
    public Threadx getPosts(int x,int y) throws AppException {
        Log.d(TAG, "getPosts()");
        Threadx thread = new Threadx();
        String opcion =null;
        if (x==1)
        {
            opcion="Tecnologia";
        }
        if (x==2)
        {
            opcion="Deportes";
        }
        if (x==3)
        {
            opcion="Motor";
        }
        if (x==4)
        {
            opcion="Videojuegos";
        }
        HttpURLConnection urlConnection = null;
        String urlTecno = "http://147.83.7.156:8080/dat-api/dat/Theme/"+opcion+"/"+y;
        try {
            url= new URL(urlTecno);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonPosts = jsonObject.getJSONArray("posts");

            for (int i = 0; i < jsonPosts.length(); i++) {
                Post post = new Post();
                JSONObject jsonThread = jsonPosts.getJSONObject(i);
                post.setContent(jsonThread.getString("content"));
                post.setIdthema(jsonThread.getInt("idthema"));
                post.setIdhilo(jsonThread.getInt("idhilo"));
                post.setIdpost(jsonThread.getInt("idpost"));
                post.setImage(jsonThread.getString("imagelink"));
                thread.getPosts().add(post);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Root API");
        }
        return thread;
    }
    public Threadx createThread(int idtema, String subject, String content, String imagen) throws AppException {
        Threadx threadx = new Threadx();
        threadx.setIdtema(idtema);
        threadx.setSubject(subject);
        threadx.setContent(content);
        threadx.setImagen(imagen);
        String opcion="thread";
        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonThread = createJsonThread(threadx);
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks()
                    .get(opcion).getTarget()).openConnection();
            urlConnection.setRequestProperty("Content-Type",
                    MediaType.DAT_API_THREAD);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonThread.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonThread = new JSONObject(sb.toString());

            threadx.setIdtema(jsonThread.getInt("idtema"));
            threadx.setSubject(jsonThread.getString("subject"));
            threadx.setContent(jsonThread.getString("content"));
            threadx.setImagen(jsonThread.getString("imagen"));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return threadx;
    }
    private JSONObject createJsonThread(Threadx threadx) throws JSONException {
        JSONObject jsonThread = new JSONObject();
        jsonThread.put("idtema", threadx.getIdtema());
        jsonThread.put("subject", threadx.getSubject());
        jsonThread.put("content", threadx.getContent());
        jsonThread.put("imagen", threadx.getImagen());
        return jsonThread;
    }
}