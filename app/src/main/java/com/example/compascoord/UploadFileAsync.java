package com.example.compascoord;

import android.app.Notification;
import android.os.AsyncTask;
import android.util.Log;

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

            String sourceFileUri = filename;
            String name1 = gradus;
            double name2 = latitude;
            double name3 = longtitude;

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
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(
                            sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP connection to the URL
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

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"record\";filename=\""
                            + sourceFileUri + "\""+ lineEnd);
                    dos.writeBytes(lineEnd);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math
                                .min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0,
                                bufferSize);

                    }

                    // send multipart form data necesssary after file
                    // data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens
                            + lineEnd);

                    // close the streams //
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
                // dialog.dismiss();

            } // End else block


        } catch (Exception ex) {
            // dialog.dismiss();

            ex.printStackTrace();
        }
        return "Executed";

    }

}