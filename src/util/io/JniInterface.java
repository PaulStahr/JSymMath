package util.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JniInterface {
    private static final String resourceFolder = "/resources/";

    public static final InputStream getResourceAsStream(String file)
    {
        return JniInterface.class.getResourceAsStream(getResourceFolder().concat(file));
    }

    public static final String getResourceFolder(){return resourceFolder;}

    public static void loadLib(String file) throws IOException {
        InputStream in = JniInterface.getResourceAsStream(file);
        String name = file.substring(Math.max(0,file.lastIndexOf('/')));
        File fileOut = new File(System.getProperty("java.io.tmpdir") +  '/' + name);
        OutputStream out = new FileOutputStream(fileOut);
        IOUtil.copy(in, out);
        in.close();
        out.close();
        System.load(fileOut.toString());
        fileOut.deleteOnExit();
    }
}
