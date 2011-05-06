cipher.init(Cipher.ENCRYPT_MODE,secret);
AlgorithmParameters params=cipher.getParameters();
byte[] iv=params.getParameterSpec(IvParameterSpec.class).getIV();

File saltFile=new File(Constants.personalDir+"/.salt");
FileOutputStream saFos=new FileOutputStream(saltFile);
saFos.write(salt);
saFos.flush();
saFos.close();
            
File ivFile=new File(Constants.personalDir+"/.iv");
FileOutputStream ivFos=new FileOutputStream(ivFile);
ivFos.write(iv);
ivFos.flush();
ivFos.close();

while((bytesRead=fis.read(buffer))!=-1){
    byte[] output=cipher.update(buffer,0,bytesRead);
    if(output!=null)
        fos.write(output);
}
byte[] output=cipher.doFinal();
if(output!=null)
    fos.write(output);
fos.flush();
