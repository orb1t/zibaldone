/*
 * Copyright Samuel Halliday 2008
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.utils;

import com.google.common.io.Closeables;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

/**
 * Class with static helper methods to reduce code boilerplate when dealing
 * with I/O. All methods throw {@link NullPointerException} if passed a {@code null}
 * parameter unless otherwise specified.
 * <p>
 * NOTE: Before writing anything into this file, check common libraries such as
 * <a href="http://code.google.com/p/guava-libraries/">Guava</a>
 * and
 * <a href="http://hc.apache.org/">HttpComponents</a>
 * for industry standard solutions.
 * 
 * @author Samuel Halliday
 */
@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IOUtils {

    /**
     * @param dir
     * @param filter which will only be applied to files, not to directories. May be null to indicate
     * no filter is to be used, although {@link #fileListing(File)} is preferable in such cases.
     * @return the files encountered under this directory, recursively. Does not include directories
     * and does not handle infinite loops in symbolic links or shortcuts.
     * @throws FileNotFoundException
     * @throws IllegalArgumentException if a directory cannot be read or the input is not a directory.
     */
    public static List<File> fileListing(File dir, @Nullable FileFilter filter) throws FileNotFoundException {
        Preconditions.checkNotNull(dir);
        Preconditions.checkArgument(dir.isDirectory(), dir + " not a directory");
        if (!dir.exists()) {
            throw new FileNotFoundException(dir.toString());
        }
        Preconditions.checkArgument(dir.canRead(), dir + " cannot be read");
        List<File> result = Lists.newArrayList();
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                result.addAll(fileListing(file, filter));
            } else if (filter == null || filter.accept(file)) {
                result.add(file);
            }
        }
        return result;
    }

    /**
     * @param dir
     * @return the files encountered under this directory, recursively. Does not include directories
     * and does not handle infinite loops in symbolic links or shortcuts.
     * @throws FileNotFoundException
     * @throws IllegalArgumentException if a directory cannot be read or the input is not a directory.
     */
    public static List<File> fileListing(File dir) throws FileNotFoundException {
        return fileListing(dir, null);
    }

    /**
     * Converts a stream to a String, converting end-of-line markers into "{@code \n}"
     * and closes the input after reading.
     * Be warned that some streams (e.g. from Sockets) will not terminate, making this
     * method hang forever.
     *
     * @param stream
     * @param charsetName
     * @return
     * @throws IOException
     */
    public static String streamToString(InputStream stream, String charsetName) throws IOException {
        Preconditions.checkNotNull(stream);
        InputStreamReader reader = new InputStreamReader(stream, charsetName);
        @Cleanup(quietly = true) BufferedReader buffered = new BufferedReader(reader);
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = buffered.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * Convert a String to an InputStream, for help with some APIs that only work with streams.
     *
     * @param string
     * @return
     */
    public static InputStream stringToStream(String string) {
        Preconditions.checkNotNull(string);
        try {
            return new ByteArrayInputStream(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new GuruMeditationFailure(ex);
        }
    }
    // finds the charset in an (X)HTML page

    private static final Pattern xhtmlCharsetPattern = Pattern.compile(
            "<meta\\s+http-equiv=['\"]content-type['\"]\\s+content=['\"].*charset=(.*)['\"]\\s*/?>",
            Pattern.CASE_INSENSITIVE);

    /**
     * "Fast forward" a reader until the input matches the given literal string pattern. The next
     * byte that can be read will be the one immediately proceeding the given pattern.
     *
     * @param reader
     * @param pattern a literal string.
     * @throws IOException
     */
    public static void ffwd(Reader reader, String pattern) throws IOException {
        Preconditions.checkNotNull(pattern);
        int i;
        int matching = 0;
        while ((matching < pattern.length()) && ((i = reader.read()) != -1)) {
            char c = (char) i;
            if (pattern.charAt(matching) == c) {
                matching++;
            } else {
                matching = 0;
            }
        }
    }

    /**
     * @param text
     * @return a URL encoded version of this string, using the unicode character set.
     * @see URLEncoder
     */
    public static String urlEncode(String text) {
        Preconditions.checkNotNull(text);
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new GuruMeditationFailure(ex);
        }
    }

    /**
     * @param text in URL encoding
     * @return an unencoded (i.e. UTF-8) version of the input.
     */
    public static String urlDecode(String text) {
        Preconditions.checkNotNull(text);
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new GuruMeditationFailure(ex);
        }
    }

    /**
     * Convert a garbled ISO-8859-1 string to a unicode String.
     * ISO-8859-1 is the default HTTP charset when the charset
     * is missing, see section 3.4.1 Missing Charset of the HTTP Specification.
     *
     * @param iso
     * @return
     * @see <a href="http://wiki.apache.org/tomcat/Tomcat/UTF-8">For inspiration and an alternative</a>
     * @see <a href="http://www.ietf.org/rfc/rfc2616.txt">HTTP Specification</a>
     */
    public static String isoToString(@Nullable String iso) {
        if (iso == null || iso.length() == 0) {
            return iso;
        }
        try {
            byte[] stringBytesISO = iso.getBytes("ISO-8859-1");
            return new String(stringBytesISO, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new GuruMeditationFailure(e);
        }
    }
}
