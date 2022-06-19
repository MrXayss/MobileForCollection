package com.example.compascoord;

import android.app.Notification;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


class UploadFileAsync extends AsyncTask<String, Void, String> {
    private Notification.Action.Builder intent;
    public String filename;
    public  String gradus;
    public double latitude, longtitude;
    public JSONObject json;
    public String id_device;
    public String text_signal;
    public String text_loc;
    public void addFormField(BufferedWriter dos, String parameter, String value){
        try {
            String twoHyphens = "--";
            String boundary = "*****";
            String lineEnd = "\r\n";
            dos.write(twoHyphens + boundary + lineEnd);
            dos.write("Content-Disposition: form-data; name=\""+parameter+"\"" + lineEnd);
            dos.write("Content-Type: text/plain; charset=UTF-8" + lineEnd);
            dos.write(lineEnd);
            dos.write(value + lineEnd);
            dos.flush();
        }
        catch(Exception e){

        }
    }
    @Override
    protected String doInBackground(String... params) {

        try {
            Log.i("file!", String.valueOf(filename));
            String sourceFileUri = filename;
            String name1 = gradus;
            double name2 = latitude;
            double name3 = longtitude;
            String name4 = id_device;
            String name5 = text_signal;
            String name6 = text_loc;
            JSONObject post_dict = json;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (sourceFile.isFile()) {

                try {

                    String upLoadServerUri = "https://signal.vita-control.ru/api/test_upload";
//                    String upLoadServerUri = "http://192.168.1.84:8000/api/test_upload";
                    FileInputStream fileInputStream = new FileInputStream(
                            sourceFile);
                    URL url = new URL(upLoadServerUri);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE",
                            "multipart/form-data");
                    conn.setRequestProperty("Content-Type",
                            "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("bill", sourceFileUri);

                    dos = new DataOutputStream(conn.getOutputStream());
                    BufferedWriter outputStream2 = new BufferedWriter(new OutputStreamWriter(dos, "UTF-8"));
                    addFormField(outputStream2, "gradus", name1);
                    addFormField(outputStream2, "latitude", Double.toString(name2));
                    addFormField(outputStream2, "longtitude", Double.toString(name3));
                    addFormField(outputStream2, "json", post_dict.toString());
                    addFormField(outputStream2, "id_device", name4);
                    addFormField(outputStream2, "text_signal", name5);
                    addFormField(outputStream2, "text_loc", name6);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"record\";filename=\""
                            + sourceFileUri + "\""+ lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);

                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    Log.i("123", String.valueOf(in.read()));

                    conn.disconnect();
                } catch (Exception e) {

                    // dialog.dismiss();
                    e.printStackTrace();

                }

            } // End else block


        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return "Executed";

    }

}
