package src.data.utils;

import java.nio.ByteBuffer;


public class GladiatorSociety_test {
    public static void main(String[] args){

       /* float a=0;
        float b=0;
        System.out.println("a(i+1)= a(i) + 0.5f - (1f-0.5f)  =/=  b(i+1)= b(i) + 0.2f - (1f-0.8f)");
        for(int i=0;i<10000000;i++){
            a=a + 0.5f - (1f-0.5f);
            b=b + 0.2f - (1f-0.8f);
            if((i%1000000)==0){
                    System.out.println(i+"th iteration : a("+i+")="+a+"  =/=  b("+i+")="+b);
            }
        }*/
       
     /*  double value = 0.05;
       
       long lv=10765160444654801472;//Double.doubleToLongBits(value);
      // 
int aBack = (int)(lv >> 32);
int bBack = (int)lv;

      System.out.println(value+" "+lv+" "+aBack+" "+bBack);
       lv = (long)aBack << 32 | bBack & 0xFFFFFFFFL;
       value=Double.longBitsToDouble(lv);
       System.out.println(value+" "+lv+" "+aBack+" "+bBack);
       */
     /* byte[] arr= longToBytes(lv);
      for(byte b : arr){
          System.out.println(b+" ");
      }
       */
       
    }
    
     private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);    

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
}
