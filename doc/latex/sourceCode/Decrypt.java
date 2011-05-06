File saltFile=new File(Constants.personalDir+"/.salt");
FileInputStream saFis=new FileInputStream(saltFile);
saFis.read(salt);
saFis.close();

File ivFile=new File(Constants.personalDir+"/.iv");
FileInputStream ivFis=new FileInputStream(ivFile);
ivFis.read(iv);
ivFis.close();

cipher.init(Cipher.DECRYPT_MODE,secret,new IvParameterSpec(iv));

while((bytesRead=fis.read(buffer))!=-1){
    byte[] output=cipher.update(buffer,0,bytesRead);
    if(output!=null)
        fos.write(output);
}
byte[] output=cipher.doFinal();
if(output!=null)
    fos.write(output);
fos.flush();
fis.close();
fos.close();
