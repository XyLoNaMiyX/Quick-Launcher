package tk.lonamiwebs.QuickLauncher;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Lonami on 16/12/2014.
 */
public class UserDetail {

    String username;
    String password;

    ArrayList<String> allowedPackagenames;

    //region Constructors

    UserDetail(String username) {
        this.username = username;
        password = "";
        allowedPackagenames = new ArrayList<>();
    }

    UserDetail(String username, String password) {
        this.username = username;
        this.password = password;
        allowedPackagenames = new ArrayList<>();
    }

    UserDetail(String username, String password, ArrayList<String> allowedPackagenames) {
        this.username = username;
        this.password = password;
        this.allowedPackagenames = allowedPackagenames;
    }

    //endregion

    String parse() {
        String username = this.username.replace(":", "&S;");
        String password = this.password.replace(":", "&S;");

        String packages = "";
        for (String pckage : allowedPackagenames)
            packages += pckage + " ";

        return username + ":" + password + ":" + packages.trim();
    }

    static UserDetail unparse(String parsedUser) {
        String[] split = parsedUser.split(":");

        String username = split[0];

        ArrayList<String> allowedPackagenames = new ArrayList<>();

        String password = "";
        if (split.length > 1) // no password
            password = split[1];

        if (split.length > 2)
            allowedPackagenames = new ArrayList<String>(Arrays.asList(split[2].split(" ")));

        return new UserDetail(username, password, allowedPackagenames);
    }
}
