import java.sql.*;
import java.util.Scanner;
public class test{
    public static int Manager(){
        Scanner scan = new Scanner(System.in);
        int cmd = -1;
        System.out.println("------------------------------");
        System.out.println("0.delete video");
        System.out.println("1.view user's information");
        System.out.println("2.view user's viewing history");
        System.out.println("3.view videos uploaded by user");
        System.out.println("4.quit");
        System.out.println("------------------------------");
        cmd = scan.nextInt();
        scan.nextLine();
        return cmd;
    }
    public static int User(){
        Scanner scan = new Scanner(System.in);
        int cmd = -1;
        System.out.println("------------------------------");
        System.out.println("0.watch other's video");
        System.out.println("1.upload your own video");
        System.out.println("2.make a playlist");
        System.out.println("3.watch video in your playlist");
        System.out.println("4.view list of your playlists");
        System.out.println("5.delete the playlists");
        System.out.println("6.add or delete video from your playlist");
        System.out.println("7.delete video you uploaded");
        System.out.println("8.view the ranking of genre");
        System.out.println("9.quit");
        System.out.println("------------------------------");
        cmd = scan.nextInt();
        scan.nextLine();
        return cmd;
    }

    public static String check_genre() {
        Scanner scan = new Scanner(System.in);
        System.out.println("------------------------------");
        System.out.println("0. action");
        System.out.println("1. romance");
        System.out.println("2. comedy");
        System.out.println("3. horror");
        System.out.println("4. eating show(mukbang)");
        System.out.println("5. talk show");
        System.out.println("6. kids");
        System.out.println("------------------------------");
        return scan.nextLine();
    }
    public static void main(String[] args)throws SQLException{
        try {
            String url = "jdbc:mysql://localhost:3307/youtube";
            String user = "root";
            String password = "password";

            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            Statement update_stat = connection.createStatement();
            Statement temp_stat = connection.createStatement();
            ResultSet result, temp_set;

            while(true){
                System.out.println("--------------------------");
                System.out.println("What do you want?");
                System.out.println("0.Exit");
                System.out.println("1.Manager");
                System.out.println("2.User");
                System.out.println("3.Make new Id");
                System.out.println("--------------------------");
                int cmd = -1;
                Scanner scan = new Scanner(System.in);
                cmd = scan.nextInt();
                scan.nextLine();
                if(cmd == 0){ //exit program
                    statement.close();
                    update_stat.close();
                    temp_stat.close();
                    connection.close();
                    return;
                }
                else if(cmd == 1){ //Manager mode
                    System.out.println("Tell me your name");
                    String Mname = scan.nextLine();
                    Mname = "'" + Mname + "'";
                    result = statement.executeQuery("Select Name from manager where Name = " + Mname);
                    if(!result.next()){
                        System.out.println("You are not registered");
                        continue;
                    }
                    System.out.println("Please tell me your Ssn " + Mname);
                    result = statement.executeQuery("Select Ssn from manager where Name = " + Mname);
                    int Mgrssn = scan.nextInt();
                    scan.nextLine();
                    boolean chk = false;
                    while(result.next()){
                        int temp_ssn = result.getInt("Ssn");
                        if(Mgrssn == temp_ssn) {
                            chk = true;
                            break;
                        }
                    }
                    if(!chk){
                        System.out.println("No such manager...");
                        continue;
                    }
                    System.out.println("Welcome " + Mname + "!");
                    boolean loop = true;
                    while(loop) {
                        int temp = Manager();
                        switch (temp) {
                            case 0: // delete video from data
                                //show videos first
                                result = statement.executeQuery("Select * from video");
                                if(!result.next()){
                                    System.out.println("There is no video now");
                                    break;
                                }
                                result = statement.executeQuery("Select * from video");
                                int v_idx = 0;
                                System.out.println("===============================");
                                while (result.next()) {
                                    String v_title = result.getString("Title");
                                    int v_count = result.getInt("view_count");
                                    int v_id = result.getInt("video_id");
                                    ResultSet genres = temp_stat.executeQuery("Select genre_name from genre_of, video, genre where video_id = vid_id AND gen_idx = genre_index AND vid_id = " + v_id);
                                    //should show channel name
                                    System.out.print(v_idx + ". Title : " + v_title + ", Video_id : " + v_id + ", view : " + v_count + ", genre : ");
                                    while (genres.next()) {
                                        System.out.print(genres.getString("genre_name") + ", ");
                                    }
                                    System.out.println();
                                    update_stat.executeUpdate("update video set video_index = " + v_idx + " where video_id = " + v_id);
                                    v_idx++;
                                }
                                System.out.println("===============================");
                                System.out.println("input the number of the video to delete");
                                int v_idx_to_delete = scan.nextInt();
                                result = statement.executeQuery("Select video_id from video where video_index = " + v_idx_to_delete);
                                if (!result.next()) {
                                    System.out.println("No such video...! try again");
                                    continue;
                                }
                                int vid_to_delete = result.getInt("video_id");
                                System.out.println("Are you sure? please enter 'Yes' if you want to delete");
                                scan.nextLine();
                                String is_sure = scan.nextLine();
                                if (!is_sure.equals("Yes") && !is_sure.equals("YES") && !is_sure.equals("yes")) {
                                    System.out.println("Ok. I won't delete it");
                                    continue;
                                }
                                update_stat.executeUpdate("Delete from includes where vid_id = " + vid_to_delete);
                                update_stat.executeUpdate("Delete from genre_of where vid_id = " + vid_to_delete);
                                update_stat.executeUpdate("Delete from watches where vid = " + vid_to_delete);
                                update_stat.executeUpdate("Delete from video where video_id = " + vid_to_delete);
                                System.out.println("video_id : " + vid_to_delete + " was deleted...");
                                break;
                            case 1: // view user's inform
                                result = statement.executeQuery("Select * from user where Mgr_ssn = " + Mgrssn);
                                System.out.println("================================================================================================================");
                                System.out.println("<User List>");
                                int user_idx = 0;
                                while(result.next()){
                                    System.out.println("user " + user_idx + ". ID : " + result.getString("Id") + ", Age : " + result.getInt("Age") + ", Sex : " +
                                            result.getString("Sex") + ", Address : " + result.getString("Address") + ", Email : " + result.getString("Email") +
                                            ", Phone number : " + result.getString("PhoneNumber") + ", Mgr_ssn : " + result.getInt("Mgr_ssn"));
                                    user_idx++;
                                }
                                System.out.println("================================================================================================================");
                                System.out.println("");
                                break;
                            case 2: // view user's watching history
                                result = statement.executeQuery("Select Id from user where Mgr_ssn = " + Mgrssn);
                                if(!result.next()){
                                    System.out.println("There is no user you are managing");
                                    break;
                                }
                                result = statement.executeQuery("Select Id from user where Mgr_ssn = " + Mgrssn);
                                int user_count = 0;
                                while(result.next()){
                                    String user_id = result.getString("Id");
                                    //String Chan_name = result.getString("ChannelName");
                                    System.out.println("user " + user_count + ". ID : " + user_id);
                                    user_count++;
                                }
                                System.out.println("enter user's ID you want to see history");
                                String id_want_to_see = scan.nextLine();
                                id_want_to_see = "'" + id_want_to_see + "'";
                                result = statement.executeQuery("Select video_id, Title, length from watches, video where video_id = vid AND uid = " + id_want_to_see);
                                System.out.println("======================================");
                                System.out.println("<history of user " + id_want_to_see + ">");
                                user_count = 0;
                                while(result.next()){
                                    String vid = result.getString("video_id");
                                    String vTitle = result.getString("Title");
                                    String vlength = result.getString("length");
                                    System.out.println(user_count + ". " + "Video_id : " + vid + ", Title : " + vTitle + ", length : " + vlength);
                                    user_count++;
                                }
                                System.out.println("======================================");
                                break;
                            case 3: // view list of the videos uploaded by user
                                result = statement.executeQuery("Select * from user where Mgr_ssn = " + Mgrssn);
                                if(!result.next()){
                                    System.out.println("you are not managing any user");
                                    break;
                                }
                                result = statement.executeQuery("Select * from user where Mgr_ssn = " + Mgrssn);
                                System.out.println("======================================");
                                System.out.println("<List of user's ID you are managing>");
                                int temp_idx = 0;
                                while(result.next()){
                                    System.out.println(temp_idx + ". " + result.getString("ID"));
                                    temp_idx++;
                                }
                                System.out.println("======================================");
                                System.out.println("enter the ID of user you want to know");
                                String uid = scan.nextLine();
                                uid = "'" + uid + "'";
                                result = statement.executeQuery("Select * from video where uploader_id = " + uid);
                                if(!result.next()){
                                    System.out.println(uid + " didn't upload any video");
                                    break;
                                }
                                result = statement.executeQuery("Select * from video where uploader_id = " + uid);
                                System.out.println("------------------------------");
                                System.out.println("<list of videos " + uid + " uploaded>");
                                temp_stat = connection.createStatement();
                                while (result.next()) {
                                    String v_title = result.getString("Title");
                                    String v_length = result.getString("length");
                                    int v_id = result.getInt("video_id");
                                    v_idx = result.getInt("video_index");
                                    int num_of_view = result.getInt("view_count");
                                    ResultSet genre_names;
                                    genre_names = temp_stat.executeQuery("Select genre_name from genre_of, genre where vid_id = " + v_id + " AND gen_idx = genre_index");
                                    System.out.print(v_id + ". " + " title : " + v_title + ", length : " + v_length + ", view : " + num_of_view + ", genres : ");
                                    while (genre_names.next())
                                        System.out.print(genre_names.getString("genre_name") + ", ");
                                    System.out.println();
                                }
                                System.out.println("------------------------------");
                                break;
                            case 4:
                                loop = false;
                                break;
                        }
                    }
                }
                else if(cmd == 2) { //user mode
                    System.out.println("Please enter your id(Ex) 'jk6722')");
                    String uid = scan.nextLine();
                    uid = "'" + uid + "'";
                    String answer = "";
                    result = statement.executeQuery("Select Id, Password from user where Id = " + uid);
                    if (!result.next()) {
                        System.out.println("No such User...sorry");
                        continue;
                    }
                    else
                        answer = result.getString("Password");
                    System.out.println("please enter your password");
                    String upass = scan.nextLine();
                    if (!answer.equals(upass)) {
                        System.out.println("wrong password!");
                        continue;
                    }
                    System.out.println("Hello, " + uid + "!");
                    boolean loop = true;
                    while (loop) {
                        int temp = User();
                        switch (temp) {
                            case 0: // show video to user
                                result = statement.executeQuery("Select * from video");
                                if(!result.next()){
                                    System.out.println("There is no video now");
                                    break;
                                }
                                result = statement.executeQuery("Select * from video, user where uploader_id = ID order by video_index ASC");
                                update_stat = connection.createStatement();
                                temp_stat = connection.createStatement();
                                int v_idx = 0;
                                while (result.next()) {
                                    String v_title = result.getString("Title");
                                    int v_count = result.getInt("view_count");
                                    int v_id = result.getInt("video_id");
                                    String channel = result.getString("ChannelName");
                                    ResultSet genres = temp_stat.executeQuery("Select genre_name from genre_of, video, genre where video_id = vid_id AND gen_idx = genre_index AND vid_id = " + v_id);
                                    System.out.print(v_idx + ". Title : " + v_title + ", Channel : " + channel + ", view : " + v_count + " genre : ");
                                    while (genres.next()) {
                                        System.out.print(genres.getString("genre_name") + ", ");
                                    }
                                    System.out.println();
                                    update_stat.executeUpdate("update video set video_index = " + v_idx + " where video_id = " + v_id);
                                    v_idx++;
                                }
                                int is_continue = 0;
                                while (is_continue == 0) {
                                    System.out.println("What video do you want to see? tell me a number");
                                    int idx_to_watch = scan.nextInt();
                                    scan.nextLine();
                                    result = statement.executeQuery("Select video_id from video where video_index = " + idx_to_watch);
                                    if (!result.next()){
                                        System.out.println("wrong input!");
                                        continue;
                                    }
                                    result = statement.executeQuery("Select video_id from video where video_index = " + idx_to_watch);
                                    result.next();
                                    int v_id = result.getInt("video_id");
                                    update_stat.executeUpdate("update video set view_count = view_count + 1 where video_index = " + idx_to_watch);
                                    ResultSet genres = statement.executeQuery("Select gen_idx from genre_of where vid_id = " + v_id);
                                    while (genres.next()) {
                                        temp_set = temp_stat.executeQuery("Select * from watches_genre where user_id = " + uid + " AND gen_idx = " + genres.getInt("gen_idx"));
                                        if(temp_set.next())
                                            update_stat.executeUpdate("update watches_genre set gen_count = gen_count + 1 where user_id = " + uid + " AND gen_idx = "
                                                    + genres.getInt("gen_idx"));
                                        else update_stat.executeUpdate("Insert into watches_genre values(" + uid + "," + genres.getInt("gen_idx")+ "," + 1 + ")");
                                    }
                                    result = statement.executeQuery("Select * from watches where uid = " + uid +" AND vid = " + v_id);
                                    if(!result.next())
                                        update_stat.executeUpdate("Insert into watches(uid, vid) values (" + uid + "," + v_id + ")");
                                    //increase view count of the video
                                    System.out.println("You are watching video now...");
                                    try {
                                        Thread.sleep(1000); //1ì´??ê¸?
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println("Do you want to see more video?");
                                    System.out.println("0. yes");
                                    System.out.println("1. no");
                                    is_continue = scan.nextInt();
                                    scan.nextLine();
                                }
                                break;
                            case 1: //upload video
                                result = statement.executeQuery("Select * from user where Id = " + uid + " AND ChannelName IS NULL");
                                if(result.next()){
                                    System.out.println("you should determine your channel name first");
                                    while(true){
                                        System.out.println("enter your own channel name to make");
                                        String new_channel = scan.nextLine();
                                        new_channel = "'" + new_channel + "'";
                                        temp_set = statement.executeQuery("Select * from user where ChannelName = " + new_channel);
                                        if(!temp_set.next()){
                                            update_stat.executeUpdate("update user set ChannelName = " + new_channel + " where Id = " + uid);
                                            System.out.println("Channel " + new_channel + " is opened!");
                                            break;
                                        }
                                        else System.out.println("There is alreay same name of channel...try again");
                                    }
                                }
                                System.out.println("enter the title of your video(Don't use symbol ' for your title!)");
                                String new_title = scan.nextLine();
                                new_title = "'" + new_title + "'";
                                System.out.println("enter the length of your video(Ex) 1:25:05");
                                String new_time = scan.nextLine();
                                new_time = "'" + new_time + "'";
                                System.out.println("enter all genres of your video(Ex) 1, 3, 5");
                                String v_genre = check_genre();
                                String[] genres = v_genre.split(",");
                                result = statement.executeQuery("Select Max(video_id) from video");
                                int new_video_id = result.next() ? result.getInt(1) + 1 : 0;
                                result = statement.executeQuery("Select Max(video_index) from video");
                                int new_video_index = result.next() ? result.getInt(1) + 1 : 0;
                                statement.executeUpdate("Insert into video(Title,length,video_id,uploader_id,video_index) values ("
                                        + new_title + "," + new_time + "," + new_video_id + "," + uid + "," + new_video_index + ")");
                                for (String genre : genres)
                                    statement.executeUpdate("Insert into genre_of(vid_id, gen_idx) values(" + new_video_id + "," + Integer.parseInt(genre.trim()) + ")");
                                System.out.println("New video was uploaded.");
                                break;
                            case 2: //make playlist
                                System.out.println("enter the Name of your new playlist(Don't use ' symbol)");
                                String new_name = scan.nextLine();
                                new_name = "'" + new_name + "'";
                                result = statement.executeQuery("Select Listname from playlist where Master_id = " + uid + " AND Listname = " + new_name);
                                if (result.next()) {
                                    System.out.println("You already have the same name list");
                                    break;
                                }
                                System.out.println("Do you want to share your playlist to other people?");
                                System.out.println("0. yes");
                                System.out.println("1. no");
                                int Input = scan.nextInt();
                                scan.nextLine();
                                boolean is_share = (Input == 0);
                                result = statement.executeQuery("Select Max(list_index) from playlist");
                                int new_list_index = result.next() ? result.getInt(1) + 1 : 0;
                                statement.executeUpdate("Insert into playlist(Master_id, Listname, is_shared, list_index) values("
                                        + uid + "," + new_name + "," + is_share + "," + new_list_index + ")");
                                System.out.println("New playlist was created");
                                break;
                            case 3: //watch video in the play list
                                int list_idx = 0;
                                result = statement.executeQuery("Select Listname from playlist where Master_id = " + uid);
                                if(!result.next()){
                                    System.out.println("you don't have any playlist now");
                                    break;
                                }
                                result = statement.executeQuery("Select Listname from playlist where Master_id = " + uid);
                                System.out.println("===========================");
                                System.out.println("<your playlists>");
                                while(result.next()){
                                    String Lname = result.getString("Listname");
                                    System.out.println(list_idx + ". " + Lname);
                                    Lname = "'" + Lname + "'";
                                    update_stat.executeUpdate("update playlist set list_index = " + list_idx + " where Listname = " + Lname + " AND Master_id = " + uid);
                                    list_idx++;
                                }
                                System.out.println("===========================");
                                System.out.println("enter the number of list to enter");
                                list_idx = scan.nextInt();
                                scan.nextLine();
                                result = statement.executeQuery("Select vid_id, Listname from includes, playlist where list_index = " + list_idx + " AND Listname = playlist_name AND Master_id = " + uid);
                                if(!result.next()){
                                    System.out.println("sorry there is no video in this playlist");
                                    break;
                                }
                                String Lname_to_watch = result.getString("Listname");
                                Lname_to_watch = "'" + Lname_to_watch + "'";
                                result = statement.executeQuery("Select video_id, video_index, Title, length, includes_index from video, includes, playlist where Listname = " + Lname_to_watch +
                                        " AND playlist_master = Master_id AND Master_id = " + uid + " AND video_id = vid_id AND playlist_name = ListName ORDER BY includes_index ASC");
                                System.out.println("===========================");
                                System.out.println("<videos in your playlist>");
                                int inc_idx = 0;
                                while(result.next()){
                                    System.out.print(inc_idx + ". Title : " + result.getString("Title") + ", length : " + result.getString("length") +
                                            ", genre : ");
                                    int vid = result.getInt("video_id");
                                    temp_set = temp_stat.executeQuery("Select genre_name from genre_of, genre where vid_id = " + vid + " AND gen_idx = genre_index");
                                    while(temp_set.next()){
                                        System.out.print(temp_set.getString("genre_name") + ", ");
                                    }
                                    update_stat.executeUpdate("Update includes Set includes_index = " + inc_idx + " where playlist_master = " + uid +
                                            " AND playlist_name = " + Lname_to_watch + " AND vid_id = " + vid);
                                    inc_idx++;
                                    System.out.println();
                                }
                                System.out.println(inc_idx + ". quit");
                                System.out.println("===========================");
                                while(true) {
                                    System.out.println("enter the number of video to watch");
                                    int inc_idx_to_watch = scan.nextInt();
                                    scan.nextLine();
                                    if(inc_idx_to_watch == inc_idx)
                                        break;
                                    System.out.println("you are watching video now...");
                                    try {
                                        Thread.sleep(1000); //1ì´??ê¸?
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    result = statement.executeQuery("Select vid_id from includes where includes_index = " + inc_idx_to_watch + " AND playlist_master = " + uid +
                                            " AND playlist_name = " + Lname_to_watch);
                                    result.next();
                                    int v_id_to_watch = result.getInt("vid_id");
                                    result = statement.executeQuery("Select video_id, gen_idx from video, genre_of where video_id = " + v_id_to_watch + " AND vid_id = video_id");
                                    new_video_id = -1;
                                    update_stat.executeUpdate("Update video Set view_count = view_count + 1 where video_id = " + v_id_to_watch);
                                    while (result.next()) {
                                        temp_set = temp_stat.executeQuery("Select * from watches_genre where user_id = " + uid + " AND gen_idx = " + result.getInt("gen_idx"));
                                        if (temp_set.next())
                                            update_stat.executeUpdate("update watches_genre set gen_count = gen_count + 1 where user_id = " + uid + " AND gen_idx = "
                                                    + result.getInt("gen_idx"));
                                        else
                                            update_stat.executeUpdate("Insert into watches_genre values(" + uid + "," + result.getInt("gen_idx") + "," + 1 + ")");
                                        if (new_video_id == -1)
                                            new_video_id = result.getInt("video_id");
                                    }
                                    temp_set = temp_stat.executeQuery("select * from watches where uid = " + uid + " AND vid = " + new_video_id);
                                    if (!temp_set.next()) {
                                        update_stat.executeUpdate("Insert into watches values(" + uid + "," + new_video_id + ")");
                                    }
                                }
                                break;
                            case 4: //view list
                                result = statement.executeQuery("Select Listname from playlist where Master_id = " + uid);
                                if(!result.next()){
                                    System.out.println("you don't have any playlist now...");
                                    System.out.println("how about making your own playlist?");
                                    break;
                                }
                                result = statement.executeQuery("Select * from playlist where Master_id = " + uid);
                                list_idx = 0;
                                update_stat = connection.createStatement();
                                while (result.next()) {
                                System.out.println("========================");
                                    String name_of_list = result.getString("Listname");
                                    System.out.println(list_idx + ". " + name_of_list);
                                    name_of_list = "'" + name_of_list + "'";
                                    update_stat.executeUpdate("update playlist set list_index = " + list_idx + " where Master_id = " + uid + " AND Listname = " + name_of_list);
                                    list_idx++;
                                }
                                System.out.println("========================");
                                break;
                            case 5: //delete list
                                System.out.println("========================");
                                result = statement.executeQuery("Select Listname from playlist where Master_id = " + uid);
                                if(!result.next()){
                                    System.out.println("there's no list to delete");
                                    break;
                                }
                                result = statement.executeQuery("Select * from playlist where Master_id = " + uid);
                                list_idx = 0;
                                update_stat = connection.createStatement();
                                while (result.next()) {
                                    list_idx = result.getInt("list_index");
                                    String name_of_list = result.getString("Listname");
                                    System.out.println(list_idx + ". " + name_of_list);
                                    name_of_list = "'" + name_of_list + "'";
                                    update_stat.executeUpdate("update playlist set list_index = " + list_idx + " where Listname = " + name_of_list + " AND Master_id = " + uid);
                                    list_idx++;
                                }
                                System.out.println("========================");
                                System.out.println("enter number of the list you want to delete");
                                int list_to_delete = scan.nextInt();
                                scan.nextLine();
                                result = statement.executeQuery("Select Listname from playlist where list_index = " + list_to_delete + " AND Master_id = " + uid);
                                if (!result.next()) {
                                    System.out.println("No such playlist!");
                                    break;
                                }
                                String name_to_delete = result.getString("Listname");
                                name_to_delete = "'" + name_to_delete + "'";
                                statement.executeUpdate("Delete from includes where playlist_master = " + uid + " AND playlist_name = " + name_to_delete);
                                statement.executeUpdate("Delete from playlist where Master_id = " + uid + " AND list_index = " + list_to_delete);
                                System.out.println("That playlist was deleted from your playlists");
                                break;
                            case 6: //delete or add video from,to playlist
                                //view the list to user first
                                result = statement.executeQuery("Select Listname from playlist where Master_id = " + uid);
                                if(!result.next()){
                                    System.out.println("you don't have playlist now. please make playlist first!");
                                    break;
                                }
                                result = statement.executeQuery("Select * from playlist where Master_id = " + uid);
                                System.out.println("========================");
                                list_idx = 0;
                                update_stat = connection.createStatement();
                                while (result.next()) {
                                    String name_of_list = result.getString("Listname");
                                    System.out.println(list_idx + ". " + name_of_list);
                                    name_of_list = "'" + name_of_list + "'";
                                    update_stat.executeUpdate("update playlist set list_index = " + list_idx + " where Listname = " + name_of_list + " AND Master_id = " + uid);
                                    list_idx++;
                                }
                                System.out.println("========================");
                                System.out.println("enter number of list you want to modify");
                                int idx_to_modify = scan.nextInt(); //index of the playlist to modify
                                scan.nextLine();
                                result = statement.executeQuery("Select Listname from playlist where Master_id = " + uid + " AND list_index = " + idx_to_modify);
                                result.next(); //move cursor to first point
                                String name_to_modify = result.getString("Listname");
                                System.out.println("========================");
                                System.out.println("<List of the video in " + name_to_modify + ">");
                                name_to_modify = "'" + name_to_modify + "'";
                                result = statement.executeQuery("Select Title, length, includes_index, video_id from includes, video where video_id = vid_id"
                                        + " AND playlist_master = " + uid + " AND playlist_name = " + name_to_modify + " ORDER BY includes_index ASC");
                                //table that user want to modify
                                inc_idx = 0;
                                while (result.next()) {
                                    String v_title = result.getString("Title");
                                    String v_length = result.getString("length");
                                    System.out.println(inc_idx + ". Title : " + v_title + ", length : " + v_length);
                                    update_stat.executeUpdate("Update includes Set includes_index = " + inc_idx + " Where playlist_master = " + uid +
                                            " AND playlist_name = " + name_to_modify + " AND vid_id = " + result.getInt("video_id"));
                                    inc_idx++;
                                }
                                System.out.println("========================");
                                System.out.println("0. add video to playlist");
                                System.out.println("1. delete video from playlist");
                                int add_or_delete = scan.nextInt();
                                scan.nextLine();
                                if (add_or_delete == 0) { // add
                                    //show all videos
                                    result = statement.executeQuery("Select * from video");
                                    System.out.println("--------------------------");
                                    temp_stat = connection.createStatement();
                                    while (result.next()) {
                                        String v_title = result.getString("Title");
                                        String v_length = result.getString("length");
                                        int v_id = result.getInt("video_id");
                                        v_idx = result.getInt("video_index");
                                        int num_of_view = result.getInt("view_count");
                                        ResultSet genre_names;
                                        genre_names = temp_stat.executeQuery("Select genre_name from genre_of, genre where vid_id = " + v_id + " AND gen_idx = genre_index");
                                        System.out.print(v_idx + ". " + " title : " + v_title + ", length : " + v_length + ", view : " + num_of_view + ", genres : ");
                                        while (genre_names.next())
                                            System.out.print(genre_names.getString("genre_name") + ", ");
                                        System.out.println();
                                    }
                                    System.out.println("--------------------------");
                                    System.out.println("enter number of the video to add");
                                    int idx_to_add = scan.nextInt(); // index of the video to add
                                    scan.nextLine();
                                    result = statement.executeQuery("Select video_id from video where video_index = " + idx_to_add);
                                    result.next();
                                    int v_to_add = result.getInt("video_id");
                                    temp_set = temp_stat.executeQuery("Select vid_id from includes where playlist_master = " + uid + " AND playlist_name = " + name_to_modify +
                                            " AND vid_id = " + v_to_add);
                                    if(temp_set.next()){
                                        System.out.println("there is a same video already");
                                        continue;
                                    }
                                    temp_set = temp_stat.executeQuery("Select Max(includes_index) from includes");
                                    int includes_idx = temp_set.next() ? temp_set.getInt(1) + 1 : 0;
                                    statement.executeUpdate("Insert into includes(playlist_master, playlist_name, vid_id, includes_index) values(" +
                                            uid + "," + name_to_modify + "," + v_to_add + "," + includes_idx + ")");
                                    System.out.println("New video was inserted into your playlist");
                                } else { //delete
                                    System.out.println("enter number of the video to delete");
                                    int idx_to_delete = scan.nextInt();
                                    scan.nextLine();
                                    result = statement.executeQuery("Select vid_id from includes where playlist_master = " + uid +
                                            " AND playlist_name = " + name_to_modify + " AND includes_index = " + idx_to_delete);
                                    result.next();
                                    int vid_to_delete = result.getInt("vid_id");
                                    statement.executeUpdate("Delete from includes where playlist_master = " + uid + " AND playlist_name = " + name_to_modify + " AND vid_id = " + vid_to_delete);
                                    System.out.println("video was deleted from your playlist");
                                }
                                break;
                            case 7: //delete video uploaded
                                result = statement.executeQuery("Select * from video where uploader_id = " + uid);
                                if(!result.next()){
                                    System.out.println("you didn't upload any video");
                                    break;
                                }
                                result = statement.executeQuery("Select * from video where uploader_id = " + uid);
                                System.out.println("--------------------------");
                                System.out.println("<list of videos you uploaded>");
                                temp_stat = connection.createStatement();
                                while (result.next()) {
                                    String v_title = result.getString("Title");
                                    String v_length = result.getString("length");
                                    int v_id = result.getInt("video_id");
                                    v_idx = result.getInt("video_index");
                                    int num_of_view = result.getInt("view_count");
                                    ResultSet genre_names;
                                    genre_names = temp_stat.executeQuery("Select genre_name from genre_of, genre where vid_id = " + v_id + " AND gen_idx = genre_index");
                                    System.out.print(v_id + ". " + " title : " + v_title + ", length : " + v_length + ", view : " + num_of_view + ", genres : ");
                                    while (genre_names.next())
                                        System.out.print(genre_names.getString("genre_name") + ", ");
                                    System.out.println();
                                }
                                System.out.println("--------------------------");
                                System.out.println("enter the number of video to delete");
                                int vid_to_delete = scan.nextInt();
                                scan.nextLine();
                                update_stat.executeUpdate("Delete from genre_of where vid_id = " + vid_to_delete);
                                update_stat.executeUpdate("Delete from watches where vid = " + vid_to_delete);
                                update_stat.executeUpdate("Delete from includes where vid_id = " + vid_to_delete);
                                update_stat.executeUpdate("Delete from video where video_id = " + vid_to_delete);
                                break;
                            case 8: //show ranking of genre user watched
                                result = statement.executeQuery("Select genre_index, genre_name, gen_count from watches_genre, genre where user_id = " + uid + " AND gen_idx = genre_index" + " order by gen_count DESC");
                                String rank_str[] = {"1st", "2nd", "3rd", "4th", "5th", "6th", "7th"};
                                int rank_idx = 0;
                                while(result.next()) {
                                    System.out.println(rank_str[rank_idx] + ". " + result.getString("genre_name") + " " + result.getString("gen_count") + " times.");
                                    rank_idx++;
                                }
                                break;
                            case 9:
                                loop = false;
                                break;
                        }
                    }
                }
                else if(cmd == 3){ //make new id (user)
                    System.out.println("--------------------------");
                    String new_id = "";
                    while(true) {
                        System.out.println("enter your new id");
                        new_id = scan.nextLine();
                        new_id = "'" + new_id + "'";
                        result = statement.executeQuery("Select Id from user where Id = " + new_id);
                        if(!result.next()) break;
                        System.out.println("Another user is using that ID!!");
                        System.out.println("Please enter again.");
                    }
                    System.out.println("enter your password");
                    String new_pass = scan.nextLine();
                    new_pass = "'" + new_pass + "'";
                    String new_channel = "";
                    System.out.println("enter your new channel name. If you don't want to make channel just press enter");
                    new_channel = scan.nextLine();
                    if(new_channel.equals(""))
                        new_channel = null;
                    else new_channel = "'" + new_channel + "'";
                    System.out.println("enter your age");
                    int new_age = scan.nextInt();
                    scan.nextLine();
                    System.out.println("enter your sex(F or M)");
                    String new_sex = scan.nextLine();
                    new_sex = "'" + new_sex + "'";
                    System.out.println("enter your Address(Ex) Seoul)");
                    String new_address = scan.nextLine();
                    new_address = "'" + new_address + "'";
                    System.out.println("enter your Email(Ex) \"jk6722@naver.com\" )");
                    String new_email = scan.nextLine();
                    new_email = "'" + new_email + "'";
                    System.out.println("enter your phone number(Ex)01012345678)");
                    String new_phone_number = scan.nextLine();
                    new_phone_number = "'" + new_phone_number + "'";
                    int new_mgr = 12345;
                    statement.executeUpdate("Insert into user values(" + new_id + "," + new_pass + "," + new_channel + "," + new_age + "," + new_sex + "," + new_address
                    + ", " + new_email + "," + new_phone_number + "," + new_mgr + ")");
                    System.out.println("Congratulate to join us!");
                    System.out.println("You can login with your new id");
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
