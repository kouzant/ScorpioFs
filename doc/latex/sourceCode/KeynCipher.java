SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
KeySpec spec=new PBEKeySpec(Constants.password,salt,1024,128);
SecretKey tmp=factory.generateSecret(spec);
SecretKey secret = new SecretKeySpec(tmp.getEncoded(),"AES");

Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
