package ipcm.tool.kit;

public class TwitterFeed {

    public String url;
    public String feedType;
    String user;
    String listName;
    long id;

    public static final String USER = "user";
    public static final String LIST = "list";

    public TwitterFeed(){}

    public TwitterFeed(String url){
        this.url = url;

        if(url.contains("$")){
            user = url.substring(0, url.indexOf("$"));
            listName = url.substring(url.indexOf("$") + 1);
            feedType = LIST;
        }else{
            user = url;
            listName = null;
            feedType = USER;
        }

    }

    public TwitterFeed(long id, String url){
        this.id = id;
        this.url = url;

        if(url.contains("$")){
            user = url.substring(0, url.indexOf("$"));
            listName = url.substring(url.indexOf("$") + 1);
            feedType = LIST;
        }else{
            user = url;
            listName = null;
            feedType = USER;
        }

    }

}
