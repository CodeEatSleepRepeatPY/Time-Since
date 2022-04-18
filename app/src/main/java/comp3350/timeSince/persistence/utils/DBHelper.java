package comp3350.timeSince.persistence.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import comp3350.timeSince.application.Main;

/**
 * Taken (and slightly modified) from the sample project, cook-ebook 2, and team rocket 15
 */
public class DBHelper {

    public static void copyDatabaseToDevice(Context context, String path) {

        String[] assetNames;
        File dataDirectory = context.getDir(path, Context.MODE_PRIVATE);
        AssetManager assetManager = context.getAssets();

        try {

            assetNames = assetManager.list(path);
            for (int i = 0; i < assetNames.length; i++) {
                assetNames[i] = path + "/" + assetNames[i];
            }

            copyAssetsToDirectory(context, assetNames, dataDirectory);

            Main.setDBPathName(dataDirectory.toString() + "/" + Main.getDBPathName());

        } catch (final IOException ioe) {
            System.out.println("Unable to access application data: " + ioe.getMessage());
        }

    }

    private static void copyAssetsToDirectory(Context context, String[] assets, File directory) throws IOException {
        AssetManager assetManager = context.getAssets();

        for (String asset : assets) {
            String[] components = asset.split("/");
            String copyPath = directory.toString() + "/" + components[components.length - 1];

            char[] buffer = new char[1024];
            int count;

            File outFile = new File(copyPath);

            if (!outFile.exists()) {
                InputStreamReader in = new InputStreamReader(assetManager.open(asset));
                FileWriter out = new FileWriter(outFile);

                count = in.read(buffer);
                while (count != -1) {
                    out.write(buffer, 0, count);
                    count = in.read(buffer);
                }

                out.close();
                in.close();
            }
        }
    }

}
