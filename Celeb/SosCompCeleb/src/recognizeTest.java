import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.net.ssl.SSLException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class recognizeTest {
    
    public double runAPI(File x, File y) throws Exception{
        
        Gson gson = new Gson();
        File file = x;
        File file2 = y;
        byte[] buff = getBytesFromFile(file);
        byte[] buff2 = getBytesFromFile(file2);
        String url = "https://api-us.faceplusplus.com/facepp/v3/compare";
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, byte[]> byteMap = new HashMap<>();
        map.put("api_key", "BK-Nzli0d-vrQVT-4kaoVReUjsT1HADj");
        map.put("api_secret", "3L0FBsNvB-qXVicOCbBpcc0MmLS6NVB_");
        byteMap.put("image_file1", buff);
        byteMap.put("image_file2", buff2);
        try{
            byte[] bacd = post(url, map, byteMap);
            String str = new String(bacd);
            Map<String,Double> myMap = new HashMap<String,Double>();
            myMap = (Map<String,Double>) gson.fromJson(str, map.getClass());
            System.out.println(myMap.get("confidence") + "...");
            return myMap.get("confidence");
        }catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    private final static int CONNECT_TIME_OUT = 30000;
    private final static int READ_OUT_TIME = 50000;
    private static String boundaryString = getBoundary();
    protected static byte[] post(String url, HashMap<String, String> map, HashMap<String, byte[]> fileMap) throws Exception {
        HttpURLConnection conne;
        URL url1 = new URL(url);
        conne = (HttpURLConnection) url1.openConnection();
        conne.setDoOutput(true);
        conne.setUseCaches(false);
        conne.setRequestMethod("POST");
        conne.setConnectTimeout(CONNECT_TIME_OUT);
        conne.setReadTimeout(READ_OUT_TIME);
        conne.setRequestProperty("accept", "*/*");
        conne.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundaryString);
        conne.setRequestProperty("connection", "Keep-Alive");
        conne.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
        DataOutputStream obos = new DataOutputStream(conne.getOutputStream());
        Iterator iter = map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            obos.writeBytes("--" + boundaryString + "\r\n");
            obos.writeBytes("Content-Disposition: form-data; name=\"" + key
                    + "\"\r\n");
            obos.writeBytes("\r\n");
            obos.writeBytes(value + "\r\n");
        }
        if(fileMap != null && fileMap.size() > 0){
            Iterator fileIter = fileMap.entrySet().iterator();
            while(fileIter.hasNext()){
                Map.Entry<String, byte[]> fileEntry = (Map.Entry<String, byte[]>) fileIter.next();
                obos.writeBytes("--" + boundaryString + "\r\n");
                obos.writeBytes("Content-Disposition: form-data; name=\"" + fileEntry.getKey()
                        + "\"; filename=\"" + encode(" ") + "\"\r\n");
                obos.writeBytes("\r\n");
                obos.write(fileEntry.getValue());
                obos.writeBytes("\r\n");
            }
        }
        obos.writeBytes("--" + boundaryString + "--" + "\r\n");
        obos.writeBytes("\r\n");
        obos.flush();
        obos.close();
        InputStream ins = null;
        int code = conne.getResponseCode();
        try{
            if(code == 200){
                ins = conne.getInputStream();
            }else{
                ins = conne.getErrorStream();
            }
        }catch (SSLException e){
            e.printStackTrace();
            return new byte[0];
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int len;
        while((len = ins.read(buff)) != -1){
            baos.write(buff, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        ins.close();
        return bytes;
    }
    private static String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }
        return sb.toString();
    }
    private static String encode(String value) throws Exception{
        return URLEncoder.encode(value, "UTF-8");
    }
    
    public static byte[] getBytesFromFile(File f) {
        if (f == null) {
            return null;
        }
        try {
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }
    
    public void displayImage(File URL, File User, String name) throws IOException {

        JFrame frame=new JFrame();

        Image first = ImageIO.read(URL);
        Image img = first.getScaledInstance(500, 500,  java.awt.Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(img);
        JLabel imageLabel = new JLabel(image);
        imageLabel.setBounds(10, 10, 350, 400);
        imageLabel.setVisible(true);
        frame.setLayout(new FlowLayout());
        frame.add(imageLabel);
 
        Image second = ImageIO.read(User);
        Image img2 = second.getScaledInstance(500, 500,  java.awt.Image.SCALE_SMOOTH);
        ImageIcon image2 = new ImageIcon(img2);
        JLabel imageLabel2 = new JLabel(image2);
        imageLabel2.setBounds(40, 10, 350, 400);
        imageLabel2.setVisible(true);
        frame.setLayout(new FlowLayout());
        frame.add(imageLabel2);

        JLabel label1 = new JLabel(name);
        frame.add(label1);
        frame.setExtendedState( frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }    
}
