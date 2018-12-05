package core;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

@SuppressWarnings("InfiniteLoopStatement")
public class App {

    private static String username;

    public static void main(String[] args) throws Exception {

        Socket socket = IO.socket("http://104.248.21.85:8000");
        setupSocket(socket);

        Scanner scn = new Scanner(System.in);

        System.out.print("Enter your username: ");
        username = scn.nextLine();
        socket.emit("new user", username);

        new Thread(() -> {
            String message;
            while (true) {
                if (scn.hasNextLine()) {
                    message = scn.nextLine();
                    socket.emit("send message", message);
                }
            }
        }).start();
    }

    private static void setupSocket(Socket socket) {
        socket.on(Socket.EVENT_CONNECT, args -> {
            System.out.println("Connected to server");
        }).on("new message", App::onNewMessage)
          .on(Socket.EVENT_DISCONNECT, args -> {
            System.out.println("Disconnected from server");
        });
        socket.connect();
    }

    private static void onNewMessage(Object...args) {
        try {
            JSONObject data = (JSONObject) args[0];
            String user = data.getString("user");
            String message = data.getString("msg");
            try {
                if (!user.equals(username))
                    System.out.println(user + ": " + message);
            } catch (Exception ex) {
                System.out.println("unknown: " + message);
            }
        } catch (JSONException e) {
            System.out.println("JSON Error");
        }
    }
}
