package sonata.kernel.placement;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import sonata.kernel.placement.pd.PackageContentObject;
import sonata.kernel.placement.pd.PackageDescriptor;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class PackageLoader {

    public static void processZipFile(ByteArrayOutputStream byteOutputStream) throws IOException {

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOutputStream.toByteArray());

        System.out.println("Start zip stuff");

        ZipInputStream zipstream;
        zipstream = new ZipInputStream(byteIn);
        ZipEntry ze = zipstream.getNextEntry();

        // Extract files to byte[] and save them in fileMap
        Map<String, byte[]> fileMap = new HashMap<String, byte[]>();
        while (ze != null) {

            if(ze.isDirectory()!=true) {

                byte[] fileData = readFile(zipstream, ze);
                String filePath = ze.getName();
                fileMap.put(filePath, fileData);

            }

            System.out.println(ze.getName());

            ze = zipstream.getNextEntry();
        }
        zipstream.closeEntry();
        zipstream.close();

        System.out.println("End zip stuff");

        Set<String> files = fileMap.keySet();

        if (files.contains("META-INF/MANIFEST.MF")) {

            PackageDescriptor pd;

            byte[] pfile = fileMap.get("META-INF/MANIFEST.MF");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            SimpleModule module = new SimpleModule();
            //module.addDeserializer(Unit.class, new UnitDeserializer());
            mapper.registerModule(module);
            mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

            StringBuilder bodyBuilder = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(pfile), Charset.forName("UTF-8")));
            String line;
            while ((line = in.readLine()) != null)
                bodyBuilder.append(line + "\n\r");

            pd = mapper.readValue(bodyBuilder.toString(), PackageDescriptor.class);

            List<byte[]> services = new ArrayList<byte[]>();
            List<byte[]> functions = new ArrayList<byte[]>();

            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


            for(PackageContentObject pObj:pd.getPackageContent()){

                String name;
                name = pObj.getName();

                if (name==null)
                    continue;

                // Remove leading "/" from file path
                if (name.charAt(0)=='/')
                    name = name.substring(1);

                System.out.println("Check out content file: "+name);

                // Get file data from map
                byte[] fileData;
                fileData = fileMap.get(name);
                if (fileData==null) {
                    System.out.println("No file data found for: "+name);
                    continue;
                }

                // Check MD5
                if (md5!=null) {
                    byte[] digest = md5.digest(fileData);

                    digest.toString();
                    BigInteger bigInt = new BigInteger(1,digest);
                    String hashtext = bigInt.toString(16);
                    // Now we need to zero pad it if you actually want the full 32 chars.
                    while(hashtext.length() < 32 ){
                        hashtext = "0"+hashtext;
                    }
                    String fileMd5 = pObj.getMd5();
                    if (fileMd5!=null) {
                        if (hashtext.toLowerCase().equals(fileMd5.toLowerCase()) == false) {
                            System.out.println("MD5 mismatch for file "+name+" (given md5: "+fileMd5.toLowerCase()+", actual md5: "+hashtext.toLowerCase()+")");
                        } else {
                            System.out.println("MD5 matches "+hashtext.toLowerCase());
                        }
                    }
                }

                System.out.println("ContentType: "+pObj.getContentType());

                // It's a service descriptor
                if ("application/sonata.service_descriptors".equals(pObj.getContentType())) {
                    services.add(fileData);
                    System.out.println("Found service descriptor: "+name);
                }

                // It's a function descriptor
                if ("application/sonata.function_descriptor".equals(pObj.getContentType())) {
                    functions.add(fileData);
                    System.out.println("Found function descriptor: "+name);
                }
            }
        }
    }

    public static byte[] readFile(ZipInputStream zipstream, ZipEntry ze) throws IOException {
        long size = ze.getSize();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[2048];
        int read = 0;
        while(size>0) {
            read = zipstream.read(buf, 0, 2048);
            outputStream.write(buf, 0, read);
            size -= read;
        }
        return outputStream.toByteArray();
    }

}
