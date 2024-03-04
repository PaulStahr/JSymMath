package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class StreamUtil {
    public static final String readStreamToString(InputStream stream) throws IOException
    {
        InputStreamReader reader = new InputStreamReader(stream);
        char ch[] = new char[4096];
        int len = 0;
        int read = 0;
        while ((read = reader.read(ch, len, ch.length - len))!=-1)
        {
            len += read;
            if (ch.length - len == 0)
            {
                ch = Arrays.copyOf(ch, ch.length * 2);
            }
        }
        reader.close();
        return new String(ch, 0, len);
    }
}

